package com.example.handlemanager.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.handlemanager.domain.requests.*;
import com.example.handlemanager.domain.responses.*;
import com.example.handlemanager.model.repositoryObjects.Handles;

public class TestUtils {
	//Date and time
	public static Instant CREATED = Instant.parse("2022-11-01T09:59:24.00Z");

	// Handles
	public static String HANDLE = "20.5000.1025/QRS-321-ABC";
	public static String HANDLE_ALT = "20.5000.1025/QRS-123-ABC";
	
	// Request Vars
	// Handles
	public static String PID_TYPE_RECORD_DOI = "20.5000.1025/PID-ISSUER";
	public static String PID_TYPE_RECORD_HANDLE = "20.5000.1025/DIGITAL-SPECIMEN";
	public static String [] LOCATIONS = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
	//DOIs
	public static String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
	//Digital Specimens
	public static String DIGITAL_OR_PHYSICAL = "physical";
	//Botany Specimens
	public static String OBJECT_TYPE = "Herbarium Sheet";
	public static String PRESERVED_OR_LIVING = "preserved";
	
	// Pid Type Record - Pid Issuer (DOI)
	public static String PTR_DOI_PID = "https://doi.org/"+PID_TYPE_RECORD_DOI;
	public static String PTR_DOI_TYPE = "DOI";
	public static String PTR_DOI_PRIMARY_NAME = "DiSSCo";
	public static String PTR_DOI_REGISTRATION_AGENCY_DOI_NAME = "10.20/2AA-3BB-4CC";
	public static String PTR_DOI_RECORD = "{ \n"
			+ "\"pid\": \"" + PTR_DOI_PID + "\", \n"
			+ "\"pidType\": \""+PTR_DOI_TYPE+"\", \n"
			+ "\"primaryNameFromPid\": \""+ PTR_DOI_PRIMARY_NAME +"\", \n"
			+ "\"registrationAgencyDoiName\": \"" + PTR_DOI_REGISTRATION_AGENCY_DOI_NAME + "\" \n"
			+ "}";
	
	// Pid Type Record - Digital Specimen (Handle(
	public static String PTR_HANDLE_PID = "http://hdl.handle.net/"+PID_TYPE_RECORD_HANDLE;
	public static String PTR_HANDLE_TYPE = "handle";
	public static String PTR_HANDLE_PRIMARY_NAME = "Digital Specimen";
	
	public static String PTR_HANDLE_RECORD = "{ \n"
			+ "\"pid\": \"" + PTR_HANDLE_PID + "\", \n"
			+ "\"pidType\": \""+PTR_HANDLE_TYPE+"\", \n"
			+ "\"primaryNameFromPid\": \""+ PTR_HANDLE_PRIMARY_NAME +"\" \n"
			+ "}";
	
	
	public static List<Handles> generateHandleRecordList(byte[] handle, long timestamp) {
		List<Handles> record = new ArrayList<>();
		
		int i = 1;
		byte[] pid = ("https://hdl.handle.net/"+HANDLE).getBytes();
		record.add(new Handles(handle, i++, "pid", pid, timestamp));		
		
		String pidIssuer = PTR_DOI_RECORD;
		record.add(new Handles(handle, i++, "pidIssuer", pidIssuer, timestamp));
		
		String digitalObjectType = PTR_HANDLE_TYPE;
		record.add(new Handles(handle, i++, "digitalObjectType", digitalObjectType, timestamp));
		
		String digitalObjectSubtype = PTR_HANDLE_TYPE;
		record.add(new Handles(handle, i++, "digitalObjectSubtype", digitalObjectSubtype, timestamp));
		
		byte[] loc = "".getBytes(); // TODO make this reflect the LOCATIONS constant
		record.add(new Handles(handle, i++, "10320/loc", loc, timestamp));
		
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-mm-dd").withZone(ZoneId.of("UTC"));
		record.add((new Handles(handle, i++, "issueDate", dt.format(CREATED), timestamp)));

		record.add((new Handles(handle, i++, "issueNumber", "1", timestamp)));
		
		record.add((new Handles(handle, i++, "pidStatus", "TEST",timestamp)));
		i++;
		i++;
		record.add((new Handles(handle, i++, "pidKernelMetadataLicense", "https://creativecommons.org/publicdomain/zero/1.0/", timestamp)));
		return record;
	}
	
