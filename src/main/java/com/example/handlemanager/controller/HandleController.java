package com.example.handlemanager.controller;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.domain.responses.*;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.exceptions.PidResolutionException;
import com.example.handlemanager.service.HandleService;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController  //Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")  // Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
@RequiredArgsConstructor
@ControllerAdvice
public class HandleController {

	@Autowired
	private final HandleService service;

	// ** Create Records: handle, doi, digital specimen, botany specimen **
	
	// Batch creation
	
	@PostMapping(value="/createRecordBatch", params= "pidType=handle")
	public ResponseEntity<List<HandleRecordResponse>> createHandleRecordBatch(@RequestBody List<HandleRecordRequest> hdl) {
		return ResponseEntity.ok(service.createHandleRecordBatch(hdl));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=doi")
	public ResponseEntity<List<DoiRecordResponse>> createDoiRecordBatch(@RequestBody List<DoiRecordRequest> doi) {
		return ResponseEntity.ok(service.createDoiRecordBatch(doi));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=digitalSpecimen")
	public ResponseEntity<List<DigitalSpecimenResponse>> createDigitalSpecimenBatch(@RequestBody List<DigitalSpecimenRequest> ds) {
		return ResponseEntity.ok(service.createDigitalSpecimenBatch(ds));
	}
	
	@PostMapping(value="/createRecordBatch", params= "pidType=digitalSpecimenBotany")
	public ResponseEntity<List<DigitalSpecimenBotanyResponse>> createDigitalSpecimenBotanyBatch(@RequestBody List<DigitalSpecimenBotanyRequest> dsB) {
		return ResponseEntity.ok(service.createDigitalSpecimenBotanyBatch(dsB));
	}
	
	// Create Single Record
	@PostMapping(value="/createRecord", params= "pidType=handle")
	public ResponseEntity<HandleRecordResponse> createRecord(
			@RequestBody HandleRecordRequest hdl) throws PidCreationException {
		return ResponseEntity.ok(service.createRecord(hdl, "hdl"));
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=doi")
	public ResponseEntity<HandleRecordResponse> createRecord(
			@RequestBody DoiRecordRequest doi) throws PidCreationException {
		return ResponseEntity.ok(service.createRecord(doi, "doi"));
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimen")
	public ResponseEntity<HandleRecordResponse> createRecord(
			@RequestBody DigitalSpecimenRequest ds) throws PidCreationException {
		return ResponseEntity.ok(service.createRecord(ds, "ds"));
	}
	
	@PostMapping(value="/createRecord",  params= "pidType=digitalSpecimenBotany")
	public ResponseEntity<HandleRecordResponse> createRecord(
			@RequestBody DigitalSpecimenBotanyRequest dsB) throws PidCreationException {
		return ResponseEntity.ok(service.createRecord(dsB, "dsB"));
	}

	// Hellos and getters
	
	@GetMapping(value="/hello") 
	public ResponseEntity<String> hello() {
		return new ResponseEntity<>("Hello", HttpStatus.OK);
	}
	
	// List all handle values
	@GetMapping(value="/all")
	public ResponseEntity<List<String>> getAllHandles() throws PidResolutionException {
		List<String> handleList = service.getHandles();
		if (handleList.isEmpty()){
			throw new PidResolutionException("Unable to locate handles");
		}
		return ResponseEntity.ok(handleList);
	}
	
	@GetMapping(value="/subset")
	public ResponseEntity<List<String>> getAllHandlesByPidStatus(
			@RequestParam(name="pidStatus") String pidStatus) throws PidResolutionException {
		List<String> handleList = service.getHandles(pidStatus);
		if (handleList.isEmpty()){
			throw new PidResolutionException("Unable to resolve pids");
		}
		return ResponseEntity.ok(handleList);
	}

	@ExceptionHandler(PidCreationException.class)
	private ResponseEntity<String> pidCreationException(PidCreationException e){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	@ExceptionHandler(PidResolutionException.class)
	private ResponseEntity<String> pidResolutionException(PidResolutionException e){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

}
