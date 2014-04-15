package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public class EventDAO {
	
	public String id;
	public String name;
	public String organization;
	public DateTime dateTime;
	public String location;
	public String description;
	public String type;

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
