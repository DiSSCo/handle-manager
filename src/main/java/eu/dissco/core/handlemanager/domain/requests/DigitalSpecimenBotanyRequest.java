package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest {
  @JsonProperty(required = true)
  @JsonPropertyDescription("Type of object (e.g. herbarium sheet)")
  private final String objectType;
  @JsonProperty(required = true)
  @JsonPropertyDescription("Indicates specimen is preserved or living")
  private final String preservedOrLiving;

  public DigitalSpecimenBotanyRequest(
      // Handle
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      // Referent
      String referentDoiNamePid,
      // Digital Specimen
      DigitalOrPhysical digitalOrPhysical,
      String specimenHostPid,
      String inCollectionFacilityPid,
      PhysicalIdentifier physicalIdentifier,
      // Botany Specimen
      String objectType,
      String preservedOrLiving
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid,
        digitalOrPhysical,
        specimenHostPid, inCollectionFacilityPid, physicalIdentifier);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }

}
