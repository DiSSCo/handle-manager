package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HasRelatedPid(
    @JsonProperty("ods:ID")
    String odsId,
    @JsonProperty("ods:relationshipType")
    String odsRelationshipType
) {

}
