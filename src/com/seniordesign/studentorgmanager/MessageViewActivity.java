package com.seniordesign.studentorgmanager;

import com.seniordesign.studentorgmanager.MailBoxActivity.SendMailTask;
import com.seniordesign.studentorgmanager.data.DataTransfer;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MessageViewActivity extends Activity {

	private String messageID, messageContent, sendingUser, messageDateTime;
	private Button replyButton, deleteButton;
	private TextView mailViewSenderLabel, mailViewSenderVal, mailViewDateTimeLabel, mailViewDateTimeVal, mailViewContent;
	private EditText contentInput;	
	private EditText receiverInput;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view);

		username = getIntent().getStringExtra(LoginActivity.UserNameTag);
		messageID = getIntent().getStringExtra(MailBoxActivity.ID_TAG);
		messageContent = getIntent().getStringExtra(MailBoxActivity.CONTENT_TAG);
		sendingUser = getIntent().getStringExtra(MailBoxActivity.SENDER_TAG);
		messageDateTime = getIntent().getStringExtra(MailBoxActivity.DATETIME_TAG);
		
		replyButton = (Button) findViewById(R.id.replyButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		mailViewSenderVal = (TextView) findViewById(R.id.mailViewSenderVal);
		mailViewDateTimeVal = (TextView) findViewById(R.id.mailViewDateTimeVal);
		mailViewContent = (TextView) findViewById(R.id.mailViewContent);
		
		mailViewSenderVal.setText(sendingUser);
		mailViewDateTimeVal.setText(messageDateTime);
		mailViewContent.setText(messageContent);
		
		deleteButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				DeleteMailTask dm = new DeleteMailTask();
				dm.execute((Void) null);
			}
		});
		
		replyButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view){
				LayoutInflater li = LayoutInflater.from(MessageViewActivity.this);
				View sendMessageView = li.inflate(R.layout.send_message, null);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageViewActivity.this);
				alertDialogBuilder.setView(sendMessageView);
								
				contentInput = (EditText) sendMessageView.findViewById(R.id.sendMessageContentInput);
				receiverInput = (EditText) sendMessageView.findViewById(R.id.sendMessageReceiverInput);
				receiverInput.setText(sendingUser);
				
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

	}


	public class DeleteMailTask extends AsyncTask<Void, Void, Void> {
		private boolean deleted;
		
		
		protected Void doInBackground(Void... params) {			
			deleted = DataTransfer.deleteMessage(messageID);
			return null;
		}
		
		protected void onPostExecute(final Void param) {
			if(deleted){
				Toast.makeText(MessageViewActivity.this, "Deleted", Toast.LENGTH_LONG).show();

				Intent i = new Intent(MessageViewActivity.this, MailBoxActivity.class);
				i.putExtra(LoginActivity.UserNameTag, username);
				startActivity(i);
			}else{
				Toast.makeText(MessageViewActivity.this, "Error", Toast.LENGTH_SHORT).show();						
			}
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
				Toast.makeText(MessageViewActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(MessageViewActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();						
			}
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


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_message_view,
					container, false);
			return rootView;
		}
	}

}
