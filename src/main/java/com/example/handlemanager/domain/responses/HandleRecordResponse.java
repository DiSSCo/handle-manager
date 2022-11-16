package com.example.handlemanager.domain.responses;

import com.example.handlemanager.repositoryobjects.Handles;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class HandleRecordResponse {
	private String pid;
	private String pidIssuer;
	private String digitalObjectType;
	private String digitalObjectSubtype;
	private String locs;
	private String issueDate;
	private String issueNumber;
	private String pidKernelMetadataLicense;
	private String hs_admin;

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
				case "HS_ADMIN" -> this.hs_admin = data;
				default -> log.info("Base constructor called");
			}
		}
		
	}
	
}
