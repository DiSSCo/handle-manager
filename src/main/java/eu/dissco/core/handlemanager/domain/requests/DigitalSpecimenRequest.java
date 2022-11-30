package eu.dissco.core.handlemanager.domain.requests;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
    DigitalSpecimenRequest that = (DigitalSpecimenRequest) o;
    return getDigitalOrPhysical().equals(that.getDigitalOrPhysical())
        && getSpecimenHostPid().equals(
        that.getSpecimenHostPid()) && getInCollectionFacilityPid().equals(
        that.getInCollectionFacilityPid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getDigitalOrPhysical(), getSpecimenHostPid(),
        getInCollectionFacilityPid());
  }
}
