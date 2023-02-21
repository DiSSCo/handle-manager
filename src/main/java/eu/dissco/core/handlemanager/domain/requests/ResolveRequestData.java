package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResolveRequestData(@JsonProperty(required = true) String id

) {

}
