package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SourceSystemRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private final String sourceSystemName;

  public SourceSystemRequest(String issuedForAgent,
      String pidIssuer, String[] locations, String sourceSystemName) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.sourceSystemName = sourceSystemName;
  }
}
