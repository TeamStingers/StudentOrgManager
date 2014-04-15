package com.seniordesign.studentorgmanager;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;
import com.seniordesign.studentorgmanager.data.UserDAO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
		getMenuInflater().inflate(R.menu.create_org, menu);
		return true;
	}

}
