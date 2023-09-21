package com.iterate2infinity.PTrack.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.models.Visit;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.LocationRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;
import com.iterate2infinity.PTrack.repos.VisitRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/visits")
public class VisitController {
	@Autowired
	VisitRepository visitRepo;
	
	@Autowired
	LocationRepository locationRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllVisits() {
		List<Visit> allVisits = visitRepo.findAll();
		return ResponseEntity.ok(allVisits);
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> saveVisit(@RequestBody HashMap<String, String> request){
		Location visitLocation = locationRepo.findByName(request.get("location")).orElse(null);
		Doctor visitDoctor = doctorRepo.findByEmail(request.get("email")).orElse(null);
		User visitPatient = userRepo.findByEmail(request.get("patient")).orElse(null);
		
		
		if(visitLocation.equals(null)) {
			return ResponseEntity.badRequest().body("Error: Visit location not found in database.");
		} else if(visitDoctor.equals(null)) {
			return ResponseEntity.badRequest().body("Error: Visit doctor not found in database.");
		} else if(visitPatient.equals(null)) {
			return ResponseEntity.badRequest().body("Error: Visit patient not found in database.");
		}
		
		Visit newVisit = new Visit(new Date(), visitLocation, visitPatient, visitDoctor);
		
		return ResponseEntity.ok(newVisit);
		// check if location exits if not send 400 response. if so ...
		// retrieve location from db 
		// check if patient exits if not send 400 response. if so ...
		// retrieve patient from db
		// check if doctor exits if not send 400 response. if so ...
		// retrieve doctor from db 
		// Instantiate new Visit object (new Date, location, patient, doctor)
		// save new Visit object to db
		// return 200 reponse with new visit object in the body
		
	}
}
