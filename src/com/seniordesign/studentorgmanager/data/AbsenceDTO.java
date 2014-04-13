package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

public class AbsenceDTO extends Helper{

	
	public static boolean addAbsence(String username, String eventID, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		params.add(new BasicNameValuePair(TAG_EVENTID, eventID));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/add_absence", "POST", params);
		
		return jArr.length() > 0;
	}

	public static boolean removeAbsence(String username, String eventID, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		params.add(new BasicNameValuePair(TAG_EVENTID, eventID));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/remove_absence", "POST", params);
		
		return jArr.length() > 0;
	}	
	
	
}
