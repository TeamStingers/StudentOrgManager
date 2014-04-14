package com.seniordesign.studentorgmanager;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.UserDAO;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class CreateUserActivity extends Activity {

	private String mUsername;
	private String mPassword;
	private String mConfirmPassword;
	private String mEmail;
	private EditText mUsernameEdit;
	private EditText mPasswordEdit;
	private EditText mConfirmPasswordEdit;
	private EditText mEmailEdit;
	private Button mRegisterButton;
	
	private RegistrationTask mRegistrationTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		mUsername = extras.getString(LoginActivity.UserNameTag);
		mPassword = extras.getString(LoginActivity.PasswordTag);
		
		mUsernameEdit = (EditText) findViewById(R.id.usernameEdit);
		mPasswordEdit = (EditText) findViewById(R.id.passwordEdit);
		mConfirmPasswordEdit = (EditText) findViewById(R.id.confirmPasswordEdit);
		mEmailEdit = (EditText) findViewById(R.id.emailEdit);
		mRegisterButton = (Button) findViewById(R.id.registerButton);
		mUsernameEdit.requestFocus();
		
		if (mUsername != null && mUsername.length() > 0) {
			mUsernameEdit.setText(mUsername);
			mPasswordEdit.requestFocus();
		}
		
		mRegisterButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegistration();
					}
				});
	}

	public void attemptRegistration() {
		if (mRegistrationTask != null) {
			return;
		}
		
		mUsername = mUsernameEdit.getText().toString();
		mPassword = mPasswordEdit.getText().toString();
		mConfirmPassword = mConfirmPasswordEdit.getText().toString();
		mEmail = mEmailEdit.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		mUsernameEdit.setError(null);
		mPasswordEdit.setError(null);
		mConfirmPasswordEdit.setError(null);
		mEmailEdit.setError(null);
		
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameEdit.setError(getString(R.string.error_field_required));
			focusView = mUsernameEdit;
			cancel = true;
		}
		else if (TextUtils.isEmpty(mPassword)) {
			mPasswordEdit.setError(getString(R.string.error_field_required));
			focusView = mPasswordEdit;
			cancel = true;
		}
		else if (TextUtils.isEmpty(mConfirmPassword)) {
			mConfirmPasswordEdit.setError(getString(R.string.error_field_required));
			focusView = mConfirmPasswordEdit;
			cancel = true;
		}
		else if (TextUtils.isEmpty(mEmail)) {
			mEmailEdit.setError(getString(R.string.error_field_required));
			focusView = mEmailEdit;
			cancel = true;
		}
		else if (mPassword.compareTo(mConfirmPassword)!=0) {
			mConfirmPasswordEdit.setError(getString(R.string.error_password_mismatch));
			focusView = mConfirmPasswordEdit;
			cancel = true;
		}
		else if (mPassword.length() < 6 || mConfirmPassword.length() < 6) {
			mPasswordEdit.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordEdit;
			cancel = true;
		}
		else if (mEmail.indexOf("@") == -1) {
			mEmailEdit.setError(getString(R.string.error_invalid_email));
			focusView = mEmailEdit;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		}
		else {
			mRegistrationTask = new RegistrationTask();
			mRegistrationTask.execute((Void) null);
		}
	}
	
	public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			
			UserDAO newUser = DataTransfer.createUser(mUsername, mPassword, mEmail);
			
			return (newUser!=null);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegistrationTask = null;
			
			if (success) {
				
				//Here's where we transition
				Intent loginIntent = new Intent(CreateUserActivity.this, MainActivity.class);
				loginIntent.putExtra(LoginActivity.UserNameTag, mUsername);
				startActivity(loginIntent);
				finish();
			} else {
				Toast.makeText(CreateUserActivity.this, "Error registering!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mRegistrationTask = null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
