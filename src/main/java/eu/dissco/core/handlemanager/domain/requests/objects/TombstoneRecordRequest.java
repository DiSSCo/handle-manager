package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
public class TombstoneRecordRequest {

  @JsonProperty(required = true)
  private final String tombstoneText;

  @Nullable
  private final String[] tombstonePids;

  public TombstoneRecordRequest(String tombstoneText) {
    this.tombstoneText = tombstoneText;
    this.tombstonePids = new String[]{};
  }

  public TombstoneRecordRequest(String tombstoneText, String[] tombstonePids) {
    this.tombstoneText = tombstoneText;
    this.tombstonePids = tombstonePids;
  }

}
