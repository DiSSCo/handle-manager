package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

// no
@Schema(name = "Annotation post request", description = "Annotation schema - handle mode only")
public record AnnotationPostRequest(
    @Schema(allowableValues = {
        "https://doi.org/21.T11148/cf458ca9ee1d44a5608f",
        "https://doi.org/21.T11148/cf458ca9ee1d44a5608f"
    })
    FdoType type,
    AnnotationRequestAttributes attributes) {

}
