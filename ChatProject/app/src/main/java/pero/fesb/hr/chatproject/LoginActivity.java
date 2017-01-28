package pero.fesb.hr.chatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final Button signUpButton = (Button) findViewById(R.id.signUpButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        logoImageView.setImageResource(R.drawable.chat_icon_blue);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                else if(passwordEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("action", "ExistingUser");
                    requestParams.add("user", usernameEditText.getText().toString());
                    requestParams.add("password", passwordEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "login", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error: server unreachable. We cannot log you in", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("LoginSuccessful")) {
                                myPreferences.setString("user", usernameEditText.getText().toString());
                                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                            else if (responseString.equals("UserNotFound")) {
                                Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}
