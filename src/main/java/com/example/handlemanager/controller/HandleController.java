package com.example.handlemanager.controller;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.service.HandleService;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController  //Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")  // Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
public class HandleController {

	@Autowired
	private HandleService service;

	private String ERROR_MESSAGE = "Unable to create Pid Record";
	
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
			return new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=doi")
	public ResponseEntity<?> createRecord(
			@RequestBody DoiRecordRequest doi){		
		try {
			return ResponseEntity.ok(service.createRecord(doi, "doi"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimen")
	public ResponseEntity<?> createRecord(
			@RequestBody DigitalSpecimenRequest ds) {
		try {
			return ResponseEntity.ok(service.createRecord(ds, "ds"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimenBotany")
	public ResponseEntity<?> createRecord(
			@RequestBody DigitalSpecimenBotanyRequest dsB) {
		try {
			return ResponseEntity.ok(service.createRecord(dsB, "dsB"));
		} catch (PidCreationException e) {
			e.printStackTrace();
			return new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
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

}
