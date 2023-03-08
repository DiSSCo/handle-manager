package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PutRequest(@JsonProperty(required = true) PutRequestData data) {

}
