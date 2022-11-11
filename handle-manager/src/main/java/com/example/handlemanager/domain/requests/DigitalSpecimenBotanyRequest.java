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
			String[] loc, 
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
		
		
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, loc, referentDoiName, digitalOrPhysical,
				specimenHostPid, inCollectionFacilityPid);
		this.objectType = objectType;
		this.preservedOrLiving = preservedOrLiving;
	}

}
