package eu.dissco.core.handlemanager.domain.openapi.patch;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Digital specimen patch request", description = "Digital specimen schema - DOI mode only")
public record DigitalSpecimenPatchRequest(
    String id,
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/894b1e6cad57e921764e",
        "https://hdl.handle.net/21.T11148/894b1e6cad57e921764e"
    })
    FdoType type,
    DigitalSpecimenRequestAttributes attributes) {

}
