package com.seniordesign.studentorgmanager.datatransfer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.seniordesign.studentorgmanager.model.User;

//Just here as an example for now

public class UserHelper {

	/**
	 * Checks if the username/password combo is valid
	 * @param username
	 * @param password
	 * @return if it is valid
	 */

	private static final String TAG_USERNAME = "Username";
	private static final String TAG_PASSWORD = "Password";
	private static final String TAG_EMAIL = "EMail";
	private static final String TAG_PHONENUMBER = "PhoneNumber";
	private static final String TAG_FIRSTNAME = "FirstName";
	private static final String TAG_LASTNAME = "LastName";
	private static final String TAG_MAJOR = "Major";
	private static final String TAG_GRADUATIONYEAR = "GraduationYear";
	private static final String TAG_BIO = "Bio";
	private static final String TAG_PICTUREREF = "PictureRef";
	private static final JSONParser jParser = new JSONParser();
	

	/**
	 * Checks if a given username password combination is valid
	 * @param username Given username
	 * @param password Given password
	 * @return Whether combination is valid or not
	 */
	public static boolean authenticate(String username, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Username", username));
		params.add(new BasicNameValuePair("Password", password));
				
		JSONArray jArr = jParser.makeHttpRequest("http://10.0.2.2/login", "POST", params);

		if(jArr.length() > 0){
//			Log.d("Debug", "authed!");
			return true;
		}{
//			Log.d("Debug", "bad attempt!");	
			return false;
		}
		
	}
	
	/**
	 * Create a new user in the system given the basic information: username, password, email and return a User object representing user
	 * @param username Given username
	 * @param password Given password
	 * @param email Given email
	 * @return new User object representing newly created user
	 */
	public static User createUser(String username, String password, String email){
		
		//Daryl's code ------------------------------
		/*JSONObject user = new JSONObject();
		user.put(TAG_USERNAME, username);
		user.put(TAG_PASSWORD, password);
		user.put(TAG_EMAIL, email);
		user.put(TAG_PHONENUMBER, phoneNumber);
		user.put(TAG_FIRSTNAME, firstName);
		user.put(TAG_LASTNAME, lastName);
		user.put(TAG_MAJOR, major);
		user.put(TAG_GRADUATIONYEAR, graduationYear);
		user.put(TAG_BIO, bio);
		user.put(TAG_PICTUREREF, pictureRef);
		return user;*/
		//End Daryl's code -----------------------------
		
		return null;
	}
	
	public static JSONObject updateUser(JSONObject user, String tag, String value){
		/*if (tag.equals(TAG_USERNAME)){
			user.put(TAG_USERNAME, value);
		}
		else if (tag.equals(TAG_PASSWORD)){
			user.put(TAG_PASSWORD, value);
		}
		else if (tag.equals(TAG_EMAIL)){
			user.put(TAG_EMAIL, value);
		}
		else if (tag.equals(TAG_PHONENUMBER)){
			user.put(TAG_PHONENUMBER, value);
		}
		else if (tag.equals(TAG_FIRSTNAME)){
			user.put(TAG_FIRSTNAME, value);
		}
		else if (tag.equals(TAG_LASTNAME)){
			user.put(TAG_LASTNAME, value);
		}
		else if (tag.equals(TAG_MAJOR)){
			user.put(TAG_MAJOR, value);
		}
		else if (tag.equals(TAG_GRADUATIONYEAR)){
			user.put(TAG_GRADUATIONYEAR, value);
		}
		else if (tag.equals(TAG_BIO)){
			user.put(TAG_BIO, value);
		}
		else if (tag.equals(TAG_PICTUREREF)){
			user.put(TAG_PICTUREREF, value);
		}
		return user;*/
		
		return null;
	}
	
	/**
	 * Creates a User object populated with the correct data given a particular username
	 * @param username User's username
	 * @return User object referring to active user
	 */
	public static User loadUser(String username){
		/*for (JSONObject user : usersList){
			if (user.get(TAG_USERNAME).equals(username)){
				return user;
			}
			else return null;
		}*/
		return null;
	}
	
	public static void deleteUser(JSONArray usersList, String username){
		/*for (int i = 0; i < usersList.length(); i++){
			JSONObject userToRemove = usersList.getJSONObject(i);
			userToRemove.remove(username);
		}*/
	}
}
