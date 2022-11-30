package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DigitalSpecimenResponse extends DoiRecordResponse {

  private String digitalOrPhysical;
  private String specimenHost;
  private String inCollectionFacility;

  public DigitalSpecimenResponse(List<Handles> entries) {
    super(entries);
    String type;

    for (Handles h : entries) {
      type = h.getType();
      if (type.equals("digitalOrPhysical")) {
        this.digitalOrPhysical = h.getData();
      }
      if (type.equals("specimenHost")) {
        this.specimenHost = h.getData();
      }
      if (type.equals("inCollectionFacility")) {
        this.inCollectionFacility = h.getData();
      }
    }
  }
}
