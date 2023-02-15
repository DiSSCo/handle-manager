package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DoiRecordRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private static final String REFERENT_PLACEHOLDER = "";
  @JsonProperty(required = true)
  private final String referentDoiNamePid;
  @JsonProperty(required = true)
  private final String referent;

  public DoiRecordRequest(
      // Handle
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      // Doi
      String referentDoiNamePid) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);
    this.referentDoiNamePid = referentDoiNamePid;
    this.referent = REFERENT_PLACEHOLDER;
  }
}
