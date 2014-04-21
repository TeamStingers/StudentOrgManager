package com.seniordesign.studentorgmanager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class RegDuesActivity extends Activity {

	// UI Elements
	TextView mTitleLabel;
	TextView mStatusLabel;
	TextView mCurrentDuesLabel;
	
	// Context variables
	String username;
	String orgName;
	String status;
	OrganizationDAO org;
	float dues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_dues);
		
		// Set context variables
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		
		// Instantiate UI Elements
		mTitleLabel = (TextView) findViewById(R.id.regDuesTitle);
		mTitleLabel.setText(orgName);
		mStatusLabel = (TextView) findViewById(R.id.regDuesStatusMessage);
		mCurrentDuesLabel = (TextView) findViewById(R.id.regDuesAmount);
		
		// Run DB Tasks
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		// Wait for DB tasks for finish
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
		
		// Update dues status
		if (status.equalsIgnoreCase("Paid")) {
			mStatusLabel.setText("Your dues have been paid!");
		}
		else {
			mStatusLabel.setText("Your dues have not been paid!");
			mStatusLabel.setTextColor(Color.parseColor("#ff0000"));
		}
		// Update org dues info
		mCurrentDuesLabel.setText(Float.toString(dues) + "$");
		
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			status = DataTransfer.getDuesStatus(username, orgName);
			org = DataTransfer.getOrganization(orgName);
			dues = org.annualDues;
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reg_dues, menu);
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