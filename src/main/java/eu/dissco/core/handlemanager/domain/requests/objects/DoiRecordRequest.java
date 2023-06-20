package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DoiRecordRequest extends HandleRecordRequest {

  private static final String PLACEHOLDER = "{This value is a placeholder}";

  private final String referentType;

  @JsonPropertyDescription("Local name of the object (human-readable)")
  private final String referentName;

  @JsonPropertyDescription("Primary referent Type. Defaults to \"creation\"")
  private final String primaryReferentType;

  @JsonPropertyDescription("Currently populated with placeholder string")
  private final String referent;

  public DoiRecordRequest(
      // Handle
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectType,
      String pidIssuer,
      String structuralType,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.referentType = PLACEHOLDER;
    this.referent = PLACEHOLDER;
    this.referentName = referentName;
    this.primaryReferentType = primaryReferentType == null ? "creation" : primaryReferentType;
  }
}
