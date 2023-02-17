package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;

public record PutRequestData(
    @JsonProperty(required = true)
    @JsonPropertyDescription("DiSSCo Identifier of object")
    String id,
    @JsonProperty(required = true)
    TombstoneRecordRequest attributes
) {

}
