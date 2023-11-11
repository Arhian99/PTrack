package com.iterate2infinity.PTrack.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iterate2infinity.PTrack.DTOs.RoleDTO;
import com.iterate2infinity.PTrack.models.ERole;
import com.iterate2infinity.PTrack.models.Role;
import com.iterate2infinity.PTrack.repos.RoleRepository;

@Service
public class RoleService {
	@Autowired
	RoleRepository roleRepo;
	
	public Role register(RoleDTO role) {
		ERole newERole = ERole.valueOf(role.getName());
		Role newRole = new Role(newERole);
		roleRepo.save(newRole);
		return newRole;
	}
}
