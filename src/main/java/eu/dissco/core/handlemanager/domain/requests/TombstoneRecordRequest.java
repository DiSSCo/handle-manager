package eu.dissco.core.handlemanager.domain.requests;

import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class TombstoneRecordRequest {

  @NonNull
  private final byte[] handle;
  @NonNull
  private final String tombstoneText;
  @NonNull
  private final String[] tombstonePids = new String[]{};

  public TombstoneRecordRequest(String handle, String tombstoneText) {
    this.handle = handle.getBytes(StandardCharsets.UTF_8);
    this.tombstoneText = tombstoneText;
  }

}
