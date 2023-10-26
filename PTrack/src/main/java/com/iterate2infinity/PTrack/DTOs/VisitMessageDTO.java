package com.iterate2infinity.PTrack.DTOs;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitMessageDTO {
	private String patientUsername;
	private String doctorUsername;
	private String locationName;
	private ObjectId visitID;
	
	public String getPatientUsername() {
		return patientUsername;
	}
	public void setPatientUsername(String patientUsername) {
		this.patientUsername = patientUsername;
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
	public VisitMessageDTO(String patientUsername, String doctorUsername, String locationName) {
		super();
		this.patientUsername = patientUsername;
		this.doctorUsername = doctorUsername;
		this.locationName = locationName;
	}
	@Override
	public String toString() {
		return "VisitMessageDTO [patientUsername=" + patientUsername + ", doctorUsername=" + doctorUsername
				+ ", locationName=" + locationName + ", visitID=" + visitID + "]";
	}
	public ObjectId getVisitID() {
		return visitID;
	}
	public void setVisitID(ObjectId visitID) {
		this.visitID = visitID;
	}
	public VisitMessageDTO(String patientUsername, String doctorUsername, String locationName, ObjectId visitID) {
		super();
		this.patientUsername = patientUsername;
		this.doctorUsername = doctorUsername;
		this.locationName = locationName;
		this.visitID = visitID;
	}
	public VisitMessageDTO() {
	}
	
	
	
	
	
	
	
}
