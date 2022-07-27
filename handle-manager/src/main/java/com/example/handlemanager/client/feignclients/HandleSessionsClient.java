package com.example.handlemanager.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(url = "https://35.178.174.137:8000/api/sessions")
public interface HandleSessionsClient {
	
	// Initialize a new session
	@PostMapping(value="")
	String newSession();
	
	@GetMapping(value="/this")
	String verifySession();
	
	@DeleteMapping(value="")
	String deleteSession();
	

}
