package com.example.handlemanager.model.HandleRecordSpecimen;

import java.util.ArrayList;
import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;
import com.fasterxml.jackson.annotation.JsonProperty;


public class HandleRecordTombstone extends HandleRecord {
	private String tombstoneText;
	private String relationStatus;
	private String relatedPid = "";
	private String childPids = "";
	private String siblingPids = "";
	
	
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
				case "siblingPids":
					this.siblingPids = h.getData();
					break;
				case "childPids":
					this.childPids = h.getData();
					break;
				default:
			}	
		}
	}
	
	public void setRelationStatusSplit(String childPids) {
		relationStatus = "SPLIT";
		this.childPids = childPids;
		setRelation();
	}
	
	public void setRelationStatusMerged(String siblings, String child) {
		relationStatus = "MERGED";
		siblingPids = siblings;
		childPids = child;
		setRelation();
	}
	
	private void setRelation() {
		entries.add(new Handles(handle, entries.size()+1, "pidRelation", relationStatus, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "siblingPids", siblingPids, timestamp));
		entries.add(new Handles(handle, entries.size()+1, "childPids", childPids, timestamp));
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
