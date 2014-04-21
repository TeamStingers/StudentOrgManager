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
import android.text.TextUtils;
import android.util.Log;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class AdminDuesActivity extends Activity {

	// UI Elements
	private TextView mTitleLabel;
	private EditText mDuesEdit;
	private ListView mMembersList;
	private Button mUpdateButton;
	
	// Context variables
	private String username;
	private String orgName;
	private float dues;
	private String curDues;
	private OrganizationDAO org;
	private ArrayList<String> users;
	private Map<String, String> userPaidStatus;
	
	private String changeStatus;
	private String changeDuesUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_dues);
		
		// Set context variables
		Intent intent = getIntent();
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		orgName = intent.getStringExtra(MainActivity.OrgNameTag);
		
		// Initialize UI Elements
		mTitleLabel = (TextView) findViewById(R.id.duesTitleLabel);
		mTitleLabel.setText(orgName);
		mDuesEdit = (EditText) findViewById(R.id.duesEdit);
		mMembersList = (ListView) findViewById(R.id.memberDuesListView);
		mUpdateButton = (Button) findViewById(R.id.updateDuesButton);
		mUpdateButton.setOnClickListener(new MyOnClickListener(this));
		
		users = new ArrayList<String>();
		userPaidStatus = new HashMap<String, String>();
		
		
		// Run DB tasks
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
		
		mDuesEdit.setText(Float.valueOf(dues).toString());
		
		setMemberDuesList();
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			org = DataTransfer.getOrganization(orgName);
			dues = org.annualDues;
			users = DataTransfer.getUsersForOrg(orgName);
			String status;
			for (String user : users) {
				status = DataTransfer.getDuesStatus(user, orgName);
				userPaidStatus.put(user, status);
			}
			return null;
		}
	}
	
	public void setMemberDuesList() {
		String[] from = {"username", "status"};
		int[] to = {R.id.duesRowUsername, R.id.duesRowStatus};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String user : userPaidStatus.keySet()) {
			map = new HashMap<String, Object>();
			map.put("username", user);
			String status = userPaidStatus.get(user);
			if (status.equalsIgnoreCase("null")) {
				status = "Unpaid";
			}
			map.put("status", status);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.member_dues_row, from, to);
		mMembersList.setAdapter(adapter);
		mMembersList.setOnItemClickListener(new MyOnItemClickListener(this));
	}
	
	public class MyOnItemClickListener implements OnItemClickListener {

		Context mContext;
		
		public MyOnItemClickListener(Context context) {
			mContext = context;
		}
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			String duesStatus = ((TextView) view.findViewById(R.id.duesRowStatus)).getText().toString();
			changeDuesUser = ((TextView) view.findViewById(R.id.duesRowUsername)).getText().toString();
			String[] items = {"Paid", "Unpaid"};
			int pos;
			if (duesStatus.equalsIgnoreCase("Paid")) {
				pos = 0;
			}
			else {
				pos = 1;
			}
			
			AlertDialog.Builder duesAlert = new AlertDialog.Builder(mContext);
			duesAlert.setTitle("Change Status");
			
			duesAlert.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
						case 0:
							changeStatus = "Paid";
							break;
						case 1:
							changeStatus = "Unpaid";
							break;
					}
				}
			});
			
			duesAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  
				  changeDuesStatus();
				  
				  }
				});
			
			duesAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
			
			duesAlert.show();
			
		}
		
	}
	
	public void changeDuesStatus() {
		ChangeDuesTask mTask = new ChangeDuesTask();
		mTask.execute((Void) null);
		
		// Wait for DB tasks for finish
		try {
			mTask.get(15000, TimeUnit.MILLISECONDS);
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
		
		Intent i = new Intent(AdminDuesActivity.this, AdminDuesActivity.class);
		i.putExtra(LoginActivity.UserNameTag, username);
		i.putExtra(MainActivity.OrgNameTag, orgName);
		startActivity(i);
		finish();
	}
	
	public class ChangeDuesTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.updateUserDues(changeStatus, orgName, changeDuesUser);
			return null;
		}
	}
	
	public class MyOnClickListener implements OnClickListener {
		Context mContext;

		public MyOnClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			updateDues();
		}
	}
	
	public void updateDues() {
		// Get value at time of attempt
		mDuesEdit.setError(null);
		curDues = mDuesEdit.getText().toString();
		if (TextUtils.isEmpty(curDues)) {
			mDuesEdit.setError(getString(R.string.error_field_required));
		}
		else {
			UpdateDuesTask updateTask = new UpdateDuesTask();
			updateTask.execute((Void) null);
			
			// Wait for DB tasks for finish
			try {
				updateTask.get(15000, TimeUnit.MILLISECONDS);
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
			Toast.makeText(this, "Dues updated successfully!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public class UpdateDuesTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.updateOrganizationDues(curDues, orgName);
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_dues, menu);
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
