package eu.dissco.core.handlemanager.domain.requests.datacite;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import java.time.Instant;
import lombok.Getter;

@Getter
public class DcRequest {

  private final DcData data;

  public DcRequest(Instant created, DigitalSpecimenRequest request, String suffix, String url){
    this.data = new DcData(created, request, suffix, url);
  }

  public void setEvent(Event event){
    data.setEvent(event);
  }

}
