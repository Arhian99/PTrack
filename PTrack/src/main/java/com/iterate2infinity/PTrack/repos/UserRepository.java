package com.iterate2infinity.PTrack.repos;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import com.iterate2infinity.PTrack.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//Repository to interact with 'users' collection (which stores User objects) in our db
@Repository
public interface UserRepository extends MongoRepository<User, ObjectId>{

	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	List<User> findAll();
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
