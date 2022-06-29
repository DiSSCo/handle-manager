package com.example.handlemanager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.*;
import com.example.handlemanager.repository.HandleObjectRepository;
import com.example.handlemanager.security.HandleSessionsAuthenticator;
import com.example.handlemanager.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResolutionResponse;
import net.handle.hdllib.Util;

//@SpringBootTest
class HandleManagerScratchApplicationTests {
	HandleService service = new HandleService();
	PrivateKey privkey;
	
	void testRecordStr() {
		DigitalSpecimen sample = DigitalSpecimen.testDigitalSpecimen();
		String now = Instant.ofEpochSecond(System.currentTimeMillis()).toString();
		System.out.println(sample.toHandleRecordString(now));
	}
	
	
	@Test void testNonce() throws IOException, GeneralSecurityException {
		HandleSessionsAuthenticator auth  = new HandleSessionsAuthenticator(null, null);
		byte[] a = auth.generateCnonceBytes();
		
		String nonceStr = auth.encodeToString(a);
		HandleSessionsAuthenticator auth2  = new HandleSessionsAuthenticator(null, nonceStr);
		String authStr = auth2.createAuthHeader();
		
		System.out.println(authStr);
		

	}
} 
	

