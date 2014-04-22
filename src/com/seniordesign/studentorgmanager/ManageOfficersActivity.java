package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.ProfileActivity.InitTask;
import com.seniordesign.studentorgmanager.data.*;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

/**
 * Screen for managing officers, accessible only by officers and admins. Users can view officers, change their types/positions,
 * and promote other org members to officer positions
 * @author Sanchit
 *
 */
public class ManageOfficersActivity extends Activity {

	// UI Elements
	private TextView mTitleLabel;
	private ListView mOfficersList;
	private Button mAddOfficerButton;
	
	// Context variables
	private String username;
	private String orgName;
	private String memberType;
	private UserDAO mLoggedIn;
	
	// Other variables
	private ArrayList<String> allMembers;
	private ArrayList<UserOfficer> officers;
	private static String[] types = {"Admin", "Officer", "Regular Member"};
	private ArrayList<String> notOfficers;
	
	private class UserOfficer {
		public String username;
		public String memberType;
		public String position;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_officers);
		
		// Set context variables
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		officers = new ArrayList<UserOfficer>();
		notOfficers = new ArrayList<String>();
		
		// Instantiate UI references
		mTitleLabel = (TextView) findViewById(R.id.manageOfficersOrgTitle);
		mTitleLabel.setText(orgName);
		mOfficersList = (ListView) findViewById(R.id.currentOfficersListView);
		mAddOfficerButton = (Button) findViewById(R.id.newOfficerButton);
		
		// Run DB actions
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		// Wait for DB actions to complete
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
		
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			memberType = DataTransfer.getUserMemberType(username, orgName);
			
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_officers, menu);
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
