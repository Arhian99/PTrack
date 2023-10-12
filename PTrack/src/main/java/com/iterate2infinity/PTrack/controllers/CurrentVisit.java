package com.iterate2infinity.PTrack.controllers;

import com.iterate2infinity.PTrack.DTOs.VisitMessage;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/currentVisit")
public class CurrentVisit {

	
	/*	Payload
	 * 	{
	 * 		"from": "userEmail",
	 * 		"patient: "patientEmail",
	 * 		"doctor": "doctorEmail",
	 * 		"location": "locationName"
	 * 	}
	 */
	
	@SendToUser("/queue/currentVisit")
	@MessageMapping("/new")
	public String newVisit(@Payload VisitMessage message) {
		return "New Visit Request from: "+message.getFrom()+" @ "+message.getLocationName();
	}
}
