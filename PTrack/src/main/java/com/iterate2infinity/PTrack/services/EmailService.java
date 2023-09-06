package com.iterate2infinity.PTrack.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;
import com.iterate2infinity.PTrack.security.JWT.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DoctorRepository docRepo;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private JavaMailSenderImpl mailSender;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	Dotenv dotenv = Dotenv.configure().load();
	
	@Autowired
	public EmailService(JavaMailSenderImpl mailSender) {
		mailSender.setUsername(dotenv.get("SPRING_MAIL_USERNAME"));
		mailSender.setPassword(dotenv.get("SPRING_MAIL_PASSWORD"));
		this.mailSender = mailSender;
	}
	
	@Async
	public void sendConfirmationEmail(String jwt) {
		User user = userRepo.findByEmail(jwtUtils.getEmailFromJwtToken(jwt)).orElse(null);
		Doctor doctor = docRepo.findByEmail(jwtUtils.getEmailFromJwtToken(jwt)).orElse(null);
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

			String confirmationPath="http://localhost:8080/auth/confirm-account?access_token="+jwt;
			String text="";
			
			
			
			if(user != null) {
				helper.setTo(user.getEmail());
				text = "Dear " +user.getUsername()+ ", <br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			} else if(doctor != null) {
				helper.setTo(doctor.getEmail());
				text = "Dear Dr." +doctor.getUsername()+ ",<br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			}
			
			helper.setSubject("Complete PTrack Registration!");
			
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
