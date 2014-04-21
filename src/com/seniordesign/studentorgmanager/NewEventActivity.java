package com.seniordesign.studentorgmanager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.Helper;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.os.Build;

public class NewEventActivity extends Activity {

	// UI Elements
	private EditText mNameEdit;
	private EditText mLocationEdit;
	private Spinner mTypeSpinner;
	private EditText mDescriptionEdit;
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private Button mCreateButton;
	
	// Variables
	private String username;
	private String orgName;
	
	// Input variables
	private String name;
	private String location;
	private String type;
	private String description;
	private String datetime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event);
		
		// Set variables
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		
		// Initialize UI Elements
		mNameEdit = (EditText) findViewById(R.id.newEventNameEdit);
		mLocationEdit = (EditText) findViewById(R.id.newEventLocationEdit);
		mTypeSpinner = (Spinner) findViewById(R.id.newEventTypeSpinner);
		mDescriptionEdit = (EditText) findViewById(R.id.newEventDescriptionEdit);
		mDatePicker = (DatePicker) findViewById(R.id.newEventDatePicker);
		mTimePicker = (TimePicker) findViewById(R.id.newEventTimePicker);
		mCreateButton = (Button) findViewById(R.id.createNewEventButton);
		mCreateButton.setOnClickListener(new ButtonClickListener(this));
		
		setSpinner();
	}

	public void setSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.event_types, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTypeSpinner.setAdapter(adapter);
	}
	
	public class ButtonClickListener implements OnClickListener {

		private Context mContext;
		
		public ButtonClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			createEvent();
		}
	}
	
	public void createEvent() {
		
		mNameEdit.setError(null);
		
		// Save values at time of button click
		name = mNameEdit.getText().toString();
		location = mLocationEdit.getText().toString();
		type = mTypeSpinner.getSelectedItem().toString();
		description = mDescriptionEdit.getText().toString();
		// Get datetime info once data transfer stuff is sorted out
		datetime = Helper.dateToString(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), 0);
				
		if (TextUtils.isEmpty(name)) {
			mNameEdit.setError(getString(R.string.error_field_required));
			mNameEdit.requestFocus();
		}
		else {
			CreateEventTask mCreateTask = new CreateEventTask();
			mCreateTask.execute((Void) null);
			
			// Wait for DB tasks for finish
			try {
				mCreateTask.get(15000, TimeUnit.MILLISECONDS);
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
			
			Intent i = new Intent(NewEventActivity.this, EventActivity.class);
			i.putExtra(LoginActivity.UserNameTag, username);
			i.putExtra(MainActivity.OrgNameTag, orgName);
			startActivity(i);
			finish();
		}
	}
	
	public class CreateEventTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.createEvent(orgName, datetime, location, description, type, name);
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_event, menu);
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
