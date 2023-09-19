package com.iterate2infinity.PTrack.security.services;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.repos.DoctorRepository;
import com.iterate2infinity.PTrack.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// This class provides a method to retrieve a user's details from the db based on the email.
@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	DoctorRepository doctorRepository;
	
	// This method retrieves and returns userDetils object from db associated with passed in email.
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		if(userRepository.existsByEmail(email)) {
			User user=userRepository.findByEmail(email).orElse(null);
			if(user == null) throw new UsernameNotFoundException("User not found with email: "+email);
			return UserDetailsImpl.build(user);
		} else if(doctorRepository.existsByEmail(email)){
			//Doctor doctor=doctorRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: "+email));
			Doctor doctor=doctorRepository.findByEmail(email).orElse(null);
			if(doctor == null) throw new UsernameNotFoundException("User not found with email: "+email);
			return UserDetailsImpl.build(doctor);
		} else return UserDetailsImpl.build();
	}

}
