package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;

public record PutRequestData(
    @JsonProperty(required = true)
    @JsonPropertyDescription("DiSSCo Identifier of object")
    String id,
    @JsonProperty(required = true)
    JsonNode attributes
) {

}
