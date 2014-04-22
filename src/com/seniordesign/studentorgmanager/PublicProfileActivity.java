package com.seniordesign.studentorgmanager;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class PublicProfileActivity extends Activity {

	public static final String UserBeingViewedTag = "USER_BEING_VIEWED";
	
	private String usernameViewed; //Username of person who is being viewed
	private String username; //Username of person logged in
	private UserDAO userViewed;
	
	//UI Elements
	private TextView publicUsername;
	private TextView publicEmail;
	private TextView publicFirstName;
	private TextView publicLastName;
	private TextView publicMajor;
	private TextView publicGradYear;
	private TextView publicPhoneNumber;
	private TextView publicBio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_profile);
		
		Intent intent = getIntent();
		username = intent.getExtras().getString(LoginActivity.UserNameTag);
		usernameViewed = intent.getExtras().getString(PublicProfileActivity.UserBeingViewedTag);
		
		//Initialize UI elements
		publicUsername = (TextView) findViewById(R.id.publicUsername);
		publicEmail = (TextView) findViewById(R.id.publicEmail);
		publicFirstName = (TextView) findViewById(R.id.publicFirstName);
		publicLastName = (TextView) findViewById(R.id.publicLastName);
		publicMajor = (TextView) findViewById(R.id.publicMajor);
		publicGradYear = (TextView) findViewById(R.id.publicGradYear);
		publicPhoneNumber = (TextView) findViewById(R.id.publicPhoneNumber);
		publicBio = (TextView) findViewById(R.id.publicBio);
		
		//Run DB tasks
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
		
		updateLabels();

	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			userViewed = DataTransfer.getUser(usernameViewed);
			return null;
		}
	}
	
	public void updateLabels() {
		publicUsername.setText(userViewed.username);
		publicEmail.setText(userViewed.email);
		publicFirstName.setText(userViewed.firstName);
		publicLastName.setText(userViewed.lastName);
		publicMajor.setText(userViewed.major);
		publicGradYear.setText(userViewed.gradYear);
		publicPhoneNumber.setText(userViewed.phoneNumber);
		publicBio.setText(userViewed.bio);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		Intent i = new Intent();
		
		switch (id) {
			case R.id.action_home:
				i = new Intent(this, MainActivity.class);
				i.putExtra(LoginActivity.UserNameTag, username);
				startActivity(i);
				break;
			case R.id.action_profile:
				i = new Intent(this, ProfileActivity.class);
				i.putExtra(LoginActivity.UserNameTag, username);
				startActivity(i);
				break;
			case R.id.action_logout:
				new AlertDialog.Builder(this)
				.setTitle("Logging Out")
				.setMessage("Do you really want to log out?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
						logout();
				    }})
				 .setNegativeButton(android.R.string.no, null).show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void logout() {
		Intent i = new Intent(this, LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}



}
