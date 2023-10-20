package com.iterate2infinity.PTrack.controllers;


import java.util.HashMap;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// This api controllers takes GET requests and returns single doctor or single user objects as decided by the email that is passed in
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DoctorRepository doctorRepo;
	
	@GetMapping("/user")
	//@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> getUser(@RequestParam("username")String username) {
		User user = userRepo.findByUsername(username).orElse(null);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/doctor")
	//@PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getDoctor(@RequestParam("username")String username){
		Doctor doctor = doctorRepo.findByUsername(username).orElse(null);
		return ResponseEntity.ok(doctor);
	}
	
	
	// TODO: Remove this endpoint or move to Admin controller, for debugging purposes only
	@PostMapping("/resetVisits")
	public ResponseEntity<?> resetVisits(){
		Doctor doctor = doctorRepo.findByUsername("DoctorOne").orElse(null);
		User patient = userRepo.findByUsername("Arhian99").orElse(null);
		
		doctor.clearCurrentVisits();
		patient.setIsInVisit(false);
		patient.setCurrentVisit(null);
		
		doctorRepo.save(doctor);
		userRepo.save(patient);
		
		return ResponseEntity.ok("Cleared");
	}

}

