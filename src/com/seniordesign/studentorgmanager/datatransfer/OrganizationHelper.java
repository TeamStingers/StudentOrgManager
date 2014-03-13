package com.seniordesign.studentorgmanager.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;

import com.seniordesign.studentorgmanager.model.Organization;

public class OrganizationHelper {
	
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
	public static Organization createOrganization(String orgname, String orgtype, String creatingUsername){
		
		return null;
	}
	
	public static boolean deleteOrganization(){
		
		return true;
	}	
	
	
	public static Organization loadOrganization(String orgname) {

		return null;
	}
	
	public static ArrayList<Organization> getUserOrganizations(String username) {		
		ArrayList<Organization> orgs = new ArrayList<Organization>();
		return orgs;
	}

	public static HashMap<String, Organization> getAllOrganizations(){
		
		return null;
	}

	
	public static String getMemberType(String username, String orgname) {

		return null;
	}

	public static boolean changeUserPosition(){
		
		return true;
	}
	
}
