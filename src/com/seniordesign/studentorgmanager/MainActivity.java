package com.seniordesign.studentorgmanager;

import java.util.ArrayList;

import com.seniordesign.studentorgmanager.datatransfer.OrganizationHelper;
import com.seniordesign.studentorgmanager.datatransfer.UserHelper;
import com.seniordesign.studentorgmanager.model.Organization;
import com.seniordesign.studentorgmanager.model.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {
	public static final String OrgNameTag = "ORG NAME";
	public static final String TAG = MainActivity.class.getSimpleName();
	private ListView orgListView;
	private ArrayList<String> sampleArray;
	private ArrayList<Organization> orgsArray;
	private String username;
	private TextView createOrgLabel;
	private Button createOrgButton;
	private Button searchOrgButton;
	
	private User mLoggedIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Instantiate UI elements
		createOrgLabel = (TextView) findViewById(R.id.addOrgLabel);
		createOrgButton = (Button) findViewById(R.id.createOrgButton);
		createOrgButton.setOnClickListener(new myButtonClickListener(this, R.id.createOrgButton));
		searchOrgButton = (Button) findViewById(R.id.searchOrgButton);
		searchOrgButton.setOnClickListener(new myButtonClickListener(this, R.id.searchOrgButton));
		
		//Get username
		Intent intent = getIntent();
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		Toast.makeText(this, "Welcome "+ username + "!", Toast.LENGTH_LONG).show();
		
		//Get organizations for user
		try {
			orgsArray = OrganizationHelper.getUserOrganizations(username);
		}
		catch (Exception e) {
			String error = e.getMessage();
			Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
		}
		orgsArray = OrganizationHelper.getUserOrganizations(username);
		
		//Populate list with user organizations
		if (orgsArray == null || orgsArray.size() == 0) {
			createOrgLabel.setVisibility(0);
		}
		else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleArray);
			orgListView = (ListView) findViewById(R.id.orgsList);
			orgListView.setAdapter(adapter);
			
			orgListView.setOnItemClickListener(new myItemClickListener(this));
		}
		
		//Load user object
		mLoggedIn = UserHelper.loadUser(username);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	public class myItemClickListener implements OnItemClickListener {
		private Context context;
		
		public myItemClickListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapter, View arg1, int position,
				long id) {
			//Toast.makeText(context, (String)adapter.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
			String orgName = (String) adapter.getItemAtPosition(position);
			Intent clickIntent = new Intent(context, OrgsActivity.class);
			clickIntent.putExtra(OrgNameTag, orgName);
			startActivity(clickIntent);
		}
		
	}
	
	public class myButtonClickListener implements OnClickListener {

		private int id;
		private Context mContext;
		
		public myButtonClickListener(Context context, int buttonid) {
			mContext = context;
			id = buttonid;
		}
		@Override
		public void onClick(View arg0) {
			switch(id) {
			case R.id.createOrgButton:
				Intent createIntent = new Intent(MainActivity.this, CreateOrgActivity.class);
				createIntent.putExtra(LoginActivity.UserNameTag, username);
				startActivity(createIntent);
				break;
			case R.id.searchOrgButton:
				Toast.makeText(mContext, "Coming soon!", Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
		
	}
}
