package eu.dissco.core.handlemanager.domain.fdo;

import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.tettris.TettrisResourceType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.tettris.TettrisServiceCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TettrisServiceRequest extends HandleRecordRequest {

  private final TettrisServiceCategory serviceCategory;
  private final TettrisResourceType resourceType;
  private final String taxonomicTopic;

  public TettrisServiceRequest(String issuedForAgent,
      String pidIssuer, String[] locations, TettrisServiceCategory serviceCategory,
      TettrisResourceType resourceType, String taxonomicTopic) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.serviceCategory = serviceCategory;
    this.resourceType = resourceType;
    this.taxonomicTopic = taxonomicTopic;
  }
}
