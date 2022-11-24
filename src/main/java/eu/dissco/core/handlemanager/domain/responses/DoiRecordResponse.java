package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.jparepository.Handles;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
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
    super(pid, pidIssuer, digitalObjectType, digitalObjectSubtype, locs, issueDate, issueNumber, pidStatus,
        pidKernelMetadataLicense, hsAdmin);
    this.referentDoiName = referentDoiName;
    this.referent = referent;

  }

  public DoiRecordResponse(List<Handles> entries) {
    super(entries);

    String type;

    for (Handles h : entries) {
      type = h.getType();
      if (type.equals("referentDoiName")) {
        this.referentDoiName = h.getData();
      }
      if (type.equals("referent")) {
        this.referent = h.getData();
      }
    }
  }

  @Override
  public void setAttribute(String type, String data)
      throws NoSuchFieldException {
    if(type.equals("referentDoiName")){
      this.referentDoiName = data;
    }
    else if (type.equals("referent")){
      this.referent = data;
    }
    else {
      super.setAttribute(type, data);
    }
  }

  /*
  private void setDoiAttribute(String type, String data)
      throws NoSuchFieldException, IllegalAccessException {
    this.getClass().getDeclaredField(type).set(this, data);
  }*/

}
