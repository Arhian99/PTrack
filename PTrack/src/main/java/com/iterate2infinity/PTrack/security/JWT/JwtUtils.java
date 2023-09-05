package com.iterate2infinity.PTrack.security.JWT;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.security.services.UserDetailsImpl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	static Dotenv dotenv = Dotenv.configure().load();
	
	private String jwtSecret=dotenv.get("JWT_SECRET");
	
	private int jwtExpirationMs=Integer.parseInt(dotenv.get("JWT_EXP"));
	
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = new UserDetailsImpl();
		String userEmail="";
		
		if(authentication.getPrincipal().getClass() == userPrincipal.getClass()) {
			userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			userEmail=userPrincipal.getEmail();
		} else {
			userEmail = (String) authentication.getPrincipal();
		}
		return Jwts.builder()
				.setSubject(userEmail)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch(SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch(MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch(ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch(UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch(IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		
		return false;
	}
}
