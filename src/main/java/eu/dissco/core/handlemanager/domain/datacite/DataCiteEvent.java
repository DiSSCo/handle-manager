package eu.dissco.core.handlemanager.domain.datacite;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import java.util.List;
import org.springframework.context.annotation.Profile;

@Profile(Profiles.DOI)
public record DataCiteEvent(List<JsonNode> pidRecords, EventType eventType) {

}
