package com.iterate2infinity.PTrack.controllers;

import com.iterate2infinity.PTrack.DTOs.VisitMessage;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
//@MessageMapping("/currentVisit")
public class CurrentVisitController {

	
	/*	Payload
	 * 	{
	 * 		"from": "userEmail",
	 * 		"patient: "patientEmail",
	 * 		"doctor": "doctorEmail",
	 * 		"location": "locationName"
	 * 	}
	 */
	
//	@SendToUser("/queue/currentVisit")
//	@MessageMapping("/new")
//	public String newVisit(@Payload VisitMessage message) {
//		return "New Visit Request from: "+message.getFrom()+" @ "+message.getLocationName();
//	}
	@MessageMapping("/currentVisit") //   /app/currentVisit/new
	@SendToUser("/queue/currentVisit")
	public String newVisit(@Payload VisitMessage message) {
		return "New Visit Request from: "+message.getFrom();
	}
}
