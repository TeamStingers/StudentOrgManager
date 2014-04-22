package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

/**
 * View absent members for a specific event
 * Only officers and admins can access this screen
 * @author Sanchit
 *
 */
public class ViewAbsencesActivity extends Activity {

	//UI Elements
	private TextView mEventTitle;
	private ListView mMembersList;
	
	// Variables
	private String username;
	private String orgName;
	private String eventName;
	private String eventID;
	
	private ArrayList<AbsenceDAO> eventAbsences;
	private ArrayList<String> memberNames = new ArrayList<String>();
	private String userViewed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_absences);
		
		// Populate variables
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		eventName = i.getStringExtra(EventActivity.EventNameTag);
		eventID = i.getStringExtra(EventActivity.EventIDTag);
		
		// Instantiate UI elements
		mEventTitle = (TextView) findViewById(R.id.viewAbsencesEventTitle);
		mEventTitle.setText(eventName);
		mMembersList = (ListView) findViewById(R.id.absentMembersListView);
		
		// Complete DB tasks
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
		
		setMembersList();
		
	}

	
	
	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			eventAbsences = DataTransfer.getEventAbsences(eventID);
			return null;
		}
		protected void onPostExecute(final Void param) {
			for (AbsenceDAO absence : eventAbsences) {
				memberNames.add(absence.username);
			}
		}
	}
	
	public void setMembersList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memberNames);
		mMembersList.setAdapter(adapter);
		mMembersList.setOnItemClickListener(new MyItemClickListener(this));
	}
	
	public class MyItemClickListener implements OnItemClickListener {

		Context mContext;
		
		public MyItemClickListener(Context context) {
			context = mContext;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			userViewed = ((TextView) view).getText().toString();
			AlertDialog.Builder viewDialog = new AlertDialog.Builder(mContext);
			viewDialog.setTitle("Member Options");
			String[] options = {"View Profile"};
			viewDialog.setItems(options, new OptionsListener(mContext));
			
		}
		public class OptionsListener implements DialogInterface.OnClickListener {

			Context mContext;
			
			public OptionsListener(Context context) {
				mContext = context;
			}
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case 0:
						// View Profile
						viewProfile();
				}	
			}	
		}
	}
	
	public void viewProfile() {
		Intent i = new Intent(ViewAbsencesActivity.this, PublicProfileActivity.class);
		i.putExtra(LoginActivity.UserNameTag, username);
		i.putExtra(PublicProfileActivity.UserBeingViewedTag, userViewed);
		startActivity(i);
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
