package com.iterate2infinity.PTrack.repos;

import java.util.Optional;

import org.bson.types.ObjectId;

import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, ObjectId>{
	Optional<Role> findByName(ERole name);
}
