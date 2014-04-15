package com.seniordesign.studentorgmanager.data;

import java.util.ArrayList;

public class UserDAO {

	public String firstName, lastName, username, major, bio, email, phoneNumber, pictureRef, gradYear;
	
//	private ArrayList<Organization> groups;
//	private ArrayList<Absence> absences;
	
	public UserDAO(String firstName, String lastName, String username, String major, String bio, 
			String email, String phoneNumber, String pictureRef, String gradYear) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.major = major;
		this.bio = bio;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.pictureRef = pictureRef;
		this.gradYear = gradYear;
	}	
}
