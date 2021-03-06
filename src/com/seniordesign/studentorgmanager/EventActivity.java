package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * View events for an organization
 * @author Sanchit
 *
 */
public class EventActivity extends Activity {

	public final static String EventNameTag = "EVENT_NAME";
	public final static String EventIDTag = "EVENT_ID";
	
	//UI Elements
	private TextView mTitleLabel;
	private ListView mEventsList;
	private Button mAddEventButton;
	
	// User variables
	private String username;
	private String orgName;
	private UserDAO mLoggedIn;
	private String memberType;
	private boolean isOfficer = false;
	
	private ArrayList<EventDAO> orgEvents;
	private HashMap<String, EventDAO> nameToEvent = new HashMap<String, EventDAO>();
	private EventDAO selectedEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		
		mTitleLabel = (TextView) findViewById(R.id.eventsOrgTitle);
		mTitleLabel.setText(orgName);
		mEventsList = (ListView) findViewById(R.id.eventsList);
		mAddEventButton = (Button) findViewById(R.id.addEventButton);
		mAddEventButton.setOnClickListener(new ButtonClickListener(this));
		
		// DB tasks
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
		
		setEventsList();
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			orgEvents = DataTransfer.getOrganizationEvents(orgName);
			mLoggedIn = DataTransfer.getUser(username);
			memberType = DataTransfer.getUserMemberType(username, orgName);
			return null;
		}
		protected void onPostExecute(final Void param) {
			for (EventDAO event : orgEvents) {
				nameToEvent.put(event.name, event);
			}
			if (!(memberType.equals("RegularMember"))) {
				isOfficer = true;
			}
		}
	}
	
	public void setEventsList() {
		String[] from = {"event name", "timestamp", "description", "location", "type"};
		int[] to = {R.id.eventTitle, R.id.eventTimestamp, R.id.eventDescription, R.id.eventLocation, R.id.eventType};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Collections.reverse(orgEvents);
		
		for (EventDAO event : orgEvents) {
			map = new HashMap<String, Object>();
			map.put("event name", event.name);
			map.put("timestamp", event.dateTime);
			map.put("description", event.description);
			map.put("location", event.location);
			map.put("type", "Type: " + event.type);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.event_row, from, to);
		mEventsList.setAdapter(adapter);
		mEventsList.setOnItemClickListener(new MyItemClickListener(this));
	}
	
	public class MyItemClickListener implements OnItemClickListener {

		Context mContext;
		
		public MyItemClickListener(Context context) {
			mContext = context;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String eventName = ((TextView) view.findViewById(R.id.eventTitle)).getText().toString();
			selectedEvent = nameToEvent.get(eventName);
			AlertDialog.Builder eventAlert = new AlertDialog.Builder(mContext);
			eventAlert.setTitle("Event Options");
			String[] options = {"View Absences", "Delete Event"};
			eventAlert.setItems(options, new OptionsListener(mContext));
			if (isOfficer) {
				eventAlert.show();
			}
		}
		
		private class OptionsListener implements DialogInterface.OnClickListener {

			Context mContext;
			
			public OptionsListener(Context context) {
				mContext = context;
			}
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						Intent viewAbs = new Intent(EventActivity.this, ViewAbsencesActivity.class);
						viewAbs.putExtra(LoginActivity.UserNameTag, username);
						viewAbs.putExtra(MainActivity.OrgNameTag, orgName);
						viewAbs.putExtra(EventActivity.EventNameTag, selectedEvent.name);
						viewAbs.putExtra(EventActivity.EventIDTag, selectedEvent.id);
						startActivity(viewAbs);
						break;
					case 1:
						deleteEvent();
						Intent i = new Intent(EventActivity.this, EventActivity.class);
						i.putExtra(LoginActivity.UserNameTag, username);
						i.putExtra(MainActivity.OrgNameTag, orgName);
						startActivity(i);
						finish();
						break;
					default:
						break;
							
				}
				
			}
			
		}
		
	}
	
	public void deleteEvent() {
		if (selectedEvent == null) {
			return;
		}
		
		DeleteEventTask mDeleteTask = new DeleteEventTask();
		mDeleteTask.execute((Void) null);
		
		// Wait for DB tasks for finish
		try {
			mDeleteTask.get(15000, TimeUnit.MILLISECONDS);
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
	
	public class DeleteEventTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.deleteEvent(selectedEvent.id);
			return null;
		}
	}
	
	public class ButtonClickListener implements OnClickListener {

		private Context mContext;
		
		public ButtonClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(EventActivity.this, NewEventActivity.class);
			i.putExtra(LoginActivity.UserNameTag, username);
			i.putExtra(MainActivity.OrgNameTag, orgName);
			startActivity(i);
			finish();
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
