package eu.dissco.core.handlemanager.domain.requests.datacite;

record DcRelatedIdentifiers (
    String relationType,
    String relatedIdentifier,
    String relatedIdentifierType,
    String resourceTypeGeneral
){

}
