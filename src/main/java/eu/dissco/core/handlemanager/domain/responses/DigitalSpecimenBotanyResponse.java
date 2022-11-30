package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import java.util.List;
import lombok.Data;

@Data
public class DigitalSpecimenBotanyResponse extends DigitalSpecimenResponse {

  String objectType;
  String preservedOrLiving;

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
}
