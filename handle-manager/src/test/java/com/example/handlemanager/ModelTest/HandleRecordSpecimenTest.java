package com.example.handlemanager.ModelTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.HandleRecordSpecimen;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HandleRecordSpecimenTest {
	
	HandleRecordSpecimen specimen;
	String digType = "BotanySpecimen";
	
	public HandleRecordSpecimen genTestRecord(String dt) {
		byte[] handle = "20.5000.1025/ABC-123".getBytes();
		String url = "dissco.eu";
		String institute = "DiSSCo";
		
		return new HandleRecordSpecimen(handle, url, dt, institute);
	}
	
	
	// "Inverse record creation" = creating object based on List<Handles> instead of by fields (as seen in testRecord()
	@Test
	public void testInverseRecordCreation() {
		specimen = genTestRecord("BotanySpecimen");
		HandleRecordSpecimen specimen2 = new HandleRecordSpecimen(specimen.getEntries(), specimen.getHandle());		
		assert(specimen.equals(specimen2));
	}
	
	@Test
	public void testIsEmpty() {
		HandleRecordSpecimen specimen = genTestRecord(digType);
		assertFalse(specimen.isEmpty());
		specimen.clearEntries();
		assertTrue(specimen.isEmpty());
	}
	
	@Test
	public void testInvalidDigType() {
		specimen = genTestRecord("Bleugh");
		assertEquals(specimen.getDigType(), "");
	}
	
	
	
	

}
