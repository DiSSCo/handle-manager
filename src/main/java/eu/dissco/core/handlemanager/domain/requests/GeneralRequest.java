package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeneralRequest(
    @JsonProperty(required = true)
    RequestData data
) {

}
