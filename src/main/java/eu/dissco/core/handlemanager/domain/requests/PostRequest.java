package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostRequest(
    @JsonProperty(required = true)
    PostRequestData data
) {

}
