package eu.dissco.core.handlemanager.domain.fdo.vocabulary.tettris;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TettrisServiceCategory {
  @JsonProperty("Reference collections") REFERENCE_COL("Reference collections"),
  @JsonProperty("Training datasets") TRAINING_DATA("Training datasets"),
  @JsonProperty("R-packages") R_PACK("R-packages"),
  @JsonProperty("Identification tools") ID_TOOLS("Identification tools"),
  @JsonProperty("MAS") MAS("MAS"),
  @JsonProperty("Lab tools") LAB_TOOLS("Lab tools"),
  @JsonProperty("Crowd sourcing tools for digitisation") CROWD_SRC(
      "Crowd sourcing tools for digitisation"),
  @JsonProperty("Training platforms (DEST)") TRAINING_PLATFORM("Training platforms (DEST)");

  private final String serviceCategory;

  TettrisServiceCategory(String s) {
    this.serviceCategory = s;
  }

  @Override
  public String toString() {
    return serviceCategory;
  }

}
