package com.iterate2infinity.PTrack.security.JWT;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
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

/* This class defines utility methods for everything related to JWT tokens */
@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	static Dotenv dotenv = Dotenv.configure().load();
	
	// stores the secret key used to sign JWT's
	private String jwtSecret=dotenv.get("JWT_SECRET");
	
	// stores how long JWT's last before expiring
	private int jwtExpirationMs=Integer.parseInt(dotenv.get("JWT_EXP"));
	
	// This method generates new JWT token from an authentication object
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = new UserDetailsImpl();
		String userEmail="";
		
		/* an authentication object will hold userDetails object in the principal field if it has already
		 *  been authenticated or simply the user credentials if it has NOT been authenticated
		 */
		if(authentication.getPrincipal().getClass() == userPrincipal.getClass()) {
			userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			userEmail=userPrincipal.getEmail();
		} else {
			userEmail = (String) authentication.getPrincipal();
		}
		// builds JWT token with user email, current date, expiration date, and signs with provided secret
		return Jwts.builder()
				.setSubject(userEmail)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	// This method builds jwt token from a user object
	public String generateJwtToken(User user) {
		return Jwts.builder()
				.setSubject(user.getEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	// This method builds jwt token from a doctor object
	public String generateJwtToken(Doctor doc) {
		return Jwts.builder()
				.setSubject(doc.getEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	// this method parses an email from a jwt token
	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	// this method ensures the token passed in is a valid JWT token by checking against secret key, expiration date, and other claims
	// returns true if valid, false otherwise
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
