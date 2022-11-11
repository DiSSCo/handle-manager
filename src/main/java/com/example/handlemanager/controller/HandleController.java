package com.example.handlemanager.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.service.HandleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.logging.*;
//import net.handle.hdllib.HandleException;
//import net.handle.hdllib.HandleValue;

@RestController  //Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")  // Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
public class HandleController {
	Logger logger = Logger.getLogger(HandleController.class.getName());
	
	@Autowired
	private HandleService service;
	
	// ** Create Records: handle, doi, digital specimen, botany specimen **
	
	// Batch creation
	
	@PostMapping(value="/createRecordBatch", params= "pidType=handle")
	public ResponseEntity<?> createHandleRecordBatch(@RequestBody List<HandleRecordRequest> hdl) {
		return ResponseEntity.ok(service.createHandleRecordBatch(hdl));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=doi")
	public ResponseEntity<?> createDoiRecordBatch(@RequestBody List<DoiRecordRequest> doi) {
		return ResponseEntity.ok(service.createDoiRecordBatch(doi));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=digitalSpecimen")
	public ResponseEntity<?> createDigitalSpecimenBatch(@RequestBody List<DigitalSpecimenRequest> ds) {
		return ResponseEntity.ok(service.createDigitalSpecimenBatch(ds));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=digitalSpecimenBotany")
	public ResponseEntity<?> createDigitalSpecimenBotanyBatch(@RequestBody List<DigitalSpecimenBotanyRequest> dsB) {
		return ResponseEntity.ok(service.createDigitalSpecimenBotanyBatch(dsB));
	}
	
	// Create Single Record
	@PostMapping(value="/createRecord", params= "pidType=handle")
	public ResponseEntity<?> createRecord(
			@RequestBody HandleRecordRequest hdl) {
		try {
			return ResponseEntity.ok(service.createRecord(hdl, "hdl"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unable to create record", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=doi")
	public ResponseEntity<?> createRecord(
			@RequestBody DoiRecordRequest doi){		
		try {
			return ResponseEntity.ok(service.createRecord(doi, "doi"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unable to create record", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimen")
	public ResponseEntity<?> createRecord(
			@RequestBody DigitalSpecimenRequest ds) {
		try {
			return ResponseEntity.ok(service.createRecord(ds, "ds"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unable to create record", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimenBotany")
	public ResponseEntity<?> createRecord(
			@RequestBody DigitalSpecimenBotanyRequest dsB) {
		try {
			return ResponseEntity.ok(service.createRecord(dsB, "dsB"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unable to create record", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	// Hellos and getters
	
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
	

	
	/*
	
	public ResponseEntity<?> resolveHandle(@RequestParam(name="pid") String pid) throws JsonMappingException, JsonProcessingException{
		HandleRecord record = service.resolveHandleRecord(pid);
		if (record.isEmpty()) {
			return new ResponseEntity<>("Handle record does not exist", HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(record);
	}
	
	// Resolve a handle
	
	
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
	} */
	
}
