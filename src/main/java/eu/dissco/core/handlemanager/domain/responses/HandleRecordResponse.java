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
  private String hsAdmin;

  public HandleRecordResponse(List<Handles> entries) {
    String type;
    String data;

    for (Handles h : entries) {
      type = h.getType();
      data = h.getData();
      switch (type) {
        case "pid" -> this.pid = data;
        case "pidIssuer" -> this.pidIssuer = data;
        case "digitalObjectType" -> this.digitalObjectType = data;
        case "digitalObjectSubtype" -> this.digitalObjectSubtype = data;
        case "10320/loc" -> this.locs = data;
        case "issueDate" -> this.issueDate = data;
        case "issueNumber" -> this.issueNumber = data;
        case "pidKernelMetadataLicense" -> this.pidKernelMetadataLicense = data;
        case "HS_ADMIN" -> this.hsAdmin = data;
        default -> log.info("Base constructor called");
      }
    }

  }

}
