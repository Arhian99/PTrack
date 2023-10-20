package com.iterate2infinity.PTrack.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iterate2infinity.PTrack.models.Visit;
import com.iterate2infinity.PTrack.repos.VisitRepository;

@Service
public class VisitService {
	@Autowired
	VisitRepository visitRepo;
	
	
	public Visit getVisitById(ObjectId id) {
		return visitRepo.findById(id).orElse(null);
	}
}
