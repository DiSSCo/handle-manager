package com.example.handlemanager.model.recordMetadataObjects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class Referent {
	String primaryReferentType = "materialSample";
	MaterialSampleName materialSampleName;
	String structuralType = "physical";
	String type = "Specimen";
	
	NameIdTypeTriplet principalAgent;
	List<NameIdTypeTriplet> identifier;
	
	@JsonIgnore
	ObjectMapper objectMapper = new ObjectMapper();
	
	
	public void setMaterialSampleName(String val, String type, String lang) {
		materialSampleName = new MaterialSampleName(val, type, lang);
	}
	
	public void setMaterialSampleName(MaterialSampleName vtl) {
		materialSampleName = vtl;
	}
	
	public void setPrincipalAgent(String n, String i, String t) {
		principalAgent = new NameIdTypeTriplet(n, i, t);
	}
	
	public void setPrincipalAgent(NameIdTypeTriplet nit) {
		principalAgent = nit;
	}
	
	
	public void setIdentifier(List<NameIdTypeTriplet> nits) {
		identifier = nits;
	}
		
	
}


