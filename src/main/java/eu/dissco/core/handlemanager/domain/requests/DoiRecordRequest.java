package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DoiRecordRequest extends HandleRecordRequest {

  private static final String REFERENT_PLACEHOLDER = "";
  private final String referentDoiNamePid;
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
