package eu.dissco.core.handlemanager.domain.requests.datacite;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.util.List;
import lombok.Getter;

@Getter
public class DcRequest {

  private final DcData data;

  public DcRequest(List<HandleAttribute> pidRecord){
    this.data = new DcData(pidRecord);
  }

}
