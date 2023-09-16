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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	// TODO: implement AuthEntryPointJwt --> catches authentication exceptions and returns 401 response to frontend
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		authProvider.setHideUserNotFoundExceptions(false);
		return authProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	//TODO: CONFIGURE cors and csrf
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.disable())
			.csrf(csrf -> csrf.disable())
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers("/auth/**").permitAll()
															   .requestMatchers("/api/admin").hasRole("ADMIN")
															   .requestMatchers("/api/welcome/user").hasRole("USER")
															   .requestMatchers("/api/welcome/doctor").hasRole("DOCTOR")
															   .requestMatchers("/api/admin").authenticated()
															   .requestMatchers("/api/welcome/user").authenticated()
															   .requestMatchers("/api/welcome/doctor").authenticated());
		
		
		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	
}


//.authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers("/api/welcome/user").hasRole("USER")
//		   .requestMatchers("/api/welcome/doctor").hasRole("DOCTOR")
//		   .requestMatchers("/api/welcome/user").authenticated()
//		   .requestMatchers("/api/welcome/doctor").authenticated());
