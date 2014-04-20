package com.seniordesign.studentorgmanager.data;

public class AbsenceDAO {

	public String username, eventID, organization, eventDateTime, eventName;

	public AbsenceDAO(String username, String eventID, String organization, String edt,String en) {
		this.username = username;
		this.eventID = eventID;
		this.organization = organization;
		eventDateTime = edt;
		eventName = en;
	}
	
}
