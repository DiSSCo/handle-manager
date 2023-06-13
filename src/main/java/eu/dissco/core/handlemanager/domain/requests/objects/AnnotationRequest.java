package eu.dissco.core.handlemanager.domain.requests.objects;

public class AnnotationRequest extends HandleRecordRequest {

  public AnnotationRequest(String fdoProfile, String issuedForAgent, String digitalObjectType,
      String pidIssuer, String structuralType, String[] locations) {
    super(fdoProfile, issuedForAgent, digitalObjectType, pidIssuer, structuralType, locations);
  }
}
