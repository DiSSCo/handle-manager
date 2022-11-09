package com.example.handlemanager.service;

import static com.example.handlemanager.utils.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.handlemanager.domain.responses.*;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.domain.requests.*;
import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.utils.HandleFactory;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class HandleServiceTest {
	
	@Mock
	HandleRepository handleRep;
	
	@Mock
	private PidTypeService pidTypeService;
	
	@Mock 
	private HandleFactory hf;
	
	@Mock
	private Clock clock;
	
	@InjectMocks
	private HandleService service; 
	
	private HandleRecordRequest request;
	private HandleRecordResponse response;
	
	private MockedStatic<Instant> mockedStatic;
	private List<Handles> handleRecord;
	private String pidTypeRecord = PTR_PID_ISSUER;
	final private String testVal = "abc";
	private List<byte[]> handleList;
	private final static LocalDate LOCAL_DATE = LocalDate.of(1989, 01, 13);
	
	
	
	@BeforeEach
	public void init() {
		request = generateTestHandleRequest();
		handleRecord = initHandleRecord();
		
		handleList = generateByteHandleList();
		
		initDate();
		
	}
	
	@AfterEach
	  void destroy() {
	    mockedStatic.close();
	  }
	
	
	@Test 
	public void createHandleRecordTest() {
		// Given handle, timestamp, date
		
		when(pidTypeService.resolveTypePid(any(String.class))).thenReturn(pidTypeRecord);
		when(handleRep.saveAll(ArgumentMatchers.<Handles>anyList())).thenReturn(handleRecord);
		when(hf.newHandle(any(int.class))).thenReturn(handleList);
		//when(now.)
		
		
	}
	
	private HandleRecordResponse initTestResponse() {
		HandleRecordResponse response = new HandleRecordResponse();
		response.setPid("20.5000.1025/abc");
		response.setPidIssuer(testVal);
		response.setDigitalObjectSubtype(testVal);
		response.setDigitalObjectType(testVal);
		response.setLoc(testVal);
		response.setIssueDate(testVal);
		response.setPidStatus(testVal);
		return response;	
	}
	
	
	private List<Handles> initHandleRecord(){
		return null;
	}
		
	private void initDate() {
		clock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));
	}
	
	private void initTime() {
		Instant instant = Instant.now(clock);
		mockedStatic = mockStatic(Instant.class);
		mockedStatic = mockStatic(Instant.class);
		mockedStatic.when(Instant::now).thenReturn(instant); // Is this global?
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
