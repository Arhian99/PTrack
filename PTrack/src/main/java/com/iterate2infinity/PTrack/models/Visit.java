package com.iterate2infinity.PTrack.models;

import java.util.Date;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="visits")
public class Visit {
	@Id
	private ObjectId id;
	
	private Date date;
	@DBRef
	private Location location;
	@DBRef
	private User patient;
	@DBRef
	private Doctor doctor;
	
	private EVisitStatus status;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setdate(Date date) {
		this.date = date;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public User getPatient() {
		return patient;
	}
	public void setPatient(User patient) {
		this.patient = patient;
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public Visit(Date date, Location location, User patient, Doctor doctor) {
		this.date = date;
		this.location = location;
		this.patient = patient;
		this.doctor = doctor;
	}
	public EVisitStatus getStatus() {
		return status;
	}
	public void setStatus(EVisitStatus status) {
		this.status = status;
	}
	
	
}
