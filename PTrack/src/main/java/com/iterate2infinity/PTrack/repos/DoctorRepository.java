package com.iterate2infinity.PTrack.repos;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import com.iterate2infinity.PTrack.models.Doctor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Repository to interact with 'doctors' collection (which stores Doctor objects) in our db
@Repository
public interface DoctorRepository extends MongoRepository<Doctor, ObjectId>{
	Optional<Doctor> findByUsername(String username);
	Optional<Doctor> findByEmail(String email);
	List<Doctor> findAll();
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
