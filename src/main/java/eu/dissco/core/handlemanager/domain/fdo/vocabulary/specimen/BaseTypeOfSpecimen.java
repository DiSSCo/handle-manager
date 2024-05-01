package eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BaseTypeOfSpecimen {
  @JsonProperty("Material entity") MATERIAL("Material entity"),
  @JsonProperty("Information artefact") INFO("Information Artefact");

  private final String state;


  BaseTypeOfSpecimen(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }


}
