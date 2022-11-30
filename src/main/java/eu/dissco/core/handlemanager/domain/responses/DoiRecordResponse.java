package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DoiRecordResponse extends HandleRecordResponse {

  String referentDoiName;
  String referent;

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
}
