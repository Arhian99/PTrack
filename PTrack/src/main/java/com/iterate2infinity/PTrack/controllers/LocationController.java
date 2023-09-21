package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;
import java.util.List;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.LocationRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/locations")
public class LocationController {
	@Autowired
	LocationRepository locationRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllLocations() {
		List<Location> locations = locationRepo.findAll();
		return ResponseEntity.ok(locations);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName",
	 * }
	 */
	@GetMapping("/byName")
	public ResponseEntity<?> getLocationByName(@RequestBody HashMap<String, String> request){
		Location location;
		if(locationRepo.existsByName(request.get("name"))) {
			location=locationRepo.findByName(request.get("name")).orElse(null);
			return ResponseEntity.ok(location);
		}
		
		return ResponseEntity.badRequest().body("Error: No location found matching that name.");
	}
	
	/* Request Body
	 * {
	 * 		"address": "locationAddress"
	 * }
	 */
	@GetMapping("/byAddress")
	public ResponseEntity<?> getLocationByAddress(@RequestBody HashMap<String, String> request){
		Location location;
		if(locationRepo.existsByAddress(request.get("address"))) {
			location=locationRepo.findByAddress(request.get("address")).orElse(null);
			return ResponseEntity.ok(location);
		}
		
		return ResponseEntity.badRequest().body("Error: No location found matching that address.");
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName",
	 * 		"address": "locationAddress"
	 * }
	 */
	@PostMapping("/new")
	public ResponseEntity<?> saveLocation(@RequestBody HashMap<String, String> request){
		if(locationRepo.existsByAddress(request.get("address"))) {
			return ResponseEntity.badRequest().body("Error: Location already exists with this address.");
		} else if(locationRepo.existsByName(request.get("name"))) {
			return ResponseEntity.badRequest().body("Error: Location already exists with that name.");
		}
		
		Location newLocation = new Location(request.get("name"), request.get("address"));
		locationRepo.save(newLocation);
		
		return ResponseEntity.ok(newLocation);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName",
	 * 		"email": "userEmail",
	 * 		"role": "userRole"
	 * }
	 */
	@PostMapping("/checkIn")
	public ResponseEntity<?> checkIn(@RequestBody HashMap<String, String> request){
		Location location = locationRepo.findByName(request.get("name")).orElse(null);
		if(location == null) {
			return ResponseEntity.badRequest().body("Error: Location not found in database.");
		}
		
		if(request.get("role").equals("ROLE_USER")) {
			User user = userRepo.findByEmail(request.get("email")).orElse(null);
			location.addActivePatient(user);
			locationRepo.save(location);
			return ResponseEntity.ok(location/*user.getUsername()+" has been checked in."*/);
		} else if (request.get("role").equals("ROLE_DOCTOR")) {
			Doctor doctor = doctorRepo.findByEmail(request.get("email")).orElse(null);
			location.addActiveDoctor(doctor);
			locationRepo.save(location);
			return ResponseEntity.ok(location/*doctor.getUsername()+" has been checked in."*/);
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	
}
