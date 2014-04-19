package com.seniordesign.studentorgmanager.data;


public class MessageDAO {

	public String messageID, messageContent, sendingUser, messageType;
	public String messageDateTime;

	public MessageDAO(String id, String cont, String sendUser, 
			String msgType, String ts){
		messageID = id;
		messageContent = cont;
		sendingUser = sendUser;
		messageType = msgType;
		messageDateTime = Helper.formatJsonDate(ts);
	}
	
}
