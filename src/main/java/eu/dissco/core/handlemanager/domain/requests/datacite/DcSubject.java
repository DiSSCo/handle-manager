package eu.dissco.core.handlemanager.domain.requests.datacite;

record DcSubject(
    String subject,
    String subjectScheme,
    String schemeUri,
    String valueUri,
    String classificationCode
) {

}
