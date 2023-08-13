package com.iterate2infinity.PTrack.DTOs;

import com.iterate2infinity.PTrack.models.Doctor;

public class JwtResponseDTO_Doctor {

	
	private String jwt;
	private Doctor doctor; // named user for frontend homogeneity
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	
	public JwtResponseDTO_Doctor(String jwt, Doctor doctor) {
		super();
		this.jwt = jwt;
		this.doctor = doctor;
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public JwtResponseDTO_Doctor() {
	}
}
