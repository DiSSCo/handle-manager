package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchByPhysIdRequest(
    @JsonProperty(required = true)
    SearchByPhysIdRequestData data
) {

}
