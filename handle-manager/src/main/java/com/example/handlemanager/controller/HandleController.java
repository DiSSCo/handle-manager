package com.example.handlemanager.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.handlemanager.model.Handles;
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
	public Response hello() {
		return Response
				.status(Response.Status.OK)
				.entity("Hello")
				.build();
	}
	
	// ** Repository Methods **
	
	// List all handle values
	@GetMapping(value="/all")
	public List<String> getAllHandles(){
		return service.getHandles();
	}
	
	// Resolve a handle
	@GetMapping(value="/**")
	public String resolveHandle(@RequestParam(name="handle") String handle){
		return service.resolveHandle(handle);
		//TODO: might be nice to allow users to select which parts of the handle record they want to resolve		
	}
	
	// Reserve a set number of handles
	@PostMapping(value="/reserve")
	public List<String> reserveHandles(@RequestParam(name="reserves") int reserves) {
		return service.reserveHandle(reserves);
	}
	
	//Create Handle
	@PostMapping(value="/**")
	public List<Handles> createHandle(
			@RequestParam(name="url") String url,
			@RequestParam(name="digType") String digType,
			@RequestParam(name="institute") String institute) {	
		return service.createHandle(url, digType, institute);
	}
	
	@PostMapping(value="/update/**")
	public String updateHandle(
			@RequestParam(name="handle") String handle,
			@RequestParam(name="idxs") int[] idxs,
			@RequestParam(name="newVals") String[] newData){
		
		// TODO: Assert all arrays are the same length		
		service.updateHandle(handle, idxs, newData);
		
		return service.resolveHandle(handle);
		
	}
	
	
	@GetMapping(value="/testdup")
	public void checkDuplicate(){
		//service.checkTest();
	}
	
	@DeleteMapping(value="/**")
	public void deleteHandle(@RequestParam(name="handle") String handle){
		service.deleteHandle(handle);
	}
			
	
	
}
