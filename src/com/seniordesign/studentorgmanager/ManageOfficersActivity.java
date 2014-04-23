package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.ProfileActivity.InitTask;
import com.seniordesign.studentorgmanager.data.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;

/**
 * Screen for managing officers, accessible only by officers and admins. Users can view officers, change their types/positions,
 * and promote other org members to officer positions
 * @author Sanchit
 *
 */
public class ManageOfficersActivity extends Activity {

	// UI Elements
	private TextView mTitleLabel;
	private ListView mOfficersList;
	private Button mAddOfficerButton;
	private Spinner mMembersSpinner;
	private Spinner mTypesSpinner;
	private EditText mPositionEdit;
	
	// Context variables
	private String username;
	private String orgName;
	private String memberType;
	private UserDAO mLoggedIn;
	
	// Other variables
	private ArrayList<String> allMembers;
	private ArrayList<UserOfficer> officers;
	private static String[] types = {"Admin", "Officer", "Regular Member"};
	private ArrayList<String> notOfficers;
	private String currentType;
	private UserOfficer modified = new UserOfficer("", "", "");
	private LayoutInflater inflater;
	private View dialogView;
	
	private class UserOfficer {
		public String username;
		public String memberType;
		public String position;
		public UserOfficer(String u, String mt, String p) {
			username = u;
			memberType = mt;
			position = p;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_officers);
		
		// Set context variables
		Intent i = getIntent();
		username = i.getStringExtra(LoginActivity.UserNameTag);
		orgName = i.getStringExtra(MainActivity.OrgNameTag);
		officers = new ArrayList<UserOfficer>();
		notOfficers = new ArrayList<String>();
		
		// Instantiate UI references
		mTitleLabel = (TextView) findViewById(R.id.manageOfficersOrgTitle);
		mTitleLabel.setText(orgName);
		mOfficersList = (ListView) findViewById(R.id.currentOfficersListView);
		mAddOfficerButton = (Button) findViewById(R.id.newOfficerButton);
		mAddOfficerButton.setOnClickListener(new ButtonClickListener(this));
		
		inflater = LayoutInflater.from(this);
		dialogView = inflater.inflate(R.layout.create_new_officer, null);
		
		mMembersSpinner = (Spinner) dialogView.findViewById(R.id.createOfficerUsernameSpinner);
		mTypesSpinner = (Spinner) dialogView.findViewById(R.id.createOfficerMemberTypeSpinner);
		mPositionEdit = (EditText) dialogView.findViewById(R.id.createOfficerPositionEdit);
		
		
		
		// Run DB actions
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		// Wait for DB actions to complete
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
		
		setOfficersList();
		
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			memberType = DataTransfer.getUserMemberType(username, orgName);
			allMembers = DataTransfer.getUsersForOrg(orgName);
			
			for (String user : allMembers) {
				
				String type = DataTransfer.getUserMemberType(user, orgName);
				if (!(type.equalsIgnoreCase("RegularMember"))) {
					String pos = DataTransfer.getUserPosition(user, orgName);
					officers.add(new UserOfficer(user, type, pos));
				}
				else {
					notOfficers.add(user);
				}
			}
			return null;
		}
	}
	
	public void setOfficersList() {
		String[] from = {"username", "type", "position"};
		int[] to = {R.id.officerRowUsername, R.id.officerRowType, R.id.officerRowPosition};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		for (UserOfficer user : officers) {
			map = new HashMap<String, Object>();
			map.put("username", user.username);
			map.put("type", user.memberType);
			map.put("position", user.position);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.officer_row, from, to);
		mOfficersList.setAdapter(adapter);
		mOfficersList.setOnItemClickListener(new MyListListener());
		
	}
	
	public class MyListListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String username = ((TextView) view.findViewById(R.id.officerRowUsername)).getText().toString();
			String type = ((TextView) view.findViewById(R.id.officerRowType)).getText().toString();
			String pos = ((TextView) view.findViewById(R.id.officerRowPosition)).getText().toString();
			
			modified = new UserOfficer(username, type, pos);
			
			AlertDialog.Builder builder = officerOptionsDialog(modified);
			builder.show();
			
		}
		
	}
	
	public AlertDialog.Builder officerOptionsDialog(UserOfficer officer) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Officer Options");
		String[] options = {"Change " + modified.username + "'s privileges", "Change " + modified.username + "'s title"};
		builder.setItems(options, new OfficerOptionsListener());
		
		return builder;
	}
	
	public class OfficerOptionsListener implements DialogInterface.OnClickListener {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case 0:
					// Change privileges
					AlertDialog.Builder privilegesDialog = changePrivilegesDialog();
					privilegesDialog.show();
					dialog.dismiss();
					break;
				case 1:
					// Change title
					AlertDialog.Builder titleDialog = changeTitleDialog();
					titleDialog.show();
					dialog.dismiss();
					break;
			}
			
		}
		
	}
	
	public AlertDialog.Builder changePrivilegesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Set Member Type");
		int pos;
		if (modified.memberType.equals("Admin")) {
			pos = 0;
		}
		else if (modified.memberType.equals("Officer")){
			pos = 1;
		}
		else {
			pos = -1;
		}
		builder.setSingleChoiceItems(types, pos, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					modified.memberType = "Admin";
					break;
				case 1:
					modified.memberType = "Officer";
					break;
				case 2:
					modified.memberType = "RegularMember";
					break;
				}
				
			}
			
		});
		
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveOfficer();
				
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return builder;
	}
	
	public AlertDialog.Builder changeTitleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Change " + modified.username + "'s title");
		builder.setMessage("New title:");
		final EditText input = new EditText(this);
		input.setText(modified.position);
		builder.setView(input);
		
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				modified.position = input.getText().toString();
				saveOfficer();
				
			}
			
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder;
	}
	
	public class ButtonClickListener implements OnClickListener {

		private Context mContext;
		
		public ButtonClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder newOfficer = newOfficerDialog();
			newOfficer.show();
		}
		
	}
	
	public AlertDialog.Builder newOfficerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Officer");
		
		if (notOfficers == null || notOfficers.size() == 0) {
			Log.d("debug", "notOfficers null");
		}
		else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notOfficers);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mMembersSpinner.setAdapter(adapter);
		}
		
		
		
		ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, types);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTypesSpinner.setAdapter(adapter1);
		
		builder.setView(dialogView);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mMembersSpinner.getSelectedItem() == null) {
				Log.d("spinner", "spinner null");
				}
				modified.username = mMembersSpinner.getSelectedItem().toString();
				modified.memberType = mTypesSpinner.getSelectedItem().toString();
				modified.position = mPositionEdit.getText().toString();
				saveOfficer();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder;
	}
	
	public void saveOfficer() {
		SaveTask mSaveTask = new SaveTask();
		mSaveTask.execute((Void) null);
		
		// Wait for DB actions to complete
		try {
			mSaveTask.get(15000, TimeUnit.MILLISECONDS);
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
		
		refresh();
	}
	
	public class SaveTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.changeUserPosition(modified.position, modified.memberType, orgName, modified.username);
			return null;
		}
	}
	
	public void refresh() {
		Intent i = new Intent(ManageOfficersActivity.this, ManageOfficersActivity.class);
		i.putExtra(LoginActivity.UserNameTag, username);
		i.putExtra(MainActivity.OrgNameTag, orgName);
		startActivity(i);
		finish();
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
