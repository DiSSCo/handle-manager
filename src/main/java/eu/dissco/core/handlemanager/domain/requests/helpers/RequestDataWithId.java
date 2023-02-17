package eu.dissco.core.handlemanager.domain.requests.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;

public record RequestDataWithId(
    @JsonProperty(required = true)
    @JsonPropertyDescription("type of object")
    String type,
    @JsonProperty(required = true)
    @JsonPropertyDescription("DiSSCo Identifier of object")
    String id,
    @JsonProperty(required = true)
    JsonNode attributes
) {

}
