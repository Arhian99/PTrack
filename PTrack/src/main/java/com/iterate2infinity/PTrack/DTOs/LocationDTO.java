package com.iterate2infinity.PTrack.DTOs;

import org.bson.types.ObjectId;

public class LocationDTO {
	private String name;
	private String address;
	private ObjectId id;
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
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public LocationDTO(String name, String address, ObjectId id) {
		super();
		this.name = name;
		this.address = address;
		this.id = id;
	}
	
	public LocationDTO() {
	}
	
}
