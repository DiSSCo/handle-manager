package eu.dissco.core.handlemanager.domain.requests.objects;

import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MasRequest extends HandleRecordRequest {

  private final String machineAnnotationServiceName;

  public MasRequest(String fdoProfile, String issuedForAgent,
      String pidIssuer, String[] locations, String masName) {
    super(fdoProfile, issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.machineAnnotationServiceName = masName;
  }
}
