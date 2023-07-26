package eu.dissco.core.handlemanager.domain.requests.datacite;

public record DcAffiliation(
    String affiliationIdentifier,
    String affiliationIdentifierScheme,
    String name,
    String SchemeUri
) {
}
