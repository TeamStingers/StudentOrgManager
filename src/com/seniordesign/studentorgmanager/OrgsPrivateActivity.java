package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.ProfileActivity.DeleteTask;
import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrgsPrivateActivity extends Activity {

	private String orgName;
	private String username;
	private boolean isOfficer = false;
	
	private TextView nameLabel;
	private Button rosterButton;
	private Button calendarButton;
	private Button profileButton;
	private Button eventsButton;
	private Button newsfeedButton;
	private Button duesButton;
	private Button optionsButton;
	private Button deleteOrgBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgs_private);
		
		Intent intent = getIntent();
		orgName = intent.getExtras().getString(MainActivity.OrgNameTag);
		username = intent.getExtras().getString(LoginActivity.UserNameTag);
		
		nameLabel = (TextView) findViewById(R.id.orgTitle);
		nameLabel.setText(orgName);
		
		rosterButton = (Button) findViewById(R.id.rosterButton);
		duesButton = (Button) findViewById(R.id.duesButton);
		newsfeedButton = (Button) findViewById(R.id.newsfeedButton);
		eventsButton = (Button) findViewById(R.id.eventsButton);	
		deleteOrgBtn = (Button) findViewById(R.id.deleteOrgBtn);
		
		rosterButton.setOnClickListener(new ButtonClickListener(this, rosterButton.getId()));
		newsfeedButton.setOnClickListener(new ButtonClickListener(this, newsfeedButton.getId()));
		deleteOrgBtn.setOnClickListener(new ButtonClickListener(this, deleteOrgBtn.getId()));
		eventsButton.setOnClickListener(new ButtonClickListener(this, eventsButton.getId()));
		duesButton.setOnClickListener(new ButtonClickListener(this, duesButton.getId()));
		
		deleteOrgBtn.setVisibility(View.GONE);
		
		InitTask it = new InitTask();
		it.execute((Void) null);
		
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
		
	}

	private class ButtonClickListener implements OnClickListener {
		Context mContext;
		int id;
		
		public ButtonClickListener(Context context, int buttonId) {
			mContext = context;
			id = buttonId;
		}
		
		@Override
		public void onClick(View arg0) {
			
			switch(id) {
				case R.id.rosterButton:
					Intent rosterIntent = new Intent(mContext, RosterActivity.class);
					rosterIntent.putExtra(MainActivity.OrgNameTag, orgName);
					rosterIntent.putExtra(LoginActivity.UserNameTag, username);
					startActivity(rosterIntent);
					break;
				case R.id.newsfeedButton:
					Intent nfi = new Intent(mContext, NewsFeedActivity.class);
					Bundle extras = new Bundle();
					extras.putString(LoginActivity.UserNameTag, username);
					extras.putString(MainActivity.OrgNameTag, orgName);
					nfi.putExtras(extras);
					startActivity(nfi);	
					break;
				case R.id.deleteOrgBtn:
					new AlertDialog.Builder(OrgsPrivateActivity.this)
					.setTitle("Delete Org")
					.setMessage("Do you really want to delete this organization?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int whichButton) {
							DeleteOrgTask dorg = new DeleteOrgTask();
							dorg.execute((Void) null);
					    }})
					 .setNegativeButton(android.R.string.no, null).show();
					break;
				case R.id.eventsButton:
					Intent ev = new Intent(mContext, EventActivity.class);
					ev.putExtra(LoginActivity.UserNameTag, username);
					ev.putExtra(MainActivity.OrgNameTag, orgName);
					startActivity(ev);
					break;
				case R.id.duesButton:
					Intent dues = new Intent();
					Log.d("OrgPirvate", new Boolean(isOfficer).toString());
					if (isOfficer) {
						dues = new Intent(mContext, AdminDuesActivity.class);	
					}
					else {
						dues = new Intent(mContext, RegDuesActivity.class);
					}
					dues.putExtra(LoginActivity.UserNameTag, username);
					dues.putExtra(MainActivity.OrgNameTag, orgName);
					startActivity(dues);
			}
			
		}
		
	}
	

	public class InitTask extends AsyncTask<Void, Void, Void> {
		private boolean deleted;
		private String pos;
		
		protected Void doInBackground(Void... params) {
			pos = DataTransfer.getUserMemberType(username, orgName);
			return null;
		}
		protected void onPostExecute(final Void param) {
			if(pos.equals("Admin")){
				deleteOrgBtn.setVisibility(View.VISIBLE);
			}
			if(!(pos.equals("RegularMember"))) {
				isOfficer = true;
			}
		}
	}
	
	public class DeleteOrgTask extends AsyncTask<Void, Void, Void> {
		private boolean deleted;
		
		protected Void doInBackground(Void... params) {
			deleted = DataTransfer.deleteOrganization(orgName);
			return null;
		}
		protected void onPostExecute(final Void param) {
			if(deleted){
				Toast.makeText(OrgsPrivateActivity.this, "Organization Deleted", 
						Toast.LENGTH_SHORT).show();				
			
				Intent i = new Intent(OrgsPrivateActivity.this, MainActivity.class);				
				i.putExtra(LoginActivity.UserNameTag, username);				
				startActivity(i);
				finish();
			
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
