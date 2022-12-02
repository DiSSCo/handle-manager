package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if (type.equals("referentDoiName")) {
      this.referentDoiName = data;
    } else if (type.equals("referent")) {
      this.referent = data;
    } else {
      super.setAttribute(type, data);
    }
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
