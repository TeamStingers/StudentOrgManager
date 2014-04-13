package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;



public class UserDTO extends Helper{

	public static boolean authenticate(String username, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_PASSWORD, password));
				
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/login", "POST", params);

		return (jArr.length() > 0);
		
	}
	
	
	public static UserDAO getUser(String username){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
				
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_info", "POST", params);

		if(jArr.length() > 0){
			JSONObject jo;
			try {
				jo = jArr.getJSONObject(0);
				String email = (String) jo.get(TAG_EMAIL);
				String phoneNumber = (String) jo.get(TAG_PHONENUMBER);
				String firstName = (String) jo.get(TAG_FIRSTNAME);
				String lastName = (String) jo.get(TAG_LASTNAME);
				String major = (String) jo.get(TAG_MAJOR);
				String gradYear = (String) jo.get(TAG_GRADUATIONYEAR);
				String bio = (String) jo.get(TAG_BIO);
				String pictureRef = (String) jo.get(TAG_PICTUREREF);
				
				return new UserDAO(firstName, lastName, username, major, bio, email,
						phoneNumber, pictureRef, gradYear);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.d("getUser", "Could Find User in DB.");
			return null;
		}
		
		return null;
	}
	
	public static UserDAO createUser(String username, String password, String email){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_PASSWORD, password));
		params.add(new BasicNameValuePair(TAG_EMAIL, email));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/create_user", "POST", params);
		
		if(jArr.length() > 0){
			return new UserDAO(null, null, username, null, null, email,
					null, null, null);
		}
		
		return null;
	}
	
	public static boolean deleteUser(String username){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/delete_user", "POST", params);
		
		return jArr.length() > 0;
	}	
	
	public static boolean updateUser(String firstName, String lastName, String password, 
			String major, String bio, String email, String phoneNumber, String gradYear){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_FIRSTNAME, firstName));
		params.add(new BasicNameValuePair(TAG_LASTNAME, lastName));
		params.add(new BasicNameValuePair(TAG_PASSWORD, password));
		params.add(new BasicNameValuePair(TAG_MAJOR, major));
		params.add(new BasicNameValuePair(TAG_BIO, bio));
		params.add(new BasicNameValuePair(TAG_EMAIL, email));
		params.add(new BasicNameValuePair(TAG_PHONENUMBER, phoneNumber));
		params.add(new BasicNameValuePair(TAG_GRADUATIONYEAR, gradYear));
				
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/update_user", "POST", params);

		return jArr.length() > 0;
	}
	
	public static boolean addUserToOrg(String username, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		params.add(new BasicNameValuePair(TAG_POSITION, "Member"));
		params.add(new BasicNameValuePair(TAG_MEMBERTYPE, "RegularMember"));
		params.add(new BasicNameValuePair(TAG_DUESPAID, "Unpaid"));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/add_user_to_org", "POST", params);
		
		return jArr.length() > 0;
	}
	
	public static boolean removeUserFromOrg(String username, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/remove_user_from_org", "POST", params);

		return jArr.length() > 0;		
	}

	public static String getUserPosition(String username, String organization) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_position", "POST", params);

		if(jArr.length() > 0){
			try {
				JSONObject jo = jArr.getJSONObject(0);
				return (String) jo.get(TAG_POSITION);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static String getUserMemberType(String username, String organization) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_position", "POST", params);

		if(jArr.length() > 0){
			try {
				JSONObject jo = jArr.getJSONObject(0);
				return (String) jo.get(TAG_MEMBERTYPE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	public static boolean changeUserPosition(String position, String memberType, 
			String organization, String username){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		params.add(new BasicNameValuePair(TAG_POSITION, position));
		params.add(new BasicNameValuePair(TAG_MEMBERTYPE, memberType));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/change_user_position", "POST", params);

		return jArr.length() > 0;
	}
	
	
	
	

}
