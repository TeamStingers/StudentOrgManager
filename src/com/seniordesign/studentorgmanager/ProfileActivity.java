package com.seniordesign.studentorgmanager;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.app.Activity;
import android.content.Context;
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
import android.os.Build;
import android.view.View.OnClickListener;

public class ProfileActivity extends Activity {

	//UI elements
	EditText mUsernameEdit;
	EditText mFirstNameEdit;
	EditText mLastNameEdit;
	EditText mMajorEdit;
	EditText mGradEdit;
	EditText mBioEdit;
	Button doneButton;
	Button cancelButton;
	
	//Input variables
	String mUsernameChange;
	String mFirstNameChange;
	String mLastNameChange;
	String mMajorChange;
	String mGradChange;
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
		mFirstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
		mLastNameEdit = (EditText) findViewById(R.id.lastNameEdit);
		mMajorEdit = (EditText) findViewById(R.id.majorEdit);
		mGradEdit = (EditText) findViewById(R.id.gradEdit);
		mBioEdit = (EditText) findViewById(R.id.bioEdit);
		doneButton = (Button) findViewById(R.id.doneButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		
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
		if (mLoggedIn.firstName != null && mLoggedIn.firstName != "" && mLoggedIn.firstName != "NULL") {
			mFirstNameEdit.setText(mLoggedIn.firstName);
		}
		if (mLoggedIn.lastName != null && mLoggedIn.lastName != "" && mLoggedIn.lastName != "NULL") {
			mLastNameEdit.setText(mLoggedIn.lastName);
		}
		if (mLoggedIn.major != null && mLoggedIn.major != "" && mLoggedIn.major != "NULL") {
			mMajorEdit.setText(mLoggedIn.major);
		}
		if (mLoggedIn.gradYear != null && mLoggedIn.gradYear != "" && mLoggedIn.gradYear != "NULL") {
			mGradEdit.setText(mLoggedIn.gradYear);
		}
		if (mLoggedIn.bio != null && mLoggedIn.bio != "" && mLoggedIn.bio != "NULL") {
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
			}
		}
		
		public void saveInfo() {
			boolean cancel = false;
			
			mUsernameEdit.setError(null);
			
			mUsernameChange = mUsernameEdit.getText().toString();
			mFirstNameChange = mFirstNameEdit.getText().toString();
			mLastNameChange = mLastNameEdit.getText().toString();
			mMajorChange = mMajorEdit.getText().toString();
			mGradChange = mGradEdit.getText().toString();
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
		
		public class SaveTask extends AsyncTask<Void, Void, Void> {
			protected Void doInBackground(Void... params) {
				//Update user fields DataTransfer.updateUser
				return null;
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
