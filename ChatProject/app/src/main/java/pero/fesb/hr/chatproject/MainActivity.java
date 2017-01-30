package pero.fesb.hr.chatproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static String API_URL = "http://192.168.1.65:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
        final TextView profileTextView = (TextView) findViewById(R.id.profileTextView);
        final Button optionsButton = (Button) findViewById(R.id.optionsButton);
        final Spinner optionsSpinner = (Spinner) findViewById(R.id.optionsSpinner);
        final ListView listView = (ListView) findViewById(R.id.listView);
        final Button friendsButton = (Button) findViewById(R.id.friendsButton);
        final Button groupsButton = (Button) findViewById(R.id.groupsButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        if(myPreferences.getString("user").equals("")) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }
        else {
            profileTextView.setText(myPreferences.getString("user"));

            RequestParams requestParams = new RequestParams();
            requestParams.add("user", myPreferences.getString("user"));
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

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(myIntent);
                //finish();
            }
        });


        List<String> list = new ArrayList<String>();
        list.add("Add Friend");
        list.add("Remove Friend");
        list.add("Change Picture");
        list.add("Sign Out");
        list.add("Close");
        final int listSize = list.size() - 1;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                return(listSize); // Truncate the list
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionsSpinner.setAdapter(dataAdapter);
        optionsSpinner.setSelection(4); // Hidden item to appear in the spinner

        optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    optionsSpinner.setSelection(listSize);
                    //Toast.makeText(getApplicationContext(), "Add Friend", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, AddFriendActivity.class);
                    startActivity(myIntent);
                }
                else if (position==1) {
                    optionsSpinner.setSelection(listSize);
                    //Toast.makeText(getApplicationContext(), "Remove Friend", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, OptionsActivity.class);
                    startActivity(myIntent);
                }
                else if (position==2) {
                    optionsSpinner.setSelection(listSize);
                    //Toast.makeText(getApplicationContext(), "Change Picture", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, OptionsActivity.class);
                    startActivity(myIntent);
                }
                else if (position==3) {
                    optionsSpinner.setSelection(listSize);
                    myPreferences.setString("user", "");
                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                    finish();
                }
                else if (position==4) {
                    // Ovo je hidden pozicija
                    //optionsSpinner.setSelection(listSize);
                    //Toast.makeText(getApplicationContext(), "Hidden Position", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("user", myPreferences.getString("user"));
                asyncHttpClient.get(API_URL+"getFriendsList", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Communication error, can't receive your friends list", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equals("UserNotFound")) {
                            FriendsDataStorage.friendsListViewData.clear();
                            listView.setAdapter(null);
                            Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                        }
                        else if(responseString.equals("FriendsListEmpty")) {
                            FriendsDataStorage.friendsListViewData.clear();
                            listView.setAdapter(null);
                            Toast.makeText(getApplicationContext(), "Your friends list is empty. Add some friends!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Gson mGson = new Gson();
                            FriendsDataStorage.friends = mGson.fromJson(responseString, Friend[].class);
                            FriendsDataStorage.fillData();
                            listView.setAdapter(new FriendsAdapter(getApplicationContext()));

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Friend friend = FriendsDataStorage.friendsListViewData.get(i);
                                    Intent myIntent = new Intent(MainActivity.this, FriendsChatActivity.class);
                                    myIntent.putExtra("friendName", String.valueOf(friend.getName()));
                                    startActivity(myIntent);
                                }
                            });
                        }
                    }
                });
            }
        });

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncHttpClient.get(API_URL+"groups", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Communication error, can't receive groups list", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Gson mGson = new Gson();
                        FriendsDataStorage.friends = mGson.fromJson(responseString, Friend[].class);
                        FriendsDataStorage.fillData();
                        listView.setAdapter(new FriendsAdapter(getApplicationContext()));
                    }
                });
            }
        });

    }
}
