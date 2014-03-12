package com.seniordesign.studentorgmanager.model;

import java.util.ArrayList;

public class User {

	private String firstname, lastname, username, major, bio;
	private int gradYear;
	private ArrayList<Organization> groups;
	private ArrayList<Absence> absences;
	
	public User(String uname) {
		username = uname;
	}
	
}
