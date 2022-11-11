package com.example.handlemanager.testUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.handlemanager.domain.requests.*;
import com.example.handlemanager.domain.responses.*;
import com.example.handlemanager.model.repositoryObjects.Handles;

public class TestUtils {
	
	public static Instant CREATED = Instant.parse("2022-11-01T09:59:24.00Z");
	
	public static String HANDLE = "20.5000.1025/QRS-321-ABC";
	public static String HANDLE_ALT = "20.5000.1025/QRS-123-ABC";
	
	// Request Vars
	// Handles
	public static String PID_ISSUER_PID = "20.5000.1025/PID-ISSUER";
	public static String DIGITAL_OBJECT_TYPE_PID = "20.5000.1025/DIGITAL-SPECIMEN";
	public static String DIGITAL_OBJECT_SUBTYPE_PID = "20.5000.1025/BOTANY-SPECIMEN";
	public static String [] LOCATIONS = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
	//DOIs
	public static String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
	//Digital Specimens
	public static String DIGITAL_OR_PHYSICAL = "physical";
	public static String SPECIMEN_HOST_PID = "20.5000.1025/OTHER-TRIPLET";
	public static String IN_COLLECTION_FACILITY = "20.5000.1025/OTHER-TRIPLET";
	//Botany Specimens
	public static String OBJECT_TYPE = "Herbarium Sheet";
	public static String PRESERVED_OR_LIVING = "preserved";
	
	// Pid Type Record vars
	public static String PTR_PID = "http://hdl.handle.net/"+PID_ISSUER_PID;
	public static String PTR_TYPE = "handle";
	public static String PTR_PRIMARY_NAME = "DiSSCo";
	public static String PTR_PID_DOI = "http://doi.org/"+PID_ISSUER_PID;
	public static String PTR_TYPE_DOI = "doi";
	
	public static String PTR_REGISTRATION_DOI_NAME = "Registration Agency";
	
	public static String PTR_HANDLE_RECORD = "{ \n"
			+ "\"pid\": \"" + PTR_PID + "\", \n"
			+ "\"pidType\": \""+PTR_TYPE+"\", \n"
			+ "\"primaryNameFromPid\": \""+ PTR_PRIMARY_NAME +"\" \n"
			+ "}";
	
	public static String PTR_DOI_RECORD = "{ \n"
			+ "\"pid\": \"" + PTR_PID_DOI + "\", \n"
			+ "\"pidType\": \""+PTR_TYPE_DOI+"\", \n"
			+ "\"primaryNameFromPid\": \""+ PTR_PRIMARY_NAME +"\", \n"
			+ "\"registrationAgencyDoiName\": \""+ PTR_REGISTRATION_DOI_NAME +"\" \n"
			+ "}";
	
	
	public static HandleRecordRequest generateTestHandleRequest() {
		return new HandleRecordRequest(
				PID_ISSUER_PID,
				DIGITAL_OBJECT_TYPE_PID,
				DIGITAL_OBJECT_SUBTYPE_PID,
				LOCATIONS);
	}
	
	public static HandleRecordResponse generateTestHandleResponse() {
		return null;
	}
	
	public static DoiRecordRequest generateTestDoiRequest() {
		return new DoiRecordRequest(
				PID_ISSUER_PID,
				DIGITAL_OBJECT_TYPE_PID,
				DIGITAL_OBJECT_SUBTYPE_PID,
				LOCATIONS,
				REFERENT_DOI_NAME_PID);				
	}
	
	public static DoiRecordResponse generateTestDoiResponse() {
		return null;
	}
	
	public static DigitalSpecimenRequest generateTestDigitalSpecimenRequest() {
		return new DigitalSpecimenRequest(
				PID_ISSUER_PID,
				DIGITAL_OBJECT_TYPE_PID,
				DIGITAL_OBJECT_SUBTYPE_PID,
				LOCATIONS,
				REFERENT_DOI_NAME_PID,
				DIGITAL_OR_PHYSICAL,
				SPECIMEN_HOST_PID,
				IN_COLLECTION_FACILITY);
	}
	
	public static DigitalSpecimenBotanyResponse generateTestDigitalSpecimenResponse() {
		return null;
	}
	
	
	public static DigitalSpecimenBotanyRequest generateTestDigitalSpecimenBotanyRequest() {
		return new DigitalSpecimenBotanyRequest(PID_ISSUER_PID,
				DIGITAL_OBJECT_TYPE_PID,
				DIGITAL_OBJECT_SUBTYPE_PID,
				LOCATIONS,
				REFERENT_DOI_NAME_PID,
				DIGITAL_OR_PHYSICAL,
				SPECIMEN_HOST_PID,
				IN_COLLECTION_FACILITY,
				OBJECT_TYPE,
				PRESERVED_OR_LIVING);
	}
	
	public static DigitalSpecimenBotanyResponse generateTestDigitalSpecimenBotanyResponse() {
		return null;
	}
	
	public static List<byte[]> generateByteHandleList() {
		List<byte[]> handles = new ArrayList<>();
		handles.add(HANDLE.getBytes());
		handles.add(HANDLE_ALT.getBytes());
		
		return handles;
	}

}
