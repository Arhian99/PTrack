package com.iterate2infinity.PTrack.controllers;

import com.iterate2infinity.PTrack.DTOs.DoctorDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.UserDTO;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.security.JWT.JwtUtils;
import com.iterate2infinity.PTrack.services.DoctorService;
import com.iterate2infinity.PTrack.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired 
	UserService userService;
	
	@Autowired
	DoctorService doctorService;
	
	@Autowired
	private JwtUtils jwtUtils;
	
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
		if(!userService.exists("byEmail", user.getEmail()) && doctorService.exists("byEmail", user.getEmail())) {
			return new ResponseEntity<>("Error: Please choose correct role.", HttpStatus.BAD_REQUEST);
			// checks if the email belongs to a user, if not returns 400 response asking user to register
		} else if(!userService.exists("byEmail", user.getEmail()) && !doctorService.exists("byEmail",user.getEmail())){
			return new ResponseEntity<>("Error: User does not exist, please register.", HttpStatus.BAD_REQUEST);
		}
		
		JwtResponseDTO<User> jwtResponse = userService.login(user);
		return ResponseEntity.ok(jwtResponse); //200
	}
	
	// Same as above for doctors
	@PostMapping("/login/doctor")
	public ResponseEntity<?> authenticateDoctor(@RequestBody DoctorDTO doctor){
		if(userService.exists("byEmail", doctor.getEmail()) && !doctorService.exists("byEmail", doctor.getEmail())) {
			return new ResponseEntity<>("Error: Please choose correct role.", HttpStatus.BAD_REQUEST);
			
		} else if(!userService.exists("byEmail", doctor.getEmail()) && !doctorService.exists("byEmail", doctor.getEmail())){
			return new ResponseEntity<>("Error: Doctor does not exist, please register.", HttpStatus.BAD_REQUEST);
		}
		
		JwtResponseDTO<Doctor> jwtResponse = doctorService.login(doctor);
		return ResponseEntity.ok(jwtResponse);
	}
	
	/*
	 * This end point creates a new Doctor object with the data received and 
	 * saves new doctor object onto the database
	 */
	@PostMapping("/save/doctor")
	public ResponseEntity<?> saveDoc(@RequestBody DoctorDTO doctor){	
		JwtResponseDTO<Doctor> jwtResponse = doctorService.register(doctor);
		// sends new user to the frontend and HTTP 201
		return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
	}
	
	// Same as above but for users
	@PostMapping("/save/user")
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user){
		JwtResponseDTO<User> jwtResponse = userService.register(user);
		return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED); //201
	}
	
	/*
	 *  This endpoint checks the validity of the JWT token that was e-mailed to the user, \
	 *  and if valid, it sets isEnabled field to true 
	 */
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<?> confirmAccount(@RequestParam("access_token")String confirmationToken){
		// if the jwt token is valid
		if(jwtUtils.validateJwtToken(confirmationToken) && userService.exists("byJwt", confirmationToken)) {
			User enabledUser = userService.enable(confirmationToken);
			//return jwtResponse to the frontend
			JwtResponseDTO<User> jwtResponse = new JwtResponseDTO<User>(confirmationToken, enabledUser);
			return ResponseEntity.ok(jwtResponse);
			
		} else if(jwtUtils.validateJwtToken(confirmationToken) && doctorService.exists("byJwt", confirmationToken)) {
			Doctor enabledDoctor = doctorService.enable(confirmationToken);
			JwtResponseDTO<Doctor> jwtResponse = new JwtResponseDTO<Doctor>(confirmationToken, enabledDoctor);
			return ResponseEntity.ok(jwtResponse);

		} 
		
		return ResponseEntity.badRequest().body("Invalid Token.");
	}
}
	