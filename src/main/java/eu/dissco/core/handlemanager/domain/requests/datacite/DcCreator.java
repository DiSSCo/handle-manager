package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DcCreator(
    String name,
    List<DcAffiliation> affiliation,
    List<DcNameIdentifiers> nameIdentifiers) {
  @JsonProperty("nameType")
  private static final String NAME_TYPE= "organizational";
}
