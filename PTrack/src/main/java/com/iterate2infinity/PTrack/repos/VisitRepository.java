package com.iterate2infinity.PTrack.repos;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.models.Visit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends MongoRepository<Visit, ObjectId>{
	List<Visit> findAll();
	List<Visit> findByDate(Date date);
	List<Visit> findByLocation(Location location);
	List<Visit> findByDoctor(Doctor doctor);
	List<Visit> findByPatient(User patient);
}
