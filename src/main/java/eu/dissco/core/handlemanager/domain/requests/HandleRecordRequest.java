package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  private final String pidIssuerPid;
  @JsonProperty(required = true)
  private final String digitalObjectTypePid;
  @JsonProperty(required = true)
  private final String digitalObjectSubtypePid;
  @JsonProperty(required = true)
  private final String[] locations;

}
