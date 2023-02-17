package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.helpers.RequestDataWithId;

public record UpdateRequest(
    @JsonProperty(required = true)
    RequestDataWithId data
) {

}
