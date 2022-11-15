package com.example.handlemanager.domain.responses;

import com.example.handlemanager.model.repositoryObjects.Handles;
import lombok.Data;

import java.util.List;

@Data
public class HandleRecordResponse {
	private String pid;
	private String pidIssuer;
	private String digitalObjectType;
	private String digitalObjectSubtype;
	private String locs;
	private String issueDate;
	private String issueNumber;
	private String pidKernelMetadataLicense;
	private String HS_ADMIN;
	
	public HandleRecordResponse (List<Handles> entries) {
		String type;
		String data;
		
		for (Handles h: entries) {
			type = h.getType();
			data = h.getData();
			switch (type) {
				case "pid" -> this.pid = data;
				case "pidIssuer" -> this.pidIssuer = data;
				case "digitalObjectType" -> this.digitalObjectType = data;
				case "digitalObjectSubtype" -> this.digitalObjectSubtype = data;
				case "10320/loc" -> this.locs = data;
				case "issueDate" -> this.issueDate = data;
				case "issueNumber" -> this.issueNumber = data;
				case "pidKernelMetadataLicense" -> this.pidKernelMetadataLicense = data;
				case "HS_ADMIN" -> this.HS_ADMIN = data;
				default -> {
				}
			}
		}
		
	}
	
}
