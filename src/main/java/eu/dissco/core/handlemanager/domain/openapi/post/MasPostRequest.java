package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.MachineAnnotationServiceRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Machine Annotation Service post request", description = "Machine annotation service schema - Handle mode only")
public record MasPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/a369e128df5ef31044d4",
        "https://hdl.handle.net/21.T11148/a369e128df5ef31044d40"
    })
    FdoType type,
    MachineAnnotationServiceRequestAttributes attributes) {

}
