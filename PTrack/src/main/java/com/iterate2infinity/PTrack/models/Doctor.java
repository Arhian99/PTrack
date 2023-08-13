package com.iterate2infinity.PTrack.models;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="doctors")
public class Doctor {
	
	@Id
	private ObjectId id;
	private String username;
	private String email;
	private String password;
	private Boolean isEnabled;
	
	private Set<Role> roles = new HashSet<>();
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<Role> getRoles(){
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public Doctor(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.isEnabled = false;
	}
	public Doctor() {
		this.isEnabled = false;
	}
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	
	
}
