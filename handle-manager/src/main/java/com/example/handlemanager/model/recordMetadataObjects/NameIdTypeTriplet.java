package com.example.handlemanager.model.recordMetadataObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NameIdTypeTriplet {
	String pid;
	String pidType;
	String primaryNameFromPid;
	
	public NameIdTypeTriplet(String id, String type, String name) {
		pid = id;
		pidType = type;
		primaryNameFromPid = name;
	}
	
	public NameIdTypeTriplet(String id, String name) {
		pid = id;
		primaryNameFromPid = name;
	}
	
	public NameIdTypeTriplet() {
		pid = "";
		pidType="";
		primaryNameFromPid="";
	}
	
	@Override
	public String toString() {
		return "{ \n"
				+ "\"pid\": \"" + pid + "\", \n"
				+ "\"pidType\": \"" + pidType + "\", \n"
				+ "\"primaryNameFromPid\": \"" + primaryNameFromPid + "\" \n"
				+ "}";
	}
	

	public boolean equals(NameIdTypeTriplet obj2) {
		return (pid.equals(obj2.getPid()) &&
				pidType.equals(obj2.getPidType()) &&
				primaryNameFromPid.equals(obj2.getPrimaryNameFromPid()));
		
	}
	
	@JsonIgnore
	public boolean isNull() {
		return (pid == null &&
				pidType == null &&
				primaryNameFromPid == null);
	}
	
}
