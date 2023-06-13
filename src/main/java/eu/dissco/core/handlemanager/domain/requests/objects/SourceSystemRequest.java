package eu.dissco.core.handlemanager.domain.requests.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SourceSystemRequest extends HandleRecordRequest{

  private final String hostInstitution;
  public SourceSystemRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String structuralType, String[] locations, String hostInstitution) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.hostInstitution = hostInstitution;
  }
}
