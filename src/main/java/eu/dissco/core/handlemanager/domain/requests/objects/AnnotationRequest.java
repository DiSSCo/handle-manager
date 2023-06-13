package eu.dissco.core.handlemanager.domain.requests.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnnotationRequest extends HandleRecordRequest {

  private final String subjectDigitalObjectId;

  public AnnotationRequest(
      String fdoProfile,
      String issuedForAgent,
      String digitalObjectType,
      String pidIssuer,
      String structuralType,
      String[] locations,
      String subjectDigitalObjectId) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
    this.subjectDigitalObjectId=subjectDigitalObjectId;
  }
}
