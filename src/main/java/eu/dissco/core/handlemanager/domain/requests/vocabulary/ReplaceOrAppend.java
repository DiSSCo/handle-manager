package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReplaceOrAppend {
  @JsonProperty("replace") REPLACE("replace"),
  @JsonProperty("append") APPEND("append");

  private final String state;

  private ReplaceOrAppend(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
