package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OrganizationDTO extends Helper{

	public static boolean addUser(){
		
		return true;
	}
	
	public static boolean removeUser(){
		
		return true;
	}
	
	/**
	 * Create a new organization in the DB
	 * @param orgname Name of the organization
	 * @param orgtype Type of the organization
	 * @param creatingUsername The username of the user that created the organization (to be added as a member)
	 * @return an Organization object representing the newly created organization
	 */
	public static OrganizationDAO createOrganization(String name, String type, String creatingUsername){
		
		return null;
	}
	
	public static boolean deleteOrganization(){
		
		return true;
	}	
	
	
	public static ArrayList<OrganizationDAO> getUserOrganizations(String username) {		
		ArrayList<OrganizationDAO> orgs = new ArrayList<OrganizationDAO>();

		
		
		return orgs;
	}
	
	public static String getMemberType(String username, String orgname) {

		return null;
	}

	public static boolean changeUserPosition(){
		
		return true;
	}
	
	public static OrganizationDAO getOrganization(String name) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_org_info", "POST", params);
		
		if(jArr.length() > 0){
			try {
				JSONObject jo = jArr.getJSONObject(0);
				String orgName = (String) jo.get(TAG_ORGNAME);
				String type = (String) jo.get(TAG_TYPE);
				String size = (String) jo.get(TAG_SIZE);
				String annualDues = (String) jo.get(TAG_ANNUALDUES);
				
				return new OrganizationDAO(orgName, type, size, annualDues);				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		}else{
			Log.d("getOrganization", "organization not in db");
			return null;
		}		
		
		return null;
	}
	
	public static HashMap<String, OrganizationDAO> getAllOrganizations(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_all_orgs", "POST", params);
		HashMap<String, OrganizationDAO> result = new HashMap<String, OrganizationDAO>();
		
		for(int i=0; i<jArr.length(); i++){
			try {
				JSONObject jo = jArr.getJSONObject(i);
				String orgName = (String) jo.get(TAG_ORGNAME);
				String type = (String) jo.get(TAG_TYPE);
				String size = (String) jo.get(TAG_SIZE);
				String annualDues = (String) jo.get(TAG_ANNUALDUES);
				
				OrganizationDAO o = new OrganizationDAO(orgName, type, size, annualDues);
				result.put(orgName, o);
				
				return result;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}


	
}
