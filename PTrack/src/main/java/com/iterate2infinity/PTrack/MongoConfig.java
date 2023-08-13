package com.iterate2infinity.PTrack;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration{
	@Value("${MONGO_USER}")
	private static String MONGO_USER;
	
	@Value("${MONGO_PASSWORD}")
	private static String MONGO_PASSWORD;
	
	@Value("${MONGO_CLUSTER}")
	private static String MONGO_CLUSTER;
	
	private static final String MONGO_URI = "mongodb+srv://"+MONGO_USER+":"+MONGO_PASSWORD+"@"+MONGO_CLUSTER;
	
	protected String getDatabaseName() {
		return "PTrack";
	}
	
	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create(new ConnectionString(MONGO_URI));
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}
	
}
