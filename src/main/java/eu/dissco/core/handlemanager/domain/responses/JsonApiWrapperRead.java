package eu.dissco.core.handlemanager.domain.responses;

import java.util.List;

public record JsonApiWrapperRead(JsonApiLinks links, List<JsonApiDataLinks> data) {

}
