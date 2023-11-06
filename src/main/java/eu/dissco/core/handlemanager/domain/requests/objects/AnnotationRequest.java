package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.annotation.Motivation;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.annotation.TargetType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnnotationRequest extends HandleRecordRequest {

  @JsonProperty(required = true)
  private final String targetPid;
  @JsonProperty(required = true)
  private final TargetType targetType;
  @JsonProperty(required = true)
  private final Motivation motivation;
  private final UUID annotationHash;

  public AnnotationRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectType,
      String pidIssuer,
      String[] locations,
      String targetPid,
      TargetType targetType,
      Motivation motivation,
      UUID annotationHash) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.targetPid = targetPid;
    this.targetType = targetType;
    this.motivation = motivation;
    this.annotationHash = annotationHash;
  }
}
