package eu.dissco.core.handlemanager.domain.jsonapi;

import java.util.List;

public record JsonApiWrapperRead(JsonApiLinks links, List<JsonApiDataLinks> data) {

}
