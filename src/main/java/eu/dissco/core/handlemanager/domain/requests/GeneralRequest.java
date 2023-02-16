package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.helpers.RequestData;

public record GeneralRequest(
    @JsonProperty(required = true)
    RequestData data
) {

}
