package com.example.handlemanager.ModelTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.HandleRecordSpecimen;
import com.example.handlemanager.model.HandleRecordSpecimenMerged;
import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

public class HandleRecordSpecimenMergedTest {

	HandleRecordSpecimenMerged merged1;
	HandleRecordSpecimenMerged merged2;
	HandleRecordSpecimenMerged merged3;
	
	HandleRecordSpecimen specimen1;
	HandleRecordSpecimen specimen2;
	HandleRecordSpecimen specimen3;
	
	byte[] handle = "20.5000.1025/ABC-123".getBytes();
	byte[] handle2 = "20.5000.1025/123-ABC".getBytes();
	String url = "dissco.eu";
	String institute = "DiSSCo";
	
	private HandleRecordSpecimen genTestRecord(String dt, byte[] h) throws JsonProcessingException {		
		return new HandleRecordSpecimen(h, url, dt, institute);
	}
	
	@BeforeEach
	private void setup() throws JsonProcessingException {
		specimen1 = genTestRecord("BotanySpecimen", handle);
		specimen2 = genTestRecord("MycologySpecimen", handle);
		specimen3 = genTestRecord("AnthropologySpecimen", handle2);
		
		List<HandleRecordSpecimen> specimenList = genSpecList(specimen1, specimen2);
		
		merged1 = new HandleRecordSpecimenMerged(handle, url, "BotanySpecimen", institute, specimenList);
		merged2 = new HandleRecordSpecimenMerged(handle, url, "BotanySpecimen", institute, genSpecList(specimen1, specimen2));
		merged3 = new HandleRecordSpecimenMerged(handle2, url, "BotanySpecimen", institute, genSpecList(specimen1, specimen3));
	}
	
	private List<HandleRecordSpecimen> genSpecList(HandleRecordSpecimen spec1, HandleRecordSpecimen spec2){
		List<HandleRecordSpecimen> specimenList = new ArrayList<>();
		specimenList.add(spec1);
		specimenList.add(spec2);
		return specimenList;
	}
	
	@Test
	public void testMergedRecordCreation() {
		assertEquals(merged1.getRelationStatus(), "MERGED");
		assertTrue(merged1.getLegacyHandles().size()>1);
	}
	
	@Test 
	public void testEquality() throws JsonProcessingException {
		assertTrue(merged1.equals(merged2));
		merged3 = new HandleRecordSpecimenMerged(handle, url, "BotanySpecimen", institute, genSpecList(specimen1, specimen3));
		assertFalse(merged1.equals(merged3));
	}
	
	@Test
	public void testInverseCreation() {
		//merged3 = new HandleRecordSpecimenMerged(merged1.getEntries(), merged1.getHandle());
		
		
	}
	
	
	
}
