package eu.dissco.core.handlemanager.domain.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdentifier;

public record SearchByPhysIdAttributes(
    @JsonProperty(required = true)
    PhysicalIdentifier physicalIdentifier,
    String specimenHostPid
) {

}
