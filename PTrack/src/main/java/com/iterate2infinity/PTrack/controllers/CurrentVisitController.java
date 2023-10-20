package com.iterate2infinity.PTrack.controllers;

import java.security.Principal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.ResourceNotFoundException;
import com.iterate2infinity.PTrack.DTOs.VisitMessage;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.EVisitStatus;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.models.Visit;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.LocationRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;
import com.iterate2infinity.PTrack.repos.VisitRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.*;


@Controller
@MessageMapping("/currentVisit")
public class CurrentVisitController {
	private static final Logger logger = LoggerFactory.getLogger(CurrentVisitController.class);
	
	@Autowired
	LocationRepository locationRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Autowired
	VisitRepository visitRepo;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	/*	Payload
	 * 	{
	 * 		"from": "patient username",
	 * 		"to": "doctor username",
	 * 		"patientEmail: "patient Email",
	 * 		"doctorUsername": "doctor username",
	 * 		"locationName": "location Name"
	 * 	}
	 */
	@MessageMapping("/new") // --> /app/currentVisit/new
	public void newVisit(@Payload VisitMessage message, Principal principal) {
		logger.info("In CurrentVisitController... /new");

		
		Location visitLocation = locationRepo.findByName(message.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("No location found on database with the name "+message.getLocationName()));
		User visitPatient = userRepo.findByEmail(message.getPatientEmail()).orElseThrow(() -> new ResourceNotFoundException("No patient found on database with the email "+message.getPatientEmail()));  
		Doctor visitDoctor = doctorRepo.findByUsername(message.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found on database with the username "+message.getDoctorUsername()));
//		String to=message.getTo();
//		String from = message.getFrom();
		EVisitStatus visitStatus = EVisitStatus.VISIT_PENDING;

		
		Visit newVisit = new Visit(
				new Date(), 
				visitLocation.getName(), 
				visitLocation.getId(), 
				visitPatient.getUsername(),
				visitPatient.getId(), 
				visitDoctor.getUsername(),
				visitDoctor.getId(), 
				visitStatus);	
		
		visitRepo.save(newVisit);
		
		visitPatient.setIsInVisit(true);
		visitPatient.setCurrentVisit(newVisit);
		userRepo.save(visitPatient);

		visitLocation.addActivePatient(visitPatient);
		locationRepo.save(visitLocation);
		
		visitDoctor.addCurrentVisit(newVisit);
		doctorRepo.save(visitDoctor);
	
	    simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/queue/currentVisit/new", visitDoctor);
	    simpMessagingTemplate.convertAndSendToUser(message.getFrom(), "/queue/currentVisit/new", visitPatient);

	    logger.info("In CurrentVisitController, message sent!");
	}
}
