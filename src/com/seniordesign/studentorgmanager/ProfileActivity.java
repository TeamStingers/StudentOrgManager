package com.seniordesign.studentorgmanager;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.view.View.OnClickListener;

public class ProfileActivity extends Activity {

	//UI elements
	EditText mUsernameEdit;
	EditText mEmailEdit;
	EditText mFirstNameEdit;
	EditText mLastNameEdit;
	EditText mMajorEdit;
	EditText mGradEdit;
	EditText mPhoneNumberEdit;
	EditText mBioEdit;
	Button doneButton;
	Button cancelButton;
	Button deleteAcctBtn;
	
	//Input variables
	String mUsernameChange;
	String mEmailChange;
	String mFirstNameChange;
	String mLastNameChange;
	String mMajorChange;
	String mGradChange;
	String mPhoneNumberChange;
	String mBioChange;
	
	//User variables
	String username;
	UserDAO mLoggedIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		//Instantiate UI elements
		mUsernameEdit = (EditText) findViewById(R.id.usernameEdit);
		mEmailEdit = (EditText) findViewById(R.id.emailEdit);
		mFirstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
		mLastNameEdit = (EditText) findViewById(R.id.lastNameEdit);
		mMajorEdit = (EditText) findViewById(R.id.majorEdit);
		mGradEdit = (EditText) findViewById(R.id.gradEdit);
		mPhoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEdit);
		mBioEdit = (EditText) findViewById(R.id.bioEdit);
		doneButton = (Button) findViewById(R.id.doneButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		deleteAcctBtn = (Button) findViewById(R.id.deleteAcctBtn);
		
		doneButton.setOnClickListener(new ButtonClickListener(this, R.id.doneButton));
		cancelButton.setOnClickListener(new ButtonClickListener(this, R.id.cancelButton));
		deleteAcctBtn.setOnClickListener(new ButtonClickListener(this, R.id.deleteAcctBtn));
		
		//Get username
		Intent intent = getIntent();
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		
		//Run DB actions
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		//Wait for DB actions to complete
		try {
			mInitTask.get(15000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Populate fields
		mUsernameEdit.setText(username);
		if (mLoggedIn.email != null && mLoggedIn.email != "" && mLoggedIn.email != "null") {
			mEmailEdit.setText(mLoggedIn.email);
		}
		if (mLoggedIn.firstName != null && mLoggedIn.firstName != "" && mLoggedIn.firstName != "null") {
			mFirstNameEdit.setText(mLoggedIn.firstName);
		}
		if (mLoggedIn.lastName != null && mLoggedIn.lastName != "" && mLoggedIn.lastName != "null") {
			mLastNameEdit.setText(mLoggedIn.lastName);
		}
		if (mLoggedIn.major != null && mLoggedIn.major != "" && mLoggedIn.major != "null") {
			mMajorEdit.setText(mLoggedIn.major);
		}
		if (mLoggedIn.gradYear != null && mLoggedIn.gradYear != "" && mLoggedIn.gradYear != "null") {
			mGradEdit.setText(mLoggedIn.gradYear);
		}
		if (mLoggedIn.phoneNumber != null && mLoggedIn.phoneNumber != "" && mLoggedIn.phoneNumber != "null") {
			mGradEdit.setText(mLoggedIn.phoneNumber);
		}
		if (mLoggedIn.bio != null && mLoggedIn.bio != "" && mLoggedIn.bio != "null") {
			mBioEdit.setText(mLoggedIn.bio);
		}
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			mLoggedIn = DataTransfer.getUser(username);
			return null;
		}
	}
	
	public class ButtonClickListener implements OnClickListener {

		Context mContext;
		int id;
		
		public ButtonClickListener(Context context, int id) {
			mContext = context;
			this.id = id;
		}
		
		@Override
		public void onClick(View v) {
			switch (id) {
				case R.id.doneButton:
					saveInfo();
					
					Intent saveIntent = new Intent(ProfileActivity.this, MainActivity.class);
					saveIntent.putExtra(LoginActivity.UserNameTag, username);
					startActivity(saveIntent);
				case R.id.cancelButton:
					Intent cancelIntent = new Intent(ProfileActivity.this, MainActivity.class);
					cancelIntent.putExtra(LoginActivity.UserNameTag, username);
					startActivity(cancelIntent);
				case R.id.deleteAcctBtn:
					new AlertDialog.Builder(ProfileActivity.this)
					.setTitle("Delete Account")
					.setMessage("Do you really want to delete your account?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int whichButton) {
							DeleteTask dt = new DeleteTask();
							dt.execute((Void) null);
					    }})
					 .setNegativeButton(android.R.string.no, null).show();
					
			}
		}
		
	}
	

	public class DeleteTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.deleteUser(username);
			return null;
		}
		
		protected void onPostExecute(final Void param) {
			startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
		}
		
	}
	
	public class SaveTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.updateUser(mUsernameChange, mFirstNameChange, mLastNameChange, mMajorChange, 
					mBioChange, mEmailChange, mPhoneNumberChange, mGradChange);
			return null;
		}
	}
	
	public void saveInfo() {
		boolean cancel = false;
		
		mUsernameEdit.setError(null);
		
		mUsernameChange = mUsernameEdit.getText().toString();
		mEmailChange = mEmailEdit.getText().toString();
		mFirstNameChange = mFirstNameEdit.getText().toString();
		mLastNameChange = mLastNameEdit.getText().toString();
		mMajorChange = mMajorEdit.getText().toString();
		mGradChange = mGradEdit.getText().toString();
		mPhoneNumberChange = mPhoneNumberEdit.getText().toString();
		mBioChange = mBioEdit.getText().toString();
		
		if (TextUtils.isEmpty(mUsernameChange)) {
			mUsernameEdit.setError(getString(R.string.error_field_required));
			cancel = true;
		}
		
		if (cancel) {
			mUsernameEdit.requestFocus();
		}
		else {
			SaveTask mSaveTask = new SaveTask();
			mSaveTask.execute((Void) null);
			
			try {
				mSaveTask.get(15000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
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