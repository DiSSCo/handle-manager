package eu.dissco.core.handlemanager.domain.requests.datacite;

public record DcNameIdentifiers(
    String schemeUri,
    String nameIdentifier,
    String nameIdentifierScheme
) {

}
