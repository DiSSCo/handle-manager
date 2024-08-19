package eu.dissco.core.handlemanager.domain.datacite;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record DataCiteTombstoneEvent(String handle, List<JsonNode> dcRelatedIdentifiers) {

}
