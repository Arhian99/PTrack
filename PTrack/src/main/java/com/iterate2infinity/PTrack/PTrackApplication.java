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
