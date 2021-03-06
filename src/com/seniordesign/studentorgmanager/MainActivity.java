package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.OrgsPrivateActivity.DeleteOrgTask;
import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;
import com.seniordesign.studentorgmanager.data.UserDAO;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	
	//UI elements
	private ListView orgListView;
	private ArrayList<String> sampleArray;
	private TextView createOrgLabel;
	private Button createOrgButton;
	private Button searchOrgButton;
	private Button myProfileButton;
	private Button messagesButton;
	
	//Variables representing user
	private String username;
	private UserDAO mLoggedIn;
	protected ArrayList<OrganizationDAO> orgsArray;
	protected ArrayList<String> orgNames;
	
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
		myProfileButton = (Button) findViewById(R.id.myProfileButton);
		myProfileButton.setOnClickListener(new myButtonClickListener(this, R.id.myProfileButton));
		messagesButton = (Button) findViewById(R.id.messagesButton);
		messagesButton.setOnClickListener(new myButtonClickListener(this, R.id.messagesButton));
		
		//Get username
		Intent intent = getIntent();
		username = intent.getExtras().getString(LoginActivity.UserNameTag);
		
		//only welcome on log in
		if(intent.getExtras().getString(LoginActivity.WelcomeTag)!=null) 
			Toast.makeText(this, "Welcome "+ username + "!", Toast.LENGTH_LONG).show();
		

		
		
		//Run DB actions
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
		
		//Populate list with user organizations
		if (orgsArray == null || orgsArray.size() == 0) {
			createOrgLabel.setVisibility(0);
		}
		else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orgNames);
			orgListView = (ListView) findViewById(R.id.orgsList);
			orgListView.setAdapter(adapter);

			orgListView.setOnItemClickListener(new myItemClickListener(this));
		}
		
	}
	
	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
//			mLoggedIn = DataTransfer.getUser(username);
			orgsArray = DataTransfer.getUserOrganizations(username);
			orgNames = new ArrayList<String>();
			return null;
		}
		protected void onPostExecute(final Void param) {
			for (OrganizationDAO org : orgsArray) {
				orgNames.add(org.name);
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
			Intent clickIntent = new Intent(context, OrgsPrivateActivity.class);
			Bundle extras = new Bundle();

			extras.putString(LoginActivity.UserNameTag, username);
			extras.putString(OrgNameTag, orgName);
			clickIntent.putExtras(extras);
			
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
					Intent i = new Intent(MainActivity.this, OrgsBrowseActivity.class);
					i.putExtra(LoginActivity.UserNameTag, username);
					startActivity(i);				
					break;
				case R.id.myProfileButton:
					Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
					profileIntent.putExtra(LoginActivity.UserNameTag, username);
					startActivity(profileIntent);
					break;
				case R.id.messagesButton:
					Intent mbi = new Intent(MainActivity.this, MailBoxActivity.class);
					mbi.putExtra(LoginActivity.UserNameTag, username);
					startActivity(mbi);	
					break;		
				default:
					break;
			}
		}
		
	}
}
