package com.seniordesign.studentorgmanager.data;

import org.joda.time.DateTime;

public class NewsItemDAO {
//	DateTime dt = new DateTime("2004-12-13T21:39:45.618-08:00");
	public String organization;
	public DateTime timeStamp;
	public String announcement;
	public String poster;

	public NewsItemDAO(String org, String time, String announce, String poster){		
		this.organization = org;
		this.timeStamp = Helper.sqlToDateTime(time);
		this.announcement = announce;
		this.poster = poster;
	}
	
}
