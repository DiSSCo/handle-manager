package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Virtual Collection post request", description = "Virtual Collection schema - Handle mode only")
public record VirtualCollectionPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/2ac65a933b7a0361b651",
        "https://hdl.handle.net/21.T11148/2ac65a933b7a0361b651"
    })
    FdoType type,
    SourceSystemRequestAttributes attributes) {
}
