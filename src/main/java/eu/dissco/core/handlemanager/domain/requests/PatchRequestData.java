package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;

public record PatchRequestData(
    @JsonProperty(required = true) @JsonPropertyDescription("DiSSCo Identifier of object") String id,
    FdoType type,
    @JsonProperty(required = true) JsonNode attributes) {

}
