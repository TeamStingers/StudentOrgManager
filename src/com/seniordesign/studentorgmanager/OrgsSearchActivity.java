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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OrgsSearchActivity extends Activity {
	public static final String SelectedOrgTag = "SelectedOrgTag_OrgsSearchActivity";
	private ListView orgsLV;
	
	private ArrayList<OrganizationDAO> orgsArray;
	private ArrayList<String> orgsNames;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orgs_search);

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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.orgs_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			orgsArray = DataTransfer.getAllOrganizations();
			orgsNames = new ArrayList<String>();
			
			for(OrganizationDAO o:orgsArray){
				if(o.type.equals("Public")) orgsNames.add(o.name);
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
			Intent clickIntent = new Intent(context, OrgsActivity.class);
			clickIntent.putExtra(SelectedOrgTag, orgName);
			startActivity(clickIntent);
		}
		
	}
	
}
