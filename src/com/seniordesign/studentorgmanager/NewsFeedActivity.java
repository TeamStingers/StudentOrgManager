package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;
import com.seniordesign.studentorgmanager.data.*;

public class NewsFeedActivity extends Activity {

	//UI Elements
	private TextView mOrgNameLabel;
	private ListView mNewsFeedListView;
	private Button mNewStoryButton;
	
	private String orgName;
	private String username;
	private ArrayList<NewsItemDAO> newsStories;
	private String story;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_feed);
		
		//Get and set org name
		Intent intent = getIntent();
		orgName = intent.getExtras().getString(MainActivity.OrgNameTag);
		mOrgNameLabel = (TextView) findViewById(R.id.orgTitle);
		mOrgNameLabel.setText(orgName);
		
		username = intent.getExtras().getString(LoginActivity.UserNameTag);
		
		//Initialize other UI elements
		mNewsFeedListView = (ListView) findViewById(R.id.newsFeedList);
		mNewStoryButton = (Button) findViewById(R.id.newStoryButton);
		mNewStoryButton.setOnClickListener(new ButtonClickListener(this));
		
		//Run DB actions
		InitTask mInitTask = new InitTask();
		mInitTask.execute((Void) null);
		
		//Wait for DB actions to complete
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
		
		
		//Set up news feed list
		
		String[] from = new String[] {"announcement", "poster", "date-time"};
		int[] to = new int[] {R.id.newsFeedStory, R.id.newsFeedPoster, R.id.newsFeedDateTime};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (NewsItemDAO item : newsStories) {
			map = new HashMap<String, Object>();
			map.put("announcement", item.announcement);
			map.put("poster", item.poster);
			map.put("date-time", item.timeStamp);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.news_feed_row, from, to);
		mNewsFeedListView.setAdapter(adapter);
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			newsStories = DataTransfer.getOrganizationNews(orgName);
			return null;
		}
	}
	
	protected void saveStory(String story) {
		this.story = story;
		
		NewStoryTask task = new NewStoryTask();
		task.execute((Void) null);

		//Wait for DB actions to complete
				try {
					task.get(15000, TimeUnit.MILLISECONDS);
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
		
		Bundle extras = new Bundle();
		extras.putString(LoginActivity.UserNameTag, username);
		extras.putString(MainActivity.OrgNameTag, orgName);
		Intent i = new Intent(this, NewsFeedActivity.class);
		i.putExtras(extras);
		startActivity(i);
		
	}
	
	public class NewStoryTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.createNewsItem(orgName, story, username);
			return null;
		}
	}
	
	public class ButtonClickListener implements OnClickListener {

		Context mContext;
		
		public ButtonClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder addAlert = new AlertDialog.Builder(mContext);
			addAlert.setTitle("New Story");
			addAlert.setMessage("What's on your mind?");
			final EditText input = new EditText(mContext);
			addAlert.setView(input);
			
			addAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String newStory = input.getText().toString();
				  Log.d("new story", newStory);
				  
				  saveStory(newStory);
				  
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news_feed, menu);
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
