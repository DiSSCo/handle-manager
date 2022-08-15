package com.example.handlemanager.ModelTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.handlemanager.model.recordMetadataObjects.NameIdTypeTriplet;
import com.example.handlemanager.model.recordMetadataObjects.Referent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReferentTest {
	
	Referent referent;
	ObjectMapper objectMapper = new ObjectMapper();
	
	@BeforeEach	
	public void setReferent() {
		referent = new Referent();
		referent.setMaterialSampleName("Tamias quadrivittatus", "Scientific Name", "En");
		referent.setPrincipalAgent("Denver Museum of Nature and Science", "003zqrx63", "ROR");
		List<NameIdTypeTriplet> tripletList = new ArrayList<>();
		tripletList.add(new NameIdTypeTriplet());
		tripletList.add(new NameIdTypeTriplet());
		
		referent.setIdentifier(tripletList);
	}
	
	@Test
	public void testReferentString() throws JsonProcessingException {
		String refStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(referent);
		System.out.println(refStr);
		
	}
	

}
