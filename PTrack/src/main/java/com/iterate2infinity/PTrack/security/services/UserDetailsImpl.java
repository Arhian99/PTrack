package com.iterate2infinity.PTrack.security.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iterate2infinity.PTrack.models.Doctor;
import com.iterate2infinity.PTrack.models.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


// This class is used to hold userDetails when user is retrieved from db or when user will be added to db.
public class UserDetailsImpl implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private ObjectId id;
	private String username;
	private String email;
	@JsonIgnore
	private String password;
	private Boolean isEnabled;
	
	//ROLE_XXXXX
	private Collection<? extends GrantedAuthority> authorities;

	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public UserDetailsImpl(ObjectId id, String username, String email, String password, Boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {
		this.id=id;
		this.username=username;
		this.email=email;
		this.password=password;
		this.isEnabled=isEnabled;
		this.authorities=authorities;
	}
	
	// by default when new user is created the account is disabled until email is confirmed
	public UserDetailsImpl() {
		this.isEnabled=false;
	}
	
	public static UserDetailsImpl build() {
		return new UserDetailsImpl();
	}
	
	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities= user.getRoles().stream().map(
					role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
		
		return new UserDetailsImpl(
						user.getId(),
						user.getUsername(),
						user.getEmail(),
						user.getPassword(),
						user.getIsEnabled(),
						authorities);
	}
	
	public static UserDetailsImpl build(Doctor doctor) {
		List<GrantedAuthority> authorities= doctor.getRoles().stream().map(
					role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
		
		return new UserDetailsImpl(
						doctor.getId(),
						doctor.getUsername(),
						doctor.getEmail(),
						doctor.getPassword(),
						doctor.getIsEnabled(),
						authorities);
	}
	
	public ObjectId getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

}
