package com.seniordesign.studentorgmanager.data;

public class NewsItemDAO {
	public String organization;
	public String timeStamp;
	public String announcement;
	public String poster;

	public NewsItemDAO(String org, String time, String announce, String poster){		
		this.organization = org;
		this.timeStamp = Helper.formatJsonDate(time);
		this.announcement = announce;
		this.poster = poster;
	}
	
}
