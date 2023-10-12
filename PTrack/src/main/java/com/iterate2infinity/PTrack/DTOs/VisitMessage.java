package com.iterate2infinity.PTrack.DTOs;

public class VisitMessage {
	private String from;
	private String patientEmail;
	private String doctorEmail;
	private String locationName;
	
	public VisitMessage(String from, String patientEmail, String doctorEmail, String locationName) {
		this.from = from;
		this.patientEmail = patientEmail;
		this.doctorEmail = doctorEmail;
		this.locationName = locationName;
	}
	
	public VisitMessage() {
		from="";
		patientEmail="";
		doctorEmail="";
		locationName="";
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public String getDoctorEmail() {
		return doctorEmail;
	}

	public void setDoctorEmail(String doctorEmail) {
		this.doctorEmail = doctorEmail;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	
	
	
	
}
