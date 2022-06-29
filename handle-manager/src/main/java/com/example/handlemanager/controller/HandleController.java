package com.example.handlemanager.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.handlemanager.model.DigitalSpecimen;
import com.example.handlemanager.service.HandleService;

import net.handle.hdllib.HandleException;

//import net.handle.hdllib.HandleException;
//import net.handle.hdllib.HandleValue;

@RestController  //Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")  // Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
public class HandleController {
	
	
	@Autowired
	private HandleService service;
	
	@GetMapping(value="/hello") 
	public Response hello() throws HandleException{
		return Response
				.status(Response.Status.OK)
				.entity("Hello")
				.build();
	}
	
	// ** HTTP Methods **
	
	// Resolve handle
		@GetMapping(value="/record/**")
		public String resolveHandle(HttpServletRequest request,
				@RequestParam(value="types", defaultValue="-1") String[] types,
				@RequestParam(value="indexes", defaultValue="-1") int[] indexes) throws IOException, InterruptedException, HandleException {
			
			// Extract handle value from argument
			String handle = getHandleFromArg(request);
			
			
			// Set undeclared values to null. I don't think we can do this earlier but I'll check
			types = (types[0].equals("-1")) ? null : types;
			indexes = (indexes[0] == -1 || indexes.length>7) ? null: indexes; // If no indexes are specified or if there's more than the max, just assume they want everything
			
			return service.resolveHandleWeb(handle);
			//DigitalSpecimen digSpec = service.resolveHandle(handle,types,indexes);
			//return (ResponseEntity<DigitalSpecimen>) ResponseEntity.ok(digSpec);
		}
	
	// Create handle record
	// Should accept body in the form of a Digital Specimen which would be transformed into something json-injestible and 
	@PostMapping(value="/record")
	public String createHandle() {
		return service.createHandleWeb();
	}

	
	
	// ** SDK Methods **
	
	// Resolve handle: accept handle, return handle record
	// Optional: supply index or type to narrow return value
	// Currently, if an incorrect type or index is submitted, it is ignored
	
	@GetMapping(value="/sdk/record/**")
	public ResponseEntity<DigitalSpecimen> resolveHandleSDK(HttpServletRequest request,
			@RequestParam(value="types", defaultValue="-1") String[] types,
			@RequestParam(value="indexes", defaultValue="-1") int[] indexes) throws IOException, InterruptedException, HandleException {
		
		// Extract handle value from argument
		String handle = getHandleFromArg(request);
		
		// Set undeclared values to null. I don't think we can do this earlier but I'll check
		types = (types[0].equals("-1")) ? null : types;
		indexes = (indexes[0] == -1 || indexes.length>7) ? null: indexes; // If no indexes are specified or if there's more than the max, just assume they want everything
		
		DigitalSpecimen digSpec = service.resolveHandle(handle,types,indexes);
		return (ResponseEntity<DigitalSpecimen>) ResponseEntity.ok(digSpec);
	}
	
	
	private String getHandleFromArg(HttpServletRequest r) {
		String requestUrl = r.getRequestURL().toString();
		return requestUrl.split("/api/record/")[1];
	}
	
}
