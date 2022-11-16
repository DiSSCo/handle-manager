package com.example.handlemanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class Config {
	
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

}
