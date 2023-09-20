package com.iterate2infinity.PTrack.models;

import java.util.HashSet;

import org.bson.types.ObjectId;

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
	
	public Location(String name, String address) {
		this.name = name;
		this.address = address;
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
