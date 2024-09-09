package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HasRelatedPid(
    @JsonProperty("pid")
    String odsId,
    @JsonProperty("relationshipType")
    String odsRelationshipType
) {

}
