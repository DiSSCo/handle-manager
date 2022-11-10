package com.example.handlemanager.domain.requests;

import lombok.Data;

@Data
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest{
	protected final String objectType;
	protected final String preservedOrLiving;
	
	public DigitalSpecimenBotanyRequest(
			// Handle
			String pidIssuerPid, 
			String digitalObjectTypePid, 
			String digitalObjectSubtypePid, 
			String[] locations, 
			// DOI
			String referentDoiNamePid, 
			// Digital Specimen
			String digitalOrPhysical,
			String specimenHostPid, 
			String inCollectionFacilityPid,
			// Botany Specimen
			String objectType, 
			String preservedOrLiving) {
		
		super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiNamePid, digitalOrPhysical,
				specimenHostPid, inCollectionFacilityPid);
		
		this.objectType = objectType;
		this.preservedOrLiving = preservedOrLiving;
	}
	
	public DigitalSpecimenBotanyRequest() {
		super();
		objectType = "";
		preservedOrLiving = "";
	}
	
	public DigitalSpecimenRequest getDigitalSpecimenRequest() {
		return new DigitalSpecimenRequest(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiNamePid, digitalOrPhysical,
				specimenHostPid, inCollectionFacilityPid);
		}
	

}
