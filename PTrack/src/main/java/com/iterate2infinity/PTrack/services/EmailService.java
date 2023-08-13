package com.iterate2infinity.PTrack.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.models.ConfirmationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private JavaMailSender mailSender;
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Async
	public void sendConfirmationEmail(ConfirmationToken confirmationToken) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			
			//SimpleMailMessage mailMessage = new SimpleMailMessage();
			//String siteURL = "http://localhost:8080/auth";
			String confirmationPath="http://localhost:8080/auth/confirm-account?token="+confirmationToken.getConfirmationToken();
			String text="";
			
			if(confirmationToken.getUser() != null) {
				helper.setTo(confirmationToken.getUser().getEmail());
				text = "Dear " +confirmationToken.getUser().getUsername()+ ", <br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			} else if(confirmationToken.getDoctor() != null) {
				helper.setTo(confirmationToken.getDoctor().getEmail());
				text = "Dear Dr." +confirmationToken.getDoctor().getUsername()+ ",<br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			}
			
			helper.setSubject("Complete PTrack Registration!");

			
			//text.replace("[[URL]]", siteURL+confirmationPath);
			
			helper.setText(text, true);
			sendEmail(mimeMessage);
			
			
		} catch (MessagingException messagingException) {
			logger.error("Email Messaging error: {}", messagingException.getMessage());
		}
		
	}
	
	@Async
	public void sendEmail(MimeMessage email) {
		mailSender.send(email);
	}
}
