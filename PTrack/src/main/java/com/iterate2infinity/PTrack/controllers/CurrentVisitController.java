package com.iterate2infinity.PTrack.controllers;

import java.util.Date;
import java.util.LinkedHashMap;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iterate2infinity.PTrack.ResourceNotFoundException;
import com.iterate2infinity.PTrack.DTOs.StompMessage;
import com.iterate2infinity.PTrack.DTOs.VisitMessageDTO;
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
	
	/*	
	 * STOMP MESSAGE
	 * 
	 * "messageType": <NewVisitRequest, NewVisitResponse, CurrentVisitFinalization, CurrentVisitMessage, Notification>
	 * "sender": <sender username>
	 * "recipient": <recipient username>
	 * "payload":  {
	 * 
	 * 				"locationName": "Test2"
	 * 				"patientUsername": "Arhian90923",
	 * 				"doctorUsername": "DoctorOne:
	 * 
	 * 				}
	 */
	@MessageMapping("/new") // --> /app/currentVisit/new
	public void newVisit(@Payload StompMessage<VisitMessageDTO> message) {
		logger.info("In CurrentVisitController... /new");
		
		// Create new Visit object with provided info and initial status of Pending and save on db
		// Set the patient's currentVisit field with this new visit
		// Set the patient's isInVisit field to true
		// Add the patient to the location's activePatients set
		// Add this new visit to the doctor's currentVisits set
		// Send the patient an UTD patient object with the new visit embedded in the currentVisit field of the patient object
	
		VisitMessageDTO visitMessage = message.getPayload();
		
		Location visitLocation = locationRepo.findByName(visitMessage.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("No location found on database with the name "+visitMessage.getLocationName()));
		User visitPatient = userRepo.findByUsername(visitMessage.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("No patient found on database with the username "+visitMessage.getPatientUsername()));  
		Doctor visitDoctor = doctorRepo.findByUsername(visitMessage.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found on database with the username "+visitMessage.getDoctorUsername()));
		
		Visit newVisit = new Visit(
				new Date(), 
				visitLocation.getName(), 
				visitLocation.getId(), 
				visitPatient.getUsername(),
				visitPatient.getId(), 
				visitDoctor.getUsername(),
				visitDoctor.getId(), 
				EVisitStatus.VISIT_PENDING);	
		
		visitRepo.save(newVisit);
		
		visitPatient.setIsInVisit(true);
		visitPatient.setCurrentVisit(newVisit);
		userRepo.save(visitPatient);

		visitLocation.addActivePatient(visitPatient);
		locationRepo.save(visitLocation);
		
		visitDoctor.addCurrentVisit(newVisit);
		doctorRepo.save(visitDoctor);
	
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", visitDoctor);
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", visitPatient);
	    logger.info(newVisit.getId().toString());
	    logger.info("In CurrentVisitController, message sent!");
	}
	
	/*	
	 * STOMP MESSAGE
	 * 
	 * "messageType": <NewVisitRequest, NewVisitResponse, CurrentVisitFinalization, CurrentVisitMessage, Notification>
	 * "sender": <sender username>
	 * "recipient": <recipient username>
	 * "payload":  {
	 * 
	 * 				"visitID": "Visit ID"
	 * 
	 * 				}
	 */
	@MessageMapping("/accept") // --> /app/currentVisit/accept
	public void acceptVisit(@Payload StompMessage<VisitMessageDTO> message) {
		logger.info("In CurrentVisitController... /accept");

		// change visit status to current and update db
		// update the user and the doctor's currentVisit field with this visit
		// send back the UTD user and doctor objects to the frontend
		
		VisitMessageDTO visitMessage = message.getPayload();
		
		Visit visit = visitRepo.findById(visitMessage.getVisitID()).orElseThrow(() -> new ResourceNotFoundException("Visit not found in database with id: "+visitMessage.getVisitID()));
		
		visit.setStatus(EVisitStatus.VISIT_CURRENT);
		visitRepo.save(visit);

		User visitPatient = userRepo.findByUsername(visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("No patient found with username: "+visit.getPatientUsername()));
		visitPatient.setCurrentVisit(visit);
		userRepo.save(visitPatient);
		
		Doctor visitDoctor = doctorRepo.findByUsername(visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found with username: "+visit.getDoctorUsername()));
//		visitDoctor.acceptVisit(visit);
//		doctorRepo.save(visitDoctor);
		
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", visitPatient);
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", visitDoctor);

	    logger.info("In CurrentVisitController, message sent!");

	}
	
	/*	
	 * STOMP MESSAGE
	 * 
	 * "messageType": <NewVisitRequest, NewVisitResponse, CurrentVisitFinalization, CurrentVisitMessage, Notification>
	 * "sender": <sender username>
	 * "recipient": <recipient username>
	 * "payload":  {
	 * 
	 * 				"visitID": "Visit ID"
	 * 
	 * 				}
	 */
	@MessageMapping("/decline")
	public void declineVisit(@Payload StompMessage<VisitMessageDTO> message) {
		logger.info("In CurrentVisitController... /decline");

		// change visit status to rejected and remove from db
		// set the patient's currentVisit field to null & isInVisit field to false
		// remove visit from the doctor's currentVisit set
		// remove the patient from the location's activePatients set
		VisitMessageDTO visitMessage = message.getPayload();

		Visit visit = visitRepo.findById(visitMessage.getVisitID()).orElseThrow(() -> new ResourceNotFoundException("No visit found with the ID: "+visitMessage.getVisitID()));
		visit.setStatus(EVisitStatus.VISIT_REJECTED);
		
		User visitPatient = userRepo.findByUsername(visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("No patient found with the Username: "+visit.getPatientUsername()));
		visitPatient.setCurrentVisit(null);
		visitPatient.setIsInVisit(false);
		userRepo.save(visitPatient);
		
		Doctor visitDoctor = doctorRepo.findByUsername(visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found with username: "+visit.getDoctorUsername()));
		visitDoctor.removeCurrentVisit(visit);
		doctorRepo.save(visitDoctor);
		
		Location visitLocation = locationRepo.findByName(visit.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("No location found with name: "+visit.getLocationName())); 
		visitLocation.removeActivePatient(visitPatient);
		locationRepo.save(visitLocation);
		
		visitRepo.delete(visit);
		
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", visitPatient);
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", visitDoctor);

	    logger.info("In CurrentVisitController, message sent!");
	}
}
