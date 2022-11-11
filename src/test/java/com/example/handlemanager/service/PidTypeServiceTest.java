package com.example.handlemanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.repository.HandleRepository;

import static com.example.handlemanager.testUtils.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PidTypeServiceTest {
	// NOTE: Pid Type Record => PTR in naming convention because these 
	// PTR refers to the handle record that stores information about a type
	
	@Mock
	private HandleRepository handleRep;
	
	@Mock
	private Clock clock;
	
	@InjectMocks
	private PidTypeService pidTypeService;
	
	// Time stuff
	private MockedStatic mockedStatic;
	private long timestamp;
	
	// These are what the values are called in the handle record
	private String pid;
	private String pidType;
	private String primaryNameFromPid;
	private String registrationAgencyDoiName;
	
	Logger logger =  Logger.getLogger(PidTypeServiceTest.class.getName());	
	
	private byte[] recordPid;
	
	private List<Handles> typeRecord;
	
	
	@BeforeEach
	public void init() {
		timestamp = initTimestamp();		
	}
	
	@Test
	public void testPidTypeRecordResolutionHandle() {
		initTestPidTypeRecordHandle();
		
		when(handleRep.resolveHandle(eq(recordPid))).thenReturn(typeRecord);
		
		String expected = PTR_HANDLE_RECORD;
		String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);
		
		logger.info("Expected: " + expected);
		logger.info("Returned: " + returned);
		
		assert(expected.equals(returned));
	}
	
	@Test
	public void TestPidTypeRecordResolutionDoi() {
		initTestPidTypeRecordDoi();
		
		when(handleRep.resolveHandle(eq(recordPid))).thenReturn(typeRecord);
		String expected = PTR_DOI_RECORD;
		String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);
		
		logger.info("Expected: "  + expected);
		logger.info("Returned: " + returned);
		
		assert(expected.equals(returned));
	}
	
	
	private void initTestPidTypeRecordHandle() {		
		recordPid = PID_ISSUER_PID.getBytes();
		
		pid = PTR_PID;
		pidType = PTR_TYPE;
		primaryNameFromPid = PTR_PRIMARY_NAME;
		
		typeRecord = initTestPidTypeRecord(false);
	}
	
	private void initTestPidTypeRecordDoi() {		
		recordPid = PID_ISSUER_PID.getBytes();
		
		pid = PTR_PID_DOI;
		pidType = PTR_TYPE_DOI;
		primaryNameFromPid = PTR_PRIMARY_NAME;
		
		registrationAgencyDoiName = PTR_REGISTRATION_DOI_NAME;
		
		typeRecord = initTestPidTypeRecord(true);
		
	}
	
	private List<Handles> initTestPidTypeRecord(boolean isDoi){
		List<Handles> record = new ArrayList<>();
		int i = 1;
		record.add(new Handles(recordPid, i++, "pid", pid, timestamp));
		record.add(new Handles(recordPid, i++, "pidType", pidType, timestamp));
		record.add(new Handles(recordPid, i++, "primaryNameFromPid", primaryNameFromPid, timestamp));
		
		if (isDoi) {
			registrationAgencyDoiName = PTR_REGISTRATION_DOI_NAME;
			record.add(new Handles(recordPid, i++, "registrationAgencyDoiName", registrationAgencyDoiName, timestamp));
		}
		return record;
	}
	
	private long initTimestamp() {
		//CREATED_INSTANT.getEpochSecond();
		return CREATED.getEpochSecond();
	}
	
}
