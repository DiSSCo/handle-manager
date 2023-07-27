package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.util.List;
import lombok.Getter;

@Getter
class DcData {
  @JsonProperty("type")
  private static final String TYPE = "dois";

  private final DcAttributes attributes;

  protected DcData(List<HandleAttribute> pidRecord){
    this.attributes = new DcAttributes(pidRecord);
  }

}
