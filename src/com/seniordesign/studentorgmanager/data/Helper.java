package com.seniordesign.studentorgmanager.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.util.Log;


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
		
	static SimpleDateFormat sdfgmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat sdfest = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	
	public static String dateToString(int year, int monthOfYear, int dayOfMonth, 
			int hourOfDay, int minuteOfHour, int secondOfMinute){
		
		Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute);
		String result = sdfgmt.format(calendar.getTime());
//		System.out.println("#1. " + sdf.format(calendar.getTime()));
	 		
//		String secondString;
//		
//		if (secondOfMinute < 10) {
//			secondString = "0" + secondOfMinute;
//		}
//		else {
//			secondString = "" + secondOfMinute;
//		}
//		
//		String result = dayOfMonth+"-"+monthOfYear + "-" + year + " " + 
//			hourOfDay + ":" + minuteOfHour + ":" + secondString;
		
		Log.d("Helper", result);
		
		return result;
	}
	
	public static String formatJsonDate(String jd){
		sdfgmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		sdfest.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
				
		String stripped = jd.substring(0,19).replace('T', ' ');
		Date date = new Date();
		
		try {
			date = sdfgmt.parse(stripped);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sdfest.format(date);
	}
	
}
