package com.seniordesign.studentorgmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.seniordesign.studentorgmanager.MainActivity.myItemClickListener;
import com.seniordesign.studentorgmanager.NewsFeedActivity.ButtonClickListener;
import com.seniordesign.studentorgmanager.NewsFeedActivity.InitTask;
import com.seniordesign.studentorgmanager.data.DataTransfer;
import com.seniordesign.studentorgmanager.data.MessageDAO;
import com.seniordesign.studentorgmanager.data.NewsItemDAO;
import com.seniordesign.studentorgmanager.data.OrganizationDAO;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class MailBoxActivity extends Activity {

	private String username;
	private ListView mailBoxListView;
	private Button newMessageButton;
	private ArrayList<MessageDAO> messages;
	private HashMap<Integer,MessageDAO> messageTracker = new HashMap<Integer,MessageDAO>();
	private EditText receiverInput;
	private EditText contentInput;
	
	public static final String 	ID_TAG= "MailBoxId", 
								CONTENT_TAG="MailBoxContent", 
								SENDER_TAG="MailBoxSender", 
								DATETIME_TAG="MailBoxDateTime";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_box);
	
		
		username = getIntent().getStringExtra(LoginActivity.UserNameTag);
		TextView mailBoxLabel = (TextView) findViewById(R.id.mailBoxLabel);
		mailBoxLabel.setText("Messages for " + username);
				
		mailBoxListView = (ListView) findViewById(R.id.mailBoxList);
		newMessageButton = (Button) findViewById(R.id.newMessageButton);
		newMessageButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view){
				LayoutInflater li = LayoutInflater.from(MailBoxActivity.this);
				View sendMessageView = li.inflate(R.layout.send_message, null);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MailBoxActivity.this);
				alertDialogBuilder.setView(sendMessageView);
								
				receiverInput = (EditText) sendMessageView.findViewById(R.id.sendMessageReceiverInput);
				contentInput = (EditText) sendMessageView.findViewById(R.id.sendMessageContentInput);
				
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Send", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// get user input and set it to result
								SendMailTask smt = new SendMailTask();
								smt.execute((Void) null);
							}
						})
				.setNegativeButton("Cancel", null);

				// create an alert dialog
				AlertDialog alertD = alertDialogBuilder.create();
		
				alertD.show();
			}
		});
				
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
		
		String[] from = new String[] {"contentPreview", "sender", "date-time"};
		int[] to = new int[] {R.id.mailBoxContentPrev, R.id.mailBoxSender, R.id.mailBoxDateTime};
		
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Collections.reverse(messages);
		int ctr=0;
		
		for (MessageDAO m : messages) {
			if(ctr>=50) break;
			map = new HashMap<String, Object>();
			
			String content = m.messageContent;
			String preview;
			
			if(content.length() > 30) preview = content.substring(0,31) + "...";								
			else preview = content;
			
			map.put("contentPreview", preview);
			map.put("content", content);
			map.put("sender", m.sendingUser);
			map.put("date-time", m.messageDateTime);
			
			messageTracker.put(ctr, m);
			ctr++;	
			fillMaps.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.mail_box_row, from, to);
		mailBoxListView.setAdapter(adapter);
		mailBoxListView.setOnItemClickListener(new myItemClickListener());

	}

	public class InitTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			messages = DataTransfer.getUserMessages(username);
			return null;
		}
	}

	public class SendMailTask extends AsyncTask<Void, Void, Void> {
		private boolean sent;
		
		
		protected Void doInBackground(Void... params) {			
			sent = DataTransfer.sendMessage(username, receiverInput.getText().toString(),
					contentInput.getText().toString());
			return null;
		}
		
		protected void onPostExecute(final Void param) {
			if(sent){
				Toast.makeText(MailBoxActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(MailBoxActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();						
			}
		}
	}
	
	
	public class myItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View arg1, int position,
				long id) {
			MessageDAO m = messageTracker.get(position);
			Log.d("Position", ""+position);
			Log.d("String Test", m.messageContent);
						
			Intent i = new Intent(MailBoxActivity.this, MessageViewActivity.class);
			i.putExtra(ID_TAG, m.messageID);
			i.putExtra(CONTENT_TAG, m.messageContent);
			i.putExtra(SENDER_TAG, m.sendingUser);
			i.putExtra(DATETIME_TAG, m.messageDateTime);
			i.putExtra(LoginActivity.UserNameTag, username);
			startActivity(i);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mail_box, menu);
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
