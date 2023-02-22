package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchByPhysIdAttributes(
    @JsonProperty(required = true)
    PhysicalIdentifier physicalIdentifier,
    @JsonProperty(required = true)
    PidTypeName specimenHost
) {

}
