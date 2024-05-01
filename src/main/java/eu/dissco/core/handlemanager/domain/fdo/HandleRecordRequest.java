package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
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
  @JsonPropertyDescription("ROR for the agent requesting the PID")
  private final String issuedForAgent;

  @JsonPropertyDescription("ROR of the Registration Agency. Defaults to DataCite's ROR")
  private final String pidIssuer;

  @JsonPropertyDescription("Structural Type of the Object. Defaults to digital")
  private final StructuralType structuralType;

  @JsonPropertyDescription("Array containing the locations of the object.")
  @Nullable
  private final String[] locations;

  @JsonIgnore
  @Getter(AccessLevel.NONE)
  private static final String DATACITE_ROR = "https://ror.org/04wxnsj81";

  public HandleRecordRequest(
      String issuedForAgent,
      String pidIssuer,
      StructuralType structuralType,
      String[] locations
  ) {
    this.issuedForAgent = issuedForAgent;
    this.pidIssuer = setDefault(pidIssuer, DATACITE_ROR);
    this.structuralType = structuralType == null ? StructuralType.DIGITAL : structuralType;
    this.locations = locations;
  }

  protected String setDefault(String attribute, String defaultVal) {
    return attribute == null ? defaultVal : attribute;
  }


}
