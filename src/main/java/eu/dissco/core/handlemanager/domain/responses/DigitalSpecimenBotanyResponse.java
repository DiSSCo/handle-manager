package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.jparepository.Handles;
import java.util.List;
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
      String objectType,
      // Botany Specimen
      String preservedOrLiving) {
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber, pidStatus,
        pidKernelMetadataLicense, hsAdmin, referentDoiName, referent, digititalOrPhysical, specimenHost, inCollectionFacility);
    this.objectType = objectType;
    this.preservedOrLiving = preservedOrLiving;
  }
  public DigitalSpecimenBotanyResponse(List<Handles> entries) {
    super(entries);
    String type;

    for (Handles h : entries) {
      type = h.getType();
      if (type.equals("objectType")) {
        this.objectType = h.getData();
      }
      if (type.equals("preservedOrLiving")) {
        this.preservedOrLiving = h.getData();
      }
    }
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
