package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;

public record TombstoneRequestData(
    @JsonPropertyDescription("DiSSCo Identifier of object")
    String id,
    FdoType type,
    TombstoneRequestAttributes attributes
) {

}
