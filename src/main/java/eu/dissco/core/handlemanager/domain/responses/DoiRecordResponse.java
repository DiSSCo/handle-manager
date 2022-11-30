package eu.dissco.core.handlemanager.domain.responses;

import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DoiRecordResponse extends HandleRecordResponse {

  private String referentDoiName;
  private String referent;

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
}
