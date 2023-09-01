package eu.dissco.core.handlemanager.domain.requests.vocabulary.media;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LinkedDigitalObjectType {

  @JsonProperty("digital specimen") SPECIMEN("digital specimen"),
  @JsonProperty("agent") AGENT("agent"),
  @JsonProperty("facility") FACILITY("facility"),
  @JsonProperty("text") TEXT("text"),
  @JsonProperty("supplementary") SUPPLEMENTARY("supplementary material");

  private final String state;

  private LinkedDigitalObjectType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }
}
