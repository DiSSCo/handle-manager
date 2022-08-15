package Service;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecord;
import com.example.handlemanager.model.HandleRecordSpecimen.HandleRecordSpecimen;
import com.example.handlemanager.repository.HandleRepository;
import com.example.handlemanager.service.HandleService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
public class HandleServiceTest {
	
	@Mock
	private HandleRepository repository;
	
	@Autowired
	private HandleService service;
	
	String testHandle = "20.5000.1025/ABC-123";
	
	Logger logger =  Logger.getLogger(HandleServiceTest.class.getName());
	
	
	@BeforeEach
	public void setUp() throws JsonProcessingException {
		HandleRecordSpecimen testSpec = genTestRecord("AstronomySpecimen");
	
		Mockito.when(repository.resolveHandle(testSpec.getHandle()))
				.thenReturn(testSpec.getEntries());
	}
	
	private HandleRecordSpecimen genTestRecord(String dt) throws JsonProcessingException {
		byte[] testHandleBytes = testHandle.getBytes();
		String url = "dissco.eu";
		String institute = "DiSSCo";
		
		return new HandleRecordSpecimen(testHandleBytes, url, dt, institute);
	}
	
	@Test
	public void ResolveHandleTest() throws JsonMappingException, JsonProcessingException {
		HandleRecord retrievedSpecimen = service.resolveHandleRecord(testHandle);		
		assertEquals(retrievedSpecimen.getHandleStr(), testHandle); 
		
	}

}
