package com.iterate2infinity.PTrack.services;

import org.springframework.stereotype.Service;

import com.iterate2infinity.PTrack.DTOs.StompMessage;

@Service
public class StompService {
	
	
	public <T> T getPayload(StompMessage<T> message) {
		T payload = message.getPayload();
		return payload;
	}
	
	public String getRecipient(StompMessage<?> message) {
		return message.getRecipientUsername();
	}
	
	public String getSender(StompMessage<?> message) {
		return message.getSenderUsername();
	}
}
