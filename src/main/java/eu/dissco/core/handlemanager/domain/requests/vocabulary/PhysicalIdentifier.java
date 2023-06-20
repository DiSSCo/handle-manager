package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhysicalIdentifier(
    @JsonProperty(required = true)
    String physicalId,
    @JsonProperty(required = true)
    PhysicalIdType physicalIdType
) {

}
