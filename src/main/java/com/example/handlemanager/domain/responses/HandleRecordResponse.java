package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;

import lombok.Data;

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
			switch(type) {
				case "pid":
					this.pid = data;
					break;
				case "pid_issuer":
					this.pidIssuer = data;
					break;
				case "digitalObjectType":
					this.digitalObjectType = data;
					break;
				case "digitalObjectSubtype":
					this.digitalObjectSubtype = data;
					break;
				case "10320/loc":
					this.locs = data;
					break;
				case "issueDate":
					this.issueDate = data;
					break;
				case "issueNumber":
					this.issueNumber = data;
					break;
				case "pidKernelMetadataLicense":
					this.pidKernelMetadataLicense = data;
					break;
				case "HS_ADMIN":
					this.HS_ADMIN = data;
					break;
				default:
					break;
				
			}
		}
		
	}
	
}
