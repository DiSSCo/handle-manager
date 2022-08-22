package com.example.handlemanager.model.recordMetadataObjects;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Referent {
	private final String primaryReferentType = "materialSample";
	private String structuralType;
	private final String type = "specimen";
	
	private MaterialSampleName materialSampleName;
	private NameIdTypeTriplet principalAgent;
	private List<NameIdTypeTriplet> identifier;
	
	@JsonIgnore
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public Referent(MaterialSampleName materialSampleName, 
			String structuralType, 
			NameIdTypeTriplet principalAgent, 
			List<NameIdTypeTriplet> identifier) {
		this.materialSampleName = materialSampleName;
		this.structuralType = structuralType;
		this.principalAgent = principalAgent;
		this.identifier = identifier;
	}
	
	
	public void setMaterialSampleName(String val, String type, String lang) {
		materialSampleName = new MaterialSampleName(val, type, lang);
	}

	public void setPrincipalAgent(String n, String i, String t) {
		principalAgent = new NameIdTypeTriplet(n, i, t);
	}
	
	@Override
	public String toString() {
		String retStr = "{ \n";
		retStr += "\"primaryReferentType\":\"" + primaryReferentType + "\","
				+ "\"materialSampleName\":\"" + materialSampleName.toString() + "\", \n" 
				+ "\"structuralType\":\"" + structuralType + "\", \n" 
				+ "\"type\": \"" + type + "\", \n"  
				+ "\"principalAgent\": \"" + principalAgent.toString() + "\", \n" 
				+ "\"identifier\": [ \n";
		
		for (NameIdTypeTriplet id : identifier) {
			retStr += id.toString() + ",";
		}
		retStr = retStr.substring(0, retStr.length()-1);
		retStr += "] \n }";	
		
		return retStr;
	}
	
	
	public boolean equals(Referent ref2) {
		return (primaryReferentType.equals(ref2.getPrimaryReferentType()) &&
				materialSampleName.equals(ref2.getMaterialSampleName()) &&
				structuralType.equals(ref2.getStructuralType()) &&
				type.equals(ref2.getType()) &&
				principalAgent.equals(ref2.getPrincipalAgent()) &&
				listEqualsIgnoreOrder(identifier, ref2.getIdentifier()));
	}
	
	private boolean listEqualsIgnoreOrder(List<NameIdTypeTriplet> list1, List<NameIdTypeTriplet> list2) {
	    return new HashSet<>(list1).equals(new HashSet<>(list2));
	}
	
}


