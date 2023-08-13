package com.iterate2infinity.PTrack.controllers;
import java.util.Map;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DoctorRepository doctorRepo;
	
	// TODO: private url, must authenticate and have ROLE_USER
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> getUser(@RequestBody Map<String, String> emailMap) {
		User user = userRepo.findByEmail(emailMap.get("email")).orElse(null);
		return ResponseEntity.ok(user);
	}
	
	// TODO: private url, must authenticate and have ROLE_DOCTOR
	@GetMapping("/doctor")
	@PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getDoctor(@RequestBody Map<String, String> emailMap){
		Doctor doctor = doctorRepo.findByEmail(emailMap.get("email")).orElse(null);
		return ResponseEntity.ok(doctor);
	}
	
	

}

