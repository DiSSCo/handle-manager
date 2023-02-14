package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.databind.JsonNode;
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
  private final InstitutionalIdentifier institutionalIdentifier;

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
      @NonNull String inCollectionFacilityPid,
      @NonNull InstitutionalIdentifier institutionalIdentifier
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid);
    this.digitalOrPhysical = digitalOrPhysical;
    this.specimenHostPid = specimenHostPid;
    this.inCollectionFacilityPid = inCollectionFacilityPid;
    this.institutionalIdentifier = institutionalIdentifier;
  }

}
