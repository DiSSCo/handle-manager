package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
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
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber, pidStatus,
        pidKernelMetadataLicense, hsAdmin, referentDoiName, referent, digititalOrPhysical, specimenHost, inCollectionFacility);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }
  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if (type.equals("objectType")) {
      this.objectType = data;
    }
    else if (type.equals("preservedOrLiving")) {
      this.preservedOrLiving = data;
    }
    else {
      super.setAttribute(type, data);
    }
  }
}
