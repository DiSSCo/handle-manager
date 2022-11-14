package com.example.handlemanager.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.handle.hdllib.AdminRecord;
import net.handle.hdllib.HandleValue;


// Need to transform JSON to list of HandleValues and vice versa

// This is the JSON record, i.e. the list of handle values. It does not contain the handle itself. That might change


@Data
@RequiredArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DigitalSpecimen {

	// JSON Parameters 
	@JsonProperty(value = "URL")
		private String url;
	@JsonProperty(value = "OBJECT_TYPE")
		private String objectType;
	@JsonProperty(value = "CURATED_OBJECT_ID")
		private String curatedObjectId;
	@JsonProperty(value = "CURATED_OBJECT_ID_TYPE")
		private String curatedObjectIdType;
	@JsonProperty(value = "SPECIMEN_NAME")
		private String specimenName;
	@JsonProperty(value = "ORGANIZATION_CODE")
		private String organizationCode;
	@JsonProperty(value = "ORGANIZATION_ID")
		private String organizationId;
	@JsonProperty(value = "RECORD_ID")
		private String recordId;
	
	//@JsonProperty(value = "HS_ADMIN")
	//	private String handleAdmin; 
	
	// TODO: Determine how handle admin records work, how to add them to a handle record (might be added automatically)
	// When handleAdmin is pulled from the API, it is stored as this string: 
	//"07F3 00000015 3330303A302E4E412F32302E353030302E31303235 000000C8" (spaces added for legibility)
	// Let's see if we can untangle this 
		//1  07F3 -> 011111110011, permissions (hex -> binary)
		//2  00000015 -> Not sure what this represents 
		//3  3330303A302E4E412F32302E353030302E31303235 -> 300:0.NA/20.5000.1025, our admin id (hex->ASCII)
		//4  000000C8 -> 200, index at which the admin index is stored, hex->dec
	
	
	// Quality of life mapping here
	private HashMap recordMap = genRecordMap();
	
	
	private HashMap<Integer, String[]> genRecordMap() {
		// So much hardcoding...
		
		HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
		String [] t1 = {"URL", url};
		map.put(1, t1);
		
		String[] t2 = {"OBJECT_TYPE", objectType};  
		map.put(2, t2);
		
		String[] t3 = {"CURATED_OBJECT_ID", curatedObjectId};  
		map.put(3, t3);
		
		String[] t4 = {"CURATED_OBJECT_ID_TYPE", curatedObjectIdType};  
		map.put(4, t4);
		
		String[] t5 = {"SPECIMEN_NAME", specimenName};  
		map.put(5, t5);
		
		String[] t6 = {"ORGANIZATION_CODE", organizationCode};  
		map.put(6, t6);
		
		String[] t7 = {"ORGANIZATION_ID", organizationId};  
		map.put(7, t7);
		
		String[] t8 = {"RECORD_ID", recordId};  
		map.put(8, t8);
		
		//String[] t100 = {"HS_ADMIN", handleAdmin};  
		//map.put(100, t100);
		
		return map;
	}
	
	public String getRecordField(Integer idx) {
		String [] pair = (String[]) recordMap.get(idx);
		return pair[0];
	}
	
	public String getRecordFieldVal(Integer idx) {
		String [] pair = (String[]) recordMap.get(idx);
		return pair[1];
	}
	
	// generate a test digital specimen (Okay I lied this is also hardcoded but its a test record it doesn't count)
	public static DigitalSpecimen testDigitalSpecimen(){
		DigitalSpecimen test = new DigitalSpecimen();
		test.setUrl("sandbox.dissco.tech");
		test.setObjectType("DStypeV0.2-Demo");
		test.setCuratedObjectId("NHMD218242");
		test.setCuratedObjectIdType("Physical Specimen ID");
		test.setSpecimenName("Hyperolius puncticulatus");
		test.setOrganizationCode("National Museum of History Denmark");
		test.setOrganizationId("NHMD");
		test.setRecordId("test/2aabd0353db9bb474d63");
		test.setRecordMap(test.genRecordMap());
		return test;
	}
	
	public HandleValue[] toHandleValueList() {
		// Transforms Digital Specimen into a list of HandeValues for injestion via the SDK 
		
		// TODO: Check if an element is null?
		// This function does not make a HandleValue for HS_admin


		HandleValue[] hvArr = new HandleValue[8];
		HandleValue hv;
		for (Integer i=1; i<8; i++) {
			hv = new HandleValue(i, getRecordField(i), getRecordFieldVal(i));
			hvArr[i-1] = hv;
		}
		return hvArr;
	}
	
	public String toHandleRecordString(String handle) {
		// Returns handle-friendly record to be posted to a record via Handle API
		
		String now = Instant.ofEpochSecond(System.currentTimeMillis()).toString();
		
		String recordStr = 
				"{"
				+ "\"handle\": " + handle + ","
					+ "\"values\":["
						+ stringHelper(now)
						// Add the admin string (is this necessary? time will tell
						+ ",{\"index\":100,\"type\":\"HS_ADMIN\",\"data\":{\"format\":\"admin\",\"value\""
							+ ":{\"handle\":\"300:0.NA/20.5000.1025\",\"index\":200,\"permissions\":\"011111110011\"}},"
							+ "\"ttl\":86400,\"timestamp\":\"" + now  + "\"}" // This is the string that worked before in python:)
					+ "]"
				+ "}";
		return recordStr;	
	}
	
	private String stringHelper(String now) {
		// Helps out the above method by looping through the values
		
		String retStr = "";
		for (Integer i=1; i<8; i++) {
			retStr += "{ "
					+ "\"index\": " + i + ","
					+ "\"type\": \""+ getRecordField(i) +"\","
					+ "\"data\": "
						+ "{"
							+ "\"format\": \"string\","
							+ "\"value\":\""+getRecordFieldVal(i)+"\""
						+"}"
					+",\"ttl\": 86400,"
					+ "\"timestamp\": \""+now+"\""
				+ "},";
		}
		
		return retStr.substring(0, retStr.length()-1);
	}

}
