package com.example.handlemanager.domain.requests;

import lombok.Getter;

@Getter
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

}
