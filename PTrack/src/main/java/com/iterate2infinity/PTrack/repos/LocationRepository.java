package com.iterate2infinity.PTrack.repos;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.iterate2infinity.PTrack.models.Location;

@Repository
public interface LocationRepository extends MongoRepository<Location, ObjectId>{

	Optional<Location> findByName(String name);
	Optional<Location> findByAddress(String address);
	List<Location> findAll();
	Boolean existsByName(String name);
	Boolean existsByAddress(String address);
}
