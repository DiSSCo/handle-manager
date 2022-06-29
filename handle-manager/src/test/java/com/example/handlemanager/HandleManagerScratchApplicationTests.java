package com.example.handlemanager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.*;
import com.example.handlemanager.repository.HandleObjectRepository;
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
	
	@Test
	void testRecordStr() {
		DigitalSpecimen sample = DigitalSpecimen.testDigitalSpecimen();
		String now = Instant.ofEpochSecond(System.currentTimeMillis()).toString();
		System.out.println(sample.toHandleRecordString(now));
	}
	
	
	/*
	void testToDS() throws JsonMappingException, JsonProcessingException, HandleException{
		
		//HandleObjectRepositoryWeb rep = new HandleObjectRepositoryWeb();
		
		String handle = "20.5000.1025/0DJS-01NM";
		byte[][] types = null;
		int[] idxs = null;
		
		//System.out.println(rep.resolveHandle(handle, types, idxs));
	}
	
	void testDigSpecIngestion() {
		
		DigitalSpecimen testDS = DigitalSpecimen.testDigitalSpecimen();
		System.out.println(testDS.toString());	
		HandleValue[] testHV = testDS.toHandleValueList();

		for (int i=0;i<testHV.length;i++) {
			System.out.println(testHV[i].getTypeAsString() + " : " + testHV[i].getDataAsString());
		}
		
	}
	
	void testTypeBytes() {
		String[] typeList = {"URL",
			"OBJECT_TYPE",
			"CURATED_OBJECT_ID"};
		service.typeBytes(typeList);
		
	}
	
	@Test
	void testAdminRejection() throws HandleException {
		String[] typeList = {"URL",
				"OBJECT_TYPE",
				"CURATED_OBJECT_ID"};
		
		HandleValue values[] = new HandleResolver().resolveHandle("20.5000.1025/YVQX-9P0G", null, null);
		for (int i=0; i < values.length; i++){
			System.out.println(String.valueOf(values[i]));
		}
	}
	
	void testKey() {
		String filename = "PrivateKey/Location";
		PrivateKey pk = null;
		String alg = "SHA-256";
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("Private Key file not found: "+filename);
		}
		try(DataInputStream stream = new DataInputStream(new FileInputStream(file))){
			//pk = PrivateKeyReader.getPrivatekey(stream, alg);
		}
		catch(IOException e) {
			System.out.println("Failed to load private key from file "+filename );
		}
		
	}*/

		
		
			
//		return retStr;
		//HandleValue values[] = new HandleResolver().resolveHandle(handle, null, null);
		
} 
	

