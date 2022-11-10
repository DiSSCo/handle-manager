package com.example.handlemanager.model.HandleRecordSpecimen;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.handlemanager.model.recordMetadataObjects.MaterialSampleName;
import com.example.handlemanager.model.recordMetadataObjects.NameIdTypeTriplet;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.example.handlemanager.utils.HandleFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@NoArgsConstructor
@Data
public class HandleRecordSpecimenMerged extends HandleRecordSpecimen {
	
	//private final String legacyStringName = "Legacy Handle";
	private String relationStatus = "MERGED";
	//private List<ByteBuffer> legacyHandles;
	private List<String> legacyHandles;
	private String legacyHandleString;
	
	
	
	private Logger logger = Logger.getLogger(HandleFactory.class.getName());
	
	
	// Constructor from inputs (usually called when record is being constructed for the first time)
	public HandleRecordSpecimenMerged(byte[] handle, String url, 
			NameIdTypeTriplet digitalObjectType,
			NameIdTypeTriplet pidIssuer, 
			NameIdTypeTriplet digitalObjectSubtype, 
			String digitalOrPhysical,
			// for referent
			MaterialSampleName materialSampleName,
			NameIdTypeTriplet principalAgent,
			List<NameIdTypeTriplet> identifier,
			// Related Pids
			List<String> legacyHandles) throws JsonProcessingException {
		
		super(url, digitalOrPhysical, pidIssuer, digitalObjectSubtype, digitalObjectType, materialSampleName, principalAgent, identifier);
		this.legacyHandles = legacyHandles;
		setLegacyString();
		intitializeEntriesMerged();
	}
	
	// Constructor from Handle Record retrievable (usually called when a handle record is resolved)
		public HandleRecordSpecimenMerged(byte[] handle, List<Handles> entries) throws JsonMappingException, JsonProcessingException {
			super(handle, entries);
			setLegacyHandlesFromHandleRecord(entries);
		}
	
	
	public void setDigitalSpecimenRecordMerged(byte [] handle) throws JsonProcessingException {
		super.setDigitalSpecimenRecord(handle);
		setLegacyString();
		intitializeEntriesMerged();
	}
	
	
	private void setLegacyHandlesFromHandleRecord(List<Handles> entries) {
		for (Handles h: entries) {
			if (h.getType().equals("legacyPids")){
				legacyHandleString = h.getData();
			}
		}
	}
	
	private void setLegacyString() {
		legacyHandleString = "{";
		for (String h: legacyHandles) {
			legacyHandleString += "\"" + h + "\",\n";
		}
		legacyHandleString = legacyHandleString.substring(0, legacyHandleString.length()-2) + "}";
	}
	
	private void intitializeEntriesMerged() {
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "legacyPids", legacyHandleString, timestamp));
	}

	
	public boolean equals(HandleRecordSpecimenMerged mergedRecord) {
		if (!super.equals(mergedRecord)) return false; // If handleRecordSpecimen values do not match, no need to go further
		return (legacyHandles.equals(mergedRecord.getLegacyHandles()));
	}
}

