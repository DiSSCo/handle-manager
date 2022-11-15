package com.example.handlemanager.service;

import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.repositoryobjects.Handles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.example.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PidTypeServiceTest {
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
	
	private byte[] recordPid;
	
	private List<Handles> typeRecord;
	
	
	@BeforeEach
	void init() {
		timestamp = initTimestamp();		
	}
	
	@Test
	void testPidTypeRecordResolutionHandle() {
		// Given
		initTestPidTypeRecordHandle();
		String expected = PTR_HANDLE_RECORD;

		given(handleRep.resolveHandle(recordPid)).willReturn(typeRecord);

		// When
		String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);

		// Then
		assertThat(expected).isEqualTo(returned);
	}
	
	@Test
	void TestPidTypeRecordResolutionDoi() {
		//Given
		initTestPidTypeRecordDoi();
		String expected = PTR_DOI_RECORD;
		given(handleRep.resolveHandle(recordPid)).willReturn(typeRecord);

		// When
		String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);

		// Then
		assertThat(expected).isEqualTo(returned);
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
