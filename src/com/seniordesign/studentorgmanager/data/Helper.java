package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public abstract class Helper {

	protected static final JSONParser jsonParser = new JSONParser();
	protected static final String HOST = "http://10.0.2.2";
	
	//@TABLE User
	protected static final String TAG_USERNAME = "Username";
	protected static final String TAG_PASSWORD = "Password";
	protected static final String TAG_EMAIL = "EMail";
	protected static final String TAG_PHONENUMBER = "PhoneNumber";
	protected static final String TAG_FIRSTNAME = "FirstName";
	protected static final String TAG_LASTNAME = "LastName";
	protected static final String TAG_MAJOR = "Major";
	protected static final String TAG_GRADUATIONYEAR = "GraduationYear";
	protected static final String TAG_BIO = "Bio";
	protected static final String TAG_PICTUREREF = "PictureRef";

	//@TABLE UserOrgs
	protected static final String TAG_ORGANIZATION = "Organization";
	protected static final String TAG_POSITION = "Position";
	protected static final String TAG_MEMBERTYPE = "MemberType";
	protected static final String TAG_DUESPAID = "DuesPaid";
	
	//@TABLE Organizations
	protected static final String TAG_ORGNAME = "OrgName";
	protected static final String TAG_TYPE = "Type";
	protected static final String TAG_SIZE = "Size";
	protected static final String TAG_ANNUALDUES = "AnnualDues";

	//@TABLE NewItems
	protected static final String TAG_NEWSTIMESTAMP = "NewsTimeStamp";
	protected static final String TAG_ANNOUNCEMENT = "Announcement";
	protected static final String TAG_POSTER = "Poster";
	

	//@TABLE Events
	protected static final String TAG_EVENTID = "EventID";
	protected static final String TAG_EVENTNAME = "EventName";
	protected static final String TAG_EVENTDATETIME = "EventDateTime";
	protected static final String TAG_LOCATION = "Location";
	protected static final String TAG_DESCRIPTION = "Description";	

	//@TABLE Absences
	
	
	//@TABLE Messages
	protected static final String TAG_MESSAGEID = "MessageID";
	protected static final String TAG_SENDINGUSER = "SendingUser";
	protected static final String TAG_MSGTIMESTAMP = "MsgTimeStamp";
	protected static final String TAG_MESSAGETYPE = "MessageType";	
	protected static final String TAG_MSGCONTENT = "MsgContent";	


	//@TABLE UserMessage
	protected static final String TAG_RECEIVINGMEMBER = "ReceivingMember";
	protected static final String TAG_READSTATUS = "ReadStatus";
	
	public static DateTime sqlToDateTime(String sqlString){
		int year = Integer.parseInt(sqlString.substring(0,4));
		int monthOfYear = Integer.parseInt(sqlString.substring(5,7));
		int dayOfMonth = Integer.parseInt(sqlString.substring(8,10));
		int hourOfDay = Integer.parseInt(sqlString.substring(11,13));
		int minuteOfHour = Integer.parseInt(sqlString.substring(14,16));
		int secondOfMinute = Integer.parseInt(sqlString.substring(17)); 
		
		return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute);
	}
	
//	public static String dateTimeToSql(DateTime dt){
//		return (dt.toString().substring(0,19).replace("T", " "));
//	}
	
}