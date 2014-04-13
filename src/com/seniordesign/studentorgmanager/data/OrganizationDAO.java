package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;

public class OrganizationDAO {

	private String orgName;
	private String type;
	private int size;
	private int annualDues;
//	
//	private ArrayList<User> memberList;
//	private ArrayList<String> positionNames;
	
	public OrganizationDAO(String orgName, String type, String size, String annualDues) {
		this.orgName = orgName;
		this.type = type;
		this.size = Integer.parseInt(size);
		this.annualDues = Integer.parseInt(annualDues);
	}

}
