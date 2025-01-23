package eu.dissco.core.handlemanager.domain.openapi.patch;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Data mapping patch request", description = "Data Mapping schema - Handle mode only")
public record DataMappingPatchRequest(
    String id,
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/ce794a6f4df42eb7e77e",
        "https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e"})
    FdoType type,
    DataMappingRequestAttributes attributes) {

}
