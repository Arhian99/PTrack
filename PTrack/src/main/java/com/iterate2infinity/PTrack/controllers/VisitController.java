package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;
import java.util.HashSet;

import com.iterate2infinity.PTrack.models.Visit;
import com.iterate2infinity.PTrack.repos.VisitRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
public class VisitController {
	@Autowired
	VisitRepository visitRepo;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllVisits() {
		HashSet<Visit> allVisits = (HashSet<Visit>) visitRepo.findAll();
		return ResponseEntity.ok(allVisits);
	}
	
	@PostMapping("/new")
	public void /*ResponseEntity<?>*/ saveVisit(@RequestBody HashMap<String, String> request){
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
