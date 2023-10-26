package com.iterate2infinity.PTrack.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import com.iterate2infinity.PTrack.models.Visit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends MongoRepository<Visit, ObjectId>{
	List<Visit> findAll();
	List<Visit> findByDate(Date date);
	List<Visit> findByLocationName(String locationName);
	List<Visit> findByDoctorUsername(String doctorUsername);
	List<Visit> findByPatientUsername(String patientUsername);
	List<Visit> findByLocationId(ObjectId location_id);
	List<Visit> findByPatientId(ObjectId patient_id);
	List<Visit> findByDoctorId(ObjectId doctor_id);


}
