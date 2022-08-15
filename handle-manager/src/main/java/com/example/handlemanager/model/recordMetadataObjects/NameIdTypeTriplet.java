package com.example.handlemanager.model.recordMetadataObjects;

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
		pid = "123";
		pidType="TestPid";
		primaryNameFromPid="TestName";
	}
	
}
