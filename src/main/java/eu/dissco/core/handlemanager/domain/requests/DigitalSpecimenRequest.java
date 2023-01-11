package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
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
      @NonNull String digitalOrPhysical,
      @NonNull String specimenHostPid,
      @NonNull String inCollectionFacilityPid
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiName);
    this.digitalOrPhysical = digitalOrPhysical;
    this.specimenHostPid = specimenHostPid;
    this.inCollectionFacilityPid = inCollectionFacilityPid;
  }

}
