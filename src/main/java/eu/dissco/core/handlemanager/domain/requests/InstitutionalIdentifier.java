package eu.dissco.core.handlemanager.domain.requests;

import lombok.NonNull;

public record InstitutionalIdentifier(
    @NonNull String institutionalId,
    String institutionalIdType
) {

}
