package com.iterate2infinity.PTrack.models;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="roles")
public class Role {
	@Id
	private ObjectId id;
	private ERole name;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ERole getName() {
		return name;
	}
	public void setName(ERole name) {
		this.name = name;
	}
	public Role(ERole name) {
		this.name = name;
	}
	public Role() {
	}
	
	

}
