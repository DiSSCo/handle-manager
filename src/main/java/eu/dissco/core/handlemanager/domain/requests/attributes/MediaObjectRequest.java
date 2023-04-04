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
  private final String subjectSpecimenHostPid;
  @JsonProperty(required = true)
  private final PhysicalIdentifier subjectPhysicalIdentifier;

  public MediaObjectRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectTypePid,
      String pidIssuer,
      String structuralType,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // Media
      String mediaHash,
      String mediaUrl,
      String subjectSpecimenHostPid,
      PhysicalIdentifier physicalIdentifier) {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations, referentName, primaryReferentType);
    this.mediaHash = mediaHash;
    this.mediaUrl = mediaUrl;
    this.subjectPhysicalIdentifier = physicalIdentifier;
    this.subjectSpecimenHostPid = subjectSpecimenHostPid;
  }
}
