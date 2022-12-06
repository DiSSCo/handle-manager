package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TombstoneRecordRequest extends HandleRecordRequest {

  private final String tombstoneText;
  private final String[] tombstonePids;

  public TombstoneRecordRequest(String pidIssuerPid, String digitalObjectTypePid,
      String digitalObjectSubtypePid, String[] locations, String tombstoneText) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);

    this.tombstoneText = tombstoneText;
    this.tombstonePids = new String[]{};
  }

  // Tombstone PIDs only to be used in the event of a merge (maybe split?)
  public TombstoneRecordRequest(String pidIssuerPid, String digitalObjectTypePid,
      String digitalObjectSubtypePid, String[] locations, String tombstoneText,
      String[] tombstonePids) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations);

    this.tombstoneText = tombstoneText;
    this.tombstonePids = tombstonePids;
  }
}
