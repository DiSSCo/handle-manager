package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResolveRequest(@JsonProperty(required = true) ResolveRequestData data) {

}
