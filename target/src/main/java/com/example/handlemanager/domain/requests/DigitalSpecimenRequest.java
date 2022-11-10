package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DigitalSpecimenRequest extends DoiRecordRequest {
	
	protected final String digitalOrPhysical;
	protected final String specimenHostPid;
	protected final String inCollectionFacilityPid;

	public DigitalSpecimenRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid,
			String[] locations, 
			// DOI
			String referentDoiNamePid,
			// Handle
			String digitalOrPhysical,
			String specimenHostPid, 
			String inCollectionFacilityPid ) {
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiNamePid);
		
		this.digitalOrPhysical = digitalOrPhysical;
		this.specimenHostPid = specimenHostPid;
		this.inCollectionFacilityPid = inCollectionFacilityPid;
	}
	
	public DigitalSpecimenRequest() {
		super();
		digitalOrPhysical = "";
		specimenHostPid = "";
		inCollectionFacilityPid = "";
	}
	
	
	public DoiRecordRequest getDoiRecordRequest() {
		return new DoiRecordRequest(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiNamePid);
	}

}
