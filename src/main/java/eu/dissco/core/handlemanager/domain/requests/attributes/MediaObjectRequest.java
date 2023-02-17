package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaObjectRequest extends DoiRecordRequest {
  @JsonProperty(required = true)
  private final String mediaHash;
  @JsonProperty(required = true)
  private final String mediaUrl;
  @JsonProperty(required = true)
  private final PhysicalIdentifier physicalIdentifier;
  public MediaObjectRequest(
      String pidIssuerPid,
      String digitalObjectTypePid,
      String digitalObjectSubtypePid,
      String[] locations,
      String referentDoiNamePid,
      String mediaHash,
      String mediaUrl,
      PhysicalIdentifier physicalIdentifier) {
    super(pidIssuerPid, digitalObjectTypePid, digitalObjectSubtypePid, locations,
        referentDoiNamePid);
    this.mediaHash = mediaHash;
    this.mediaUrl = mediaUrl;
    this.physicalIdentifier = physicalIdentifier;
  }
}
