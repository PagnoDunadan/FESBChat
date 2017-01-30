package pero.fesb.hr.chatproject;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class FriendsChatActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;
    private static String user = "";
    private static String friendName = "";
    final Handler messagesHandler = new Handler();
    final Handler onlineStatusHandler = new Handler();
    private static String messagesBuffer = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chat);

        final ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
        final TextView profileTextView = (TextView) findViewById(R.id.profileTextView);
        final TextView friendStatus = (TextView) findViewById(R.id.friendStatus);
        final ListView listView = (ListView) findViewById(R.id.listView);
        final EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
        final Button sendMessageButton = (Button) findViewById(R.id.sendMessageButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        messagesBuffer = "";

        final MyPreferences myPreferences = new MyPreferences(this);
        user = myPreferences.getString("user");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            friendName = extras.getString("friendName");
            profileTextView.setText(friendName);

            RequestParams requestParams = new RequestParams();
            requestParams.add("user", friendName);
            asyncHttpClient.get(API_URL+"getProfilePictureUrl", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Communication error, can't receive your profile picture URL", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if(responseString.equals("UserNotFound")) {
                        Toast.makeText(getApplicationContext(), "We can't find you, please log in again", Toast.LENGTH_SHORT).show();
                        profileImageView.setImageResource(R.drawable.chat_icon_orange);
                    } else if(responseString.equals("")) {
                        Ion.with(profileImageView)
                                .placeholder(R.drawable.chat_icon_blue)
                                .error(R.drawable.chat_icon_orange)
                                .load(API_URL+"profile");
                    } else {
                        Ion.with(profileImageView)
                                .resize(80, 80)
                                .placeholder(R.drawable.chat_icon_blue)
                                .error(R.drawable.chat_icon_orange)
                                .load(responseString);
//                        Ion.with(getApplicationContext())
//                                .load(responseString)
//                                .noCache()
//                                .withBitmap()
//                                .placeholder(R.drawable.chat_icon_blue)
//                                .error(R.drawable.chat_icon_orange)
//                                .intoImageView(profileImageView);
                    }
                }
            });
        }


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageEditText.getText().toString().equals("")) {
                    sendMessageButton.setBackgroundResource(R.drawable.sending_message);
                    // Posalji poruku
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user", user);
                    requestParams.add("friend", friendName);
                    requestParams.add("message", messageEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "sendFriendMessage", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error, can't send message", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("UserNotFound")) {
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            } else if (responseString.equals("FriendNotFound")) {
                                Toast.makeText(getApplicationContext(), "We cannot find your friend" + friendName + ", please refresh your friends list", Toast.LENGTH_SHORT).show();
                            } else if (responseString.equals("MessageSaved")) {

                                // On success skini sve poruke
                                messageEditText.setText("");
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("user", user);
                                requestParams.add("friend", friendName);
                                asyncHttpClient.post(API_URL + "getFriendMessages", requestParams, new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Toast.makeText(getApplicationContext(), "Communication error, can't receive messages", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                        if (responseString.equals("UserNotFound")) {
                                            Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                                        } else if (responseString.equals("FriendNotFound")) {
                                            Toast.makeText(getApplicationContext(), "We cannot find your friend" + friendName + ", please refresh your friends list", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Gson mGson = new Gson();
                                            FriendMessagesDataStorage.friendMessages = mGson.fromJson(responseString, FriendMessage[].class);
                                            FriendMessagesDataStorage.fillData();
                                            listView.setAdapter(new FriendMessagesAdapter(getApplicationContext()));

                                            listView.setSelection(listView.getCount());

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    if (view.findViewById(R.id.friendMessageTime).getVisibility() == View.GONE) {
                                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.VISIBLE);
                                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.VISIBLE);
                                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(true);
                                                    } else {
                                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.GONE);
                                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.GONE);
                                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(false);
                                                    }
                                                }
                                            });
                                        }
                                        sendMessageButton.setBackgroundResource(R.drawable.send_message);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        messagesHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.add("user", user);
                requestParams.add("friend", friendName);
                asyncHttpClient.post(API_URL+"getFriendMessages", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Communication error, can't receive messages", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equals("UserNotFound")) {
                            Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                        }
                        else if(responseString.equals("FriendNotFound")) {
                            Toast.makeText(getApplicationContext(), "We cannot find your friend"+friendName+", please refresh your friends list", Toast.LENGTH_SHORT).show();
                        }
                        else if(responseString.equals("")) {

                        }
                        else if(messagesBuffer.equals("")) {
                            Gson mGson = new Gson();
                            FriendMessagesDataStorage.friendMessages = mGson.fromJson(responseString, FriendMessage[].class);
                            FriendMessagesDataStorage.fillData();
                            listView.setAdapter(new FriendMessagesAdapter(getApplicationContext()));
                            listView.setSelection(listView.getCount());

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (view.findViewById(R.id.friendMessageTime).getVisibility() == View.GONE) {
                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.VISIBLE);
                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(true);
                                    } else {
                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.GONE);
                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.GONE);
                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(false);
                                    }
                                }
                            });
                            messagesBuffer = responseString;
                        }
                        else if(!responseString.equals(messagesBuffer)) {
                            Gson mGson = new Gson();
                            FriendMessagesDataStorage.friendMessages = mGson.fromJson(responseString, FriendMessage[].class);
                            FriendMessagesDataStorage.fillData();
                            listView.setAdapter(new FriendMessagesAdapter(getApplicationContext()));

                            listView.setSelection(listView.getCount());

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (view.findViewById(R.id.friendMessageTime).getVisibility() == View.GONE) {
                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.VISIBLE);
                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(true);
                                    } else {
                                        view.findViewById(R.id.friendMessageTime).setVisibility(View.GONE);
                                        view.findViewById(R.id.friendMessageSeen).setVisibility(View.GONE);
                                        FriendMessagesDataStorage.friendMessagesListViewData.get(i).setDetailsVisible(false);
                                    }
                                }
                            });
                            messagesBuffer = responseString;
                        }
                    }
                });
                messagesHandler.postDelayed(this, 2000);
            }
        }, 0);

        onlineStatusHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.add("user", friendName);
                asyncHttpClient.get(API_URL+"getOnlineStatus", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Communication error, can't receive "+friendName+"'s online status", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equals("UserNotFound")) {
                            Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Date now = new Date();
                            long difference = now.getTime() - Long.valueOf(responseString);

                            if(difference >= 0 && difference < 60000) {
                                friendStatus.setTextColor(Color.parseColor("#00C853"));
                                friendStatus.setText("\u2022 Online");
                            } else if(difference >= 60000 && difference < 3600000) {
                                friendStatus.setTextColor(Color.parseColor("#000000"));
                                friendStatus.setText( (int) difference/60000 + " minutes ago");
                            } else if(difference >= 3600000 && difference < 86400000) {
                                friendStatus.setTextColor(Color.parseColor("#000000"));
                                friendStatus.setText( (int) difference/3600000 + " hours ago");
                            } else {
                                friendStatus.setTextColor(Color.parseColor("#000000"));
                                DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                friendStatus.setText(formatter.format(Long.valueOf(responseString)));
                            }

                        }
                    }
                });
                onlineStatusHandler.postDelayed(this, 10000);
            }
        }, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messagesHandler.removeCallbacksAndMessages(null);
        onlineStatusHandler.removeCallbacksAndMessages(null);
        this.finish();
    }
}
