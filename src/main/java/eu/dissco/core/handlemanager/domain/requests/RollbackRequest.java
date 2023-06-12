package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record RollbackRequest(List<JsonNode> data) {

}
