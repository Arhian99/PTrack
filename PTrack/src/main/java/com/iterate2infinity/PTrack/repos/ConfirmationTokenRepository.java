package com.iterate2infinity.PTrack.repos;

import java.util.Optional;

import org.bson.types.ObjectId;

import com.iterate2infinity.PTrack.models.ConfirmationToken;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, ObjectId>{
	Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
}
