package com.example.handlemanager.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandleManagerConfig {
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}
	

}
