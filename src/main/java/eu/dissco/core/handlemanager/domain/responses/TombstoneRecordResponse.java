package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Getter;

@Getter
public class TombstoneRecordResponse extends HandleRecordResponse {

  private String tombstoneText;
  private String tombstonePids;

  public TombstoneRecordResponse(){
    this.pidStatus = "ARCHIVED";
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
    TombstoneRecordResponse that = (TombstoneRecordResponse) o;
    return getTombstoneText().equals(that.getTombstoneText()) && Objects.equals(
        getTombstonePids(), that.getTombstonePids());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTombstoneText(), getTombstonePids());
  }

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

  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if (type.equals("referentDoiName")) {
      this.tombstoneText = data;
    } else if (type.equals("referent")) {
      this.tombstonePids = data;
    } else {
      super.setAttribute(type, data);
    }
  }





}
