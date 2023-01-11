package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DoiRecordRequest extends HandleRecordRequest {
  @NonNull
  private static final String REFERENT_PLACEHOLDER = "";
  @NonNull
  private final String referentDoiNamePid;
  @NonNull
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
