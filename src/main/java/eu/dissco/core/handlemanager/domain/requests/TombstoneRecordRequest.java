package eu.dissco.core.handlemanager.domain.requests;

import java.nio.charset.StandardCharsets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TombstoneRecordRequest {

  private final byte[] handle;
  private final String tombstoneText;
  private final String[] tombstonePids = new String[]{};

  public TombstoneRecordRequest(String handle, String tombstoneText){
    this.handle = handle.getBytes(StandardCharsets.UTF_8);
    this.tombstoneText = tombstoneText;
  }

}
