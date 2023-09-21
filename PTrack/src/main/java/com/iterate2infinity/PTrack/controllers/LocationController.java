package com.iterate2infinity.PTrack.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.repos.LocationRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/locations")
public class LocationController {
	@Autowired
	LocationRepository locationRepo;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllLocations() {
		List<Location> locations = locationRepo.findAll();
		return ResponseEntity.ok(locations);
	}
	
	@GetMapping("/byName")
	public ResponseEntity<?> getLocationByName(@RequestBody HashMap<String, String> request){
		Location location;
		if(locationRepo.existsByName(request.get("name"))) {
			location=locationRepo.findByName(request.get("name")).orElse(null);
			return ResponseEntity.ok(location);
		}
		
		return ResponseEntity.badRequest().body("Error: No location found matching that name.");
	}
	
	@GetMapping("/byAddress")
	public ResponseEntity<?> getLocationByAddress(@RequestBody HashMap<String, String> request){
		Location location;
		if(locationRepo.existsByAddress(request.get("address"))) {
			location=locationRepo.findByAddress(request.get("address")).orElse(null);
			return ResponseEntity.ok(location);
		}
		
		return ResponseEntity.badRequest().body("Error: No location found matching that address.");
	}
	
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
	
	
}
