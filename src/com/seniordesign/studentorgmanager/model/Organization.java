package com.seniordesign.studentorgmanager.model;

import java.util.ArrayList;

public class Organization {

	private String name;
	private ArrayList<User> memberList;
	private ArrayList<String> positionNames;
	
	public Organization(String orgname) {
		name = orgname;
	}
}
