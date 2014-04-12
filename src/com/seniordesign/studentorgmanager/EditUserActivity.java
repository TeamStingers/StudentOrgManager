//package com.seniordesign.studentorgmanager;
//
//import java.util.ArrayList;
//import java.util.List;
// 
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
// 
//
//import com.seniordesign.studentorgmanager.datatransfer.JSONParser;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
// 
//public class EditUserActivity extends Activity {
// 
//    EditText txtUserName;
//    EditText txtPassword;
//    EditText txtEmail;
//    EditText txtCreatedAt;
//    Button btnSave;
//    Button btnDelete;
// 
//    String userName;
// 
//    // Progress Dialog
//    private ProgressDialog pDialog;
// 
//    // JSON parser class
//    JSONParser jsonParser = new JSONParser();
// 
//    // single user url
//    private static final String url_user_detials = "http://api.androidhive.info/android_connect/get_user_details.php";
// 
//    // url to update user
//    private static final String url_update_user = "http://api.androidhive.info/android_connect/update_user.php";
// 
//    // url to delete user
//    private static final String url_delete_user = "http://api.androidhive.info/android_connect/delete_user.php";
// 
//    // JSON Node names
//    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_USER = "User";
//    private static final String TAG_USERNAME = "Username";
//    private static final String TAG_PASSWORD = "Password";
//    private static final String TAG_EMAIL = "EMail";
// 
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.edit_users);
// 
//        // save button
//        btnSave = (Button) findViewById(R.id.btnSave);
//        btnDelete = (Button) findViewById(R.id.btnDelete);
// 
//        // getting user details from intent
//        Intent i = getIntent();
// 
//        // getting user id (pid) from intent
//        userName = i.getStringExtra(TAG_USERNAME);
// 
//        // Getting complete user details in background thread
//        new GetUserDetails().execute();
// 
//        // save button click event
//        btnSave.setOnClickListener(new View.OnClickListener() {
// 
//            @Override
//            public void onClick(View arg0) {
//                // starting background task to update user
//                new SaveUserDetails().execute();
//            }
//        });
// 
//        // Delete button click event
//        btnDelete.setOnClickListener(new View.OnClickListener() {
// 
//            @Override
//            public void onClick(View arg0) {
//                // deleting user in background thread
//                new DeleteUser().execute();
//            }
//        });
// 
//    }
// 
//    /**
//     * Background Async Task to Get complete user details
//     * */
//    class GetUserDetails extends AsyncTask<String, String, String> {
// 
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(EditUserActivity.this);
//            pDialog.setMessage("Loading user details. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
// 
//        /**
//         * Getting user details in background thread
//         * */
//        protected String doInBackground(String... params) {
// 
//            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    // Check for success tag
//                    int success;
//                    try {
//                        // Building Parameters
//                        List<NameValuePair> params = new ArrayList<NameValuePair>();
//                        params.add(new BasicNameValuePair("UserName", userName));
// 
//                        // getting user details by making HTTP request
//                        // Note that user details url will use GET request
//                        JSONObject json = jsonParser.makeHttpRequest(
//                                url_user_detials, "GET", params);
// 
//                        // check your log for json response
//                        Log.d("Single User Details", json.toString());
// 
//                        // json success tag
//                        success = json.getInt(TAG_SUCCESS);
//                        if (success == 1) {
//                            // successfully received user details
//                            JSONArray userObj = json
//                                    .getJSONArray(TAG_USER); // JSON Array
// 
//                            // get first user object from JSON Array
//                            JSONObject user = userObj.getJSONObject(0);
// 
//                            // user with this pid found
//                            // Edit Text
//                            txtUserName = (EditText) findViewById(R.id.userName);
//                            txtPassword = (EditText) findViewById(R.id.password);
//                            txtEmail = (EditText) findViewById(R.id.EMail);
// 
//                            // display user data in EditText
//                            txtUserName.setText(user.getString(TAG_USERNAME));
//                            txtPassword.setText(user.getString(TAG_PASSWORD));
//                            txtEmail.setText(user.getString(TAG_EMAIL));
// 
//                        }else{
//                            // user with pid not found
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
// 
//            return null;
//        }
// 
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once got all details
//            pDialog.dismiss();
//        }
//    }
// 
//    /**
//     * Background Async Task to  Save user Details
//     * */
//    class SaveUserDetails extends AsyncTask<String, String, String> {
// 
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(EditUserActivity.this);
//            pDialog.setMessage("Saving user ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
// 
//        /**
//         * Saving user
//         * */
//        protected String doInBackground(String... args) {
// 
//            // getting updated data from EditTexts
//            String userName = txtUserName.getText().toString();
//            String password = txtPassword.getText().toString();
//            String EMail = txtEmail.getText().toString();
// 
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair(TAG_USERNAME, userName));
//            params.add(new BasicNameValuePair(TAG_PASSWORD, password));
//            params.add(new BasicNameValuePair(TAG_EMAIL, EMail));
// 
//            // sending modified data through http request
//            // Notice that update user url accepts POST method
//            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
//                    "POST", params);
// 
//            // check json success tag
//            try {
//                int success = json.getInt(TAG_SUCCESS);
// 
//                if (success == 1) {
//                    // successfully updated
//                    Intent i = getIntent();
//                    // send result code 100 to notify about user update
//                    setResult(100, i);
//                    finish();
//                } else {
//                    // failed to update user
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
// 
//            return null;
//        }
// 
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once user uupdated
//            pDialog.dismiss();
//        }
//    }
// 
//    /*****************************************************************
//     * Background Async Task to Delete User
//     * */
//    class DeleteUser extends AsyncTask<String, String, String> {
// 
//        /**
//         * Before starting background thread Show Progress Dialog
//         * */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(EditUserActivity.this);
//            pDialog.setMessage("Deleting User...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
// 
//        /**
//         * Deleting user
//         * */
//        protected String doInBackground(String... args) {
// 
//            // Check for success tag
//            int success;
//            try {
//                // Building Parameters
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("Username", userName));
// 
//                // getting user details by making HTTP request
//                JSONObject json = jsonParser.makeHttpRequest(
//                        url_delete_user, "POST", params);
// 
//                // check your log for json response
//                Log.d("Delete User", json.toString());
// 
//                // json success tag
//                success = json.getInt(TAG_SUCCESS);
//                if (success == 1) {
//                    // user successfully deleted
//                    // notify previous activity by sending code 100
//                    Intent i = getIntent();
//                    // send result code 100 to notify about user deletion
//                    setResult(100, i);
//                    finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
// 
//            return null;
//        }
// 
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once user deleted
//            pDialog.dismiss();
// 
//        }
// 
//    }
//}