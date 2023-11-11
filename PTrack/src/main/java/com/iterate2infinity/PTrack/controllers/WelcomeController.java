package com.iterate2infinity.PTrack.controllers;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.services.DoctorService;
import com.iterate2infinity.PTrack.services.LocationService;
import com.iterate2infinity.PTrack.services.UserService;
import com.iterate2infinity.PTrack.services.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// This api controllers takes GET requests and returns single doctor or single user objects as decided by the email that is passed in
@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {
	private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private DoctorService doctorService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired 
	VisitService visitService;
	
	@GetMapping("/user")
	public ResponseEntity<?> getUser(@RequestParam("username") String username) {
		User user = userService.find("byUsername", username).orElse(null);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/doctor")
	public ResponseEntity<?> getDoctor(@RequestParam("username")String username){
		Doctor doctor = doctorService.find("byUsername", username).orElse(null);
		return ResponseEntity.ok(doctor);
	}
	
	
	// TODO: Remove this endpoint or move to Admin controller, for debugging purposes only
	@PostMapping("/resetVisits")
	public ResponseEntity<?> resetVisits(){
		Doctor doctor = doctorService.find("byUsername", "DoctorOne").orElse(null);
		User patient = userService.find("byUsername", "Arhian99").orElse(null);
		Location location = locationService.find("byName", "Test3").orElse(null);
		
		doctor.clearCurrentVisits();
		patient.setIsInVisit(false);
		patient.setCurrentVisit(null);
		location.clearActivePatients();
		
		visitService.deleteAll();
		locationService.save(location);
		doctorService.save(doctor);
		userService.save(patient);
		
		logger.info("Resetting current visits for DoctorOne and Arhian99...");
		logger.info("Clearing active patients at location Test3...");
		logger.info("Deleting all visits on database...");

		return ResponseEntity.ok("Cleared");
	}

}

