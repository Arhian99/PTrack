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

/* The email service class sends confirmation emails (sendConfirmationEmail() method) with jwt token
 * and link that users click to hit /confirm-email endpoint. In order to confirm email and enable account
 * */

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
	
	// used to load env variables to this class
	Dotenv dotenv = Dotenv.configure().load();
	
	//returns mailSender with appropriate username and password, see .env.example file to set appropriate enviromental variables
	@Autowired
	public EmailService(JavaMailSenderImpl mailSender) {
		mailSender.setUsername(dotenv.get("SPRING_MAIL_USERNAME"));
		mailSender.setPassword(dotenv.get("SPRING_MAIL_PASSWORD"));
		this.mailSender = mailSender;
	}
	
	// sends confirmation email to the user that matches the jwt token that is passed in
	@Async
	public void sendConfirmationEmail(String jwt) {
		/* gets the email from jwt token, then queries db to get either user or doctor that corresponds to the jwt token
		 * if email belongs to user then doctor will be null and vice-versa
		 * */
		User user = userRepo.findByEmail(jwtUtils.getEmailFromJwtToken(jwt)).orElse(null);
		Doctor doctor = docRepo.findByEmail(jwtUtils.getEmailFromJwtToken(jwt)).orElse(null);
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

			// the confirmationPath variable stores the link that will be sent to the user's email.
			String confirmationPath="http://localhost:8080/auth/confirm-account?access_token="+jwt;
			// stores other text that will be sent in the body of the email
			String text="";
			
			// sends email either to user or doctor (whichever one is NOT null)
			if(user != null) {
				// sets the "To" or the recipient's email
				helper.setTo(user.getEmail());
				// This is the body of the email
				text = "Dear " +user.getUsername()+ ", <br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			} else if(doctor != null) {
				// sets the "To" or the recipient's email
				helper.setTo(doctor.getEmail());
				// This is the body of the email
				text = "Dear Dr." +doctor.getUsername()+ ",<br>"
						+ "Please click the link below to verify your account and complete your registration: <br>"
						+ "<h2><a href=\""+confirmationPath+"\" target=\"_self\"> COMPLETE REGISTRATION! </a></h2>"
						+ "Thank you, <br>"
						+ "PTrack.";
			}
			// sets the subject of the email
			helper.setSubject("Complete PTrack Registration!");
			// sets text variable as the body of the email
			helper.setText(text, true);
			// sends the email
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
