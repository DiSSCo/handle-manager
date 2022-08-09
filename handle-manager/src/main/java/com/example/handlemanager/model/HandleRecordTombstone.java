package com.example.handlemanager.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HandleRecordTombstone extends HandleRecord {
	private String tombstoneText;
	private String relationStatus;
	private String relatedPid;
	
	
	public HandleRecordTombstone(byte[] handle, String tombstoneText) {
		super(handle);
		pidStatus = "OBSOLETE";
		this.tombstoneText = tombstoneText;
		setEntries();
		setTombstone();
	}
	
	public HandleRecordTombstone(byte[] handle, List<Handles> entries) {
		super(handle);
		pidStatus="OBSOLETE";
		setMetadata(entries);
		this.entries = entries;
	}
	
	private void setTombstone() {
		entries.add(new Handles(handle, entries.size()+1, "tombstoneText", tombstoneText, timestamp));
	}
	
	private void setMetadata(List<Handles> entries) {
		String type;
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "tombstoneText":
					this.tombstoneText = h.getData();
					break;
				case "pidRelation":
					this.relationStatus = h.getData();
					break;
				case "relatedPid":
					this.relatedPid = h.getData();
					break;				
				default:
			}	
		}
	}
	
	public void setRelationStatusSplit(List<String> pid) {
		relationStatus = "SPLIT";
		relatedPid = "{";
		for (String p: pid) {
			relatedPid += "\"" + p + "\",";
		}
		relatedPid=relatedPid.substring(0, relatedPid.length()-1) + "}";
		setRelation();
	}
	
	public void setRelationStatusMerged(String pid) {
		relationStatus = "MERGED";
		relatedPid = pid;
		setRelation();
	}
	
	private void setRelation() {
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "relatedPid", relatedPid, timestamp));
	}
	
	@JsonProperty("Tombstone Text")
	public String getTombstone() {
		return tombstoneText;
	}
	
	@JsonProperty("Relation Status")
	public String getRelationStatus(){
		return this.relationStatus;
	}
	
	@JsonProperty("Related PID")
	public String getRelatedPid() {
		return this.relatedPid;
	}
	
	public boolean equals(HandleRecordTombstone ts) {
		return (getHandleStr().equals(ts.getHandleStr()) &&
				pidStatus.equals(ts.getPidStatus()) &&
				tombstoneText.equals(ts.getTombstone()));
	}
	
	
	
	

}
