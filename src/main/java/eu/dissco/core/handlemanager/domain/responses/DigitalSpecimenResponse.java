package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DigitalSpecimenResponse extends DoiRecordResponse {

  private String digitalOrPhysical;
  private String specimenHost;
  private String inCollectionFacility;

  public DigitalSpecimenResponse(
      // Handle Record
      String pid,
      String pidIssuer,
      String digitalObjectType,
      String digitalObjectSubtype,
      String locs,
      String issueDate,
      String issueNumber,
      String pidStatus,
      String pidKernelMetadataLicense,
      String hsAdmin,
      // Doi
      String referentDoiName,
      String referent,
      // Digital Specimen
      String digititalOrPhysical,
      String specimenHost,
      String inCollectionFacility) {
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber,
        pidStatus,
        pidKernelMetadataLicense, hsAdmin, referentDoiName, referent);

    this.digitalOrPhysical = digititalOrPhysical;
    this.specimenHost = specimenHost;
    this.inCollectionFacility = inCollectionFacility;
  }

  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    switch (type) {
      case "digitalOrPhysical" -> this.digitalOrPhysical = data;
      case "specimenHost" -> this.specimenHost = data;
      case "inCollectionFacility" -> this.inCollectionFacility = data;
      default -> super.setAttribute(type, data);
    }
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
    DigitalSpecimenResponse that = (DigitalSpecimenResponse) o;
    return getDigitalOrPhysical().equals(that.getDigitalOrPhysical()) && getSpecimenHost().equals(
        that.getSpecimenHost()) && getInCollectionFacility().equals(that.getInCollectionFacility());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getDigitalOrPhysical(), getSpecimenHost(),
        getInCollectionFacility());
  }
}
