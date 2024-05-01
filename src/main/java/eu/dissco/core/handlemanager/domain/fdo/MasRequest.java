package eu.dissco.core.handlemanager.domain.fdo;

import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MasRequest extends HandleRecordRequest {

  private final String machineAnnotationServiceName;

  public MasRequest(String issuedForAgent,
      String pidIssuer, String[] locations, String masName) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.machineAnnotationServiceName = masName;
  }
}
