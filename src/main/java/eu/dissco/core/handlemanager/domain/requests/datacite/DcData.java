package eu.dissco.core.handlemanager.domain.requests.datacite;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.util.List;
import lombok.Getter;

@Getter
class DcData {
  private final String type = "dois";

  private final DcAttributes attributes;

  protected DcData(List<HandleAttribute> pidRecord){
    this.attributes = new DcAttributes(pidRecord);
  }

}
