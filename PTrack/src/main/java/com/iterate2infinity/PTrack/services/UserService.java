package com.iterate2infinity.PTrack.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.UserDTO;
import com.iterate2infinity.PTrack.ExceptionHandling.AlreadyExistsException;
import com.iterate2infinity.PTrack.ExceptionHandling.ResourceNotFoundException;
import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.RoleRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;
import com.iterate2infinity.PTrack.security.JWT.JwtUtils;
import com.iterate2infinity.PTrack.security.services.UserDetailsImpl;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private EmailService emailService;
	
	public List<User> getAll(){
		return userRepo.findAll();
	}
	
	public void save(User user) {
		userRepo.save(user);
	}
	
	public Optional<User> find(String method, String principal) {
		switch(method) {
		case "byUsername": return userRepo.findByUsername(principal);
		case "byEmail": return userRepo.findByEmail(principal);
		default: return null;
		}
	}
	
	public boolean exists(String method, String principal) {
		switch(method) {
		case "byUsername": return userRepo.existsByUsername(principal);
		case "byEmail": return userRepo.existsByEmail(principal);
		case "byJwt": {
			String email = jwtUtils.getEmailFromJwtToken(principal);
			return exists("byEmail", email);
		}
		default: return false;
		}
	}
	
	public JwtResponseDTO<User> login(UserDTO user) {
		/* 
		 * uses the passed in credentials (email and password) to create UsernamePasswordAuthenticationToken
		 * which is passed in to the authentication manager's authenticate() method. This method calls the 
		 * the authentication providers and returns either a fully authenticated Authentication object OR 
		 * throws an exception. 
		 */
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

		/*
		 * The fully authenticated Authentication object (which holds the authenticated user's principal) is 
		 * stored in security context holder (which holds the currently authenticated user's Authentication object)
		 */
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// jwt token is generated for the authenticated user using the authentication object
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		/*
		 * User details are retrieved from inside the authentication object (Authentication object can hold either 
		 * UserDetails as the Principal for that user (if the user has been authenticated) OR it can hold a user credentials
		 * and nothing in the principal field if the user has not been authenticated.
		 */ 
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		// Retrieve user object from database 
		User authUser = find("byEmail", userDetails.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email"+userDetails.getEmail()));
		// Create instance of JwtResponseDTO with user object and the jwt token to send to the front end.
		return new JwtResponseDTO<User>(jwt, authUser);
	}
	
	public JwtResponseDTO<User> register(UserDTO user){
		if(exists("byEmail", user.getEmail())) throw new AlreadyExistsException("Error: Email is already in use! Please login");
		if(exists("byUsername", user.getUsername())) throw new AlreadyExistsException("Error: Username is already in use! Please login");
		
		User newUser = new User(user.getUsername(), user.getEmail(), encoder.encode(user.getPassword()));
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_USER).orElseThrow(() -> new ResourceNotFoundException("No role found with specified name")));
		newUser.setRoles(roles);
		
		save(newUser);
		
		String jwt = jwtUtils.generateJwtToken(newUser);
		
		emailService.sendConfirmationEmail(jwt);
		
		return new JwtResponseDTO<User>(jwt, newUser);
	}
	
	public User enable(String confirmationToken){
		String email = jwtUtils.getEmailFromJwtToken(confirmationToken);
		User user = find("byEmail", email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+email));
		user.setIsEnabled(true);
		save(user);
		return user;
	}
}
