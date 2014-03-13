package com.seniordesign.studentorgmanager.datatransfer;

import java.util.ArrayList;

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
	

	public static boolean authenticate(String username, String password) {
		//TODO

		return true;
	}

	public static JSONObject createUser(String username, String password, String email, String phoneNumber, String firstName, String lastName, String major, String graduationYear, String bio, String pictureRef){
		JSONObject user = new JSONObject();
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
		return user;
	}
	
	public static JSONObject updateUser(JSONObject user, String tag, String value){
		if (tag.equals(TAG_USERNAME)){
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
		return user;
	}
	
	public static JSONObject loadUser(JSONArray usersList, String username){
		for (JSONObject user : usersList){
			if (user.get(TAG_USERNAME).equals(username)){
				return user;
			}
			else return null;
		}
	}
	
	public static void deleteUser(JSONArray usersList, String username){
		for (int i = 0; i < usersList.length(); i++){
			JSONObject userToRemove = usersList.getJSONObject(i);
			userToRemove.remove(username);
		}
	}
}
