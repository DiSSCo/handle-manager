package eu.dissco.core.handlemanager.domain.openapi.tombstone;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record TombstoneRequest(
    String id,
    FdoType type,
    TombstoneRequestAttributes attributes
) {

}
