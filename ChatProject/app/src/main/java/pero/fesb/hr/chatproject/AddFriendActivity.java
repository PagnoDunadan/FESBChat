package pero.fesb.hr.chatproject;


import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class AddFriendActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        final EditText addFriendEditText = (EditText) findViewById(R.id.addFriendEditText);
        final ListView addFriendListView = (ListView) findViewById(R.id.addFriendListView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final MyPreferences myPreferences = new MyPreferences(this);

        addFriendEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){

                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user", myPreferences.getString("user"));
                    requestParams.add("friendName", addFriendEditText.getText().toString());
                    asyncHttpClient.post(API_URL+"addFriendList", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error, can't receive users list", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if(responseString.equals("UserNotFound")) {
                                AddFriendDataStorage.addFriendsListViewData.clear();
                                addFriendListView.setAdapter(null);
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            }
                            else if(responseString.equals("None")) {
                                AddFriendDataStorage.addFriendsListViewData.clear();
                                addFriendListView.setAdapter(null);
                            }
                            else {
                                Gson mGson = new Gson();
                                AddFriendDataStorage.addFriends = mGson.fromJson(responseString, AddFriend[].class);
                                AddFriendDataStorage.fillData();
                                addFriendListView.setAdapter(new AddFriendAdapter(getApplicationContext()));

                                addFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                                        RequestParams requestParams = new RequestParams();
                                        requestParams.add("user", myPreferences.getString("user"));
                                        requestParams.add("friendName", AddFriendDataStorage.addFriendsListViewData.get(i).getName());
                                        asyncHttpClient.post(API_URL + "addFriend", requestParams, new TextHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                Toast.makeText(getApplicationContext(), "Communication error, can't add a friend", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                if (responseString.equals("FriendAdded")) {
                                                    Toast.makeText(getApplicationContext(), AddFriendDataStorage.addFriendsListViewData.get(i).getName() + " added as a friend", Toast.LENGTH_SHORT).show();
                                                    addFriendEditText.setText("");
                                                } else if (responseString.equals("AlreadyFriends")) {
                                                    Toast.makeText(getApplicationContext(), AddFriendDataStorage.addFriendsListViewData.get(i).getName() + " is already your friend", Toast.LENGTH_SHORT).show();
                                                } else if (responseString.equals("FriendNotFound")) {
                                                    Toast.makeText(getApplicationContext(), AddFriendDataStorage.addFriendsListViewData.get(i).getName() + " not found", Toast.LENGTH_SHORT).show();
                                                } else if (responseString.equals("UserNotFound")) {
                                                    Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    AddFriendDataStorage.addFriendsListViewData.clear();
                    addFriendListView.setAdapter(null);
                }
            }
        });
    }
}
