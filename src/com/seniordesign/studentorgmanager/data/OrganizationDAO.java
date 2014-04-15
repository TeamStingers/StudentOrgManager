package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;

public class OrganizationDAO {

	public String name;
	public String type;
	public int size;
	public int annualDues;
//	
//	ArrayList<User> memberList;
//	ArrayList<String> positionNames;
	
	public OrganizationDAO(String orgName, String type, String size, String annualDues) {
		this.name = orgName;
		this.type = type;
		this.size = Integer.parseInt(size);
		this.annualDues = Integer.parseInt(annualDues);
	}

}