	public static List<Handles> generateDoiRecordList(byte[] handle, long timestamp) {
		List<Handles> record = new ArrayList<>();
		record.addAll(generateHandleRecordList(handle, timestamp));
		
		int i = 12;
		String referentDoiName = PTR_DOI_RECORD;
		record.add(new Handles(handle, i++, "referentDoiName", referentDoiName, timestamp));
		
		String referent = "";
		record.add(new Handles(handle, i++, "referent", referent, timestamp));
		
		return record;
	}
	
	public static List<Handles> generateDigitalSpecimenList(byte[] handle, long timestamp) {
		List<Handles> record = new ArrayList<>();
		record.addAll(generateDoiRecordList(handle, timestamp));
		
		int i = 14;
		String digitalOrPhysical = "physical";
		record.add(new Handles(handle, i++, "digitalOrPhysical", digitalOrPhysical, timestamp));
		
		String specimenHost = PTR_HANDLE_RECORD;
		record.add(new Handles(handle, i++, "specimenHost", specimenHost, timestamp));
		
		String inCollectionFacility = PTR_DOI_RECORD;
		record.add(new Handles(handle, i++, "inCollectionFacility", inCollectionFacility, timestamp));
		
		return record;
	}
	
	public static List<Handles> generateDigitalSpecimenBotanyList(byte[] handle, long timestamp) {
		List<Handles> record = new ArrayList<>();
		record.addAll(generateDigitalSpecimenList(handle, timestamp));
		
		int i = 16;
		String objectType= "Herbarium Sheet";
		record.add(new Handles(handle, i++, "objectType", objectType, timestamp));
		
		String preservedOrLiving = "preserved";
		record.add(new Handles(handle, i++, "preservedOrLiving", preservedOrLiving, timestamp));
		
		return record;
	}
	
	
	
	
	public static HandleRecordResponse generateTestHandleResponse() {
		return new HandleRecordResponse(generateHandleRecordList(HANDLE.getBytes(), initTimestamp()));	
	}
	public static HandleRecordRequest generateTestHandleRequest() {
		return new HandleRecordRequest(
				PID_TYPE_RECORD_DOI,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_HANDLE,
				LOCATIONS);
	}
	
	public static DoiRecordRequest generateTestDoiRequest() {
		return new DoiRecordRequest(
				PID_TYPE_RECORD_DOI,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_HANDLE,
				LOCATIONS,
				PID_TYPE_RECORD_DOI);				
	}
	public static DoiRecordResponse generateTestDoiResponse() {
		return new DoiRecordResponse(generateDoiRecordList(HANDLE.getBytes(), initTimestamp()));	
	}
	
	public static DigitalSpecimenRequest generateTestDigitalSpecimenRequest() {
		return new DigitalSpecimenRequest(
				PID_TYPE_RECORD_DOI,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_HANDLE,
				LOCATIONS,
				REFERENT_DOI_NAME_PID,
				DIGITAL_OR_PHYSICAL,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_DOI);
	}
	
	public static DigitalSpecimenResponse generateTestDigitalSpecimenResponse() {
		return new DigitalSpecimenResponse(generateDigitalSpecimenList(HANDLE.getBytes(), initTimestamp()));	
	}
	
	public static DigitalSpecimenBotanyRequest generateTestDigitalSpecimenBotanyRequest() {
		return new DigitalSpecimenBotanyRequest(
				PID_TYPE_RECORD_DOI,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_HANDLE,
				LOCATIONS,
				REFERENT_DOI_NAME_PID,
				DIGITAL_OR_PHYSICAL,
				PID_TYPE_RECORD_HANDLE,
				PID_TYPE_RECORD_DOI,
				OBJECT_TYPE,
				PRESERVED_OR_LIVING);
	}
	
	public static DigitalSpecimenBotanyResponse generateTestDigitalSpecimenBotanyResponse() {
		return new DigitalSpecimenBotanyResponse(generateDigitalSpecimenBotanyList(HANDLE.getBytes(), initTimestamp()));	
	}
	
	public static List<byte[]> generateByteHandleList() {
		List<byte[]> handles = new ArrayList<>();
		handles.add(HANDLE.getBytes());
		handles.add(HANDLE_ALT.getBytes());
		
		return handles;
	}
	
	public static long initTimestamp() {
		return CREATED.getEpochSecond();
	}

}
