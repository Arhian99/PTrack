package com.iterate2infinity.PTrack.DTOs;

//POJO used (as a model) to receive data from the frontend
public class RoleDTO {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoleDTO(String name) {
		super();
		this.name = name;
	}

	public RoleDTO() {
	}
	
	
}
