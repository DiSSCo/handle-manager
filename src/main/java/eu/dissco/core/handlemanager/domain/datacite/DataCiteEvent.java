package eu.dissco.core.handlemanager.domain.datacite;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import org.springframework.context.annotation.Profile;

@Profile(Profiles.DOI)
public record DataCiteEvent(JsonNode pidRecord, EventType eventType) {

}
