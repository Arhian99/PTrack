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

/*
 * This controller exposes end points to login users and doctors, save new users and doctors, 
 * and enable users and doctors accounts
 */
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
	
	/*
	 *  Post request to this end point authenticates the user passed in and returns an HTTP
	 *  200 response with a JwtResponseDTO object instance that contains a JWT token and a user object.
	 *  
	 *  If it fails to authenticate the user, an exception will be thrown (BadCredentials or DisabledException, etc) 
	 *  and either 400 or 401 response will be returned to the front end (depending on exception thrown).
	 *  
	 *  Exception will be caught by AuthExceptionHandler or AuthEntryPointJwt classes and response to front end will be
	 *  sent by those classes
	 */
	@PostMapping("/login/user")
	public ResponseEntity<?> authenticateUser(@RequestBody UserDTO user){
		// checks if the email is associated with a doctor, if so returns 400 response with the message shown below
		if(!userRepo.existsByEmail(user.getEmail()) && doctorRepo.existsByEmail(user.getEmail())) {
			return new ResponseEntity<>("Error: Please choose correct role.", HttpStatus.BAD_REQUEST);
			// checks if the email belongs to a user, if not returns 400 response asking user to register
		} else if(!userRepo.existsByEmail(user.getEmail()) && !doctorRepo.existsByEmail(user.getEmail())){
			return new ResponseEntity<>("Error: User does not exist, please register.", HttpStatus.BAD_REQUEST);
		}
		
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
		User authUser = userRepo.findByEmail(userDetails.getEmail()).orElse(null);
		// Create instance of JwtResponseDTO with user object and the jwt token to send to the front end.
		JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, authUser);
		//sends response to frontend 
		return ResponseEntity.ok(jwtResponse); //200
	}
	
	// Same as above for doctors
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
	
	/*
	 * This end point creates a new Doctor object with the data received and 
	 * saves new doctor object onto the database
	 */
	@PostMapping("/save/doctor")
	public ResponseEntity<?> saveDoc(@RequestBody DoctorDTO doctor){
		// checks if there is already a doctor with email
		if(doctorRepo.existsByEmail(doctor.getEmail())) {
			//returns HTTP 400 if so
			return new ResponseEntity<>("Error: Email is already in use! Please login", HttpStatus.BAD_REQUEST);
		}
		
		// checks if username already exists
		if(doctorRepo.existsByUsername(doctor.getUsername())) {
			//returns HTTP 400 if so
			return new ResponseEntity<>("Error: Username is already in taken!", HttpStatus.BAD_REQUEST);
		}
		
		// If both email and username are free, creates new Doctor object.
		Doctor newDoc = new Doctor(doctor.getUsername(), doctor.getEmail(), encoder.encode(doctor.getPassword()));
		
		//adds the 'roles' field
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_DOCTOR).orElse(null));
		newDoc.setRoles(roles);
		
		//saves the doctor object to the db
		doctorRepo.save(newDoc);
		
		//creates new jwt from the doctor object
		String jwt = jwtUtils.generateJwtToken(newDoc);
		
		// calls send confirmationEmail() method which sends an email to the new doctor with a link and jwt.
		emailService.sendConfirmationEmail(jwt);
		
		// sends new user to the frontend and HTTP 201
		return new ResponseEntity<>(new JwtResponseDTO_Doctor(jwt, newDoc), HttpStatus.CREATED);
	}
	
	// Same as above but for users
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
		
		return new ResponseEntity<>(new JwtResponseDTO(jwt, newUser), HttpStatus.CREATED); //201
	}
	
	/*
	 *  This endpoint checks the validity of the JWT token that was e-mailed to the user, \
	 *  and if valid, it sets isEnabled field to true 
	 */
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<?> confirmAccount(@RequestParam("access_token")String confirmationToken){
		// if the jwt token is valid
		if(jwtUtils.validateJwtToken(confirmationToken)) {
			// gets email of user associated with token
			String email = jwtUtils.getEmailFromJwtToken(confirmationToken);
			// gets user object associated with email or null if none
			User user = userRepo.findByEmail(email).orElse(null);
			
			// gets doctor object associated with email or null if none
			Doctor doctor = doctorRepo.findByEmail(email).orElse(null);
			
			// checks if either a user or a doctor was associated with the email.
			if(user != null) {
				// set isEnabled field to true
				user.setIsEnabled(true);
				//save the update in the db
				userRepo.save(user);
				//return jwtResponse to the frontend
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
	