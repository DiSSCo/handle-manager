package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ReplaceOrAppend;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnnotationRequest extends HandleRecordRequest {
  @JsonProperty(required = true)
  private final String subjectDigitalObjectId;
  @JsonProperty(required = true)
  private final String annotationTopic;
  @JsonProperty(required = true)
  private final ReplaceOrAppend replaceOrAppend;
  @Nullable
  private final Boolean accessRestricted;
  @Nullable
  private final String linkedObjectUrl;

  public AnnotationRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectType,
      String pidIssuer,
      String structuralType,
      String[] locations,
      String subjectDigitalObjectId,
      String annotationTopic,
      ReplaceOrAppend replaceOrAppend,
      Boolean accessRestricted,
      String linkedObjectUrl) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.subjectDigitalObjectId=subjectDigitalObjectId;
    this.annotationTopic = annotationTopic;
    this.replaceOrAppend=replaceOrAppend;
    this.accessRestricted = accessRestricted != null && accessRestricted;
    this.linkedObjectUrl = linkedObjectUrl;
  }
}
