package eu.dissco.core.handlemanager.domain.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dissco.core.handlemanager.Profiles;
import org.springframework.context.annotation.Profile;

@Profile(Profiles.DOI)
public enum EventType {

  @JsonProperty("create") CREATE,
  @JsonProperty("update") UPDATE;
}
