package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdentifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaObjectRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  private final String mediaHash; // Remove
  @JsonProperty(required = true)
  private final String mediaHashAlgorithm; // Remove
  @JsonProperty(required = true)
  private final String subjectSpecimenHost; // Remove
  @JsonProperty(required = true)
  private final String mediaUrl;
  @JsonProperty(required = true)
  private final PhysicalIdentifier subjectIdentifier; // Rename -> subjectLocalId? TBD

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
      String mediaHashAlgorithm,
      String subjectSpecimenHost,
      String mediaUrl,
      PhysicalIdentifier subjectIdentifier) {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, structuralType, locations, referentName, primaryReferentType);
    this.mediaHash = mediaHash;
    this.mediaHashAlgorithm = mediaHashAlgorithm;
    this.subjectSpecimenHost = subjectSpecimenHost;
    this.mediaUrl = mediaUrl;
    this.subjectIdentifier = subjectIdentifier;
  }
}
