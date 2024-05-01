package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.annotation.Motivation;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
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
  private final String targetType;
  @JsonProperty(required = true)
  private final Motivation motivation;
  private final UUID annotationHash;

  public AnnotationRequest(
      String issuedForAgent,
      String pidIssuer,
      String[] locations,
      String targetPid,
      String targetType,
      Motivation motivation,
      UUID annotationHash) {
    super(issuedForAgent, pidIssuer, StructuralType.DIGITAL,
        locations);
    this.targetPid = targetPid;
    this.targetType = targetType;
    this.motivation = motivation;
    this.annotationHash = annotationHash;
  }
}
