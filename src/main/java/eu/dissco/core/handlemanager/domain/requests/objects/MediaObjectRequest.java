package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.StructuralType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaObjectRequest extends DoiRecordRequest {

  @JsonProperty(required = true)
  private final String mediaUrl;
  @JsonProperty(required = true)
  private final String subjectLocalIdentifier; // Rename -> subjectLocalId? TBD
  private static final String REFERENT_TYPE = "Digital Media Object";

  public MediaObjectRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectTypePid,
      String pidIssuer,
      String[] locations,
      // Doi
      String referentName,
      String primaryReferentType,
      // Media
      String mediaUrl,
      String subjectLocalIdentifier) {
    super(fdoProfile, issuedForAgent, digitalObjectTypePid, pidIssuer, StructuralType.DIGITAL,
        locations,
        referentName, REFERENT_TYPE, primaryReferentType);
    this.mediaUrl = mediaUrl;
    this.subjectLocalIdentifier = subjectLocalIdentifier;
  }
}
