package eu.dissco.core.handlemanager.domain.responses;

import java.util.Objects;
import lombok.Getter;

import lombok.NoArgsConstructor;

@Getter
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

  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    switch (type) {
      case "pid" -> this.pid = data;
      case "pidIssuer" -> this.pidIssuer = data;
      case "digitalObjectType" -> this.digitalObjectType = data;
      case "digitalObjectSubtype" -> this.digitalObjectSubtype = data;
      case "10320/loc" -> this.locs = data;
      case "issueDate" -> this.issueDate = data;
      case "issueNumber" -> this.issueNumber = data;
      case "pidStatus" -> this.pidStatus = data;
      case "pidKernelMetadataLicense" -> this.pidKernelMetadataLicense = data;
      case "HS_ADMIN" -> this.hsAdmin = data;
      default -> throw new NoSuchFieldException();
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
    HandleRecordResponse response = (HandleRecordResponse) o;
    return getPid().equals(response.getPid()) && getPidIssuer().equals(response.getPidIssuer())
        && getDigitalObjectType().equals(response.getDigitalObjectType())
        && getDigitalObjectSubtype().equals(response.getDigitalObjectSubtype()) && getLocs().equals(
        response.getLocs()) && getIssueDate().equals(response.getIssueDate())
        && getIssueNumber().equals(response.getIssueNumber())
        && getPidKernelMetadataLicense().equals(
        response.getPidKernelMetadataLicense()) && getPidStatus().equals(response.getPidStatus())
        && getHsAdmin().equals(response.getHsAdmin());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPid(), getPidIssuer(), getDigitalObjectType(), getDigitalObjectSubtype(),
        getLocs(), getIssueDate(), getIssueNumber(), getPidKernelMetadataLicense(), getPidStatus(),
        getHsAdmin());
  }
}
