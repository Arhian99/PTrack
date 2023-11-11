package com.iterate2infinity.PTrack.DTOs;

//POJO used (as a model) to send data to the frontend (sends JWT along with User object)
public class JwtResponseDTO<T> {
	private String jwt;
	private T user;
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	
	public JwtResponseDTO(String jwt, T user) {
		super();
		this.jwt = jwt;
		this.user = user;
	}
	public T getUser() {
		return user;
	}
	public void setUser(T user) {
		this.user = user;
	}
	public JwtResponseDTO() {
	}
	
}
