package com.iterate2infinity.PTrack.DTOs;

import com.iterate2infinity.PTrack.models.User;

//POJO used (as a model) to send data to the frontend (sends JWT along with User object)
public class JwtResponseDTO {
	private String jwt;
	private User user;
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	
	public JwtResponseDTO(String jwt, User user) {
		super();
		this.jwt = jwt;
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public JwtResponseDTO() {
	}
	
}
