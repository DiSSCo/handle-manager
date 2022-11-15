package com.example.handlemanager.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class Config {
	
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

}
