package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MappingRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private final String sourceDataStandard;

  public MappingRequest(String issuedForAgent,
      String pidIssuer, String[] locations, String sourceDataStandard) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.sourceDataStandard = sourceDataStandard;
  }
}
