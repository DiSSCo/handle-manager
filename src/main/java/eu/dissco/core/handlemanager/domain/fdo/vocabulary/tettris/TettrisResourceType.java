package eu.dissco.core.handlemanager.domain.fdo.vocabulary.tettris;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TettrisResourceType {
  @JsonProperty(value = "Digital") DIGITAL("Digital"),
  @JsonProperty(value = "Physical") PHYSICAL("Physical"),
  @JsonProperty(value = "Expertise") EXPERTISE("Expertise");
  private final String resourceType;

  TettrisResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  @Override
  public String toString() {
    return resourceType;
  }
}
