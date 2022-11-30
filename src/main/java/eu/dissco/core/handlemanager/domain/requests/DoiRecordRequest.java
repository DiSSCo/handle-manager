package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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


  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DoiRecordRequest that = (DoiRecordRequest) o;
    return Objects.equals(getReferentDoiNamePid(), that.getReferentDoiNamePid()) && Objects.equals(
        getReferent(), that.getReferent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getReferentDoiNamePid(), getReferent());
  }
}
