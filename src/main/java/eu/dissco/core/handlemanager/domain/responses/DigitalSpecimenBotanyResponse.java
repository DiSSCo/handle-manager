package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DigitalSpecimenBotanyResponse extends DigitalSpecimenResponse {

  String objectType;
  String preservedOrLiving;

  public DigitalSpecimenBotanyResponse(
      // Handle
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
      String inCollectionFacility,
      // Botany Specimen
      String objectType,
      String preservedOrLiving) {
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber,
        pidStatus,
        pidKernelMetadataLicense, hsAdmin, referentDoiName, referent, digititalOrPhysical,
        specimenHost, inCollectionFacility);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }

  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if (type.equals("objectType")) {
      this.objectType = data;
    } else if (type.equals("preservedOrLiving")) {
      this.preservedOrLiving = data;
    } else {
      super.setAttribute(type, data);
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
    DigitalSpecimenBotanyResponse that = (DigitalSpecimenBotanyResponse) o;
    return getObjectType().equals(that.getObjectType()) && getPreservedOrLiving().equals(
        that.getPreservedOrLiving());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getObjectType(), getPreservedOrLiving());
  }
}
