package com.seniordesign.studentorgmanager;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RosterActivity extends Activity {
	private String orgName;
	private TextView nameLabel;
	private ArrayList<String> sampleMembers;
	private ListView membersListView;
	private ArrayAdapter<String> adapter;
	private Button addMemberButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		
		Intent intent = getIntent();
		orgName = intent.getStringExtra(MainActivity.OrgNameTag);
		nameLabel = (TextView) findViewById(R.id.orgTitle);
		nameLabel.setText(orgName);
		
		sampleMembers = new ArrayList<String>();
		sampleMembers.add("Sanchit Malhotra");
		sampleMembers.add("Vikram Somu");
		sampleMembers.add("Daryl Halima");
		sampleMembers.add("Oudy Yang");
		sampleMembers.add("Utkarsh Vaidya");
		
		membersListView = (ListView) findViewById(R.id.membersListView);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleMembers);
		membersListView.setAdapter(adapter);
		membersListView.setOnItemClickListener(new MemberListener(this));
		
		addMemberButton = (Button) findViewById(R.id.addMemberButton);
		addMemberButton.setOnClickListener(new AddMemberClickListener(this));
	}

	private class AddMemberClickListener implements OnClickListener {
		Context mContext;
		
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
				  String newMemberName = input.getText().toString();
				  Log.d("New Member Name", newMemberName);
				  
				  adapter.add(newMemberName);
				  membersListView.setAdapter(adapter);
				  
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
	
	private class MemberListener implements OnItemClickListener {
		Context mContext;
		
		public MemberListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapter, View arg1, int index,
				long arg3) {
			AlertDialog.Builder memberAlert = new AlertDialog.Builder(mContext);
			memberAlert.setTitle("Member Options");
			String[] options = {"Remove Member"};
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
					adapter.remove(name);
					membersListView.setAdapter(adapter);
				}	
			}	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.roster, menu);
		return true;
	}

}
