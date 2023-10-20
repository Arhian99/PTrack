package com.iterate2infinity.PTrack.DTOs;

public class VisitMessage {
	private String from;
	private String to;
	private String patientEmail;
	private String doctorUsername;
	private String locationName;
	
	public VisitMessage(String from, String patientEmail, String doctorUsername, String locationName) {
		this.from = from;
		this.patientEmail = patientEmail;
		this.doctorUsername = doctorUsername;
		this.locationName = locationName;
	}
	
	public VisitMessage() {
		from="";
		to="";
		patientEmail="";
		doctorUsername="";
		locationName="";
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public String getDoctorUsername() {
		return doctorUsername;
	}

	public void setDoctorUsername(String doctorUsername) {
		this.doctorUsername = doctorUsername;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@Override
	public String toString() {
		return "VisitMessage [from=" + from + ", to=" + to + ", patientEmail=" + patientEmail + ", doctorUsername="
				+ doctorUsername + ", locationName=" + locationName + "]";
	}
	
	
	
	
	
}
