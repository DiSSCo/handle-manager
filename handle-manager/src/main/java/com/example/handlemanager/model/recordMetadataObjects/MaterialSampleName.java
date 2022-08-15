package com.example.handlemanager.model.recordMetadataObjects;

import lombok.Data;

@Data
public class MaterialSampleName {
	String value;
	String type;
	String primaryLanguage;
	
	public MaterialSampleName(String val, String ty, String lang) {
		value = val;
		type = ty;
		primaryLanguage = lang;
	}

}
