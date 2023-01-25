package eu.dissco.core.handlemanager.domain.jsonapi;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonApiDataLinks(String id, String type, JsonNode attributes, JsonApiLinks links) {

}
