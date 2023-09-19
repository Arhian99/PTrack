package com.iterate2infinity.PTrack;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.github.cdimascio.dotenv.Dotenv;

// This class provides configuration to MongoDB database instance

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration{
	
	static Dotenv dotenv = Dotenv.configure().load();
	private static String MONGO_URI=dotenv.get("MONGO_URI");
	
	protected String getDatabaseName() {
		return "PTrack";
	}
	
	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create(MONGO_URI+"?tls=true");
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}
	
}
