package pero.fesb.hr.chatproject;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.io.ByteArrayOutputStream;

public class OptionsActivity extends Activity {

    private static String API_URL = MainActivity.API_URL;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView profilePictureImageView;
    private EditText addFriendEditText;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //final ImageView profilePictureImageView = (ImageView) findViewById(R.id.profilePictureImageView);
        this.profilePictureImageView = (ImageView) this.findViewById(R.id.profilePictureImageView);
        final EditText changeProfilePictureEditText = (EditText) findViewById(R.id.changeProfilePictureEditText);
        final Button checkProfilePictureButton = (Button) findViewById(R.id.checkProfilePictureButton);
        final Button changeProfilePictureButton = (Button) findViewById(R.id.changeProfilePictureButton);
        final Button cameraProfilePictureButton = (Button) findViewById(R.id.cameraProfilePictureButton);
        final Button galleryProfilePictureButton = (Button) findViewById(R.id.galleryProfilePictureButton);

        //final EditText addFriendEditText = (EditText) findViewById(R.id.addFriendEditText);
        this.addFriendEditText = (EditText) this.findViewById(R.id.addFriendEditText);
        final TextView addFriendPreviewTextView = (TextView) findViewById(R.id.addFriendPreviewTextView);
        final Button addFriendButton = (Button) findViewById(R.id.addFriendButton);

        final EditText removeFriendEditText = (EditText) findViewById(R.id.removeFriendEditText);
        final TextView removeFriendPreviewTextView = (TextView) findViewById(R.id.removeFriendPreviewTextView);
        final Button removeFriendButton = (Button) findViewById(R.id.removeFriendButton);


        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

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
                    profilePictureImageView.setImageResource(R.drawable.chat_icon_orange);
                } else if(responseString.equals("")) {
                    Ion.with(profilePictureImageView)
                            .placeholder(R.drawable.chat_icon_blue)
                            .error(R.drawable.chat_icon_orange)
                            .load(API_URL+"profile");
                } else {
                    Ion.with(profilePictureImageView)
                            .resize(80, 80)
                            .placeholder(R.drawable.chat_icon_blue)
                            .error(R.drawable.chat_icon_orange)
                            .load(responseString);
//                    Ion.with(getApplicationContext())
//                            .load(responseString)
//                            .noCache()
//                            .withBitmap()
//                            .placeholder(R.drawable.chat_icon_blue)
//                            .error(R.drawable.chat_icon_orange)
//                            .intoImageView(profilePictureImageView);
                    changeProfilePictureEditText.setText(responseString);
                }
            }
        });

        checkProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(profilePictureImageView)
                        .resize(80, 80)
                        .placeholder(R.drawable.chat_icon_blue)
                        .error(R.drawable.chat_icon_orange)
                        .load(changeProfilePictureEditText.getText().toString());
