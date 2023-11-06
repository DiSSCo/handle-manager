package eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BaseTypeOfSpecimen {
  @JsonProperty("Material entity") MATERIAL("Material entity"),
  @JsonProperty("Information artefact") INFO("informationArtefact");

  private final String state;


  private BaseTypeOfSpecimen(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }


}
