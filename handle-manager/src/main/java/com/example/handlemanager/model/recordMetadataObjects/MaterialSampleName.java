package com.example.handlemanager.model.recordMetadataObjects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MaterialSampleName {
	String value;
	String type;
	String primaryLanguage;
	
	public MaterialSampleName(String val, String ty, String lang) {
		value = val;
		type = ty;
		primaryLanguage = lang;
	}
	
	@Override
	public String toString() {
		return "{ \n"
				+ "\"value\": \"" + value + "\", \n"
				+ "\"type\": \"" + type + "\",\n"
				+ "\"primaryLanguage\": \"" + primaryLanguage + "\"\n"
				+ "}";
	}
	
	public boolean equals(MaterialSampleName m2) {
		return (value.equals(m2.getValue()) &&
				type.equals(m2.getType()) &&
				primaryLanguage.equals(m2.getPrimaryLanguage()));
	}

}
