package com.example.handlemanager.domain.requests;

import lombok.Getter;

@Getter
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest {

	protected final String objectType;
	protected final String preservedOrLiving;

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


}
