package com.iterate2infinity.PTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

//Excludes MongoDB auto configuration classes since config is provided manually in MongoConfig class
@SpringBootApplication(exclude = {
		MongoAutoConfiguration.class,
		MongoDataAutoConfiguration.class
})
public class PTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PTrackApplication.class, args);
	
	}

}

/*
 *  TODO: 
 *  	- Extract business logic in controllers into service classes.
 *  	- Configure CORS and CSRF policies.
 *  	- Add caching headers to responses --> across all the controllers and end points
 *  		- locations responses  --> Cache-Control: max-age=4hrs, stale-while-revalidate=20hrs
 *  		- visits responses  --> Cache-Control: max-age=43200(12hours), stale-while-revalidate=43200(12hours)
 *  	- Implement caching between db and controllers and add appropriate CacheControl headers to controllers (see above bullet point)
 *  	- Implement service worker classes/methods
 *  		* ends all pts visits at the end of the day
 *  		* clears location's activePatients and activeDoctors lists in db at the end of the day
 *  		* check all doctors out of locations at the end of the day
 *  
 */