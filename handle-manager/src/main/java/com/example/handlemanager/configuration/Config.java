package com.example.handlemanager.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Config {
	
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

}
