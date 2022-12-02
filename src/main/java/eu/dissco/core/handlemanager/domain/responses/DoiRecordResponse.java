package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoiRecordResponse extends HandleRecordResponse {

  private String referentDoiName;
  private String referent;

  public DoiRecordResponse(
      // Handle
      String pid,
      String pidIssuer,
      String digitalObjectType,
      String digitalObjectSubtype,
      String locs,
      String issueDate,
      String issueNumber,
      String pidStatus,
      String pidKernelMetadataLicense,
      String hsAdmin,
      //Doi
      String referentDoiName,
      String referent) {
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber,
        pidStatus,
        pidKernelMetadataLicense, hsAdmin);
    this.referentDoiName = referentDoiName;
    this.referent = referent;
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
    DoiRecordResponse response = (DoiRecordResponse) o;
    return getReferentDoiName().equals(response.getReferentDoiName()) && getReferent().equals(
        response.getReferent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getReferentDoiName(), getReferent());
  }
}
