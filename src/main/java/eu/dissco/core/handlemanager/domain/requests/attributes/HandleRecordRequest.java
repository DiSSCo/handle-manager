package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class HandleRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("PID of the FDO Profile")
  private final String fdoProfile;

  @JsonProperty(required = true)
  @JsonPropertyDescription("ROR for the agent requesting the PID")
  private final String issuedForAgent;

  @JsonProperty(required = true)
  @JsonPropertyDescription("PID for the Type of Digital Object")
  private final String digitalObjectTypePid;
  @JsonProperty(required = true)

  @JsonPropertyDescription("Array containing the locations of the object.")
  private final String[] locations;

  @JsonPropertyDescription("ROR of the Registration Agency. Defaults to DataCite's ROR")
  private final String pidIssuer;

  @JsonPropertyDescription("Structural Type of the Object. Defaults to digital")
  private final String structuralType;



  /*
  ** Handles **
  * fdoProfile -> Required
  * digitalObjectType -> Required
  * issuedForAgent
  *



   */

}
