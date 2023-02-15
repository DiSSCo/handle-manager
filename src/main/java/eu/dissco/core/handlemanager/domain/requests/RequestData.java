package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;

public record RequestData(
    @JsonProperty(required = true)
    @JsonPropertyDescription("type of object")
    String type,
    @JsonProperty(required = true)
    JsonNode attributes
) {

}
