package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	
	private boolean isOfficer = false;
	private String memberType;
	
	private String timestampSelected;
	
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
		
		Collections.reverse(newsStories);
		
		for (NewsItemDAO item : newsStories) {
			map = new HashMap<String, Object>();
			map.put("announcement", item.announcement);
			map.put("poster", item.poster);
			map.put("date-time", item.timeStamp);
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.news_feed_row, from, to);
		mNewsFeedListView.setAdapter(adapter);
		mNewsFeedListView.setOnItemClickListener(new MyItemClickListener(this));
	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			newsStories = DataTransfer.getOrganizationNews(orgName);
			memberType = DataTransfer.getUserMemberType(username, orgName);
			if (!(memberType.equals("RegularMember"))) {
				isOfficer = true;
			}
			return null;
		}
	}
	
	public class MyItemClickListener implements OnItemClickListener {

		private Context mContext;
		
		public MyItemClickListener(Context context) {
			mContext = context;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String userViewed = ((TextView) view.findViewById(R.id.newsFeedPoster)).getText().toString();
			timestampSelected = ((TextView) view.findViewById(R.id.newsFeedDateTime)).getText().toString();
			AlertDialog.Builder newsFeedAlert = new AlertDialog.Builder(mContext);
			newsFeedAlert.setTitle("Options");
			String[] options;
			
			if (isOfficer) {
				options = new String[2];
				options[0] = "View " + userViewed + "'s profile";
				options[1] = "Delete Post";
			}
			else {
				options = new String[1];
				options[0] = "View " + userViewed + "'s profile";
			}
			
			newsFeedAlert.setItems(options, new OptionsListener(mContext, userViewed)).show();			
		}
		
		private class OptionsListener implements DialogInterface.OnClickListener {

			Context mContext;
			String userViewed;
			
			public OptionsListener(Context context, String user) {
				mContext = context;
				userViewed = user;
			}
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case 0:
						// View profile
						viewProfile(userViewed);
						break;
					case 1:
						if (!isOfficer) {
							break;
						}
						else {
							deletePost();
						}
				}
				
			}
			
		}
		
	}
	
	public void viewProfile(String user) {
		Intent i = new Intent(NewsFeedActivity.this, PublicProfileActivity.class);
		i.putExtra(LoginActivity.UserNameTag, username);
		i.putExtra(MainActivity.OrgNameTag, orgName);
		i.putExtra(PublicProfileActivity.UserBeingViewedTag, user);
		startActivity(i);
	}
	
	public void deletePost() {
		DeletePostTask mDeleteTask = new DeletePostTask();
		mDeleteTask.execute((Void) null);
		
		//Wait for DB actions to complete
		try {
			mDeleteTask.get(15000, TimeUnit.MILLISECONDS);
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
		
		Intent i = new Intent(NewsFeedActivity.this, NewsFeedActivity.class);
		i.putExtra(LoginActivity.UserNameTag, username);
		i.putExtra(MainActivity.OrgNameTag, orgName);
		startActivity(i);
		finish();
		
	}
	
	public class DeletePostTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			DataTransfer.deleteNewsItem(orgName, timestampSelected);
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
		finish();
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
