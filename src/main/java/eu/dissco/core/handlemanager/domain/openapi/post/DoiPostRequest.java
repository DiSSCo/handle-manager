package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DOI Kernel post request", description = "DOI kernel schema - DOI mode only")
public record DoiPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/527856fd709ec8c5bc8c",
        "https://doi.org/21.T11148/527856fd709ec8c5bc8c"
    })
    FdoType type,
    DoiKernelRequestAttributes attributes) {

}
