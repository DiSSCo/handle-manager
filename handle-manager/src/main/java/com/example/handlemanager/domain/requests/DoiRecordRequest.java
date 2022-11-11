package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DoiRecordRequest extends HandleRecordRequest {
	final String referentDoiName;
	final String referent = "";

	public DoiRecordRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid,
			String[] locations, 
			// Doi
			String referentDoiName) {
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);
		this.referentDoiName = referentDoiName;
	}
	
	public HandleRecordRequest getHandleRecordRequest() {
		return new HandleRecordRequest(
				this.pidIssuerPid, 
				this.digitalObjectTypePid,
				this.digitalObjectSubtypePid,
				this.locations);				
	}

}
