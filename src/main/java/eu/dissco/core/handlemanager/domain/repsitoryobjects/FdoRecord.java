package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import java.util.Collection;
import java.util.Map;

public record FdoRecord(
    @JsonProperty("_id")
    String handle,
    @JsonIgnore
    FdoType fdoType,
    @JsonIgnore
    Map<FdoProfile, FdoAttribute> attributes,
    @JsonIgnore
    String primaryLocalId,
    Collection<FdoAttribute> values
) {

}
