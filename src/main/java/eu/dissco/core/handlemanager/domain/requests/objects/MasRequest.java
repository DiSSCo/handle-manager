package eu.dissco.core.handlemanager.domain.requests.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MasRequest extends HandleRecordRequest {
  private final String masName;

  public MasRequest(String fdoProfile, String issuedForAgent,
      String digitalObjectType, String pidIssuer, String structuralType, String[] locations, String masName) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.masName = masName;
  }
}
