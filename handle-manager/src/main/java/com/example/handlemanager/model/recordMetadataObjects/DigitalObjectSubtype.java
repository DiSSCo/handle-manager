package com.example.handlemanager.model.recordMetadataObjects;

import lombok.Data;

@Data
public class DigitalObjectSubtype {
	String pid;
	String pidType = "Handle";
	String primaryNameFromPid;
}
