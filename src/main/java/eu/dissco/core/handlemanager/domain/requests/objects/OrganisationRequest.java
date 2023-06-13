package eu.dissco.core.handlemanager.domain.requests.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class OrganisationRequest extends DoiRecordRequest{

  private final String organisationIdentifier;
  private final String organisationIdentifierType;

  public OrganisationRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String structuralType, String[] locations, String referentName,
      String primaryReferentType, String organisationIdentifier, String organisationIdentifierType) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations,
        referentName, primaryReferentType);
    this.organisationIdentifier = organisationIdentifier;
    this.organisationIdentifierType = organisationIdentifierType;
  }
}
