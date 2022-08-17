package com.example.handlemanager.model.HandleRecordSpecimen;

import java.util.List;

import com.example.handlemanager.model.recordMetadataObjects.MaterialSampleName;
import com.example.handlemanager.model.recordMetadataObjects.NameIdTypeTriplet;
import com.example.handlemanager.model.repositoryObjects.Handles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandleRecordSpecimenSplit extends HandleRecordSpecimen {
	
	private final String relationStatus = "SPLIT";
	private String siblingPids;
	private String parentPid;
	

	public HandleRecordSpecimenSplit(byte[] handle, 
			String url, 
			NameIdTypeTriplet digitalObjectType,
			NameIdTypeTriplet pidIssuer, 
			NameIdTypeTriplet digitalObjectSubtype, 
			String digitalOrPhysical,
			// for referent
			MaterialSampleName materialSampleName,
			NameIdTypeTriplet principalAgent,
			List<NameIdTypeTriplet> identifier, 
			// Related pids			
			String siblingPids, 
			String parentPid) throws JsonProcessingException {
		
		super(url, digitalOrPhysical, pidIssuer, digitalObjectSubtype, digitalObjectType, materialSampleName, principalAgent, identifier);
		this.parentPid = parentPid;
		this.siblingPids = siblingPids;
		
		initializeEntriesSplit();
	}
	
	public void setDigitalSpecimenRecordSplit(byte[] handle, String parentPid, String siblingPids) throws JsonProcessingException {
		super.setDigitalSpecimenRecord(handle);
		this.pidStatus = "TEST";
		this.parentPid = parentPid;
		this.siblingPids = siblingPids;
		initializeEntriesSplit();
	}
	
	private void initializeEntriesSplit() {
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "siblingPids", siblingPids, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "parentPid", parentPid, timestamp));
	}
	
	public HandleRecordSpecimenSplit(byte[] handle, List<Handles> entries) throws JsonMappingException, JsonProcessingException {
		super(handle, entries);
		setSiblingInfoFromRecords(entries);
	                                            	
	}
	

	
	private void setSiblingInfoFromRecords(List<Handles> entries) {
		for (Handles h: entries) {
			if (h.getType().equals("siblingPids")) {
				siblingPids = h.getData();
			}
			else if(h.getType().equals("parentPid")) {
				parentPid = h.getData();
			}
		}
	}

}
