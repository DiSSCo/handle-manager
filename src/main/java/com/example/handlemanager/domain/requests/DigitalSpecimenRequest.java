package com.example.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
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

}
