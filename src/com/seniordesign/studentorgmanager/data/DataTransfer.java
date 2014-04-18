package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("UseValueOf")
public class DataTransfer extends Helper{

	//USER
	
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
		UserDAO result = null;
		
		if(jArr.length() > 0){
			JSONObject jo;
			try {
				jo = jArr.getJSONObject(0);
				String email = (String) jo.get(TAG_EMAIL);
				String phoneNumber = jo.get(TAG_PHONENUMBER).toString();
				String firstName = (String) jo.get(TAG_FIRSTNAME).toString();
				String lastName = (String) jo.get(TAG_LASTNAME).toString();
				String major = (String) jo.get(TAG_MAJOR).toString();
				String gradYear = (String) jo.get(TAG_GRADUATIONYEAR).toString();
				String bio = (String) jo.get(TAG_BIO).toString();
				String pictureRef = (String) jo.get(TAG_PICTUREREF).toString();
				
				result = new UserDAO(firstName, lastName, username, major, bio, email,
						phoneNumber, pictureRef, gradYear);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d("getUser", "fuck");
				e.printStackTrace();
			}
		}
		
		return result;		
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
	
	public static boolean updateUser(String username, String firstName, String lastName, String major, 
			String bio, String email, String phoneNumber, String gradYear){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_FIRSTNAME, firstName));
		params.add(new BasicNameValuePair(TAG_LASTNAME, lastName));
		params.add(new BasicNameValuePair(TAG_MAJOR, major));
		params.add(new BasicNameValuePair(TAG_BIO, bio));
		params.add(new BasicNameValuePair(TAG_EMAIL, email));
		params.add(new BasicNameValuePair(TAG_PHONENUMBER, phoneNumber));
		params.add(new BasicNameValuePair(TAG_GRADUATIONYEAR, gradYear));
				
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/update_user", "POST", params);

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
	
	public static ArrayList<OrganizationDAO> getUserOrganizations(String username){
		ArrayList<OrganizationDAO> result = new ArrayList<OrganizationDAO>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_orgs", "POST", params);
				
		JSONObject jo;	
		
		for(int i=0; i < jArr.length(); i++){
			try {
				jo = jArr.getJSONObject(i);
				String name = (String) jo.get(TAG_ORGANIZATION);
				result.add(new OrganizationDAO(name, null, null, null));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		
		return result;
	}

	public static boolean addUserToOrganization(String username, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		params.add(new BasicNameValuePair(TAG_POSITION, "Member"));
		params.add(new BasicNameValuePair(TAG_MEMBERTYPE, "RegularMember"));
		params.add(new BasicNameValuePair(TAG_DUESPAID, "Unpaid"));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/add_user_to_org", "POST", params);
		
		return jArr.length() > 0;
	}
	
	public static boolean removeUserFromOrganization(String username, String organization){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, username));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, organization));
		
		Log.d("removeFrom", "in");
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/remove_user_from_org", "POST", params);
		
		try {
			Boolean res = (Boolean) jArr.getJSONObject(0).get("success");
			Log.d("removeFromRes", "res");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jArr.length() > 0;		
	}
	
	//ORGANIZATON
	
	public static OrganizationDAO createOrganization(String orgName, String type, 
			String creatingUsername, String annualDues){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGNAME, orgName));
		params.add(new BasicNameValuePair(TAG_TYPE, type));
		params.add(new BasicNameValuePair(TAG_ANNUALDUES, annualDues));
		params.add(new BasicNameValuePair("CreatorUser", creatingUsername));		
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/create_org", "POST", params);
		
		if(jArr.length()>0) return new OrganizationDAO(orgName, type, "1", annualDues);
		
		return null;
	}
	
	public static boolean deleteOrganization(String orgName){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGNAME, orgName));

		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/delete_org", "POST", params);
		
		return jArr.length() > 0;
	}	
	

	public static OrganizationDAO getOrganization(String name) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, name));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_org_info", "POST", params);
		
		if(jArr.length() > 0){
			try {
				JSONObject jo = jArr.getJSONObject(0);
				String orgName = (String) jo.get(TAG_ORGNAME);
				String type = (String) jo.get(TAG_TYPE);
				String size = new Integer((Integer) jo.get(TAG_SIZE)).toString();
				String annualDues = new Integer((Integer) jo.get(TAG_ANNUALDUES)).toString();
				
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
	
	public static ArrayList<OrganizationDAO> getAllOrganizations(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_all_orgs", "POST", params);
		ArrayList<OrganizationDAO> result = new ArrayList<OrganizationDAO>();
		
		for(int i=0; i<jArr.length(); i++){
			try {
				JSONObject jo = jArr.getJSONObject(i);
				String orgName = (String) jo.get(TAG_ORGNAME);
				String type = (String) jo.get(TAG_TYPE);
				String size = new Integer((Integer) jo.get(TAG_SIZE)).toString();
				String annualDues = new Integer((Integer) jo.get(TAG_ANNUALDUES)).toString();
				OrganizationDAO o = new OrganizationDAO(orgName, type, size, annualDues);
				result.add(o);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static ArrayList<NewsItemDAO> getOrganizationNews(String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_org_news", "POST", params);
		ArrayList<NewsItemDAO> result = new ArrayList<NewsItemDAO>();
		
		Log.d("dd" , new Integer(jArr.length()).toString());
		
		for(int i=0; i<jArr.length(); i++){
			try {
				JSONObject jo = jArr.getJSONObject(i);
				String orgName = (String) jo.get(TAG_ORGANIZATION);
				String ts = (String) jo.get(TAG_NEWSTIMESTAMP);
				String announcement = (String) jo.get(TAG_ANNOUNCEMENT);
				String poster = (String) jo.get(TAG_POSTER);
				
				NewsItemDAO n = new NewsItemDAO(orgName, ts, announcement, poster);
				result.add(n);				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d("getOrgNews", "" + result.size());

		return result;
	}
	
	public static ArrayList<EventDAO> getOrganizationEvents(String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));	
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_org_events", "POST", params);
		ArrayList<EventDAO> result = new ArrayList<EventDAO>();
		
		for(int i=0; i<jArr.length(); i++){
			try {
				JSONObject jo = jArr.getJSONObject(i);
								
				String id = (String) jo.get(TAG_EVENTID);
				String name = (String) jo.get(TAG_EVENTNAME); 
				String orgName = (String) jo.get(TAG_ORGANIZATION);
				String dt = (String) jo.get(TAG_EVENTDATETIME); 
				String loc = (String) jo.get(TAG_LOCATION);
				String desc = (String) jo.get(TAG_DESCRIPTION);
				String type	= (String) jo.get(TAG_TYPE);
				
				EventDAO n = new EventDAO(id, name, orgName, dt, loc, desc, type);
				result.add(n);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	//NEWITEM
	
	public static boolean createNewsItem(String org, String announce, String poster) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		params.add(new BasicNameValuePair(TAG_ANNOUNCEMENT, announce));
		params.add(new BasicNameValuePair(TAG_POSTER, poster));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/create_news_item", "POST", params);

		return (jArr.length() > 0);
	}

	public static boolean deleteNewsItem(String org, String ts) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		params.add(new BasicNameValuePair(TAG_NEWSTIMESTAMP, ts));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/delete_news_item", "POST", params);

		return (jArr.length() > 0);
	}
	
	//EVENT
	
	public static boolean createEvent(String org, String announce, String poster, 
			String desc, String type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		params.add(new BasicNameValuePair(TAG_EVENTDATETIME, announce));
		params.add(new BasicNameValuePair(TAG_LOCATION, poster));
		params.add(new BasicNameValuePair(TAG_DESCRIPTION, desc));
		params.add(new BasicNameValuePair(TAG_TYPE, type));

		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/create_event", "POST", params);

		return (jArr.length() > 0);
	}	

	public static boolean deleteEvent(String eventID){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_EVENTID, eventID));
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/delete_event", "POST", params);
		
		return (jArr.length() > 0);		
	}
	
	//ABSENCE
	
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
	
	//MESSAGE
	
	public static ArrayList<AbsenceDAO> getEventAbsences(String eventID){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_EVENTID, eventID));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_event_absences", "POST", params);
		ArrayList<AbsenceDAO> result = new ArrayList<AbsenceDAO>();
			
		JSONObject jo;
			
		for(int i=0; i<jArr.length() ;i++){
			try{
				jo = jArr.getJSONObject(i);
				String username = (String) jo.get(TAG_USERNAME);
				String org = (String) jo.get(TAG_ORGANIZATION);
				
				AbsenceDAO a = new AbsenceDAO(username, eventID, org);
				result.add(a);			
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static ArrayList<AbsenceDAO> getUserAbsencesForOrg(String user, String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, user));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_absences_for_org", "POST", params);
		ArrayList<AbsenceDAO> result = new ArrayList<AbsenceDAO>();

		JSONObject jo;
		
		for(int i=0; i<jArr.length() ;i++){
			try{
				jo = jArr.getJSONObject(i);
				String eventID = (String) jo.get(TAG_EVENTID);
				
				AbsenceDAO a = new AbsenceDAO(user, eventID, org);
				result.add(a);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static ArrayList<MessageDAO> getUserMessages(String user){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, user));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_user_msgs", "POST", params);
		ArrayList<MessageDAO> result = new ArrayList<MessageDAO>();
		
		JSONObject jo;

		for(int i=0; i<jArr.length() ;i++){
			try{
				jo = jArr.getJSONObject(i);
				String id = (String) jo.get(TAG_MESSAGEID);
				String content = (String) jo.get(TAG_MSGCONTENT);
				String sender = (String) jo.get(TAG_SENDINGUSER);
				String type = (String) jo.get(TAG_TYPE);
				String ts = (String) jo.get(TAG_MSGTIMESTAMP);
				
				MessageDAO m = new MessageDAO(id, content, sender, type, ts);
				result.add(m);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static boolean sendMessage(String sender, String receiver){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_SENDINGUSER, sender));
		params.add(new BasicNameValuePair(TAG_RECEIVINGMEMBER, receiver));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/send_message", "POST", params);
		
		return jArr.length() > 0;
	}

	public static boolean readMessage(String id){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_MESSAGEID, id));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/read_message", "POST", params);
		
		return jArr.length() > 0;
	}	
	
	public static String getDuesStatus(String user, String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, user));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_dues_status", "POST", params);
		
		if(jArr.length() > 0){
			try {
				JSONObject jo = jArr.getJSONObject(0);
				return (String) jo.get(TAG_DUESPAID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		return null;
	}
	
	public static boolean updateOrganizationDues(String dues, String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ANNUALDUES, dues));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/update_org_dues", "POST", params);
		
		return (jArr.length() > 0);
	}

	public static boolean updateUserDues(String dues, String org, String user){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Status", dues));
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		params.add(new BasicNameValuePair(TAG_USERNAME, user));

		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/update_user_dues", "POST", params);
		
		return (jArr.length() > 0);
	}
	
	public static ArrayList<String> getUsersForOrg(String org){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_ORGANIZATION, org));
		ArrayList<String> result = new ArrayList<String>();
		
		JSONArray jArr = jsonParser.makeHttpRequest(HOST+"/get_users_for_org", "POST", params);
		JSONObject jo;
		
		for(int i=0; i<jArr.length(); i++){
			try{
				jo = jArr.getJSONObject(i);
				result.add((String) jo.get(TAG_USERNAME) );				
			}catch(Exception e){
				
			}
		}
		
		return result;
	}
	
}
