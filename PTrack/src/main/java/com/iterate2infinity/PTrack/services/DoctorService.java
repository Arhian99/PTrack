package com.iterate2infinity.PTrack.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.iterate2infinity.PTrack.DTOs.DoctorDTO;
import com.iterate2infinity.PTrack.DTOs.JwtResponseDTO;
import com.iterate2infinity.PTrack.ExceptionHandling.AlreadyExistsException;
import com.iterate2infinity.PTrack.ExceptionHandling.ResourceNotFoundException;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.RoleRepository;
import com.iterate2infinity.PTrack.security.JWT.JwtUtils;
import com.iterate2infinity.PTrack.security.services.UserDetailsImpl;

@Service
public class DoctorService {
	@Autowired
	private DoctorRepository doctorRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private EmailService emailService;
	
	public List<Doctor> getAll(){
		return doctorRepo.findAll();
	}
	
	public void save(Doctor doctor) {
		doctorRepo.save(doctor);
	}
	
	public Optional<Doctor> find(String method, String principal) {
		switch(method) {
		case "byUsername": return doctorRepo.findByUsername(principal);
		case "byEmail": return doctorRepo.findByEmail(principal);
		default: return null;
		}
	}
	
	public boolean exists(String method, String principal) {
		switch(method) {
		case "byUsername": return doctorRepo.existsByUsername(principal);
		case "byEmail": return doctorRepo.existsByEmail(principal);
		case "byJwt": {
			String email = jwtUtils.getEmailFromJwtToken(principal);
			return exists("byEmail", email);
		}
		default: return false;
		}
	}
	
	public JwtResponseDTO<Doctor> login(DoctorDTO doctor){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(doctor.getEmail(), doctor.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Doctor authDoctor = find("byEmail", userDetails.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email"+userDetails.getEmail()));
		
		return new JwtResponseDTO<Doctor>(jwt, authDoctor);
	}
	
	public JwtResponseDTO<Doctor> register(DoctorDTO doctor){
		// checks if there is already a doctor with email
		if(exists("byEmail", doctor.getEmail())) throw new AlreadyExistsException("Error: Email is already in use! Please login");
		// checks if username already exists
		if(exists("byUsername", doctor.getUsername())) throw new AlreadyExistsException("Error: Username is already in use! Please login");
		
		// If both email and username are free, creates new Doctor object.
		Doctor newDoc = new Doctor(doctor.getUsername(), doctor.getEmail(), encoder.encode(doctor.getPassword()));
		
		//adds the 'roles' field
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByName(ERole.ROLE_DOCTOR).orElseThrow(() -> new ResourceNotFoundException("No role found with specified name")));
		newDoc.setRoles(roles);
		
		//saves the doctor object to the db
		save(newDoc);
		
		//creates new jwt from the doctor object
		String jwt = jwtUtils.generateJwtToken(newDoc);
		
		// calls send confirmationEmail() method which sends an email to the new doctor with a link and jwt.
		emailService.sendConfirmationEmail(jwt);
		
		return new JwtResponseDTO<Doctor>(jwt, newDoc);
	}
	
	public Doctor enable(String confirmationToken) {
		String email = jwtUtils.getEmailFromJwtToken(confirmationToken);
		Doctor doctor = find("byEmail", email).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: "+email));
		doctor.setIsEnabled(true);
		save(doctor);
		return doctor;
	}
}
