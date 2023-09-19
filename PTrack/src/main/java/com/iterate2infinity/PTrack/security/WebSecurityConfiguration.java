package com.iterate2infinity.PTrack.security;

import com.iterate2infinity.PTrack.security.JWT.AuthEntryPointJwt;
import com.iterate2infinity.PTrack.security.JWT.AuthTokenFilter;
import com.iterate2infinity.PTrack.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// This class provides the configuration for spring security
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	// Initializes AuthEntryPointJwt object which is the authentication entry point (catches authentication exceptions and returns response to front end)
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	// BCrypt is used as the password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// returns instance of AuthTokenFilter which is the user-defined filter that we defined and authenticates users by JWT token
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	// This method returns the username and password authentication provider. The class that performs authentication based on username and password
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		authProvider.setHideUserNotFoundExceptions(false);
		return authProvider;
	}
	
	// the authentication manager "manages" all the authentication providers and calls the authenticate() method on each of them.
	// this method returns the default authentication manager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	// this method sets the configuration for the security filter chain
	//TODO: CONFIGURE cors and csrf
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.disable()) // disable cors (for development purposes)
			.csrf(csrf -> csrf.disable()) // disable cors (for development purposes)
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and() // sets the AuthEntryPointJwt as the authentication entry point (catches unauthenticated requests and returns unauthorized response to the front end)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // we are using JWT to manage a user's session so we set our sessions to stateless
			.authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers("/auth/**").permitAll() // permit all requests to /auth/* end points
															   .requestMatchers("/api/admin").hasRole("ADMIN") // requests to /api/admin MUST have role of ADMIN
															   .requestMatchers("/api/welcome/user").hasRole("USER") // requests to /api/welcome/user MUST have role of USER
															   .requestMatchers("/api/welcome/doctor").hasRole("DOCTOR") // requests to /api/welcome/doctor MUST have role of DOCTOR
															   .requestMatchers("/api/admin").authenticated() // requests to api/admin MUST authenticate
															   .requestMatchers("/api/welcome/user").authenticated() // requests to api/welcome/user MUST authenticate
															   .requestMatchers("/api/welcome/doctor").authenticated()); // requests to api/welcome/doctor MUST authenticate
		
		
		http.authenticationProvider(authenticationProvider()); // sets the username and password authentication provider as AN authentication provider in this filter chain
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // adds the JWT token filter (AuthTokenFilter) class as a filter BEFORE the UsernameAndPasswordAuthentication filter
		
		return http.build();
	}
}

