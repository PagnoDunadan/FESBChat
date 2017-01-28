package pero.fesb.hr.chatproject;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

public class SignUpActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final TextView passwordCheckerTextView = (TextView) findViewById(R.id.passwordCheckerTextView);
        final Button signUpButton = (Button) findViewById(R.id.signUpButton);
        final ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final Button loginButton = (Button) findViewById(R.id.loginButton);

        final MyPreferences myPreferences = new MyPreferences(this);

        logoImageView.setImageResource(R.drawable.chat_icon_orange);

        passwordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    passwordCheckerTextView.setText("");
                } else if(s.length()<6) {
                    passwordCheckerTextView.setText("WEAK");
                    passwordCheckerTextView.setTextColor(Color.parseColor("#F44336"));
                } else if(s.length()>=6) {
                    passwordCheckerTextView.setText("GOOD");
                    passwordCheckerTextView.setTextColor(Color.parseColor("#4CAF50"));
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                else if(passwordEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("action", "NewUser");
                    requestParams.add("user", usernameEditText.getText().toString());
                    requestParams.add("password", passwordEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "login", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error: server unreachable. We cannot sign you up", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("SignUpSuccessful")) {
                                myPreferences.setString("user", usernameEditText.getText().toString());
                                Intent myIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                            else if (responseString.equals("UserAlreadyExists")) {
                                Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}
