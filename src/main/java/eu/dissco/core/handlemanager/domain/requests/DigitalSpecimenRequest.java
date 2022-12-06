package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  private final String digitalOrPhysical;
  private final String specimenHostPid;
  private final String inCollectionFacilityPid;

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
