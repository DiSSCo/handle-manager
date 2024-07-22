package eu.dissco.core.handlemanager.domain.fdo.vocabulary.tombstone;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HasRelatedPid(
    @JsonProperty("ods:ID")
    String odsId,
    @JsonProperty("ods:relationshipType")
    String odsRelationshipType
) {

}
