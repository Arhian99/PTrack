package com.iterate2infinity.PTrack.models;

import java.util.Date;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="visits")
public class Visit {
	@Id
	private ObjectId id;
	
	private Date date;

	private String locationName;
	private ObjectId locationId;
	
	private String patientUsername;
	private ObjectId patientId;
	
	private String doctorUsername;
	private ObjectId doctorId;
	
	private EVisitStatus status;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getLocationName() {
		return locationName;
	}
	
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
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

	public EVisitStatus getStatus() {
		return status;
	}
	public void setStatus(EVisitStatus status) {
		this.status = status;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ObjectId getLocationId() {
		return locationId;
	}
	public void setLocation_id(ObjectId locationId) {
		this.locationId = locationId;
	}
	public ObjectId getPatientId() {
		return patientId;
	}
	public void setPatient_id(ObjectId patientId) {
		this.patientId = patientId;
	}
	public ObjectId getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}
	
	public Visit(Date date, String locationName, ObjectId locationId, String patientUsername, ObjectId patientId,
			String doctorUsername, ObjectId doctorId, EVisitStatus status) {
		super();
		this.date = date;
		this.locationName = locationName;
		this.locationId = locationId;
		this.patientUsername = patientUsername;
		this.patientId = patientId;
		this.doctorUsername = doctorUsername;
		this.doctorId = doctorId;
		this.status = status;
	}
	@Override
	public String toString() {
		return "Visit [id=" + id + ", date=" + date + ", locationName=" + locationName + ", locationId=" + locationId
				+ ", patientUsername=" + patientUsername + ", patientId=" + patientId + ", doctorUsername="
				+ doctorUsername + ", doctorId=" + doctorId + ", status=" + status + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null || obj.getClass() != this.getClass()) {
			return false;
		} 
		
		Visit otherObj = (Visit) obj;
		
		return this.id.equals(otherObj.id);
	}

	
	
}
