package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MappingRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private final String sourceDataStandard;
  public MappingRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String structuralType, String[] locations, String sourceDataStandard) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.sourceDataStandard = sourceDataStandard;
  }
}
