package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import java.util.List;

public record FdoRecord(
    String handle,
    FdoType fdoType,
    List<FdoAttribute> attributes,
    String primaryLocalId) {

}
