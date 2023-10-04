package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReferentType {

  @JsonProperty("Digital Specimen") DIGITAL_SPECIMEN("Digital Specimen"),
  @JsonProperty("Media Object") MEDIA_OBJECT("Media Object"),
  @JsonProperty("Organisation") ORGANISATION("organisation"),
  @JsonProperty("Other") OTHER("Other");

  private final String state;

  private ReferentType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
