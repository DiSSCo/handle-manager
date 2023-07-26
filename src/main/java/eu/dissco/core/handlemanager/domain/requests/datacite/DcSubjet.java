package eu.dissco.core.handlemanager.domain.requests.datacite;

public record DcSubjet(
    String subject,
    String subjectScheme,
    String schemeUri,
    String valueUri,
    String classificationCode
) {

}
