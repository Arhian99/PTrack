package com.iterate2infinity.PTrack.models;

import java.util.HashSet;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="locations")
public class Location {

	@Id
	private ObjectId id;
	private String name;
	private String address;
	
	@DBRef
	private HashSet<Doctor> activeDoctors;
	@DBRef
	private HashSet<User> activePatients;
	
	@JsonBackReference
	public HashSet<Doctor> getActiveDoctors() {
		return activeDoctors;
	}

	public void setActiveDoctors(HashSet<Doctor> activeDoctors) {
		this.activeDoctors = activeDoctors;
	}

	@JsonBackReference
	public HashSet<User> getActivePatients() {
		return activePatients;
	}

	public void setActivePatients(HashSet<User> activePatients) {
		this.activePatients = activePatients;
	}

	public void addActivePatient(User user) {
		activePatients.add(user);
	}
	
	public void addActiveDoctor(Doctor doctor) {
		activeDoctors.add(doctor);
	}
	
	public void clearActivePatients() {
		activePatients.clear();
	}
	
	public void clearActiveDoctors() {
		activeDoctors.clear();
	}
	
	
	public Location(String name, String address) {
		this.name = name;
		this.address = address;
		this.activeDoctors = new HashSet<>();
		this.activePatients = new HashSet<>();
	}
	
	public Location() {
		this.activeDoctors = new HashSet<>();
		this.activePatients = new HashSet<>();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
