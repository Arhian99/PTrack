package com.iterate2infinity.PTrack.controllers;

import java.util.List;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Admin API endpoint recieves GET requests and returns list of ALL users and ALL doctors
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DoctorRepository doctorRepo;
	
	@GetMapping("/getAllUsers")
	public List<User> getAllUsers(){
		return userRepo.findAll();
	}
	
	@GetMapping("/getAllDoctors")
	public List<Doctor> getAllDoctors(){
		return doctorRepo.findAll();
	}
	
}
