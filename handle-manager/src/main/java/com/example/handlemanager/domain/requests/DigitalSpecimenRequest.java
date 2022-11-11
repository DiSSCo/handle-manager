package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DigitalSpecimenRequest extends DoiRecordRequest {
	final String digitalOrPhysical;
	final String specimenHostPid;
	final String inCollectionFacilityPid;
	
	public DigitalSpecimenRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid,
			String[] locations, 
			// Doi
			String referentDoiName,
			// Digital Specimen
			String digitalOrPhysical,
			String specimenHostPid,
			String inCollectionFacilityPid
			) {
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiName);
		this.digitalOrPhysical = digitalOrPhysical;
		this.specimenHostPid = specimenHostPid;
		this.inCollectionFacilityPid = inCollectionFacilityPid;
	}
	
	public DoiRecordRequest getDoiRecordRequest() {
		return new DoiRecordRequest(
				this.pidIssuerPid, 
				this.digitalObjectTypePid, 
				this.digitalObjectSubtypePid, 
				this.locations,
				this.referentDoiName);
	}

}
