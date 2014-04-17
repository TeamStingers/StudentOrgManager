package com.seniordesign.studentorgmanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class OrgsPrivateActivity extends Activity {

	private String orgName;
	
	private TextView nameLabel;
	private Button rosterButton;
	private Button calendarButton;
	private Button profileButton;
	private Button eventsButton;
	private Button newsfeedButton;
	private Button duesButton;
	private Button optionsButton;
	private Button messagesButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgs_private);
		
		Intent intent = getIntent();
		orgName = intent.getStringExtra(MainActivity.OrgNameTag);
		
		nameLabel = (TextView) findViewById(R.id.orgTitle);
		nameLabel.setText(orgName);
		
		rosterButton = (Button) findViewById(R.id.rosterButton);
		rosterButton.setOnClickListener(new ButtonClickListener(this, rosterButton.getId()));
		calendarButton = (Button) findViewById(R.id.calendarButton);
		messagesButton = (Button) findViewById(R.id.messagesButton);
		profileButton = (Button) findViewById(R.id.profileButton);
		duesButton = (Button) findViewById(R.id.duesButton);
		newsfeedButton = (Button) findViewById(R.id.newsfeedButton);
		eventsButton = (Button) findViewById(R.id.eventsButton);
		optionsButton = (Button) findViewById(R.id.optionsButton);	
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
				startActivity(rosterIntent);
			}
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.orgs, menu);
		return true;
	}

}
