package eu.dissco.core.handlemanager.domain.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaObjectRequest extends DoiRecordRequest {

  @NonNull
  private final String mediaHash;
  @NonNull
  private final String mediaUrl;
  @NonNull
  private final String primaryPhysicalId;


  public MediaObjectRequest(
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      @NonNull String referentDoiNamePid,
      @NonNull String mediaHash,
      @NonNull String mediaUrl,
      @NonNull String primaryPhysicalId) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid);
    this.mediaHash = mediaHash;
    this.mediaUrl = mediaUrl;
    this.primaryPhysicalId = primaryPhysicalId;
  }
}
