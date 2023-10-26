package com.iterate2infinity.PTrack.DTOs;

public class StompMessage<T> {
	private String messageType;
	private String senderUsername;
	private String recipientUsername;
	private T payload;
	
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public T getPayload() {
		return payload;
	}
	public void setPayload(T payload) {
		this.payload = payload;
	}
	public String getSenderUsername() {
		return senderUsername;
	}
	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}
	public String getRecipientUsername() {
		return recipientUsername;
	}
	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}
	public StompMessage(String messageType, String senderUsername, String recipientUsername, T payload) {
		super();
		this.messageType = messageType;
		this.senderUsername = senderUsername;
		this.recipientUsername = recipientUsername;
		this.payload = payload;
	}
	@Override
	public String toString() {
		return "StompMessage [messageType=" + messageType + ", senderUsername=" + senderUsername
				+ ", recipientUsername=" + recipientUsername + ", payload=" + payload + "]";
	}

	
	
	
	
	
}
