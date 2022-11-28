package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DigitalSpecimenResponse extends DoiRecordResponse {

  String digitalOrPhysical;
  String specimenHost;
  String inCollectionFacility;

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
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber, pidStatus,
        pidKernelMetadataLicense, hsAdmin, referentDoiName, referent);

    this.digitalOrPhysical = digititalOrPhysical;
    this.specimenHost = specimenHost;
    this.inCollectionFacility = inCollectionFacility;
  }

  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if (type.equals("digitalOrPhysical")) {
      this.digitalOrPhysical = data;
    } else if (type.equals("specimenHost")) {
      this.specimenHost = data;
    } else if (type.equals("inCollectionFacility")) {
      this.inCollectionFacility = data;
    } else {
      super.setAttribute(type, data);
    }
  }
}
