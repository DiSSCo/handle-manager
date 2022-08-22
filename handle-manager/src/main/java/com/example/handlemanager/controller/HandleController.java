package com.example.handlemanager.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecord;
import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecordSpecimen;
import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecordSpecimenMerged;
import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecordSpecimenSplit;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.service.HandleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.handle.hdllib.HandleException;
import java.util.logging.*;
//import net.handle.hdllib.HandleException;
//import net.handle.hdllib.HandleValue;

@RestController  //Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")  // Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
public class HandleController {
	Logger logger = Logger.getLogger(HandleController.class.getName());
	
	@Autowired
	private HandleService service;
	
	@GetMapping(value="/hello") 
	public ResponseEntity<String> hello() {
		return new ResponseEntity<>("Hello", HttpStatus.OK);
	}
	
	// List all handle values
	@GetMapping(value="/all")
	public ResponseEntity<?> getAllHandles() throws HttpResponseException{
		List<String> handleList = service.getHandles();
		if (handleList.isEmpty()){
			return new ResponseEntity<>("Unable to locate handles", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(handleList);
	}
	
	@GetMapping(value="/subset")
	public ResponseEntity<?> getAllHandlesByPidStatus(
			@RequestParam(name="pidStatus") String pidStatus) {
		List<String> handleList = service.getHandles(pidStatus);
		if (handleList.isEmpty()){
			return new ResponseEntity<>("Unable to locate handles", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(handleList);
	}
	
	// Resolve a handle
	@GetMapping(value="/**")
	public ResponseEntity<?> resolveHandle(@RequestParam(name="handle") String handle) throws JsonMappingException, JsonProcessingException{
		HandleRecord record = service.resolveHandleRecord(handle);
		if (record.isEmpty()) {
			return new ResponseEntity<>("Handle record does not exist", HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(record);
		
		//TODO: might be nice to allow users to select which parts of the handle record they want to resolve		
	}
	
	// Reserve a set number of handles
	@PostMapping(value="/reserve")
	public ResponseEntity<?> reserveHandles(@RequestParam(name="reserves") int reserves) {
		if (reserves < 1) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Must request one or more handles");
		}
		HashSet<String> reservedHandles = service.reserveHandle(reserves);
		if (reservedHandles.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Unable to reserve handles");
		}
		return ResponseEntity.ok(reservedHandles);
	}
	
	
	@PostMapping(value="/**")
	public ResponseEntity<?> createSpecimenRecord(
			@RequestBody HandleRecordSpecimen specimen) throws JsonMappingException, JsonProcessingException {
		HandleRecordSpecimen postedSpecimen = service.createHandleSpecimen(specimen);
		if (postedSpecimen.isEmpty()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to Post record");
		}
		return ResponseEntity.ok(postedSpecimen);
	}
	
	@PostMapping(value="/update/")
	public ResponseEntity<?> updateHandleBody(
			@RequestParam(name="handle") String handle, 
			@RequestBody HandleRecordSpecimen specimen) throws JsonMappingException, JsonProcessingException, JSONException{
		
		service.updateHandle(handle, specimen);
		HandleRecord posted = service.resolveHandleRecord(handle);
		
		return ResponseEntity.ok(posted);
	}
	

	@PutMapping(value="/merge")
	public ResponseEntity<?> mergeHandle(
			@RequestBody HandleRecordSpecimenMerged newRecord) throws JsonProcessingException {
		if (newRecord.getLegacyHandles().size() < 2) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please list 2 or more handles to be merged");
		}
		if(containsDuplicates(newRecord.getLegacyHandles())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Merged handles must be distinct");
		}
		
		HandleRecordSpecimenMerged mergedRecord = service.mergeHandle(newRecord);
		
		return ResponseEntity.ok(mergedRecord);
		//HandleRecordSpecimenMerged mergedRecord = service.mergeRecord(newRecord);
		
		//record.setDigitalSpecimenRecordMerged("123".getBytes());
		
	}
	
	private boolean containsDuplicates(List<String> handles) {
		Set<String> set = new HashSet<String>(handles);
		return(set.size() < handles.size());
	}
	
	
	@PutMapping(value="/split")
	public ResponseEntity<?> splitHandle(
			@RequestParam String handle,
			@RequestBody List<HandleRecordSpecimenSplit> newRecords) throws JsonMappingException, JsonProcessingException{
		
						
		if (service.resolveHandleRecord(handle).isEmpty()) {
			return new ResponseEntity<>("Handle record does not exist", HttpStatus.NOT_FOUND);
		}
		
		List<HandleRecordSpecimenSplit> postedRecords = service.splitHandleRecord(newRecords, handle);
		
		return ResponseEntity.ok(postedRecords);
		
	}
	
	
	@DeleteMapping(value="/**")
	public ResponseEntity<?> deleteHandle(
			@RequestParam(name="handle") String handleStr,
			@RequestBody(required=false) String tombstone) throws JsonMappingException, JsonProcessingException{
		
		if (service.resolveHandleRecord(handleStr).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Handle not found");
		}
		
		byte[] handle = handleStr.getBytes();
		
		service.deleteHandleSafe(handleStr);
		
		return ResponseEntity.ok(service.createTombstone(handle, tombstone));
	}
	
}
