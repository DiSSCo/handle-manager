package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import javax.validation.constraints.NotEmpty;
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
  @JsonPropertyDescription("Identifier for the agency issuing the PID.")
  private final String pidIssuerPid;
  @JsonProperty(required = true)
  @JsonPropertyDescription("PID for the Type of Digital Object")
  private final String digitalObjectTypePid;
  @JsonProperty(required = true)
  @JsonPropertyDescription("PID for the Subtype of Digital Object.")
  private final String digitalObjectSubtypePid;
  @JsonProperty(required = true)
  @JsonPropertyDescription("Array containing the locations of the object.")
  @NotEmpty
  private final String[] locations;

}
