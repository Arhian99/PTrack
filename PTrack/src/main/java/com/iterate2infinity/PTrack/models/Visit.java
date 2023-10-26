package com.iterate2infinity.PTrack.models;

import java.util.Date;
import java.util.Objects;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="visits")
public class Visit {
	@Id
	private String id;
	
	private Date date;

	private String locationName;
	private String locationId;
	
	private String patientUsername;
	private String patientId;
	
	private String doctorUsername;
	private String doctorId;
	
	private EVisitStatus status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getLocationId() {
		return locationId;
	}
	public void setLocation_id(String locationId) {
		this.locationId = locationId;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatient_id(String patientId) {
		this.patientId = patientId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	
	
	public Visit(Date date, String locationName, String locationId, String patientUsername, String patientId,
			String doctorUsername, String doctorId, EVisitStatus status) {
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
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Visit other = (Visit) obj;
		return Objects.equals(id, other.id);
	}

	
	
}
