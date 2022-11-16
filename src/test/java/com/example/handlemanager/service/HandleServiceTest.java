package com.example.handlemanager.service;

import com.example.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import com.example.handlemanager.domain.requests.DigitalSpecimenRequest;
import com.example.handlemanager.domain.requests.DoiRecordRequest;
import com.example.handlemanager.domain.requests.HandleRecordRequest;
import com.example.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import com.example.handlemanager.domain.responses.DigitalSpecimenResponse;
import com.example.handlemanager.domain.responses.DoiRecordResponse;
import com.example.handlemanager.domain.responses.HandleRecordResponse;
import com.example.handlemanager.exceptions.PidCreationException;
import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.repositoryobjects.Handles;
import com.example.handlemanager.utils.HandleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.example.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class HandleServiceTest {
	
	@Mock
	HandleRepository handleRep;
	
	@Mock
	private PidTypeService pidTypeService;
	
	@Mock 
	private HandleFactory hf;

	@Mock
	private Clock mockClock;

	@InjectMocks
	private HandleService service;

	private List<byte[]> handlesList;


	Logger logger = Logger.getLogger(HandleServiceTest.class.getName());
	
	@BeforeEach
	void init() {
		// Pid type record
		given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

		// Generating list of handles
		initHandles();
		given(hf.newHandle(anyInt())).willReturn(handlesList);

		// Return empty list to indicate handle is not taken
		given(handleRep.checkDuplicateHandles(handlesList)).willReturn(new ArrayList<>());

		//Date and time
		initTime();
	}

	@Test 
	void createHandleRecordTest() throws PidCreationException {
		// Given
		byte [] handle = handlesList.get(0);
		HandleRecordRequest request = generateTestHandleRequest();
		HandleRecordResponse response_expected = generateTestHandleResponse(handle);
		List<Handles> recordTest = generateTestHandleRecord(handle);
		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		HandleRecordResponse response_received = service.createRecord(request, "hdl");

		// Then
		assertThat(response_received).isEqualTo(response_expected);
	}

	@Test
	void CreateDoiRecordTest() throws PidCreationException {
		// Given
		byte [] handle = handlesList.get(0);
		DoiRecordRequest request = generateTestDoiRequest();
		DoiRecordResponse response_expected = generateTestDoiResponse(handle);
		List<Handles> recordTest = generateTestDoiRecord(handle);
		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		HandleRecordResponse response_received = service.createRecord(request, "doi");

		// Then
		assertThat(response_received).isEqualTo(response_expected);
	}

	@Test
	void CreateDigitalSpecimenTest() throws PidCreationException {
		// Given
		byte [] handle = handlesList.get(0);
		DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
		DigitalSpecimenResponse response_expected = generateTestDigitalSpecimenResponse(handle);
		List<Handles> recordTest = generateTestDigitalSpecimenRecord(handle);
		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		HandleRecordResponse response_received = service.createRecord(request, "ds");

		// Then
		assertThat(response_received).isEqualTo(response_expected);
	}

	@Test
	void CreateDigitalSpecimenBotanyTest() throws PidCreationException {
		// Given
		byte [] handle = handlesList.get(0);
		DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
		DigitalSpecimenBotanyResponse response_expected = generateTestDigitalSpecimenBotanyResponse(handle);
		List<Handles> recordTest = generateTestDigitalSpecimenBotanyRecord(handle);

		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		HandleRecordResponse response_received = service.createRecord(request, "dsB");

		// Then
		assertThat(response_received).isEqualTo(response_expected);
	}
	@Test
	void createBatchHandleRecordTest() throws PidCreationException {
		// Given
		List<HandleRecordRequest> request = generateBatchHandleRequest();
		List<HandleRecordResponse> responseExpected = generateBatchHandleResponse();
		List<Handles> recordTest = generateBatchHandleList();

		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		List<HandleRecordResponse> responseReceived = service.createHandleRecordBatch(request);

		// Then
		assertThat(responseExpected).isEqualTo(responseReceived);
	}
	@Test
	void createBatchDoiRecordTest() throws PidCreationException {
		// Given
		List<DoiRecordRequest> request = generateBatchDoiRequest();
		List<DoiRecordResponse> responseExpected = generateBatchDoiResponse();
		List<Handles> recordTest = generateBatchDoiList();

		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		List<DoiRecordResponse> responseReceived = service.createDoiRecordBatch(request);

		// Then
		assertThat(responseExpected).isEqualTo(responseReceived);
	}
	@Test
	void createBatchDigitalSpecimenTest() throws PidCreationException {
		// Given
		List<DigitalSpecimenRequest> request = generateBatchDigitalSpecimenRequest();
		List<DigitalSpecimenResponse> responseExpected = generateBatchDigitalSpecimenResponse();
		List<Handles> recordTest = generateBatchDigitalSpecimenList();

		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		List<DigitalSpecimenResponse> responseReceived = service.createDigitalSpecimenBatch(request);

		// Then
		assertThat(responseExpected).isEqualTo(responseReceived);
	}

	@Test
	void createBatchDigitalSpecimenBotanyTest() throws PidCreationException {
		// Given
		List<DigitalSpecimenBotanyRequest> request = generateBatchDigitalSpecimenBotanyRequest();
		List<DigitalSpecimenBotanyResponse> responseExpected = generateBatchDigitalSpecimenBotanyResponse();
		List<Handles> recordTest = generateBatchDigitalSpecimenBotanyList();

		given(handleRep.saveAll(recordTest)).willReturn(recordTest);

		// When
		List<DigitalSpecimenBotanyResponse> responseReceived = service.createDigitalSpecimenBotanyBatch(request);

		// Then
		assertThat(responseExpected).isEqualTo(responseReceived);
	}

	private List<HandleRecordRequest> generateBatchHandleRequest(){
		List<HandleRecordRequest> requestList = new ArrayList<>();
		for(int i = 0; i<handlesList.size(); i++){
			requestList.add(generateTestHandleRequest());
		}
		return requestList;
	}

	private List<HandleRecordResponse> generateBatchHandleResponse(){
		List<HandleRecordResponse> responseList = new ArrayList<>();
		for(byte[] h : handlesList){
			responseList.add(generateTestHandleResponse(h));
		}
		return responseList;
	}

	private List<Handles> generateBatchHandleList (){
		List<Handles> handleList = new ArrayList<>();
		for(byte[] h : handlesList){
			handleList.addAll(generateTestHandleRecord(h));
		}
		return handleList;
	}


	private List<DoiRecordRequest> generateBatchDoiRequest(){
		List<DoiRecordRequest> requestList = new ArrayList<>();
		for(int i = 0; i<handlesList.size(); i++){
			requestList.add(generateTestDoiRequest());
		}
		return requestList;
	}

	private List<DoiRecordResponse> generateBatchDoiResponse(){
		List<DoiRecordResponse> responseList = new ArrayList<>();
		for(byte[] h : handlesList){
			responseList.add(generateTestDoiResponse(h));
		}
		return responseList;
	}

	private List<Handles> generateBatchDoiList (){
		List<Handles> handleList = new ArrayList<>();
		for(byte[] h : handlesList){
			handleList.addAll(generateTestDoiRecord(h));
		}
		return handleList;
	}

	private List<DigitalSpecimenRequest> generateBatchDigitalSpecimenRequest(){
		List<DigitalSpecimenRequest> requestList = new ArrayList<>();
		for(int i = 0; i<handlesList.size(); i++){
			requestList.add(generateTestDigitalSpecimenRequest());
		}
		return requestList;
	}

	private List<DigitalSpecimenResponse> generateBatchDigitalSpecimenResponse(){
		List<DigitalSpecimenResponse> responseList = new ArrayList<>();
		for(byte[] h : handlesList){
			responseList.add(generateTestDigitalSpecimenResponse(h));
		}
		return responseList;
	}

	private List<Handles> generateBatchDigitalSpecimenList (){
		List<Handles> handleList = new ArrayList<>();
		for(byte[] h : handlesList){
			handleList.addAll(generateTestDigitalSpecimenRecord(h));
		}
		return handleList;
	}

	private List<DigitalSpecimenBotanyRequest> generateBatchDigitalSpecimenBotanyRequest(){
		List<DigitalSpecimenBotanyRequest> requestList = new ArrayList<>();
		for(int i = 0; i<handlesList.size(); i++){
			requestList.add(generateTestDigitalSpecimenBotanyRequest());
		}
		return requestList;
	}

	private List<DigitalSpecimenBotanyResponse> generateBatchDigitalSpecimenBotanyResponse(){
		List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
		for(byte[] h : handlesList){
			responseList.add(generateTestDigitalSpecimenBotanyResponse(h));
		}
		return responseList;
	}

	private List<Handles> generateBatchDigitalSpecimenBotanyList (){
		List<Handles> handleList = new ArrayList<>();
		for(byte[] h : handlesList){
			handleList.addAll(generateTestDigitalSpecimenBotanyRecord(h));
		}
		return handleList;
	}



	private void initTime() {
		Clock fixedClock = Clock.fixed(CREATED, ZoneOffset.UTC);
		doReturn(fixedClock.instant()).when(mockClock).instant();
		doReturn(fixedClock.getZone()).when(mockClock).getZone();
	}

	private void initHandles(){
		handlesList = new ArrayList<>();
		handlesList.add(HANDLE.getBytes());
		handlesList.add(HANDLE_ALT.getBytes());
	}
	
	
	/*
	 * createHandleRecordBatch
	 * createDoiRecordBatch
	 * createDigitalSpecimenBatch
	 * createDigitalSpecimenBotanyBatch
	 * 
	 * createRecord
	 * 		recordType = "hdl"
	 * 		recordType = "doi"
	 * 		recordType = "ds"
	 * 		recordType = "dsB"
	 * 
	 */
	
	
}
