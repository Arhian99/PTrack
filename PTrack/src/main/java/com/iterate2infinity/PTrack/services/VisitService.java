package com.iterate2infinity.PTrack.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iterate2infinity.PTrack.DTOs.VisitMessageDTO;
import com.iterate2infinity.PTrack.ExceptionHandling.ResourceNotFoundException;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.EVisitStatus;
import com.iterate2infinity.PTrack.models.Location;
import com.iterate2infinity.PTrack.models.User;
import com.iterate2infinity.PTrack.models.Visit;
import com.iterate2infinity.PTrack.repos.VisitRepository;

@Service
public class VisitService {
	@Autowired
	private VisitRepository visitRepo;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DoctorService doctorService;

	public Optional<Visit> getVisit(ObjectId id) {
		return visitRepo.findById(id);
	}
	
	public void save(Visit visit) {
		visitRepo.save(visit);
	}
	
	public List<Visit> getAll(){
		return visitRepo.findAll();
	}
	
	public List<Visit> getPatientVisits(String username){
		return visitRepo.findByPatientUsername(username);
	}
	
	public List<Visit> getDoctorVisits(String username){
		return visitRepo.findByDoctorUsername(username);
	}
	
	public void deleteAll() {
		visitRepo.deleteAll();
	}
	
	public void delete(Visit visit) {
		visitRepo.delete(visit);
	}
	
	public Visit newVisit(VisitMessageDTO visit) {
		Location visitLocation = locationService.find("byName", visit.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		Doctor visitDoctor = doctorService.find("byUsername", visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		User visitPatient = userService.find("byUsername", visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		EVisitStatus visitStatus = EVisitStatus.VISIT_PENDING;
		
		Visit newVisit = new Visit(
				new Date(),
				visitLocation.getName(),
				visitLocation.getId(),
				visitPatient.getUsername(),
				visitPatient.getId(),
				visitDoctor.getUsername(),
				visitDoctor.getId(),
				visitStatus
		);
		save(newVisit);
		
		visitPatient.setIsInVisit(true);
		visitPatient.setCurrentVisit(newVisit);
		userService.save(visitPatient);
		
		visitLocation.addActivePatient(visitPatient);
		locationService.save(visitLocation);
		
		visitDoctor.addCurrentVisit(newVisit);
		doctorService.save(visitDoctor);

		return newVisit;
	}
	
	public HashMap<String, Object> newVisitStomp(VisitMessageDTO visit){
		Location visitLocation = locationService.find("byName", visit.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		Doctor visitDoctor = doctorService.find("byUsername", visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		User visitPatient = userService.find("byUsername", visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("Error: Visit location not found in database."));
		EVisitStatus visitStatus = EVisitStatus.VISIT_PENDING;
		
		Visit newVisit = new Visit(
				new Date(),
				visitLocation.getName(),
				visitLocation.getId(),
				visitPatient.getUsername(),
				visitPatient.getId(),
				visitDoctor.getUsername(),
				visitDoctor.getId(),
				visitStatus
		);
		save(newVisit);
		
		visitPatient.setIsInVisit(true);
		visitPatient.setCurrentVisit(newVisit);
		userService.save(visitPatient);
		
		visitLocation.addActivePatient(visitPatient);
		locationService.save(visitLocation);
		
		visitDoctor.addCurrentVisit(newVisit);
		doctorService.save(visitDoctor);
		
		HashMap<String, Object> stakeHolders = new HashMap<String, Object>();
		stakeHolders.put("visitDoctor", visitDoctor);
		stakeHolders.put("visitPatient", visitPatient);
		
		return stakeHolders;
	}
	
	public HashMap<String, Object> acceptVisitStomp(VisitMessageDTO visit){
		Visit currrentVisit = getVisit(visit.getVisitID()).orElseThrow(() -> new ResourceNotFoundException("Visit not found with ID: "+visit.getVisitID()));
		
		currrentVisit.setStatus(EVisitStatus.VISIT_CURRENT);
		save(currrentVisit);

		User visitPatient = userService.find("byUsername", visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("No patient found with username: "+visit.getPatientUsername()));
		visitPatient.setCurrentVisit(currrentVisit);
		userService.save(visitPatient);
		
		Doctor visitDoctor = doctorService.find("byUsername", visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found with username: "+visit.getDoctorUsername()));
		visitDoctor.addCurrentVisit(currrentVisit);
		doctorService.save(visitDoctor);
		
		HashMap<String, Object> stakeHolders = new HashMap<String, Object>();
		stakeHolders.put("visitDoctor", visitDoctor);
		stakeHolders.put("visitPatient", visitPatient);
		
		return stakeHolders;
	}
	
	public HashMap<String, Object> declineVisitStomp(VisitMessageDTO visit){
		Visit currrentVisit = getVisit(visit.getVisitID()).orElseThrow(() -> new ResourceNotFoundException("Visit not found with ID: "+visit.getVisitID()));
		currrentVisit.setStatus(EVisitStatus.VISIT_REJECTED);
		
		User visitPatient = userService.find("byUsername", visit.getPatientUsername()).orElseThrow(() -> new ResourceNotFoundException("No patient found with the Username: "+visit.getPatientUsername()));
		visitPatient.setCurrentVisit(null);
		visitPatient.setIsInVisit(false);
		userService.save(visitPatient);
		
		Doctor visitDoctor = doctorService.find("byUsername", visit.getDoctorUsername()).orElseThrow(() -> new ResourceNotFoundException("No doctor found with username: "+visit.getDoctorUsername()));
		visitDoctor.removeCurrentVisit(currrentVisit);
		doctorService.save(visitDoctor);
		
		Location visitLocation = locationService.find("byName", visit.getLocationName()).orElseThrow(() -> new ResourceNotFoundException("No location found with name: "+visit.getLocationName())); 
		visitLocation.removeActivePatient(visitPatient);
		locationService.save(visitLocation);

		HashMap<String, Object> stakeHolders = new HashMap<String, Object>();
		stakeHolders.put("visitDoctor", visitDoctor);
		stakeHolders.put("visitPatient", visitPatient);

		return stakeHolders;
	}
}
































