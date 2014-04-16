package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;

public class OrganizationDAO {

	public String name;
	public String type;
	public Integer size;
	public Float annualDues;
//	
//	ArrayList<User> memberList;
//	ArrayList<String> positionNames;
	
	public OrganizationDAO(String orgName, String type, String size, String annualDues) {
		this.name = orgName;
		this.type = type;
		
		if(size==null) this.size=null;
		else this.size=Integer.parseInt(size);

		if(annualDues==null) this.annualDues=null;
		else this.annualDues=Float.parseFloat(annualDues);
	}
	
	

}
