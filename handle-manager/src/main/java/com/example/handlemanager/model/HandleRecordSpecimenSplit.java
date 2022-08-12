package com.example.handlemanager.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HandleRecordSpecimenSplit extends HandleRecordSpecimen {
	
	private String relationStatus = "SPLIT";
	private String siblingPidStr;
	

	public HandleRecordSpecimenSplit(byte[] handle, String url, String digitalObjectType, String institute, List<String> siblingPids) throws JsonProcessingException {
		super(handle, url, digitalObjectType, institute);
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
		setSiblingInfo(siblingPids);
	}
	
	public HandleRecordSpecimenSplit(List<Handles> entries, byte[] handle) throws JsonMappingException, JsonProcessingException {
		super(entries, handle);
		setSiblingInfoFromRecords(entries);
	                                            	
	}
	
	private void setSiblingInfo(List<String> siblingPids){
		siblingPidStr = "{";
		for (String sib : siblingPids) {
			siblingPidStr += " \"" + sib + " \",";
		}
		
		siblingPidStr = siblingPidStr.substring(0, siblingPidStr.length()-1) + "}";
		entries.add(new Handles(handle, entries.size()+1, "siblingPids", siblingPidStr, timestamp));
	}
	
	private void setSiblingInfoFromRecords(List<Handles> entries) {
		for (Handles h: entries) {
			if (h.getType().equals("siblingPids")) {
				siblingPidStr = h.getData();
			}
		}
	}

}
