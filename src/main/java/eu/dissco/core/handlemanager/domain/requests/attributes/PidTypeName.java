package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PidTypeName(
    @JsonProperty(required = true)
    String pid,
    @JsonProperty(required = true)
    String pidType,
    @JsonProperty(required = true)
    String primaryNameFromPid

) {

}
