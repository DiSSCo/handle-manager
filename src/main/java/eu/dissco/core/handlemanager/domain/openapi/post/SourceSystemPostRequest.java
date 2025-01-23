package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Source System post request", description = "Source system schema - Handle mode only")
public record SourceSystemPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/23a63913d0c800609a50",
        "https://hdl.handle.net/21.T11148/23a63913d0c800609a50"
    })
    FdoType type,
    SourceSystemRequestAttributes attributes) {

}
