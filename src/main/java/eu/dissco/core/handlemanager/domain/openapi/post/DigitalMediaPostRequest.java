package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Digital media post request", description = "Digital media schema - DOI mode only")
public record DigitalMediaPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/bbad8c4e101e8af01115",
        "https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115"
    })
    FdoType type,
    DigitalMediaRequestAttributes attributes
) {

}
