package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TombstoneRecordRequest {

  @JsonProperty(required = true)
  private final byte[] handle;
  @JsonProperty(required = true)
  private final String tombstoneText;
  @JsonProperty(required = true)
  private final String[] tombstonePids = new String[]{};

  public TombstoneRecordRequest(String handle, String tombstoneText) {
    this.handle = handle.getBytes(StandardCharsets.UTF_8);
    this.tombstoneText = tombstoneText;
  }

}
