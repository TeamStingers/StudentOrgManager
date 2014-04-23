package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.NewsItemDAO;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;
import com.seniordesign.studentorgmanager.data.UserDAO;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RosterActivity extends Activity {
	private String orgName;
	private String actionUser;
	private String username;
	private String memberType;
	private TextView nameLabel;
	private ArrayList<String> sampleMembers;
	private ListView membersListView;
	private ArrayAdapter<String> adapter;
	private Button addMemberButton;
	private Button mOfficersButton;
	private ArrayList<String> orgMembers = new ArrayList<String>();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		
		Intent intent = getIntent();
		orgName = intent.getStringExtra(MainActivity.OrgNameTag);
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		nameLabel = (TextView) findViewById(R.id.orgTitle);
		nameLabel.setText(orgName);
		
		InitTask it = new InitTask();
		it.execute((Void) null);
		
		//Wait for db tasks to finish
		try {
			it.get(15000, TimeUnit.MILLISECONDS);
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
		
		membersListView = (ListView) findViewById(R.id.membersListView);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orgMembers);
		membersListView.setAdapter(adapter);
		membersListView.setOnItemClickListener(new MemberListener(this));
		
		addMemberButton = (Button) findViewById(R.id.addMemberButton);
		addMemberButton.setVisibility(View.GONE);
		addMemberButton.setOnClickListener(new AddMemberClickListener(this));
		
		mOfficersButton = (Button) findViewById(R.id.officersButton);
		mOfficersButton.setOnClickListener(new OfficerClickListener());
		
		if(!memberType.equals("RegularMember")) {
			// Is an officer
			addMemberButton.setVisibility(View.VISIBLE);
		}
		else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)LayoutParams.WRAP_CONTENT, (int)LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mOfficersButton.setLayoutParams(params);
		}
	}

	private class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
//			mLoggedIn = DataTransfer.getUser(actionUser);
			orgMembers = DataTransfer.getUsersForOrg(orgName);
			memberType = DataTransfer.getUserMemberType(username, orgName);
			return null;
		}
	}


	private class JoinOrgTask extends AsyncTask<Void, Void, Boolean> {
		private FI functionInterface;
		
		public JoinOrgTask(FI fi){
			functionInterface = fi;
		}
		
		protected Boolean doInBackground(Void... params) {
			boolean res = DataTransfer.addUserToOrganization(actionUser, orgName);
			return res;
		}
		protected void onPostExecute(Boolean result) {
			functionInterface.doWork(result);
		}
	}
	
	private class LeaveOrgTask extends AsyncTask<Void, Void, Boolean> {
		private FI functionInterface;
		
		public LeaveOrgTask(FI fi){
			functionInterface = fi;
		}
		
		protected Boolean doInBackground(Void... params) {
			boolean res = DataTransfer.removeUserFromOrganization(actionUser, orgName);
			return res;
		}
		protected void onPostExecute(Boolean result) {
			functionInterface.doWork(result);
		}
	}
	
	private class AddMemberClickListener implements OnClickListener {
		Context mContext;
		protected String newMemberName;
		
		public AddMemberClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder addAlert = new AlertDialog.Builder(mContext);
			addAlert.setTitle("Add Member");
			addAlert.setMessage("Enter member name:");
			final EditText input = new EditText(mContext);
			addAlert.setView(input);
			
			
			addAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  newMemberName = input.getText().toString();
				  actionUser = newMemberName;
				  
				  Log.d("RosterActivity", newMemberName);
				  
				  //add user db task
					JoinOrgTask jot = new JoinOrgTask(new FI(){
						public void doWork(boolean result){
							if(result){
								Toast.makeText(RosterActivity.this, "Added " + newMemberName, Toast.LENGTH_LONG).show();
								adapter.add(newMemberName);
								membersListView.setAdapter(adapter);
							}else{
								Toast.makeText(RosterActivity.this, "Already a member", Toast.LENGTH_SHORT).show();						
							}
						}
					});
					
					jot.execute((Void) null);
				
				  }
				});
			
			addAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
			
			addAlert.show();
		}
		
	}
	
	private class OfficerClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			if (memberType.equals("RegularMember")) {
				i = new Intent(RosterActivity.this, ViewOfficersActivity.class);
				i.putExtra(LoginActivity.UserNameTag, username);
				i.putExtra(MainActivity.OrgNameTag, orgName);
			}
			else {
				i = new Intent(RosterActivity.this, ManageOfficersActivity.class);
				i.putExtra(LoginActivity.UserNameTag, username);
				i.putExtra(MainActivity.OrgNameTag, orgName);
			}
			startActivity(i);
			
		}
		
	}
	
	private class MemberListener implements OnItemClickListener {
		Context mContext;
		
		public MemberListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapter, View arg1, int index,
				long arg3) {
			actionUser = (String) ((TextView) arg1).getText();
			AlertDialog.Builder memberAlert = new AlertDialog.Builder(mContext);
			memberAlert.setTitle("Member Options");
			
			String[] options;
			
			if (!(memberType.equals("RegularMember"))) {
				options = new String[]{"Profile","Report Absence", "Remove Member"};
			}
			else {
				options = new String[]{"Profile"};
			}
			
			memberAlert.setItems(options, new OptionsListener(mContext, (String) adapter.getItemAtPosition(index)));
			memberAlert.show();
		}
		
		private class OptionsListener implements DialogInterface.OnClickListener {
			Context mContext;
			String name;
			
			public OptionsListener(Context context, String name) {
				mContext = context;
				this.name = name;
			}
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						//profile
						Intent i = new Intent(RosterActivity.this, PublicProfileActivity.class);
						i.putExtra(LoginActivity.UserNameTag, username);
						i.putExtra(PublicProfileActivity.UserBeingViewedTag, actionUser);
						startActivity(i);
						break;
					case 1:
						//absence
						Intent abs = new Intent(RosterActivity.this, ReportAbsenceActivity.class);
						abs.putExtra(LoginActivity.UserNameTag, username);
						abs.putExtra(MainActivity.OrgNameTag, orgName);
						abs.putExtra(PublicProfileActivity.UserBeingViewedTag, actionUser);
						startActivity(abs);						
						break;
					case 2:
						//remove user
						LeaveOrgTask lot = new LeaveOrgTask(new FI(){
							public void doWork(boolean result){
								if(result){
									Toast.makeText(RosterActivity.this, "Removed", Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(RosterActivity.this, "Error", Toast.LENGTH_SHORT).show();						
								}							
							}
						});
						
						lot.execute((Void) null);
						
						adapter.remove(name);
						membersListView.setAdapter(adapter);
						break;
					default:
						break;
				}	
			}	
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
