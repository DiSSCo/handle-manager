package eu.dissco.core.handlemanager.domain.requests.objects;

public record DigitalSpecimenUpdateWrapper(
    String handle,
    DigitalSpecimenRequest digitalSpecimenRequest
) {

}
