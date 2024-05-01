package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class OrganisationRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  private final String organisationIdentifier;
  private final String organisationIdentifierType;

  public OrganisationRequest(String issuedForAgent,
      String pidIssuer, String[] locations, String referentName,
      String primaryReferentType, String organisationIdentifier,
      String organisationIdentifierType) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, FdoType.ORGANISATION.getDigitalObjectName(), primaryReferentType);
    this.organisationIdentifier = organisationIdentifier;
    this.organisationIdentifierType =
        organisationIdentifierType == null ? "ROR" : organisationIdentifierType;
  }
}
