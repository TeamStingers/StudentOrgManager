package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;


/**
 * Report an absence for a specific member, view previous absences, and clear previous absences
 * Only accessible by officers and admins
 * @author Sanchit
 *
 */
public class ReportAbsenceActivity extends Activity {

	//UI Elements
	private TextView mTitleLabel;
	private Spinner mEventSpinner;
	private Button mReportButton;
	private ListView mListView;
	private Button mDoneButton;
	
	//User variables
	private String username;
	private String userViewed;
	private String orgName;
	
	private ArrayList<EventDAO> events;
	private ArrayList<String> eventNames;
	private Map<String, EventDAO> nameToEvent; // Store mapping for retrieval
	private EventDAO selectedEvent;
	
	// Storing previous absences of user
	private ArrayList<AbsenceDAO> absences;
	private Map<String, AbsenceDAO> nameToAbsence;
	private AbsenceDAO absenceToClear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_absence);
		
		//Get username and org name
		Intent intent = getIntent();
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		orgName = intent.getStringExtra(MainActivity.OrgNameTag);
		userViewed = intent.getStringExtra(PublicProfileActivity.UserBeingViewedTag);
		
		//Initialize UI elements
		mTitleLabel = (TextView) findViewById(R.id.reportAbsenceTitleLabel);
		mTitleLabel.setText("Absences for " + username);
		mEventSpinner = (Spinner) findViewById(R.id.eventForAbsenceSpinner);
		mReportButton = (Button) findViewById(R.id.reportAbsenceButton);
		mReportButton.setOnClickListener(new ButtonClickListener(this, mReportButton.getId()));
		mListView = (ListView) findViewById(R.id.previousAbsencesListView);
		mDoneButton = (Button) findViewById(R.id.reportAbsencesDoneButton);
		mDoneButton.setOnClickListener(new ButtonClickListener(this, mDoneButton.getId()));
		
		eventNames = new ArrayList<String>();
		nameToEvent = new HashMap<String, EventDAO>();
		nameToAbsence = new HashMap<String, AbsenceDAO>();

		//Represents currently selected element of event spinner
		selectedEvent = null;
		
		// Run DB tasks
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		
		//Wait for db tasks to finish
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
				
				  
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eventNames);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mEventSpinner.setAdapter(adapter);
				mEventSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				    @Override
				    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				        // your code here
						String selectedEventName = parentView.getItemAtPosition(position).toString();
						Log.d("RP1", String.valueOf(mEventSpinner.getSelectedItem()));
						Log.d("SelectedEvent", selectedEventName);
						selectedEvent = nameToEvent.get(selectedEventName);	
				    }

				    @Override
				    public void onNothingSelected(AdapterView<?> parentView) {
				        // your code here
				    }

				});
				
		setPreviousAbsences(); // Populate list with previous absences of user
		
	}
	
	public void setPreviousAbsences() {
		String[] from = {"event name", "timestamp"};
		int[] to = {R.id.prevAbsenceEvent, R.id.prevAbsenceTime};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (AbsenceDAO absence : absences) {
			map = new HashMap<String, Object>();
			map.put("event name", absence.eventName);
			map.put("timestamp", absence.eventDateTime);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.previous_absence, from, to);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new MyItemClickListener(this));
	}
	
	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			events = DataTransfer.getOrganizationEvents(orgName);
			absences = DataTransfer.getUserAbsencesForOrg(userViewed, orgName);
			return null;
		}
		protected void onPostExecute(final Void param) {
			for (EventDAO event : events) {
				eventNames.add(event.name);
				nameToEvent.put(event.name, event);
			}
			for (AbsenceDAO absence : absences) {
				nameToAbsence.put(absence.eventName, absence);
			}
		}
	}
	
	public class MyItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String selectedEventName = parent.getItemAtPosition(position).toString();
			Log.d("RP1", String.valueOf(mEventSpinner.getSelectedItem()));
			Log.d("SelectedEvent", selectedEventName);
			selectedEvent = nameToEvent.get(selectedEventName);			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			selectedEvent = null;	
		}
		
	}
	
	public class MyItemClickListener implements OnItemClickListener {

		private Context mContext;
		
		public MyItemClickListener(Context context) {
			mContext = context;
		}
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		
			String eventName = ((TextView) view.findViewById(R.id.prevAbsenceEvent)).getText().toString();
			AbsenceDAO prev = nameToAbsence.get(eventName);
			AlertDialog.Builder absenceAlert = new AlertDialog.Builder(mContext);
			absenceAlert.setTitle("Absence Options");
			String[] options = {"Clear Absence"};
			absenceAlert.setItems(options, new OptionsListener(mContext, prev));
			absenceAlert.show();
		}
		
		private class OptionsListener implements DialogInterface.OnClickListener {

			private Context mContext;
			private AbsenceDAO prev;
			
			public OptionsListener(Context context, AbsenceDAO prev) {
				mContext = context;
				this.prev = prev;
			}
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						// Clear absence
						clearAbsence(prev);
						Intent i = new Intent(ReportAbsenceActivity.this, ReportAbsenceActivity.class);
						i.putExtra(LoginActivity.UserNameTag, username);
						i.putExtra(MainActivity.OrgNameTag, orgName);
						i.putExtra(PublicProfileActivity.UserBeingViewedTag, userViewed);
						startActivity(i);
						break;
				}
			}
		}
	}
	
	public void clearAbsence(AbsenceDAO prev) {
		absenceToClear = prev;
		ClearAbsenceTask mClearAbsenceTask = new ClearAbsenceTask();
		mClearAbsenceTask.execute((Void) null);
		
		// Wait to finish
		try {
			mClearAbsenceTask.get(15000, TimeUnit.MILLISECONDS);
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
	
	public class ClearAbsenceTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.removeAbsence(userViewed, absenceToClear.eventID, orgName);
			return null;
		}
	}
	
	public class ButtonClickListener implements OnClickListener {

		private Context mContext;
		private int id;
		
		public ButtonClickListener(Context context, int id) {
			mContext = context;
			this.id = id;
			//selectedEvent = mEventSpinner
		}
		
		
		@Override
		public void onClick(View v) {
			switch(id) {
				case R.id.reportAbsenceButton:
					if (selectedEvent == null) {
						Toast.makeText(mContext, "Choose an event", Toast.LENGTH_SHORT).show();
					}
					else {
						addNewAbsence();
						Intent i = new Intent(ReportAbsenceActivity.this, ReportAbsenceActivity.class);
						i.putExtra(LoginActivity.UserNameTag, username);
						i.putExtra(MainActivity.OrgNameTag, orgName);
						i.putExtra(PublicProfileActivity.UserBeingViewedTag, userViewed);
						startActivity(i);
					}
					break;
				case R.id.reportAbsencesDoneButton:
					goBack();
					break;
			}
		}
		
		public void addNewAbsence() {
			AddAbsenceTask mAddAbsenceTask = new AddAbsenceTask();
			mAddAbsenceTask.execute((Void) null);
			
			try {
				mAddAbsenceTask.get(15000, TimeUnit.MILLISECONDS);
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
		
		public class AddAbsenceTask extends AsyncTask<Void, Void, Void> {
			protected Void doInBackground(Void... params) {
				DataTransfer.addAbsence(userViewed, selectedEvent.id, orgName);
				return null;
			}
		}	
	}
	
	public void goBack() {
		onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_absence, menu);
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
