package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public class MessageDAO {

	public String messageID, messageContent, sendingUser, messageType;
	public DateTime messageDateTime;

	public MessageDAO(String id, String cont, String sendUser, 
			String msgType, String ts){
		messageID = id;
		messageContent = cont;
		sendingUser = sendUser;
		messageType = msgType;
		messageDateTime = Helper.sqlToDateTime(ts);	
	}
	
}
