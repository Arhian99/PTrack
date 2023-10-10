package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO_Doctor;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.LocationRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

		return ResponseEntity
				.ok()
				.cacheControl(CacheControl.empty().sMaxAge(4, TimeUnit.HOURS).staleWhileRevalidate(20, TimeUnit.HOURS))
				.eTag(locations.get(0).toString()) // TODO: find appropriate eTag
				.body(locations);
		/*
		 *  - CacheControl: s-MaxAge=2hrs, staleWhileRevalidate=22hrs
		 *  - Will reuse the same response from the cache for 4 hours after the response has been generated. 
		 *  - Then for the next 20 hours it will keep using the same response (even if stale) but will revalidate and/or 
		 *    update the cache in the background if the content of the response has changed, every time it revalidates the 
		 *    4hr and the 20hr timer resets
		 */
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
	 * 		"jwt": "userJWT,
	 * 		"role": "userRole"
	 * }
	 */
	@PostMapping("/checkIn")
	public ResponseEntity<?> checkIn(@RequestBody HashMap<String, String> request){
		Location location = locationRepo.findByName(request.get("name")).orElse(null);
		if(location.equals(null)) {
			return ResponseEntity.badRequest().body("Error: Location not found in database.");
		}
		
//		if(request.get("role").equals("ROLE_USER")) {
//			User user = userRepo.findByEmail(request.get("email")).orElse(null);
//			if(user.equals(null)) {
//				return ResponseEntity.badRequest().body("Error: User not found in database.");
//			} else if(user.getIsCheckedIn()) {
//				return ResponseEntity.badRequest().body("Error: User is already checked in at a location");
//			}
//			location.addActivePatient(user);
//			locationRepo.save(location);
//			
//			user.setIsCheckedIn(true);
//			user.setCurrentLocation(location);
//			userRepo.save(user);
//			
//			return ResponseEntity.ok(user);
//			
//		} else if (request.get("role").equals("ROLE_DOCTOR")) {
			Doctor doctor = doctorRepo.findByEmail(request.get("email")).orElse(null);
			if(doctor.equals(null)) {
				return ResponseEntity.badRequest().body("Error: Doctor not found in database.");
			} else if(doctor.getIsCheckedIn()) {
				return ResponseEntity.badRequest().body("Error: Doctor is already checked in at a location.");
			}
			
			location.addActiveDoctor(doctor);
			locationRepo.save(location);
			
			doctor.setIsCheckedIn(true);
			doctor.setCurrentLocation(location);
			doctorRepo.save(doctor);
			
			return ResponseEntity.ok(new JwtResponseDTO_Doctor(request.get("jwt"), doctor));
//		}
//		
//		return ResponseEntity.badRequest().build();
	}
	
	/* Request Body
	 * {

	 * 		"email": "userEmail",
	 * 		"jwt": "userJWT,
	 * 		"role": "userRole"
	 * }
	 */
	@PostMapping("/checkOut")
	public ResponseEntity<?> checkOut(@RequestBody HashMap<String, String> request){
//		User patient;
		Doctor doctor;
//		
//		if(request.get("role").equals("ROLE_USER")) {
//			patient = userRepo.findByEmail(request.get("email")).orElse(null);
//			
//			if(patient.equals(null)) {
//				return ResponseEntity.badRequest().body("Error: User not found in database.");
//				
//			} else if(!patient.getIsCheckedIn() || patient.getCurrentLocation().equals(null)) {
//				return ResponseEntity.badRequest().body("Error: User is not currently checked in or checked in location not found");
//			}
//			
//			Location currentLocation = patient.getCurrentLocation();
//			currentLocation.getActivePatients().remove(patient);
//			locationRepo.save(currentLocation);
//			
//			patient.setIsCheckedIn(false);
//			patient.setCurrentLocation(null);
//			userRepo.save(patient);
//			
//			return ResponseEntity.ok("User successfully Checked Out.");
//			
//			
//		} else if(request.get("role").equals("ROLE_DOCTOR")) {
			doctor = doctorRepo.findByEmail(request.get("email")).orElse(null);
			
			if(doctor.equals(null)) {
				return ResponseEntity.badRequest().body("Error: Doctor not found in database.");
				
			} else if(!doctor.getIsCheckedIn() || doctor.getCurrentLocation().equals(null)) {
				return ResponseEntity.badRequest().body("Error: Doctor is not currently checked in or checked in location not found");
			}
			
			Location currentLocation = doctor.getCurrentLocation();
			currentLocation.getActiveDoctors().remove(doctor);
			locationRepo.save(currentLocation);
			
			doctor.setIsCheckedIn(false);
			doctor.setCurrentLocation(null);
			doctorRepo.save(doctor);
			
			return ResponseEntity.ok(new JwtResponseDTO_Doctor(request.get("jwt"), doctor));
//		}
//		
//		return ResponseEntity.badRequest().build();
		
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName"
	 * }
	 *
	 */
	@PostMapping("/clearDoctors")
	public ResponseEntity<?> clearActiveDoctors(@RequestBody HashMap<String, String> request){
		Location location = locationRepo.findByName(request.get("name")).orElse(null);
		if(location == null){
			return ResponseEntity.badRequest().body("Error: Location with specified name not found in database.");
		}
		location.getActiveDoctors().forEach(doctor -> {
			doctor.setIsCheckedIn(false);
			doctorRepo.save(doctor);
			});
		
		location.clearActiveDoctors();
		locationRepo.save(location);
		
		return ResponseEntity.ok(location);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName"
	 * }
	 *
	 */
	@PostMapping("/clearPatients")
	public ResponseEntity<?> clearActivePatients(@RequestBody HashMap<String, String> request){
		Location location = locationRepo.findByName(request.get("name")).orElse(null);
		if(location==null) {
			return ResponseEntity.badRequest().body("Error: Location with specified name not found in database.");
		}  
		location.getActivePatients().forEach(patient -> {
			patient.setIsInVisit(false);
			patient.setCurrentLocation(null);
			userRepo.save(patient);
			//TODO: Update Visit object status to finalized (and save in db) or to rejected (and remove from db)
		});
		location.clearActivePatients();
		locationRepo.save(location);
		
		return ResponseEntity.ok(location);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName"
	 * }
	 *
	 */
	@PostMapping("/clearAll")
	public ResponseEntity<?> clearAll(@RequestBody HashMap<String, String> request){
		Location location = locationRepo.findByName(request.get("name")).orElse(null);
		if(location==null) {
			return ResponseEntity.badRequest().body("Error: Location with specified name not found in database.");
		}
		
		location.getActiveDoctors().forEach(doctor -> {
			doctor.setIsCheckedIn(false);
			doctorRepo.save(doctor);
		});
		
		location.clearActiveDoctors();

		location.getActivePatients().forEach(patient -> {
			patient.setIsInVisit(false);
			patient.setCurrentLocation(null);
			userRepo.save(patient);
			//TODO: Update Visit object status to finalized (and save in db) or to rejected (and remove from db)
		});
		
		location.clearActivePatients();
		locationRepo.save(location);
		
		return ResponseEntity.ok(location);
	}
	
	/* Request Params
	 * URL: /api/locations/activePatients?location_name=XXXXXX
	 *
	 */
	@GetMapping("/activeDoctors")
	public ResponseEntity<?> getActiveDoctors(@RequestParam("location_name") String locationName){
		Location location = locationRepo.findByName(locationName).orElse(null);
		if(location==null) {
			return ResponseEntity.badRequest().body("Error: Location with specified name not found in database.");
		}
		
		HashSet<Doctor> activeDoctors = location.getActiveDoctors();
		
		return ResponseEntity.ok(activeDoctors);
	}
	
	/* Request Params
	 * URL: /api/locations/activePatients?location_name=XXXXXX
	 *
	 */
	@GetMapping("/activePatients")
	public ResponseEntity<?> getActivePatients(@RequestParam("location_name") String locationName){
		Location location = locationRepo.findByName(locationName).orElse(null);
		if(location==null) {
			return ResponseEntity.badRequest().body("Error: Location with specified name not found in database.");
		}
		
		HashSet<User> activePatients = location.getActivePatients();
		
		return ResponseEntity.ok(activePatients);
	}
}
