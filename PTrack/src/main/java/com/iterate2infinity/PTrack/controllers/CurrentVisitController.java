package com.iterate2infinity.PTrack.controllers;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.DTOs.VisitMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
//@MessageMapping("/currentVisit")
public class CurrentVisitController {
	private static final Logger logger = LoggerFactory.getLogger(CurrentVisitController.class);

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	/*	Payload
	 * 	{
	 * 		"from": "username",
	 * 		"to": "username",
	 * 		"patient: "patientEmail",
	 * 		"doctor": "doctorEmail",
	 * 		"location": "locationName"
	 * 	}
	 */
	

	@MessageMapping("/currentVisit") // --> /app/currentVisit
	public void newVisit(@Payload VisitMessage message, Principal principal) {
		
		String to=message.getTo();
		String msg= "New Visit Request from: "+message.getFrom();
		
		logger.info("In CurrentVisitController: to: "+to);
		logger.info("In CurrentVisitController: msg: "+msg);
	
	    simpMessagingTemplate.convertAndSendToUser(to, "/queue/currentVisit", msg);
	    logger.info("In CurrentVisitController, message sent!");
	}
}
