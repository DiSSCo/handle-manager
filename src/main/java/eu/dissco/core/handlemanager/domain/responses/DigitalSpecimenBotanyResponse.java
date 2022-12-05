package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
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
}
