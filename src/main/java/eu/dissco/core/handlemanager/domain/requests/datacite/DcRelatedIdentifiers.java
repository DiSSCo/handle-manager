package eu.dissco.core.handlemanager.domain.requests.datacite;

record DcRelatedIdentifiers (
    String schemeUri,
    String schemeType,
    String relationType,
    String relatedIdentifier,
    String resourceTypeGeneral,
    String relatedMetadataScheme
){

}
