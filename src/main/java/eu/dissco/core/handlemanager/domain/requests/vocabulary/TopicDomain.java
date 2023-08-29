package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TopicDomain {

  @JsonProperty("Life") LIFE("Life"),
  @JsonProperty("Environment") ENV("Environment"),
  @JsonProperty("Earth System") EARTH("Earth System"),
  @JsonProperty("Extraterrestrial") EXTRATERRESTRIAL("Extraterrestrial"),
  @JsonProperty("Cultural Artefacts") CULTURE("Cultural Artefacts"),
  @JsonProperty("Archive Material") ARCHIVE("Archive Material");
  private final String state;

  private TopicDomain(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
