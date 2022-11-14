package com.example.handlemanager.controller;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import com.example.handlemanager.domain.responses.DigitalSpecimenResponse;
import com.example.handlemanager.domain.responses.DoiRecordResponse;
import com.example.handlemanager.domain.responses.HandleRecordResponse;
import com.example.handlemanager.service.HandleService;
import com.example.handlemanager.service.PidTypeServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.example.handlemanager.testUtils.TestUtils.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(HandleController.class)
public class HandleControllerTest {
	
	@MockBean
	HandleService service;
	
	@Autowired
	private MockMvc mockMvc;

	ObjectMapper mapper = new ObjectMapper();
	final int requestLen = 3;
	
	Logger logger =  Logger.getLogger(PidTypeServiceTest.class.getName());
	
	@BeforeEach
	public void init() {
		
	}
	
	// Single Record Creation
	@Test
	public void handleRecordCreationTest() throws Exception {
		HandleRecordRequest request = generateTestHandleRequest();
		HandleRecordResponse response = generateTestHandleResponse(HANDLE.getBytes());
		
		when(service.createRecord(eq(request), eq("hdl"))).thenReturn(response);
			
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "handle")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pid").value(response.getPid()))
				.andExpect(jsonPath("$.pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$.digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$.digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$.locs").value(response.getLocs()))
				.andExpect(jsonPath("$.issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$.issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$.pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$.hs_ADMIN").value(response.getHS_ADMIN()));
	}
	
	@Test
	public void doiRecordCreationTest() throws Exception {
		DoiRecordRequest request = generateTestDoiRequest();
		DoiRecordResponse response = generateTestDoiResponse(HANDLE.getBytes());
		
		when(service.createRecord(eq(request), eq("doi"))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "doi")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pid").value(response.getPid()))
				.andExpect(jsonPath("$.pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$.digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$.digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$.locs").value(response.getLocs()))
				.andExpect(jsonPath("$.issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$.issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$.pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$.hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$.referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$.referent").value(response.getReferent()));
	}
	
	@Test
	public void digitalSpecimenCreationTest() throws Exception {
		DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
		DigitalSpecimenResponse response = generateTestDigitalSpecimenResponse(HANDLE.getBytes());
		
		when(service.createRecord(eq(request), eq("ds"))).thenReturn(response);
		
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "digitalSpecimen")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pid").value(response.getPid()))
				.andExpect(jsonPath("$.pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$.digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$.digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$.locs").value(response.getLocs()))
				.andExpect(jsonPath("$.issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$.issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$.pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$.hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$.referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$.referent").value(response.getReferent()))
				.andExpect(jsonPath("$.digitalOrPhysical").value(response.getDigitalOrPhysical()))
				.andExpect(jsonPath("$.specimenHost").value(response.getSpecimenHost()))
				.andExpect(jsonPath("$.inCollectionFacility").value(response.getInCollectionFacility()));
				//.andExpect(jsonPath("$.digitalOrPhysical").value(response.getDigitalOrPhysical()));
	}
	
	@Test
	public void digitalSpecimenBotanyCreationTest() throws Exception {
		DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest(); 
		DigitalSpecimenBotanyResponse response = generateTestDigitalSpecimenBotanyResponse(HANDLE.getBytes());
	
		when(service.createRecord(eq(request), eq("dsB"))).thenReturn(response);
		
		mockMvc.perform(post("/api/createRecord")
				.content(mapper.writeValueAsString(request))
				.param("pidType", "digitalSpecimenBotany")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pid").value(response.getPid()))
				.andExpect(jsonPath("$.pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$.digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$.digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$.locs").value(response.getLocs()))
				.andExpect(jsonPath("$.issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$.issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$.pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$.hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$.referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$.referent").value(response.getReferent()))
				.andExpect(jsonPath("$.digitalOrPhysical").value(response.getDigitalOrPhysical()))
				.andExpect(jsonPath("$.specimenHost").value(response.getSpecimenHost()))
				.andExpect(jsonPath("$.inCollectionFacility").value(response.getInCollectionFacility()))
				.andExpect(jsonPath("$.objectType").value(response.getObjectType()))
				.andExpect(jsonPath("$.preservedOrLiving").value(response.getPreservedOrLiving()));
	}
	
	// Batch Record Creation
	
	@Test
	public void handleRecordBatchCreationTest() throws Exception {
		List<HandleRecordRequest> requestList = buildHandleRequestList();
		List<HandleRecordResponse> responseList = buildHandleResponseList();
		HandleRecordResponse response = responseList.get(0);
		
		when(service.createHandleRecordBatch(eq(requestList))).thenReturn(responseList);
		
		mockMvc.perform(post("/api/createRecordBatch")
				.content(mapper.writeValueAsString(requestList))
				.param("pidType", "handle")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$[0].digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$[0].digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$[0].locs").value(response.getLocs()))
				.andExpect(jsonPath("$[0].issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$[0].issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$[0].pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$[0].hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$.length()").value(requestLen));
	}
	
	@Test
	public void doiRecordBatchCreationTest() throws Exception {
		List<DoiRecordRequest> requestList = buildDoiRequestList();
		List<DoiRecordResponse> responseList = buildDoiResponseList();
		DoiRecordResponse response = responseList.get(0);
		
		when(service.createDoiRecordBatch(eq(requestList))).thenReturn(responseList);
		
		mockMvc.perform(post("/api/createRecordBatch")
				.content(mapper.writeValueAsString(requestList))
				.param("pidType", "doi")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$[0].digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$[0].digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$[0].locs").value(response.getLocs()))
				.andExpect(jsonPath("$[0].issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$[0].issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$[0].pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$[0].hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$[0].referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$[0].referent").value(response.getReferent()))
				.andExpect(jsonPath("$.length()").value(requestLen));
	}
	
	@Test
	public void digitalSpecimenBatchCreationTest() throws Exception {
		List<DigitalSpecimenRequest> requestList = buildDigitalSpecimenRequestList();
		List<DigitalSpecimenResponse> responseList = buildDigitalSpecimenResponseList();
		DigitalSpecimenResponse response = responseList.get(0);		
		
		when(service.createDigitalSpecimenBatch(eq(requestList))).thenReturn(responseList);
		
		mockMvc.perform(post("/api/createRecordBatch")
				.content(mapper.writeValueAsString(requestList))
				.param("pidType", "digitalSpecimen")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$[0].digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$[0].digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$[0].locs").value(response.getLocs()))
				.andExpect(jsonPath("$[0].issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$[0].issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$[0].pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$[0].hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$[0].referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$[0].referent").value(response.getReferent()))
				.andExpect(jsonPath("$[0].digitalOrPhysical").value(response.getDigitalOrPhysical()))
				.andExpect(jsonPath("$[0].specimenHost").value(response.getSpecimenHost()))
				.andExpect(jsonPath("$[0].inCollectionFacility").value(response.getInCollectionFacility()))
				.andExpect(jsonPath("$.length()").value(requestLen));
			
	}
	
	@Test
	public void digitalSpecimenBotanyBatchCreationTest() throws Exception {
		List<DigitalSpecimenBotanyRequest> requestList = buildDigitalSpecimenBotanyRequestList();
		List<DigitalSpecimenBotanyResponse> responseList = buildDigitalSpecimenBotanyResponseList();
		DigitalSpecimenBotanyResponse response = responseList.get(0);
		
		when(service.createDigitalSpecimenBotanyBatch(eq(requestList))).thenReturn(responseList);
		
		mockMvc.perform(post("/api/createRecordBatch")
				.content(mapper.writeValueAsString(requestList))
				.param("pidType", "digitalSpecimenBotany")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pid").value(response.getPid()))
				.andExpect(jsonPath("$[0].pidIssuer").value(response.getPidIssuer()))
				.andExpect(jsonPath("$[0].digitalObjectType").value(response.getDigitalObjectType()))
				.andExpect(jsonPath("$[0].digitalObjectSubtype").value(response.getDigitalObjectSubtype()))
				.andExpect(jsonPath("$[0].locs").value(response.getLocs()))
				.andExpect(jsonPath("$[0].issueDate").value(response.getIssueDate()))
				.andExpect(jsonPath("$[0].issueNumber").value(response.getIssueNumber()))
				.andExpect(jsonPath("$[0].pidKernelMetadataLicense").value(response.getPidKernelMetadataLicense()))
				.andExpect(jsonPath("$[0].hs_ADMIN").value(response.getHS_ADMIN()))
				.andExpect(jsonPath("$[0].referentDoiName").value(response.getReferentDoiName()))
				.andExpect(jsonPath("$[0].referent").value(response.getReferent()))
				.andExpect(jsonPath("$[0].digitalOrPhysical").value(response.getDigitalOrPhysical()))
				.andExpect(jsonPath("$[0].specimenHost").value(response.getSpecimenHost()))
				.andExpect(jsonPath("$[0].inCollectionFacility").value(response.getInCollectionFacility()))
				.andExpect(jsonPath("$[0].objectType").value(response.getObjectType()))
				.andExpect(jsonPath("$[0].preservedOrLiving").value(response.getPreservedOrLiving()))
				.andExpect(jsonPath("$.length()").value(requestLen));
	}
	
	
	// For completion's sake
	@Test
	public void helloTest() throws Exception {
		mockMvc.perform(get("/api/hello")).andExpect(status().isOk());
	}
	
	
	
	// Build Request and Response Lists
	
	// Handles
	private List<HandleRecordRequest> buildHandleRequestList(){
		List<HandleRecordRequest> requestList= new ArrayList<>();
		HandleRecordRequest request = generateTestHandleRequest();
		
		for (int i=0; i<requestLen; i++) {
			requestList.add(request);
		}
		return requestList;
	}
	private List<HandleRecordResponse> buildHandleResponseList(){
		List<HandleRecordResponse> responseList= new ArrayList<>();
		HandleRecordResponse response = generateTestHandleResponse(HANDLE.getBytes());
		
		for (int i=0; i<requestLen; i++) {
			responseList.add(response);
		}
		return responseList;
	}
	
	// DOIs
	private List<DoiRecordRequest> buildDoiRequestList(){
		List<DoiRecordRequest> requestList= new ArrayList<>();
		DoiRecordRequest request = generateTestDoiRequest();
		for (int i=0; i<requestLen; i++) {
			requestList.add(request);
		}
		return requestList;
	}
	private List<DoiRecordResponse> buildDoiResponseList(){
		List<DoiRecordResponse> responseList= new ArrayList<>();
		DoiRecordResponse response = generateTestDoiResponse(HANDLE.getBytes());
		
		for (int i=0; i<requestLen; i++) {
			responseList.add(response);
		}
		return responseList;
	}
	
	// DigitalSpecimens
	private List<DigitalSpecimenRequest> buildDigitalSpecimenRequestList(){
		List<DigitalSpecimenRequest> requestList= new ArrayList<>();
		DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
		for (int i=0; i<requestLen; i++) {
			requestList.add(request);
		}
		return requestList;
	}
	private List<DigitalSpecimenResponse> buildDigitalSpecimenResponseList(){
		List<DigitalSpecimenResponse> responseList= new ArrayList<>();
		DigitalSpecimenResponse response = generateTestDigitalSpecimenResponse(HANDLE.getBytes());

		for (int i=0; i<requestLen; i++) {
			responseList.add(response);
		}
		return responseList;
	}
	
	
	//DigitalSpecimenBotany
	private List<DigitalSpecimenBotanyRequest> buildDigitalSpecimenBotanyRequestList(){
		List<DigitalSpecimenBotanyRequest> responseList= new ArrayList<>();
		DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
		for (int i=0; i<requestLen; i++) {
			responseList.add(request);
		}
		return responseList;
	}
	private List<DigitalSpecimenBotanyResponse> buildDigitalSpecimenBotanyResponseList(){
		List<DigitalSpecimenBotanyResponse> responseList= new ArrayList<>();
		DigitalSpecimenBotanyResponse response = generateTestDigitalSpecimenBotanyResponse(HANDLE.getBytes());
		
		for (int i=0; i<requestLen; i++) {
			responseList.add(response);
		}
		return responseList;
	}
	
}
