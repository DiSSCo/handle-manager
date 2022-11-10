package com.example.handlemanager.domain.responses;

import java.util.List;

import com.example.handlemanager.model.repositoryObjects.Handles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonSerialize
public class HandleRecordResponse {
	protected String pid;
	protected String pidIssuer;
	protected String digitalObjectType;
	protected String digitalObjectSubtype;
	protected String loc;
	protected String issueDate;
	protected String issueNumber;
	protected String pidStatus;
	protected String pidKernelMetadataLicense;
	protected String HS_ADMIN;
	
	public HandleRecordResponse(List<Handles> entries) {
		String type;
		for (Handles h: entries) {
			type = h.getType();
			switch(type) {
				case "pid":
					this.pid= h.getData();
					break;
				case "pidIssuer":
					this.pidIssuer = h.getData();
					break;
				case "digitalObjectType":
					this.digitalObjectType = h.getData();
					break;
				case "digitalObjectSubtype":
					this.digitalObjectSubtype = h.getData();
					break;
				case "10320/loc":
					this.loc = h.getData();
					break;
				case "issueDate":
					this.issueDate = h.getData();
					break;
				case "issueNumber":
					this.issueNumber = h.getData();
					break;
				case "pidStatus":
					this.pidStatus = h.getData();
					break;
				case "pidKernelMetadataLicense":
					this.pidKernelMetadataLicense = h.getData();
					break;
				case "HS_ADMIN":
					this.HS_ADMIN = h.getData();
					break;
				default:
					break;
			}
			
		}	
	}
	
	@JsonIgnore
	public boolean isFullyInit() {
		return (pid != null &&
				pidIssuer != null &&
				digitalObjectType != null &&
				digitalObjectSubtype != null &&
				loc != null &&
				issueDate != null &&
				issueNumber != null &&
				pidStatus != null &&
				pidKernelMetadataLicense != null &&
				HS_ADMIN != null);				
	}
	
	

	
}
