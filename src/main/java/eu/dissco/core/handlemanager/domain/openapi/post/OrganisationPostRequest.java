package eu.dissco.core.handlemanager.domain.openapi.post;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Organisation post request", description = "Organisation schema - Handle mode only")
public record OrganisationPostRequest(
    @Schema(allowableValues = {"https://doi.org/21.T11148/413c00cbd83ae33d1ac0"}) FdoType type,
    OrganisationRequestAttributes attributes) {

}
