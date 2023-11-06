package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;

public record PostRequestData(
    @JsonProperty(required = true)
    @JsonPropertyDescription("type of object")
    ObjectType type,
    @JsonProperty(required = true)
    JsonNode attributes
) {

}
