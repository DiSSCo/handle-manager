package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.domain.requests.attributes.SearchByPhysIdAttributes;

public record SearchByPhysIdRequestData(
    @JsonProperty(required = true)
    SearchByPhysIdAttributes attributes

) {

}
