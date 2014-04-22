package com.seniordesign.studentorgmanager;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;
import com.seniordesign.studentorgmanager.data.UserDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

public class CreateOrgActivity extends Activity {

	private EditText orgNameEdit;
	private Spinner orgTypeSpinner;
	private Button createOrgButton;
	private String username;
	private UserDAO mLoggedIn;
	private String mOrgName;
	private String mGroupType;
	private OrgCreateTask mOrgCreateTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_org);
		
		Intent intent = getIntent();
		username = intent.getStringExtra(LoginActivity.UserNameTag);
		
		orgNameEdit = (EditText) findViewById(R.id.orgNameEdit);
		orgTypeSpinner = (Spinner) findViewById(R.id.orgTypeSpinner);
		createOrgButton = (Button) findViewById(R.id.createNewOrgButton);
		createOrgButton.setOnClickListener(new ButtonClickListener(this));
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.group_types, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		orgTypeSpinner.setAdapter(adapter);
	}

	public class ButtonClickListener implements OnClickListener {
		private Context mContext;
		
		public ButtonClickListener(Context context) {
			mContext = context;
		}

		@Override
		public void onClick(View arg0) {
			createOrg();
			
		}
	}
	
	public void createOrg() {
		mOrgName = orgNameEdit.getText().toString();
		mGroupType = (String) orgTypeSpinner.getSelectedItem();
		if (TextUtils.isEmpty(mOrgName)) {
			orgNameEdit.setError(getString(R.string.error_field_required));
			orgNameEdit.requestFocus();
		}
		else {
			mOrgCreateTask = new OrgCreateTask();
			mOrgCreateTask.execute((Void) null);
		}
	}
	
	public class OrgCreateTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			
			//Need to implement input for annual dues for the org.
			OrganizationDAO newOrg = DataTransfer.createOrganization(mOrgName, mGroupType, username, "0.0");
			
			if (newOrg == null) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mOrgCreateTask = null;
			
			if (success) {
				//Here's where we transition
				Intent returnIntent = new Intent(CreateOrgActivity.this, MainActivity.class);
				returnIntent.putExtra(LoginActivity.UserNameTag, username);
				startActivity(returnIntent);
				finish();
			} else {
				Toast.makeText(CreateOrgActivity.this, "Error creating organization!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mOrgCreateTask = null;
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
