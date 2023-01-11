package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@Slf4j
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
      @NonNull String referentDoiNamePid,
      // Digital Specimen
      @NonNull String digitalOrPhysical,
      @NonNull String specimenHostPid,
      @NonNull String inCollectionFacilityPid
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiNamePid);
    this.digitalOrPhysical = digitalOrPhysical;
    this.specimenHostPid = specimenHostPid;
    this.inCollectionFacilityPid = inCollectionFacilityPid;
  }

}
