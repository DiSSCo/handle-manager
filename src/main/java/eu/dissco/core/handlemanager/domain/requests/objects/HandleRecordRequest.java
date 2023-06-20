package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode
public class HandleRecordRequest {

  @JsonProperty(required = true)
  @JsonPropertyDescription("Handle of the FDO Profile")
  private final String fdoProfile;

  @JsonProperty(required = true)
  @JsonPropertyDescription("ROR for the agent requesting the PID")
  private final String issuedForAgent;

  @JsonProperty(required = true)
  @JsonPropertyDescription("PID for the Type of Digital Object")
  private final String digitalObjectType;

  @JsonPropertyDescription("ROR of the Registration Agency. Defaults to DataCite's ROR")
  private final String pidIssuer;

  @JsonPropertyDescription("Structural Type of the Object. Defaults to digital")
  private final String structuralType;

  @JsonPropertyDescription("Array containing the locations of the object.")
  @Nullable
  private final String[] locations;

  @JsonIgnore
  @Getter(AccessLevel.NONE)
  private static final String DATACITE_ROR = "https://ror.org/04wxnsj81";

  public HandleRecordRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectType,
      String pidIssuer,
      String structuralType,
      String[] locations
  ){
    this.fdoProfile = fdoProfile;
    this.issuedForAgent = issuedForAgent;
    this.digitalObjectType = digitalObjectType;
    this.pidIssuer = setDefault(pidIssuer, DATACITE_ROR);
    this.structuralType = setDefault(structuralType, "digital");
    this.locations = locations;
  }

  protected String setDefault(String attribute, String defaultVal){
    return attribute == null ? defaultVal : attribute;
  }


}
