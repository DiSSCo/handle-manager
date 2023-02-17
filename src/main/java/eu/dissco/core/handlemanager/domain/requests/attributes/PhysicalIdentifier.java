package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhysicalIdentifier(
    @JsonProperty(required = true)
    String physicalId,
    String physicalIdType
) {

}
