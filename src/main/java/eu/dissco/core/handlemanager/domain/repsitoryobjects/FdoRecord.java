package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import java.util.List;

public record FdoRecord(
    @JsonProperty("_id")
    String handle,
    @JsonIgnore
    FdoType fdoType,
    @JsonProperty("values")
    List<FdoAttribute> attributes,
    @JsonIgnore
    String primaryLocalId) {

}
