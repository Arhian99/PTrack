package com.iterate2infinity.PTrack.security.JWT;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	//TODO: implement so that it sends different errors/ messages depending on the excpetion it catches
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		
		logger.error("Unauthorized error: {}", authException.getMessage());
		
		if(authException.getMessage().equals("Unauthorized error: Bad credentials")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: Unauthorized");
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
		}
		
		
		
	}

}
