package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  private static final String REFERENT_TYPE = "Organisation";

  public OrganisationRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String structuralType, String[] locations, String referentName,
      String primaryReferentType, String organisationIdentifier,
      String organisationIdentifierType) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations,
        referentName, REFERENT_TYPE, primaryReferentType);
    this.organisationIdentifier = organisationIdentifier;
    this.organisationIdentifierType =
        organisationIdentifierType == null ? "ROR" : organisationIdentifierType;
  }
}
