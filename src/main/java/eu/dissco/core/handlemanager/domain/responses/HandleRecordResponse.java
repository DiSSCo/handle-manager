package eu.dissco.core.handlemanager.domain.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandleRecordResponse {

  private String pid;
  private String pidIssuer;
  private String digitalObjectType;
  private String digitalObjectSubtype;
  private String locs;
  private String issueDate;
  private String issueNumber;
  private String pidKernelMetadataLicense;
  private String pidStatus;
  private String hsAdmin;

  public HandleRecordResponse(
      String pid,
      String pidIssuer,
      String digitalObjectType,
      String digitalObjectSubtype,
      String locs,
      String issueDate,
      String issueNumber,
      String pidStatus,
      String pidKernelMetadataLicense,
      String hsAdmin) {
    this.pid = pid;
    this.pidIssuer = pidIssuer;
    this.digitalObjectType = digitalObjectType;
    this.digitalObjectSubtype = digitalObjectSubtype;
    this.locs = locs;
    this.issueDate = issueDate;
    this.issueNumber = issueNumber;
    this.pidStatus = pidStatus;
    this.pidKernelMetadataLicense = pidKernelMetadataLicense;
    this.hsAdmin = hsAdmin;
  }
}
