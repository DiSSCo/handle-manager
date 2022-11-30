package eu.dissco.core.handlemanager.domain.requests;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations, referentDoiName,
        digitalOrPhysical,
        specimenHostPid, inCollectionFacilityPid);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DigitalSpecimenBotanyRequest that = (DigitalSpecimenBotanyRequest) o;
    return getObjectType().equals(that.getObjectType()) && getPreservedOrLiving().equals(
        that.getPreservedOrLiving());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getObjectType(), getPreservedOrLiving());
  }
}
