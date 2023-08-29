package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MaterialOrDigitalEntity {

  @JsonProperty("Material entity") MATERIAL("Material entity"),
  @JsonProperty("Digital entity") DIGITAL("Digital entity");

  private final String state;


  private MaterialOrDigitalEntity(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }

}
