package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DoiRecordRequest extends HandleRecordRequest {

  private final String referentType;

  @JsonPropertyDescription("Local name of the object (human-readable)")
  private final String referentName;

  @JsonPropertyDescription("Primary referent Type. Defaults to \"creation\"")
  private final String primaryReferentType;

  public DoiRecordRequest(
      // Handle
      String issuedForAgent,
      String pidIssuer,
      StructuralType structuralType,
      String[] locations,
      // Doi
      String referentName,
      String referentType,
      String primaryReferentType) {
    super(issuedForAgent, pidIssuer, structuralType, locations);
    this.referentType = referentType;
    this.referentName = referentName;
    this.primaryReferentType = setDefault(primaryReferentType, "creation");
  }
}
