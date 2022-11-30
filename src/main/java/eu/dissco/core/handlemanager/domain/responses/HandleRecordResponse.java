package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import java.util.List;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Slf4j
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

}
