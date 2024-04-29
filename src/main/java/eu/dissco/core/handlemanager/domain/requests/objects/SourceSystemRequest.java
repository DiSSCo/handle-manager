package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.FdoType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SourceSystemRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private final String sourceSystemName;

  public SourceSystemRequest(String fdoProfile, String issuedForAgent, FdoType digitalObjectType,
      String pidIssuer, String[] locations, String sourceSystemName) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.sourceSystemName = sourceSystemName;
  }
}
