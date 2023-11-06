package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
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

  public OrganisationRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String[] locations, String referentName,
      String primaryReferentType, String organisationIdentifier,
      String organisationIdentifierType) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, ObjectType.DIGITAL_SPECIMEN, primaryReferentType);
    this.organisationIdentifier = organisationIdentifier;
    this.organisationIdentifierType =
        organisationIdentifierType == null ? "ROR" : organisationIdentifierType;
  }
}
