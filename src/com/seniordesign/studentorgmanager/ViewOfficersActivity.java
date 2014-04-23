package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.DataTransfer;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
		
		setOfficersList();
		
	}

	public void setOfficersList() {
		String[] from = {"username", "type", "position"};
		int[] to = {R.id.officerRowUsername, R.id.officerRowType, R.id.officerRowPosition};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		for (UserOfficer user : officers) {
			map = new HashMap<String, Object>();
			map.put("username", user.username);
			map.put("type", user.memberType);
			map.put("position", user.position);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.officer_row, from, to);
		mOfficersList.setAdapter(adapter);
		
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
