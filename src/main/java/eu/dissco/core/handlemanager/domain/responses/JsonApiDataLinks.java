package eu.dissco.core.handlemanager.domain.responses;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonApiDataLinks(String id, String type, JsonNode attributes, JsonApiLinks links) {

}
