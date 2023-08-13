package com.iterate2infinity.PTrack.models;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="confirmationTokens")
public class ConfirmationToken {
	@Id
	private ObjectId tokenId;
	
	private String confirmationToken;
	
	private Date createdDate;
	
	private Date expiryDate;
	
	@DBRef
	private User user;

	@DBRef
	private Doctor doctor;
	
	private static final int EXPIRATION = 12; // (12 hours)
	
	public ObjectId getTokenId() {
		return tokenId;
	}

	public void setTokenId(ObjectId tokenId) {
		this.tokenId = tokenId;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public ConfirmationToken() {
		this.user=null;
		this.doctor=null;
		createdDate= Calendar.getInstance().getTime();
		expiryDate = calculateExpiryDate();
		confirmationToken= UUID.randomUUID().toString();
	}
	
	public ConfirmationToken(User user) {
		this.user = user;
		this.doctor=null;
		createdDate = Calendar.getInstance().getTime();
		expiryDate = calculateExpiryDate();
		confirmationToken = UUID.randomUUID().toString();
	}
	
	public ConfirmationToken(Doctor doctor) {
		this.user = null;
		this.doctor=doctor;
		createdDate = Calendar.getInstance().getTime();
		expiryDate = calculateExpiryDate();
		confirmationToken = UUID.randomUUID().toString();
	}
	
	private Date calculateExpiryDate() {
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(createdDate);
		tempCal.add(Calendar.HOUR_OF_DAY, EXPIRATION);
		expiryDate=tempCal.getTime();
		return expiryDate;
	}
	
}
