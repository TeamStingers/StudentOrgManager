package com.seniordesign.studentorgmanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {
	public static final String OrgNameTag = "ORG NAME";
	public static final String TAG = MainActivity.class.getSimpleName();
	private ListView orgListView;
	private String duhfufhdr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Start of Sanchit's code
//		String[] sampleArray = new String[4];
//		sampleArray[0] = "Organization X";
//		sampleArray[1] = "Organization Y";
//		sampleArray[2] = "Organization Z";
//		sampleArray[3] = "Organization S";
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleArray);
//		
//		orgListView = (ListView) findViewById(R.id.orgsList);
//		orgListView.setAdapter(adapter);
//		
//		orgListView.setOnItemClickListener(new myItemClickListener(this));
		//End of Sanchit's code
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage(View view){
		Intent intent = new Intent(this, NewUserActivity.class);
		startActivity(intent);
	}
	
	//Start of Sanchit's code
//	public class myItemClickListener implements OnItemClickListener {
//		private Context context;
//		
//		public myItemClickListener(Context context) {
//			this.context = context;
//		}
//		
//		@Override
//		public void onItemClick(AdapterView<?> adapter, View arg1, int position,
//				long id) {
//			//Toast.makeText(context, (String)adapter.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//			String orgName = (String) adapter.getItemAtPosition(position);
//			Intent clickIntent = new Intent(context, OrgsActivity.class);
//			clickIntent.putExtra(OrgNameTag, orgName);
//			startActivity(clickIntent);
//		}
//		
//	}
	//End of Sanchit's code

}
