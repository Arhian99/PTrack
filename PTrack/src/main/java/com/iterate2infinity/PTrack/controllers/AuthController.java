package com.iterate2infinity.PTrack.controllers;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.iterate2infinity.PTrack.DTOs.DoctorDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO_Doctor;
import com.iterate2infinity.PTrack.DTOs.RoleDTO;
import com.iterate2infinity.PTrack.DTOs.UserDTO;
import com.iterate2infinity.PTrack.models.ConfirmationToken;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.ConfirmationTokenRepository;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.RoleRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;
import com.iterate2infinity.PTrack.security.JWT.JwtUtils;
import com.iterate2infinity.PTrack.security.services.UserDetailsImpl;
import com.iterate2infinity.PTrack.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired 
	UserRepository userRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Autowired
	ConfirmationTokenRepository confirmationTokenRepo;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	EmailService emailService;
	
	@PostMapping("/login/user")
	public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.get("email"), loginRequest.get("password")));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		User user = userRepo.findByEmail(userDetails.getEmail()).orElse(null);
		JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, user);
		return ResponseEntity.ok(jwtResponse);
		
		
		
	}
	
	@PostMapping("/login/doctor")
	public ResponseEntity<?> authenticateDoctor(@RequestBody Map<String, String> loginRequest){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.get("email"), loginRequest.get("password")));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Doctor doctor = doctorRepo.findByEmail(userDetails.getEmail()).orElse(null);
		JwtResponseDTO_Doctor jwtResponse = new JwtResponseDTO_Doctor(jwt, doctor);
		return ResponseEntity.ok(jwtResponse);
	}
	
	@PostMapping("/save/doctor")
	public ResponseEntity<?> saveDoc(@RequestBody DoctorDTO doctor){
		if(doctorRepo.existsByEmail(doctor.getEmail())) {
			return new ResponseEntity<>("Error: Email is already in use! Please login", HttpStatus.BAD_REQUEST);
		}
		
		if(doctorRepo.existsByUsername(doctor.getUsername())) {
			return new ResponseEntity<>("Error: Username is already in taken!", HttpStatus.BAD_REQUEST);
		}
		
		Doctor newDoc = new Doctor(doctor.getUsername(), doctor.getEmail(), encoder.encode(doctor.getPassword()));
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_DOCTOR).orElse(null));
		newDoc.setRoles(roles);
		
		doctorRepo.save(newDoc);
		ConfirmationToken confirmationToken = new ConfirmationToken(newDoc);
		confirmationTokenRepo.save(confirmationToken);
		
		emailService.sendConfirmationEmail(confirmationToken);
		
		//TODO: Authneticate, set security context, generate jwt token and return jwt response with 201 status to the frontend.
		return new ResponseEntity<>(newDoc, HttpStatus.CREATED);
	}
	
	@PostMapping("/save/user")
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user){
		if(userRepo.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>("Error: Email is already in use! Please login", HttpStatus.BAD_REQUEST);
		}
		
		if(userRepo.existsByUsername(user.getUsername())) {
			return new ResponseEntity<>("Error: Username is already in taken!", HttpStatus.BAD_REQUEST);
		}
		
		User newUser = new User(user.getUsername(), user.getEmail(), encoder.encode(user.getPassword()));
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_USER).orElse(null));
		newUser.setRoles(roles);
		
		userRepo.save(newUser);
		
		ConfirmationToken confirmationToken = new ConfirmationToken(newUser);
		confirmationTokenRepo.save(confirmationToken);
		
		emailService.sendConfirmationEmail(confirmationToken);
		
		//TODO: Authenticate, set the SecurityContext, generate jwt token and return jwt response with 201 status to the frontend.
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}
	
	@RequestMapping("/confirm-account")
	public ResponseEntity<?> confirmAccount(@RequestParam("token") String confirmationToken){
		ConfirmationToken token = confirmationTokenRepo.findByConfirmationToken(confirmationToken).orElse(null);
		
		//check if token is expired, if expired return BAD_REQUEST error
		Calendar cal = Calendar.getInstance();
		if(token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
			return new ResponseEntity<>("Error: Token is Expired. Unable to verify account.", HttpStatus.BAD_REQUEST);
		}
		
		if(token != null && token.getUser() != null) {
			User user = userRepo.findByEmail(token.getUser().getEmail()).orElse(null);
			user.setIsEnabled(true);
			userRepo.save(user);
			
			
//			UserDetailsImpl userDetails = UserDetailsImpl.build(user);
//			Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword(), userDetails.getAuthorities());
//			
//			SecurityContextHolder.getContext().setAuthentication(auth);
//			String jwt = jwtUtils.generateJwtToken(auth);
//			JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, user);
			
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, user);
			
			
			return ResponseEntity.ok(jwtResponse);
			
		} else if(token != null && token.getDoctor() != null) {
			Doctor doctor = doctorRepo.findByEmail(token.getDoctor().getEmail()).orElse(null);
			doctor.setIsEnabled(true);
			doctorRepo.save(doctor);
			
			UserDetailsImpl userDetails = UserDetailsImpl.build(doctor);
			Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getEmail(), userDetails.getPassword(), userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			String jwt = jwtUtils.generateJwtToken(auth);
			JwtResponseDTO_Doctor jwtResponse = new JwtResponseDTO_Doctor(jwt, doctor);
			
			return ResponseEntity.ok(jwtResponse);
			
		}
		
		return new ResponseEntity<>("Error: Token is invalid. Unable to verify account.", HttpStatus.BAD_REQUEST);
	}
	
	// TODO: Move to admin controller, and ensure this is a private endpoint where only USER_ADMIN can post to
	@PostMapping("/save/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> saveRole(@RequestBody RoleDTO role){
		ERole newERole = ERole.valueOf(role.getName());
		Role newRole = new Role(newERole);
		roleRepo.save(newRole);
		return new ResponseEntity<>(newRole, HttpStatus.CREATED);
	}
	
	
}
	