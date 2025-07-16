package eu.dissco.core.handlemanager.domain.openapi.patch;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.VirtualCollectionRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Virtual Collection patch request", description = "Virtual Collection schema - Handle mode only")
public record VirtualCollectionPatchRequest(
    String id,
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/2ac65a933b7a0361b651",
        "https://hdl.handle.net/21.T11148/2ac65a933b7a0361b651"
    })
    FdoType type,
    VirtualCollectionRequestAttributes attributes) {

}
