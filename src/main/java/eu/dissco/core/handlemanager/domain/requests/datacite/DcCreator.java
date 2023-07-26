package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

record DcCreator(
    String name,
    List<DcNameIdentifiers> nameIdentifiers) {

  @JsonProperty("nameType")
  private static final String NAME_TYPE= "Organizational";
}
