package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.DataTransfer;

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
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

/**
 * View an organization's officers. Accessible if you are a regular member
 * @author Sanchit
 *
 */
public class ViewOfficersActivity extends Activity {

	// UI References
	private TextView mTitleLabel;
	private ListView mOfficersList;
	
	// Context variables
	private String username;
	private String orgName;
	
	// Other variables
	private ArrayList<UserOfficer> officers;
	private ArrayList<String> allMembers;
	
	private class UserOfficer {
		public String username;
		public String memberType;
		public String position;
		public UserOfficer(String u, String mt, String p) {
			username = u;
			memberType = mt;
			position = p;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_officers);
		
		// Set context
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		
		// Instantiate UI Elements
		mTitleLabel = (TextView) findViewById(R.id.viewOfficersOrgTitle);
		mTitleLabel.setText(orgName);
		mOfficersList = (ListView) findViewById(R.id.viewOfficersListView);
		
		allMembers = new ArrayList<String>();
		officers = new ArrayList<UserOfficer>();
		
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
			allMembers = DataTransfer.getUsersForOrg(orgName);
			
			for (String user : allMembers) {
				
				String type = DataTransfer.getUserMemberType(user, orgName);
				if (!(type.equalsIgnoreCase("RegularMember"))) {
					String pos = DataTransfer.getUserPosition(user, orgName);
					officers.add(new UserOfficer(user, type, pos));
				}
			}
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_officers, menu);
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