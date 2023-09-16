package com.iterate2infinity.PTrack.DTOs;

import org.bson.types.ObjectId;

public class UserDTO {
	private ObjectId id;
	private String username;
	private String email;
	private String password;
	private Boolean isEnabled;
	
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
	public UserDTO(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.isEnabled=false;
	}
	public UserDTO() {
		this.isEnabled=false;
	}

	public String toString() {
		return "UserDTO [id=" + id + ", username=" + username + ", email=" + email + ", isEnabled=" + isEnabled + "]";
	}
	
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	
	
	
}