//                Ion.with(getApplicationContext())
//                        .load(changeProfilePictureEditText.getText().toString())
//                        .noCache()
//                        .withBitmap()
//                        .placeholder(R.drawable.chat_icon_blue)
//                        .error(R.drawable.chat_icon_orange)
//                        .intoImageView(profilePictureImageView);
            }
        });

        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("user", myPreferences.getString("user"));
                requestParams.add("pictureUrl", changeProfilePictureEditText.getText().toString());
                asyncHttpClient.post(API_URL + "changeProfilePictureUrl", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Communication error, can't update profile picture", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (responseString.equals("UserNotFound")) {
                            Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                        } else if(responseString.equals("PictureUpdated")) {
                            Toast.makeText(getApplicationContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cameraProfilePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        galleryProfilePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

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
                    asyncHttpClient.post(API_URL + "addFriendPreview", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            addFriendPreviewTextView.setText("");
                            Toast.makeText(getApplicationContext(), "Communication error, can't receive friends preview", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("UserNotFound")) {
                                addFriendPreviewTextView.setText("");
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            } else {
                                addFriendPreviewTextView.setText(responseString);
                            }
                        }
                    });
                }
                else {
                    addFriendPreviewTextView.setText("");
                }
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addFriendEditText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter your friend's name", Toast.LENGTH_SHORT).show();
                }
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user", myPreferences.getString("user"));
                    requestParams.add("friendName", addFriendEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "addFriend", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error, can't add a friend", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("FriendAdded")) {
                                Toast.makeText(getApplicationContext(), addFriendEditText.getText().toString() + " added as a friend", Toast.LENGTH_SHORT).show();
                                addFriendEditText.setText("");
                            } else if (responseString.equals("AlreadyFriends")) {
                                Toast.makeText(getApplicationContext(), addFriendEditText.getText().toString() + " is already your friend", Toast.LENGTH_SHORT).show();
                                addFriendEditText.setText("");
                            } else if (responseString.equals("FriendNotFound")) {
                                Toast.makeText(getApplicationContext(), addFriendEditText.getText().toString() + " not found", Toast.LENGTH_SHORT).show();
                            } else if (responseString.equals("UserNotFound")) {
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        removeFriendEditText.addTextChangedListener(new TextWatcher() {

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
                    requestParams.add("friendName", removeFriendEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "removeFriendPreview", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            removeFriendPreviewTextView.setText("");
                            Toast.makeText(getApplicationContext(), "Communication error, can't receive friends preview", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("UserNotFound")) {
                                removeFriendPreviewTextView.setText("");
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            } else {
                                removeFriendPreviewTextView.setText(responseString);
                            }
                        }
                    });
                }
                else {
                    removeFriendPreviewTextView.setText("");
                }
            }
        });

        removeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(removeFriendEditText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter your friend's name", Toast.LENGTH_SHORT).show();
                }
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user", myPreferences.getString("user"));
                    requestParams.add("friendName", removeFriendEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "removeFriend", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Communication error, can't remove a friend", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("FriendRemoved")) {
                                Toast.makeText(getApplicationContext(), removeFriendEditText.getText().toString() + " removed from friends", Toast.LENGTH_SHORT).show();
                                removeFriendEditText.setText("");
                            } else if (responseString.equals("NotFriends")) {
                                Toast.makeText(getApplicationContext(), removeFriendEditText.getText().toString() + " is not your friend", Toast.LENGTH_SHORT).show();
                                removeFriendEditText.setText("");
                            } else if (responseString.equals("FriendNotFound")) {
                                Toast.makeText(getApplicationContext(), removeFriendEditText.getText().toString() + " not found", Toast.LENGTH_SHORT).show();
                            } else if (responseString.equals("UserNotFound")) {
                                Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePictureImageView.setImageBitmap(photo);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            final MyPreferences myPreferences = new MyPreferences(this);

            RequestParams requestParams = new RequestParams();
            requestParams.add("user", myPreferences.getString("user"));
            requestParams.add("image", image_str);
            asyncHttpClient.post(API_URL + "/uploadProfilePicture", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Communication error, can't upload picture", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if (responseString.equals("UserNotFound")) {
                        Toast.makeText(getApplicationContext(), "We cannot find you, please log in again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Picture uploaded", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
//            Uri selectedImageUri = data.getData();
//            selectedImagePath = getPath(selectedImageUri);
//            addFriendEditText.setText(selectedImageUri.toString());
//
//            Bitmap photo = BitmapFactory.decodeFile(selectedImageUri.toString());
//            profilePictureImageView.setImageBitmap(photo);

            Uri selectedImageURI = data.getData();
            selectedImagePath = getRealPathFromURI(selectedImageURI);
            //imageFile = new File(getRealPathFromURI(selectedImageURI));
            addFriendEditText.setText(selectedImagePath);
            Bitmap photo = BitmapFactory.decodeFile(selectedImagePath);
            profilePictureImageView.setImageBitmap(photo);
        }
    }

    public String getPath(Uri uri) {
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
