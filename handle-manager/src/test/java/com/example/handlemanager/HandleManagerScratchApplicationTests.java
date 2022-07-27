package com.example.handlemanager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.handlemanager.model.DigitalSpecimen;
import com.example.handlemanager.model.HandleRecord;
import com.example.handlemanager.model.HandleRecordSpecimen;
import com.example.handlemanager.model.Handles;
import com.example.handlemanager.security.HandleSessionsAuthenticator;
import com.example.handlemanager.service.HandleService;

import feign.Feign;
import feign.FeignException;

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
	
	private String byteToStr(byte[] b) {
		return Base64.getEncoder().encodeToString(b);
	}
	

}
	

