package com.example.handlemanager.model;

import java.util.ArrayList;
import java.util.List;

import com.example.handlemanager.model.recordMetadataObjects.MaterialSampleName;
import com.example.handlemanager.model.recordMetadataObjects.NameIdTypeTriplet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DigitalSpecimenInput {
	private String url;
	private NameIdTypeTriplet digSubtype;

	private NameIdTypeTriplet pidIssuer;
	private final NameIdTypeTriplet digitalObjectType = new NameIdTypeTriplet("21.T11148/ccdb8cda8a785230257e", "Handle", "DigitalSpecimen");
	private NameIdTypeTriplet digitalObjectSubtype;
	
	private final String pidKernelMetadataLicense = "CreativeCommonsZero";
	
	//Referent parameters
	private MaterialSampleName materialSampleName;
	private NameIdTypeTriplet principalAgent;
	private List<NameIdTypeTriplet> identifier;
	
	
	public DigitalSpecimenInput(String test) {
		url = "test";
		digSubtype = new NameIdTypeTriplet();
		pidIssuer = new NameIdTypeTriplet();
		digitalObjectSubtype = new NameIdTypeTriplet();
		
		materialSampleName = new MaterialSampleName("", "", "");
		principalAgent = new NameIdTypeTriplet();
		
		List<NameIdTypeTriplet> tripletList = new ArrayList<>();
		tripletList.add(new NameIdTypeTriplet());
		tripletList.add(new NameIdTypeTriplet());
	}

}
