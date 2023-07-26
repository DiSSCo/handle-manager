package eu.dissco.core.handlemanager.domain.requests.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;

record DcDescription(
    String description
) {
  // Note: other or techincalInfo?
  @JsonProperty("descriptionType")
  private static final String DESCRIPTION_TYPE = "TechnicalInfo";

}
