package com.example.handlemanager;

import java.security.PrivateKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.handlemanager.model.HandleRecord;
import com.example.handlemanager.model.HandleRecordSpecimen;
import com.example.handlemanager.service.HandleService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
//@SpringBootTest
class HandleManagerScratchApplicationTests {
	PrivateKey privkey;
	
	@Autowired
	HandleService service = new HandleService();
	
	
	HandleFactory hf = new HandleFactory();
	
	
	//@Test
	public void handleRecordCreationTest() {
		//public HandleRecord(String handle, String url, String digType, String institute)
		HandleRecord record = new HandleRecordSpecimen("123".getBytes(), "dissco.tech", "DigitalObject", "Naturalis");		
	}
	
	public void creationTest() {
		//service.setHandleList();
		assert service.getHandles().isEmpty();
		
		//System.out.println(service.getHandles());
		
		//List<Handles> h = service.createHandle(hf.newHandle(), "dissco.tech", "naturalis");
		//String hStr = h.get(1).getHandle();
		//assert service.handleExists(hStr.getBytes());
		//assert !service.handleExists("blahblah".getBytes());
	}
	
	@Test
	public void handleGen() {
		
	}
	
	
	
	
}
	

