package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import java.time.Instant;
import lombok.Getter;

@Getter
public class DcData {
  @JsonProperty("type")
  private static final String TYPE = "dois";

  private final DcAttributes attributes;

  public DcData(Instant created, DigitalSpecimenRequest request, String suffix, String url){
    this.attributes = new DcAttributes(created, request, suffix, url);
  }

}
