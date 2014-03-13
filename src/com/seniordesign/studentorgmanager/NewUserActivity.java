package com.seniordesign.studentorgmanager;
import java.util.ArrayList;
import java.util.List;
 

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 

import com.seniordesign.studentorgmanager.datatransfer.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class NewUserActivity extends Activity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    EditText inputUserName;
    EditText inputPassword;
    EditText inputEmail;
 
    // url to create new user
    private static String url_create_user = "http://api.androidhive.info/android_connect/create_user.php";
 
    // JSON Node userNames
    private static final String TAG_SUCCESS = "success";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orgs_list);
 
        // Edit Text
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
 
        // Create button
        Button btnCreateuser = (Button) findViewById(R.id.buttonCreateUser);
 
        // button click event
        btnCreateuser.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewUser().execute();
            }
        });
    }
 
    /**
     * Background Async Task to Create new user
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewUserActivity.this);
            pDialog.setMessage("Creating User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating user
         * */
        protected String doInBackground(String... args) {
            String userName = inputUserName.getText().toString();
            String password = inputPassword.getText().toString();
            String email = inputEmail.getText().toString();
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Username", userName));
            params.add(new BasicNameValuePair("Password", password));
            params.add(new BasicNameValuePair("EMail", email));
 
            // getting JSON Object
            // Note that create user url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                    "POST", params);
 
            // check log cat for response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created user
                    Intent i = new Intent(getApplicationContext(), AllUsersActivity.class);
                    startActivity(i);
 
                    // closing this screen
                    finish();
                } else {
                    // failed to create user
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
}