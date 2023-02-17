package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TombstoneRecordRequest {

  @JsonProperty(required = true)
  @JsonAlias("id")
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
