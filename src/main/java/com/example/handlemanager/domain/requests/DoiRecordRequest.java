package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DoiRecordRequest extends HandleRecordRequest {

	protected final String referentDoiNamePid;
	protected final String referent = "";
	
	public DoiRecordRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid,
			String[] locations, 
			// Doi
			String referentDoiNamePid) {
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);
		this.referentDoiNamePid = referentDoiNamePid;
	}
	
	public DoiRecordRequest() {
		super();
		referentDoiNamePid = "";
	}
	
	public HandleRecordRequest getHandleRecordRequest() {
		return new HandleRecordRequest(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);
	}
	
	

}


/*

/*
 * PID Record at this level
 * 
 * <Handle Kernel> (See HandleRecordRequest)
 * 
 * referentDoiName 	-> Retrieved from request PID
 * referent 		-> Metadata schema in progress. TODO
 * 
 */