package com.iterate2infinity.PTrack.security.JWT;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This class is a Security filter which authenticates users based on JWT token
// This class is inserted into Spring Security's filter chain
public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
	
	// this method parses the JWT token from an HTTP request and returns the JWT token or null if there is none
	private String parseJwt(HttpServletRequest request) {
		String headerAuth= request.getHeader("Authorization");
		
		if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		
		return null;
	}
	
	// This method is the actual security filter and authenticates the user based on the JWT token 
	// returned from the parseJWT() method
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// jwt stores the JWT token associated with the request or null if none exists.
			String jwt = parseJwt(request);
			
			// if there is a JWT token associated with the HTTP request (jwt is not null) AND that jwt token is valid --> authenticate user
			if(jwt!=null && jwtUtils.validateJwtToken(jwt)) {
				// parses email from jwt token
				String email=jwtUtils.getEmailFromJwtToken(jwt);
				// loads userDetails from the email
				UserDetails userDetails=userDetailsService.loadUserByUsername(email);
				// instantiates new UsernamePasswordAuthenticationToken from the userDetails. This object will be used to store currently authenticated user details and set the security context
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// the security context holds the currently authenticated user, the calls below stores the authentication object from above in the security context, completing authentication.
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			
		} catch(Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
		
		// invokes the next filter in the filter chain
		filterChain.doFilter(request, response);
	}

}
