package com.iterate2infinity.PTrack.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;

import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.DTOs.LocationDTO;
import com.iterate2infinity.PTrack.ExceptionHandling.AlreadyExistsException;
import com.iterate2infinity.PTrack.ExceptionHandling.ResourceNotFoundException;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.LocationRepository;

@Service
public class LocationService {
	@Autowired
	private LocationRepository locationRepo;
	
	@Autowired
	private DoctorService doctorService;
	
	@Autowired
	private UserService userService;
	
	public boolean exists(String method, String identifier) {
		switch(method) {
		case "byName": return locationRepo.existsByName(identifier);
		case "byAddress": return locationRepo.existsByAddress(identifier);
		default: return false;
		}
	}
	
	public Optional<Location> find(String method, String identifier){
		switch(method) {
		case "byName": return locationRepo.findByName(identifier);
		case "byAddress": return locationRepo.findByAddress(identifier);
		default: return null;
		}
	}
	
	public void save(Location location) {
		locationRepo.save(location);
	}
	
	public List<Location> getAll(){
		return locationRepo.findAll();
	}
	
	public Location register(LocationDTO location) {
		if(exists("byName", location.getName())) throw new AlreadyExistsException("Location already exists with name: "+location.getName());
		if(exists("byAddress", location.getAddress())) throw new AlreadyExistsException("Location already exists with address: "+location.getAddress());
		Location newLocation = new Location(location.getName(), location.getAddress());
		save(newLocation);
		return newLocation;
	}
	
	public JwtResponseDTO<Doctor> checkIn(HashMap<String, String> request){
		Location location = find("byName", request.get("name")).orElseThrow(() -> new ResourceNotFoundException("Error: Location not found in database."));
		Doctor doctor = doctorService.find("byEmail", request.get("email")).orElseThrow(() -> new ResourceNotFoundException("Error: Doctor not found in database."));
		if(doctor.getIsCheckedIn()) throw new AlreadyExistsException("Error: Doctor is already checked in at a location.");
		
		location.addActiveDoctor(doctor);
		save(location);
		
		doctor.setIsCheckedIn(true);
		doctor.setCurrentLocation(location);
		doctorService.save(doctor);
		
		//TODO: Validate token before returning
		return new JwtResponseDTO<Doctor>(request.get("jwt"), doctor);
	}
	
	
	public JwtResponseDTO<Doctor> checkOut(HashMap<String, String> request){
		Doctor doctor = doctorService.find("byEmail", request.get("email")).orElseThrow(() -> new ResourceNotFoundException("Error: Doctor not found in database."));
		if(!doctor.getIsCheckedIn() || doctor.getCurrentLocation().equals(null)) {
			throw new AlreadyExistsException("Error: Doctor is not currently checked in or checked in location not found");
		}
		
		Location currentLocation = doctor.getCurrentLocation();
		currentLocation.getActiveDoctors().remove(doctor);
		save(currentLocation);
		
		doctor.setIsCheckedIn(false);
		doctor.setCurrentLocation(null);
		doctorService.save(doctor);
		
		return new JwtResponseDTO<Doctor>(request.get("jwt"), doctor);
	}
	
	public Location clearActiveDoctors(String locationName) {
		Location location = find("byName", locationName).orElseThrow(() -> new ResourceNotFoundException("Error: Location with specified name not found in database."));
		location.getActiveDoctors().forEach(doctor -> {
			doctor.setIsCheckedIn(false);
			doctorService.save(doctor);
		});
		
		location.clearActiveDoctors();
		save(location);
		
		return location;
	}
	
	public Location clearActivePatients(String locationName) {
		Location location = find("byName", locationName).orElseThrow(() -> new ResourceNotFoundException("Error: Location with specified name not found in database."));
		location.getActivePatients().forEach(patient -> {
			patient.setCurrentLocation(null);
			userService.save(patient);
			//TODO: Update Visit object status to finalized (and save in db) or to rejected (and remove from db)
		});
		location.clearActivePatients();
		save(location);
		
		return location;
	}
	
	public Location clearAll(String locationName) {
		Location location = clearActiveDoctors(locationName);
		location = clearActivePatients(locationName);
		return location;
	}
	
	public HashSet<Doctor> getActiveDoctors(String locationName){
		Location location = find("byName", locationName).orElseThrow(() -> new ResourceNotFoundException("Error: Location with specified name not found in database."));
		return location.getActiveDoctors();
	}
	
	public HashSet<User> getActivePatients(String locationName){
		Location location = find("byName", locationName).orElseThrow(() -> new ResourceNotFoundException("Error: Location with specified name not found in database."));
		return location.getActivePatients();
	}
}




