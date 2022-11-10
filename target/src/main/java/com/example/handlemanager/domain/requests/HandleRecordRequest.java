package com.example.handlemanager.domain.requests;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HandleRecordRequest {
	protected final String pidIssuerPid;
	protected final String digitalObjectTypePid;
	protected final String digitalObjectSubtypePid;
	protected final String [] locations;
	
	public HandleRecordRequest() {
		pidIssuerPid = "";
		digitalObjectTypePid = "";
		digitalObjectSubtypePid = "";
		locations = new String[0];
	}
	
}
/*
 * PID Record at this level
 * 
 * pid					-> generated on creation
 * pidIssuer 			-> Retrieved from request PID
 * digitalObjectType 	-> Retrieved from request PID
 * digitalObjectSubtype	-> Retrieved from request PID
 * 10320/loc			-> generated from locations array
 * issueDate			-> generated on creation
 * issueNumber			-> generated on creation/incremented by program
 * pidStatus			-> generated on creation (context)
 * pidKernelMetadataLic -> generated on creation (constant)
 */