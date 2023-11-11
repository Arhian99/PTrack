package com.iterate2infinity.PTrack.controllers;

import java.util.List;
import com.iterate2infinity.PTrack.DTOs.VisitMessageDTO;
import com.iterate2infinity.PTrack.models.Visit;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
public class VisitController {
	private static final Logger logger = LoggerFactory.getLogger(VisitController.class);

	@Autowired
	VisitService visitService;
	
	@Autowired
	LocationService locationService;
	
	@Autowired
	DoctorService doctorService;
	
	@Autowired
	UserService userService;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllVisits() {
		logger.info("In Visit Controller /all");
		List<Visit> allVisits = visitService.getAll();
		
		return ResponseEntity.ok(allVisits);
	}
	
	@GetMapping("/byPatient")
	public ResponseEntity<?> byPatient(@RequestParam("patient") String patientUsername){
		logger.info("In Visit Controller /byPatient");
		List<Visit> ptVisits = visitService.getPatientVisits(patientUsername);
		return ResponseEntity.ok(ptVisits);
	}
	
	@GetMapping("/byDoctor")
	public ResponseEntity<?> byDoctor(@RequestParam("doctor") String doctorUsername){
		logger.info("In Visit Controller /byDoctor");
		List<Visit> doctorVisits = visitService.getDoctorVisits(doctorUsername);

		return ResponseEntity.ok(doctorVisits);
	}
	

	@PostMapping("/new")
	public ResponseEntity<?> saveVisit(@RequestBody VisitMessageDTO visit){
		Visit newVisit = visitService.newVisit(visit);
		return ResponseEntity.ok(newVisit);
	}
	
}
