package com.example.handlemanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.servlet.WebConfig;

import com.example.handlemanager.domain.requests.*;
import com.example.handlemanager.domain.responses.*;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.service.HandleService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(HandleController.class)
public class HandleControllerTest {
	
	@MockBean
	HandleService service;
	
	@Autowired
	private MockMvc mockMvc;

	ObjectMapper mapper = new ObjectMapper();
	final String testVal = "abc";
	final int requestLen = 3;
	
	
	@Test
	public void handleRecordCreationTest() throws PidCreationException, Exception {
		HandleRecordRequest request = new HandleRecordRequest();
		HandleRecordResponse response = new HandleRecordResponse();
		response.setPid(testVal);
		
		when(service.createRecord(any(HandleRecordRequest.class), any(String.class))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "handle")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.pid").value(testVal));
	}
	
	@Test
	public void doiRecordCreationTest() throws PidCreationException, Exception {
		DoiRecordRequest request = new DoiRecordRequest(); 
		DoiRecordResponse response = new DoiRecordResponse();
		response.setReferent(testVal);
		
		when(service.createRecord(any(DoiRecordRequest.class), any(String.class))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "doi")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.referent").value(testVal));
	}
	
	@Test
	public void digitalSpecimenCreationTest() throws PidCreationException, Exception {
		DigitalSpecimenRequest request = new DigitalSpecimenRequest(); 
		DigitalSpecimenResponse response = new DigitalSpecimenResponse();
		response.setDigitalOrPhysical(testVal);
		
		when(service.createRecord(any(DigitalSpecimenRequest.class), any(String.class))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "digitalSpecimen")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.digitalOrPhysical").value(testVal));
	}
	
	@Test
	public void digitalSpecimenBotanyCreationTest() throws PidCreationException, Exception {
		DigitalSpecimenBotanyRequest request = new DigitalSpecimenBotanyRequest(); 
		DigitalSpecimenBotanyResponse response = new DigitalSpecimenBotanyResponse();
		response.setObjectType(testVal);
		
		when(service.createRecord(any(DigitalSpecimenRequest.class), any(String.class))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "digitalSpecimenBotany")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.objectType").value(testVal));
	}
	
	@Test
	public void handleRecordBatchCreationTest() throws PidCreationException, Exception {
		HandleRecordRequest request = new HandleRecordRequest();
		HandleRecordResponse response = new HandleRecordResponse();
		response.setPid(testVal);
		
		when(service.createRecord(any(HandleRecordRequest.class), any(String.class))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecordBatch")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "handle")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	
	// Build Lists
	
	private List<HandleRecordRequest> buildHandleRequestList(){
		List<HandleRecordRequest> requestList= new ArrayList<HandleRecordRequest>();
		for (int i=0; i<requestLen; i++) {
			requestList.add(new HandleRecordRequest());
		}
		return requestList;
	}
	
	private List<DoiRecordRequest> buildDoiRequestList(){
		List<DoiRecordRequest> requestList= new ArrayList<DoiRecordRequest>();
		for (int i=0; i<requestLen; i++) {
			requestList.add(new DoiRecordRequest());
		}
		return requestList;
	}
	
	private List<DigitalSpecimenRequest> buildDigitalSpecimenRequestList(){
		List<DigitalSpecimenRequest> requestList= new ArrayList<DigitalSpecimenRequest>();
		for (int i=0; i<requestLen; i++) {
			requestList.add(new DigitalSpecimenRequest());
		}
		return requestList;
	}
	
	private List<DigitalSpecimenBotanyRequest> buildDigitalSpecimenBotanyRequestList(){
		List<DigitalSpecimenBotanyRequest> requestList= new ArrayList<DigitalSpecimenBotanyRequest>();
		for (int i=0; i<requestLen; i++) {
			requestList.add(new DigitalSpecimenBotanyRequest());
		}
		return requestList;
	}
	
	
	
	
}
