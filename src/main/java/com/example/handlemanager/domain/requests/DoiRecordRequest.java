package com.example.handlemanager.domain.requests;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DoiRecordRequest extends HandleRecordRequest {

  private final String referentDoiName;
  private final String referent = "";

  public DoiRecordRequest(
      // Handle
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      // Doi
      String referentDoiName) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);
    this.referentDoiName = referentDoiName;
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
    return Objects.equals(getReferentDoiName(), that.getReferentDoiName()) && Objects.equals(
        getReferent(), that.getReferent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getReferentDoiName(), getReferent());
  }
}
