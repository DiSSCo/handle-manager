package eu.dissco.core.handlemanager.domain.upsert;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import java.util.List;
import java.util.Map;

public record UpsertMediaResult(
    List<DigitalMediaRequestAttributes> newMediaRequests,
    Map<DigitalMediaRequestAttributes, FdoRecord> updateMediaRequests
) {

}
