package eu.dissco.core.handlemanager.domain.openapi.patch;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Annotation patch request", description = "Annotation schema - Handle mode only")
public record AnnotationPatchRequest(
    String id,
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/cf458ca9ee1d44a5608f",
        "https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f"})
    FdoType type,
    AnnotationRequestAttributes attributes) {

}
