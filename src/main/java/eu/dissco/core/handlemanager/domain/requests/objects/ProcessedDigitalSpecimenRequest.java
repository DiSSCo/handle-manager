package eu.dissco.core.handlemanager.domain.requests.objects;

import java.util.List;

public record ProcessedDigitalSpecimenRequest(
    List<DigitalSpecimenRequest> newRequests,
    List<DigitalSpecimenUpdateWrapper> updateRequests
) {

}
