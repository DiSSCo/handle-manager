package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.requests.helpers.DigitalOrPhysical;
import eu.dissco.core.handlemanager.domain.requests.helpers.PhysicalIdentifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("Identifies specimen as digital or physical")
  private final DigitalOrPhysical digitalOrPhysical;
  @JsonProperty(required = true)
  @JsonPropertyDescription("PID of the host institution.")
  private final String specimenHostPid;
  @JsonProperty(required = true)
  @JsonPropertyDescription("PID of the collection the specimen is hosted in")
  private final String inCollectionFacilityPid;
  @JsonProperty(required = true)
  @JsonPropertyDescription("Primary identifier used by host institution")
  private final PhysicalIdentifier physicalIdentifier;

  public DigitalSpecimenRequest(
      // Handle
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      // Doi
      String referentDoiNamePid,
      // Digital Specimen
      DigitalOrPhysical digitalOrPhysical,
      String specimenHostPid,
      String inCollectionFacilityPid,
      PhysicalIdentifier physicalIdentifier
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid);
    this.digitalOrPhysical = digitalOrPhysical;
    this.specimenHostPid = specimenHostPid;
    this.inCollectionFacilityPid = inCollectionFacilityPid;
    this.physicalIdentifier = physicalIdentifier;
  }

}
