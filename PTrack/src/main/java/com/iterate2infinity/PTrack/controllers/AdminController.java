package com.iterate2infinity.PTrack.controllers;

import java.util.List;

import com.iterate2infinity.PTrack.DTOs.RoleDTO;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.services.DoctorService;
import com.iterate2infinity.PTrack.services.RoleService;
import com.iterate2infinity.PTrack.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Admin API endpoint recieves GET requests and returns list of ALL users and ALL doctors
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	@Autowired
	private UserService userService;

	@Autowired
	private DoctorService doctorService;
	
	@Autowired
	RoleService roleService;

	@Autowired
	@GetMapping("/getAllUsers")
	public List<User> getAllUsers(){
		return userService.getAll();
	}
	
	@GetMapping("/getAllDoctors")
	public List<Doctor> getAllDoctors(){
		return doctorService.getAll();
	}
	
	@PostMapping("/save/role")
	public ResponseEntity<?> saveRole(@RequestBody RoleDTO role){
		Role newRole = roleService.register(role);
		return new ResponseEntity<>(newRole, HttpStatus.CREATED);
	}
	
}
