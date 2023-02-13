package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DigitalSpecimenBotanyRequest extends DigitalSpecimenRequest {

  @NonNull
  private final String objectType;
  @NonNull
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
      String digitalOrPhysical,
      String specimenHostPid,
      String inCollectionFacilityPid,
      // Botany Specimen
      @NonNull String objectType,
      @NonNull String preservedOrLiving
  ) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid,
        digitalOrPhysical,
        specimenHostPid, inCollectionFacilityPid);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }

}
