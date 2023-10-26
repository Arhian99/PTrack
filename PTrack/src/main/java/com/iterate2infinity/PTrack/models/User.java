package com.iterate2infinity.PTrack.models;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {
	
	@Id
	private String id;
	private String username;
	private String email;
	private String password;
	private Boolean isEnabled;
	private Boolean isInVisit;
	
	@DBRef
	private Visit currentVisit;
	
	@DBRef
	private Location currentLocation;

	@DBRef
	private Set<Role> roles= new HashSet<>();
		
	public Boolean getIsInVisit() {
		return isInVisit;
	}
	
	public void setIsInVisit(Boolean isInVisit) {
		this.isInVisit = isInVisit;
	}
	
	public String getId() {
		return id;
	}
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.isEnabled = false;
		this.isInVisit = false;
		this.currentVisit = null;
		this.currentLocation = null;
	}
	
	public User() {
		this.isEnabled = false;
		this.isInVisit = false;
		this.currentVisit = null;
		this.currentLocation = null;
	}
	
	public Visit getCurrentVisit() {
		return currentVisit;
	}
	public void setCurrentVisit(Visit currentVisit) {
		this.currentVisit = currentVisit;
	}
	public void setId(String id) {
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
//	public User(String username, String email, String password) {
//		super();
//		this.username = username;
//		this.email = email;
//		this.password = password;
//		this.isEnabled=false;
//		this.isInVisit = false;
//		this.currentLocation = null;
//
//	}
//	public User() {
//		this.isEnabled=false;
//		this.isInVisit=false;
//		this.currentLocation = null;
//	}
	
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@JsonManagedReference
	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	
}
