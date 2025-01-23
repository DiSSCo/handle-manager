package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Handle Kernel post request", description = "Handle Kernel schema - Handle mode only")
public record HandlePostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/532ce6796e2828dd2be6",
        "https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6"
    })
    FdoType type,
    HandleRequestAttributes attributes) {

}
