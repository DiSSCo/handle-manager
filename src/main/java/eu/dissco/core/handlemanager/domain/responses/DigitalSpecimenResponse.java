package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
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

}
