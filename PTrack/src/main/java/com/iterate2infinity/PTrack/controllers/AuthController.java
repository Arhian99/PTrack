package com.iterate2infinity.PTrack.controllers;

import java.util.HashSet;
import java.util.Set;

import com.iterate2infinity.PTrack.DTOs.DoctorDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO_Doctor;
import com.iterate2infinity.PTrack.DTOs.RoleDTO;
import com.iterate2infinity.PTrack.DTOs.UserDTO;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.models.User;
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
import org.springframework.web.bind.annotation.RequestMethod;
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
	public ResponseEntity<?> authenticateUser(@RequestBody UserDTO user){
		if(!userRepo.existsByEmail(user.getEmail()) && doctorRepo.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>("Error: Please choose correct role.", HttpStatus.BAD_REQUEST);
			
		} else if(!userRepo.existsByEmail(user.getEmail()) && !doctorRepo.existsByEmail(user.getEmail())){
			return new ResponseEntity<>("Error: User does not exist, please register.", HttpStatus.BAD_REQUEST);
		}
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		User authUser = userRepo.findByEmail(userDetails.getEmail()).orElse(null);
		JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, authUser);
		return ResponseEntity.ok(jwtResponse); //200
	}
	
	@PostMapping("/login/doctor")
	public ResponseEntity<?> authenticateDoctor(@RequestBody DoctorDTO doctor){
		if(userRepo.existsByEmail(doctor.getEmail()) && !doctorRepo.existsByEmail(doctor.getEmail())) {
			return new ResponseEntity<>("Error: Please choose correct role.", HttpStatus.BAD_REQUEST);
			
		} else if(!userRepo.existsByEmail(doctor.getEmail()) && !doctorRepo.existsByEmail(doctor.getEmail())){
			return new ResponseEntity<>("Error: Doctor does not exist, please register.", HttpStatus.BAD_REQUEST);
		}
		
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(doctor.getEmail(), doctor.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Doctor authDoctor = doctorRepo.findByEmail(userDetails.getEmail()).orElse(null);
		JwtResponseDTO_Doctor jwtResponse = new JwtResponseDTO_Doctor(jwt, authDoctor);
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
		
		
		String jwt = jwtUtils.generateJwtToken(newDoc);
		
		emailService.sendConfirmationEmail(jwt);
		
		//TODO: Authneticate, set security context, generate jwt token and return jwt response with 201 status to the frontend.
		return new ResponseEntity<>(newDoc, HttpStatus.CREATED);
	}
	
	@PostMapping("/save/user")
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user){
		if(userRepo.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>("Error: Email is already in use! Please login", HttpStatus.BAD_REQUEST); //400
		}
		
		if(userRepo.existsByUsername(user.getUsername())) {
			return new ResponseEntity<>("Error: Username is already in taken!", HttpStatus.BAD_REQUEST);
		}
		
		User newUser = new User(user.getUsername(), user.getEmail(), encoder.encode(user.getPassword()));
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_USER).orElse(null));
		newUser.setRoles(roles);
		
		userRepo.save(newUser);
		
		String jwt = jwtUtils.generateJwtToken(newUser);
		
		emailService.sendConfirmationEmail(jwt);
		
		//TODO: Authenticate, set the SecurityContext, generate jwt token and return jwt response with 201 status to the frontend.
		return new ResponseEntity<>(newUser, HttpStatus.CREATED); //201
	}
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<?> confirmAccount(@RequestParam("access_token")String confirmationToken){
		if(jwtUtils.validateJwtToken(confirmationToken)) {
			String email = jwtUtils.getEmailFromJwtToken(confirmationToken);
			User user = userRepo.findByEmail(email).orElse(null);
			Doctor doctor = doctorRepo.findByEmail(email).orElse(null);
			
			if(user != null) {
				user.setIsEnabled(true);
				userRepo.save(user);
				JwtResponseDTO jwtResponse = new JwtResponseDTO(confirmationToken, user);
				return ResponseEntity.ok(jwtResponse);
			} else if(doctor !=null) {
				doctor.setIsEnabled(true);
				doctorRepo.save(doctor);
				JwtResponseDTO_Doctor jwtResponse = new JwtResponseDTO_Doctor(confirmationToken, doctor);
				return ResponseEntity.ok(jwtResponse);
			} else {
				return ResponseEntity.badRequest().body("Unable to find account that matches this token.");
			}
		}
		
		return ResponseEntity.badRequest().body("Invalid Token.");
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
	