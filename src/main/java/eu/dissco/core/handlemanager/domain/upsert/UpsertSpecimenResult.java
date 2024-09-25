package eu.dissco.core.handlemanager.domain.upsert;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import java.util.List;
import java.util.Map;

public record UpsertSpecimenResult(
    List<DigitalSpecimenRequestAttributes> newSpecimenRequests,
    Map<DigitalSpecimenRequestAttributes, FdoRecord> updateRequests
) {

}
