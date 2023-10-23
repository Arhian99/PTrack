package com.iterate2infinity.PTrack.models;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="doctors")
public class Doctor {
	
	@Id
	private ObjectId id;
	private String username;
	private String email;
	private String password;
	private Boolean isEnabled;
	private Boolean isCheckedIn;
	@DBRef
	private Location currentLocation;
	@DBRef
	private Set<Role> roles = new HashSet<>();
	@DBRef
	private HashSet<Visit> currentVisits = new HashSet<>();
	
	public void acceptVisit(Visit updatedVisit) {
		currentVisits.forEach(v -> {
			if(v.equals(updatedVisit)) {
				removeCurrentVisit(v);
				addCurrentVisit(updatedVisit);
			}
		});
	}
	
	public void addCurrentVisit(Visit visit){
		currentVisits.add(visit);
	}
	
	public void removeCurrentVisit(Visit visit) {
		currentVisits.remove(visit);
	}
	
	public void clearCurrentVisits() {
		currentVisits.clear();
	}
	public HashSet<Visit> getCurrentVisits() {
		return currentVisits;
	}

	public void setCurrentVisits(HashSet<Visit> currentVisits) {
		this.currentVisits = currentVisits;
	}

	public Boolean getIsCheckedIn() {
		return isCheckedIn;
	}
	
	public void setIsCheckedIn(Boolean isCheckedIn) {
		this.isCheckedIn = isCheckedIn;
	}
	
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
		this.isCheckedIn = false;
		this.currentLocation = null;
	}
	public Doctor() {
		this.isEnabled = false;
		this.isCheckedIn = false;
		this.currentLocation = null;
	}
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
