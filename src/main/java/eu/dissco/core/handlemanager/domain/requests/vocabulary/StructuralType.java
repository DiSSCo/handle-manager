package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StructuralType {

  @JsonProperty("digital") DIGITAL("digital"),
  @JsonProperty("physical") PHYSICAL("physical"),
  @JsonProperty("performance") PERFORM("performance"),
  @JsonProperty("performance") ABSTRACT("abstraction");

  private final String state;

  private StructuralType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }
}
