package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.MainActivity.InitTask;
import com.seniordesign.studentorgmanager.MainActivity.myButtonClickListener;
import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class OrgsPublicActivity extends Activity {

	private String username;
	private String orgName;
	private ArrayList<OrganizationDAO> orgsArray;
	private OrganizationDAO org;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgs_public);

		orgName = getIntent().getStringExtra(MainActivity.OrgNameTag);
		username = getIntent().getStringExtra(LoginActivity.UserNameTag);
	
		TextView orgPublicName = (TextView) findViewById(R.id.orgsPublicNameTextView);
		orgPublicName.setText(orgName);
		
		TextView typeVal = (TextView) findViewById(R.id.orgsPublicTypeVal);
		TextView sizeVal = (TextView) findViewById(R.id.orgsPublicSizeVal);
		TextView duesVal = (TextView) findViewById(R.id.orgsPublicDuesVal);
		
		//Run DB actions
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);

		boolean done = false;
		
		try {
			mInitTask.get(15000, TimeUnit.MILLISECONDS);
			typeVal.setText(org.type);
			sizeVal.setText(new Integer(org.size).toString());
			
			if(org.annualDues == 0) duesVal.setText("No dues required.");
			else duesVal.setText(org.annualDues.toString());	
			
			done = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		//
		
		if(!done){
			typeVal.setText("CONNECTION TIMEOUT");
		}
		
		Button joinBtn = (Button) findViewById(R.id.joinOrgBtn);
		Button leaveBtn = (Button) findViewById(R.id.leaveOrgBtn);
		joinBtn.setOnClickListener(new ClickListener(R.id.joinOrgBtn));
		leaveBtn.setOnClickListener(new ClickListener(R.id.leaveOrgBtn));
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			org = DataTransfer.getOrganization(orgName);
			Log.d("InitTask", org.name);
			return null;
		}
		
		protected void onPostExecute(final Void param) {
			return;
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


	private class JoinOrgTask extends AsyncTask<Void, Void, Boolean> {
		private FI functionInterface;
		
		public JoinOrgTask(FI fi){
			functionInterface = fi;
		}
		
		protected Boolean doInBackground(Void... params) {
			boolean res = DataTransfer.addUserToOrganization(username, orgName);
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
			boolean res = DataTransfer.removeUserFromOrganization(username, orgName);
			return res;
		}
		protected void onPostExecute(Boolean result) {
			functionInterface.doWork(result);
		}
	}	

	private class ClickListener implements OnClickListener {
		private int id;
		
		public ClickListener(int buttonid) {
			id = buttonid;
		}
		
		@Override
		public void onClick(View arg0) {
			
			switch(id){
				case R.id.leaveOrgBtn:
					LeaveOrgTask lot = new LeaveOrgTask(new FI(){
						public void doWork(boolean result){
							if(result){
								Toast.makeText(OrgsPublicActivity.this, "You have left", Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(OrgsPublicActivity.this, "Not a member", Toast.LENGTH_SHORT).show();						
							}							
						}
					});
					
					lot.execute((Void) null);
					break;
				case R.id.joinOrgBtn:
					JoinOrgTask jot = new JoinOrgTask(new FI(){
						public void doWork(boolean result){
							if(result){
								Toast.makeText(OrgsPublicActivity.this, "Joined " + orgName, Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(OrgsPublicActivity.this, "Already a member", Toast.LENGTH_SHORT).show();						
							}
						}
					});
					
					jot.execute((Void) null);
					break;
				default:
					break;
			}
			

			
			
		}
		
	}

}
