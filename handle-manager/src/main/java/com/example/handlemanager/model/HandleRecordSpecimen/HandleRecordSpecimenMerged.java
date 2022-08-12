package com.example.handlemanager.model.HandleRecordSpecimen;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.HandleFactory;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HandleRecordSpecimenMerged extends HandleRecordSpecimen {
	private final String legacyStringName = "Legacy Handle";
	private String relationStatus = "MERGED";
	//private List<ByteBuffer> legacyHandles;
	private List<String> legacyHandles;
	private String legacyHandleString;
	
	
	
	private Logger logger = Logger.getLogger(HandleFactory.class.getName());
	
	
	// Constructor from inputs (usually called when record is being constructed for the first time)
	public HandleRecordSpecimenMerged(byte[] handle, String url, String digitalObjectType, 
			String institute, List<HandleRecordSpecimen> legacyRecords) throws JsonProcessingException {
		super(handle, url, digitalObjectType, institute);
		setLegacyHandlesFromSpecimenRecord(legacyRecords);
		setRelationRecord();
	}
	
	// Constructor from Handle Record retrievable (usually called when a handle record is resolved)
	public HandleRecordSpecimenMerged(List<Handles> entries, byte[] handle) throws JsonMappingException, JsonProcessingException {
		super(entries, handle);
		setLegacyHandlesFromHandleRecord(entries);
	}
	
	
	private void setLegacyHandlesFromHandleRecord(List<Handles> entries) {
		legacyHandles = new ArrayList<String>();
		
		for (Handles h: entries) {
			if (h.getType().equals(legacyStringName)){
				legacyHandles.add(h.getData());
			}
		}
		
		setLegacyString();
		this.entries.add(new Handles(handle, entries.size()+1, legacyStringName, legacyHandleString, timestamp));
	}
	
	private void setLegacyHandlesFromSpecimenRecord(List<HandleRecordSpecimen> records) {
		legacyHandles = new ArrayList<>();
		
		for (HandleRecordSpecimen specimen : records) {
			legacyHandles.add(specimen.getHandleStr());
		}
		setLegacyString();
		this.entries.add(new Handles(handle, entries.size()+1, legacyStringName, legacyHandleString, timestamp));
		
	}
	
	private void setLegacyString() {
		legacyHandleString = "{";
		for (String h: legacyHandles) {
			legacyHandleString += "\"" + h + "\",";
		}
		legacyHandleString = legacyHandleString.substring(0, legacyHandleString.length()-1) + "}";
	}
	
	private void setRelationRecord() {
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
	}
	
	public String getRelationStatus() {
		return relationStatus;
	}
	
	@JsonIgnore
	public List<String> getLegacyHandles(){
		return legacyHandles;
	}
	
	@JsonProperty("Legacy Handles")
	public String getLegacyHandlesStr(){
		return legacyHandleString;
	}
	
	public boolean equals(HandleRecordSpecimenMerged mergedRecord) {
		if (!super.equals(mergedRecord)) return false; // If handleRecordSpecimen values do not match, no need to go further
		return (legacyHandles.equals(mergedRecord.getLegacyHandles()));
					
	}
	
	public boolean isEmpty() {
		return (entries.isEmpty());
	}

}

