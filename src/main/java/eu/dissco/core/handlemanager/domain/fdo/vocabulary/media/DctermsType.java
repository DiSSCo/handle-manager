package eu.dissco.core.handlemanager.domain.fdo.vocabulary.media;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DctermsType {
  @JsonProperty("sound") SOUND("sound"),
  @JsonProperty("dataset") DATASET("dataset"),
  @JsonProperty("text") TEXT("text"),
  @JsonProperty("software") SOFTWARE("software"),
  @JsonProperty("interactive") INTERACTIVE("interactive"),
  @JsonProperty("event") EVENT("event"),
  @JsonProperty("Physical object + Audiovisual Core") PHYSICAL_AND_AV(
      "Physical object + Audiovisual Core"),
  @JsonProperty("3D object type") THREE_DIMENSIONAL("3D objectType"),
  @JsonProperty("image") IMAGE("image");

  private final String state;

  private DctermsType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
