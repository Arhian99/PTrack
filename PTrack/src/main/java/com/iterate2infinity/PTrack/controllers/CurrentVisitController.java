package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.iterate2infinity.PTrack.DTOs.StompMessage;
import com.iterate2infinity.PTrack.DTOs.VisitMessageDTO;
import com.iterate2infinity.PTrack.services.DoctorService;
import com.iterate2infinity.PTrack.services.LocationService;
import com.iterate2infinity.PTrack.services.StompService;
import com.iterate2infinity.PTrack.services.UserService;
import com.iterate2infinity.PTrack.services.VisitService;

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
	LocationService locationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	DoctorService doctorService;
	
	@Autowired
	VisitService visitService;
	
	@Autowired
	StompService stompService;
	
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
	 * 				"locationName": "Test2",
	 * 				"patientUsername": "Arhian90923",
	 * 				"doctorUsername": "DoctorOne",
	 * 				"visitID" : "23842034945645"
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
	
		VisitMessageDTO visitDTO = stompService.getPayload(message);
		HashMap<String, Object> stakeHolders = visitService.newVisitStomp(visitDTO);
		
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", stakeHolders.get("visitDoctor"));
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", stakeHolders.get("visitPatient"));

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
	 * 				"locationName": "Test2",
	 * 				"patientUsername": "Arhian90923",
	 * 				"doctorUsername": "DoctorOne",
	 * 				"visitID" : "23842034945645"
	 * 
	 * 				}
	 */
	@MessageMapping("/accept") // --> /app/currentVisit/accept
	public void acceptVisit(@Payload StompMessage<VisitMessageDTO> message) {
		logger.info("In CurrentVisitController... /accept");

		// change visit status to current and update db
		// update the user and the doctor's currentVisit field with this visit
		// send back the UTD user and doctor objects to the frontend
		
		VisitMessageDTO visitDTO = stompService.getPayload(message);
		HashMap<String, Object> stakeHolders = visitService.acceptVisitStomp(visitDTO);
		
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", stakeHolders.get("visitPatient"));
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", stakeHolders.get("visitDoctor"));

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
		VisitMessageDTO visitDTO = stompService.getPayload(message);
		HashMap<String, Object> stakeHolders = visitService.declineVisitStomp(visitDTO);
		
	    simpMessagingTemplate.convertAndSendToUser(message.getRecipientUsername(), "/queue/currentVisit/new", stakeHolders.get("visitPatient"));
	    simpMessagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/currentVisit/new", stakeHolders.get("visitDoctor"));

	    logger.info("In CurrentVisitController, message sent!");
	}
}
