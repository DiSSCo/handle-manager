package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest {
	final String objectType;
	final String preservedOrLiving;

	public DigitalSpecimenBotanyRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid,
			String digitalObjectSubtypePid, 
			String[] locations, 
			// Referent
			String referentDoiName,
			// Digital Specimen
			String digitalOrPhysical,
			String specimenHostPid, 
			String inCollectionFacilityPid,
			// Botany Specimen
			String objectType,
			String preservedOrLiving
			) {
		
		
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiName, digitalOrPhysical,
				specimenHostPid, inCollectionFacilityPid);
		this.objectType = objectType;
		this.preservedOrLiving = preservedOrLiving;
	}
	
	public DigitalSpecimenRequest getDigitalSpecimenRequest() {
		return new DigitalSpecimenRequest(
				this.pidIssuerPid, 
				this.digitalObjectTypePid, 
				this.digitalObjectSubtypePid, 
				this.locations,
				this.referentDoiName, 
				this.digitalOrPhysical, 
				this.specimenHostPid, 
				this.inCollectionFacilityPid);
	}

}
