package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TombstoneRecordResponse extends HandleRecordResponse {

  private String tombstoneText;
  private String tombstonePids;

  public TombstoneRecordResponse(
      // Handle
      String pid,
      String pidIssuer,
      String digitalObjectType,
      String digitalObjectSubtype,
      String locs,
      String issueDate,
      String issueNumber,
      String pidKernelMetadataLicense,
      String hsAdmin,
      //
      String tombstoneText,
      String tombstonePids) {
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber,
        "ARCHIVED",
        pidKernelMetadataLicense, hsAdmin);
    this.tombstoneText = tombstoneText;
    this.tombstonePids = tombstonePids;
  }

  public TombstoneRecordResponse() {
    super();
    this.setPidStatus("ARCHIVED");
  }


}
