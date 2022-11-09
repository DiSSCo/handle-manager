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

import static com.example.handlemanager.utils.TestUtils.*;
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
	private final String pid = PTR_PID;
	private final String pidType = PTR_TYPE;
	private final String primaryNameFromPid = PTR_PRIMARY_NAME;
	
	private final byte[] recordPid = PID_ISSUER_PID.getBytes();
	
	private List<Handles> typeRecord;
	
	
	@BeforeEach
	public void init() {
		timestamp = initTimestamp();
		initTestPidTypeRecord();
		
	}
	
	@Test
	public void testPidTypeRecordResolution() {
		//when(handleRep.resolveHandle(eq(recordPid))).thenReturn(typeRecord);
		when(handleRep.resolveHandle(any(byte[].class))).thenReturn(typeRecord);
		String expected = PTR_PID_ISSUER;
		String returned = pidTypeService.resolveTypePid(PID_ISSUER_PID);
		
		assert(expected.equals(returned));
	}
	
	private void initTestPidTypeRecord() {		
		typeRecord = new ArrayList<>();
		int i = 1;
		typeRecord.add(new Handles(recordPid, i++, "pid", pid, timestamp));
		typeRecord.add(new Handles(recordPid, i++, "pidType", pidType, timestamp));
		typeRecord.add(new Handles(recordPid, i++, "primaryNameFromPid", primaryNameFromPid, timestamp));
		
	}
	
}
