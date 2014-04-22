package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OrgsBrowseActivity extends Activity {
	private ListView orgsLV;
	
	private ArrayList<OrganizationDAO> orgsArray;
	private ArrayList<OrganizationDAO> userOrgsArray;
	private ArrayList<String> userOrgsNames;
	private ArrayList<String> orgsNames;
	private String username;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgs_search);

		username = getIntent().getStringExtra(LoginActivity.UserNameTag);
		
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
	
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
		
		if (orgsArray == null || orgsArray.size() == 0) {
			return;
		}
		else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orgsNames);
			orgsLV = (ListView) findViewById(R.id.allOrgsListView);
			orgsLV.setAdapter(adapter);

			orgsLV.setOnItemClickListener(new myItemClickListener(this));
		}
		
	}
	
	@Override
	public void onBackPressed() {
		Intent refreshMain = new Intent(this, MainActivity.class);
		refreshMain.putExtra(LoginActivity.UserNameTag, username);
		startActivity(refreshMain);
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


	
	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			orgsArray = DataTransfer.getAllOrganizations();

			
			userOrgsArray = DataTransfer.getUserOrganizations(username);
			
			orgsNames = new ArrayList<String>();
			userOrgsNames = new ArrayList<String>();
			
			for(OrganizationDAO o:orgsArray){
				if(o.type.equals("Public")) orgsNames.add(o.name);
			}
			
			for(OrganizationDAO o: userOrgsArray){
				userOrgsNames.add(o.name);
			}
			
//			Collections.sort(orgsArray, new Comparator<OrganizationDAO>(){
//		        public int compare(OrganizationDAO  o1, OrganizationDAO  o2){
//		            return  o1.name.compareTo(o2.name);
//		        }
//			});
			
			return null;
		}
		
		protected void onPostExecute(final Void param) {
			return;
		}
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
			
			Intent clickIntent = new Intent(context, OrgsPublicActivity.class);
			clickIntent.putExtra(MainActivity.OrgNameTag, orgName);
			clickIntent.putExtra(LoginActivity.UserNameTag, username);				
			startActivity(clickIntent);
			
		}
		
	}
	
}
