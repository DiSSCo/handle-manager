package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DoiRecordRequest extends HandleRecordRequest {
	final String referentDoiName;

	public DoiRecordRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid,
			String[] loc, 
			// Doi
			String referentDoiName) {
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, loc);
		this.referentDoiName = referentDoiName;
	}

}
