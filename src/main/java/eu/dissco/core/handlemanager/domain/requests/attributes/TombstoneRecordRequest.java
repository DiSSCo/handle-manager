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
  private final String tombstoneText;

  private final String[] tombstonePids;;

  public TombstoneRecordRequest(String tombstoneText) {
    this.tombstoneText = tombstoneText;
    this.tombstonePids = null;
  }

  public TombstoneRecordRequest(String tombstoneText, String[] tombstonePids) {
    this.tombstoneText = tombstoneText;
    this.tombstonePids = tombstonePids;
  }

}
