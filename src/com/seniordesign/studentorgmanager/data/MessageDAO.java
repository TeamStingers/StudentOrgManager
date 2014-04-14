package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public class MessageDAO {

	String messageID, messageContent, sendingUser, messageType;
	DateTime messageDateTime;

	public MessageDAO(String id, String cont, String sendUser, 
			String msgType, String ts){
		messageID = id;
		messageContent = cont;
		sendingUser = sendUser;
		messageType = msgType;
		messageDateTime = Helper.sqlToDateTime(ts);	
	}
	
}
