package com.iterate2infinity.PTrack;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleCredentialsException(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect email or password."); //400
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<?> handleDisabledException(DisabledException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is disabled."); //401
	}
	

}
