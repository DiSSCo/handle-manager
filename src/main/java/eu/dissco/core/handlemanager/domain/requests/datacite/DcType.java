package eu.dissco.core.handlemanager.domain.requests.datacite;

import lombok.Value;

@Value
public class DcType {

  String resourceType;
  String resourceTypeGeneral = "Dataset";

  public DcType(String resourceType){
    this.resourceType = resourceType;
  }

}
