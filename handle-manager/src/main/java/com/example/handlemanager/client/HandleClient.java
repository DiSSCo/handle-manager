package com.example.handlemanager.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(url = "https://35.178.174.137:8000")
public interface HandleClient{
	
	// Create new handle record (Note: Handle API uses PUT for new handles and updating handles)
	@PutMapping(value = "/api/handles/") 
	String createHandleRecord();
	
	// Resolve handle
	@GetMapping(value="/api/handles/{handle}")
	String resolveHandle(@PathVariable("handle") String handle);
	
	// Update existing handle record
	@PutMapping(value = "/api/handles/{handle}") 
	String updateHandleRecord(@PathVariable("handle") String handle);
	
	@DeleteMapping(value="/api/handles/{handle}")
	String deleteHandle(@PathVariable("handle") String handle);
	
}

