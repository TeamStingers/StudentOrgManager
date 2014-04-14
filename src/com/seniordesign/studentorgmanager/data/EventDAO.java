package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public class EventDAO {
	
	String id;
	String name;
	String organization;
	DateTime dateTime;
	String location;
	String description;
	String type;

	public EventDAO(String id, String name, String org, String dt, String loc, 
			String desc, String type){		
		this.id = id;
		this.name = name;
		organization = org;
		dateTime = Helper.sqlToDateTime(dt);
		location = loc;
		description = desc;
		this.type = type;
	}
		
	
}
