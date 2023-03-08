package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PatchRequest(
    @JsonProperty(required = true)
    PatchRequestData data
) {

}
