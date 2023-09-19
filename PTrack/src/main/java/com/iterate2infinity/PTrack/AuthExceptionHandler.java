package com.iterate2infinity.PTrack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//This class catches exceptions thrown by controllers and handles them accordingly
@ControllerAdvice
public class AuthExceptionHandler {
	// Catches BadCredentialsException and returns 400 response to the front end with text displayed below
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleCredentialsException(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect email or password."); //400
	}
	
	// Catches DisabledException and returns 401 response to the front end with text displayed below
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<?> handleDisabledException(DisabledException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is disabled."); //401
	}
	

}
