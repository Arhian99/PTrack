package com.iterate2infinity.PTrack;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@ConfigurationProperties("spring.mail")
public class MailConfig {
	Dotenv dotenv = Dotenv.configure().load();
	
	private String username = dotenv.get("SPRING_MAIL_USERNAME");
	private String password = dotenv.get("SPRING_MAIL_PASSWORD");
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
