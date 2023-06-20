package eu.dissco.core.handlemanager.domain.requests;

import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;

public record UpsertDigitalSpecimen(
    String handle,
    String physicalId,
    DigitalSpecimenRequest request
) {

}
