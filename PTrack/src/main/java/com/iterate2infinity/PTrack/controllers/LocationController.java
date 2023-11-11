package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.LocationDTO;
import com.iterate2infinity.PTrack.ExceptionHandling.ResourceNotFoundException;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.services.DoctorService;
import com.iterate2infinity.PTrack.services.LocationService;
import com.iterate2infinity.PTrack.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
	@Autowired
	LocationService locationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	DoctorService doctorService;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllLocations() {
		List<Location> locations = locationService.getAll();

		return ResponseEntity
				.ok()
				.cacheControl(CacheControl.empty().sMaxAge(4, TimeUnit.HOURS).staleWhileRevalidate(20, TimeUnit.HOURS))
				.eTag(locations.get(0).toString()) // TODO: find appropriate eTag
				.body(locations);
		/*
		 *  - CacheControl: s-MaxAge=4hrs, staleWhileRevalidate=20hrs
		 *  - Will reuse the same response from the cache for 4 hours after the response has been generated. 
		 *  - Then for the next 20 hours it will keep using the same response (even if stale) but will revalidate and/or 
		 *    update the cache in the background if the content of the response has changed, every time it revalidates the 
		 *    response; the 4hr and the 20hr timer resets
		 */
	}
	
	
	
	/* URL
	 * /api/locations/byName?name=locationName
	 */
	@GetMapping("/byName")
	public ResponseEntity<?> getLocationByName(@RequestParam("name") String locationName){
		Location location = locationService.find("byName", locationName).orElseThrow(() -> new ResourceNotFoundException("No location found with name: "+locationName));
		return ResponseEntity.ok(location);
	}
	
	/* URL
	 * /api/locations/byAddress?address=locationAddress
	 */
	@GetMapping("/byAddress")
	public ResponseEntity<?> getLocationByAddress(@RequestParam("address") String locationAddress){
		Location location=locationService.find("byAddress", locationAddress).orElseThrow(() -> new ResourceNotFoundException("No location found with the address: "+locationAddress));
		return ResponseEntity.ok(location);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName",
	 * 		"address": "locationAddress"
	 * }
	 */
	@PostMapping("/new")
	public ResponseEntity<?> saveLocation(@RequestBody LocationDTO location){
		Location newLocation = locationService.register(location);
		return ResponseEntity.ok(newLocation);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName",
	 * 		"email": "doctorEmail",
	 * 		"jwt": "doctorJWT,
	 * 		"role": "doctorRole"
	 * }
	 */
	@PostMapping("/checkIn")
	public ResponseEntity<?> checkIn(@RequestBody HashMap<String, String> request){
		JwtResponseDTO<Doctor> jwtResponse = locationService.checkIn(request);
		return ResponseEntity.ok(jwtResponse);
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
		JwtResponseDTO<Doctor> jwtResponse = locationService.checkOut(request);
		return ResponseEntity.ok(jwtResponse);
	}
	
	/* Request Body
	 * {
	 * 		"name": "locationName"
	 * }
	 *
	 */
	@PostMapping("/clearDoctors")
	public ResponseEntity<?> clearActiveDoctors(@RequestBody HashMap<String, String> request){
		Location location = locationService.clearActiveDoctors(request.get("name"));
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
		Location location = locationService.clearActivePatients(request.get("name"));
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
		Location location = locationService.clearAll(request.get("name"));		
		return ResponseEntity.ok(location);
	}
	
	/* Request Params
	 * URL: /api/locations/activeDoctors?location_name=XXXXXX
	 *
	 */
	@GetMapping("/activeDoctors")
	public ResponseEntity<?> getActiveDoctors(@RequestParam("location_name") String locationName){
		HashSet<Doctor> activeDoctors = locationService.getActiveDoctors(locationName);
		return ResponseEntity.ok(activeDoctors);
	}
	
	/* Request Params
	 * URL: /api/locations/activePatients?location_name=XXXXXX
	 *
	 */
	@GetMapping("/activePatients")
	public ResponseEntity<?> getActivePatients(@RequestParam("location_name") String locationName){		
		HashSet<User> activePatients = locationService.getActivePatients(locationName);
		return ResponseEntity.ok(activePatients);
	}
}
