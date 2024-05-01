package eu.dissco.core.handlemanager.domain.fdo.vocabulary.media;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MediaFormat {


  @JsonProperty("audio") AUDIO("audio"),
  @JsonProperty("model") MODEL("model"),
  @JsonProperty("video") VIDEO("video"),
  @JsonProperty("text") TEXT("text"),
  @JsonProperty("application") APP("application"),
  @JsonProperty("image") IMAGE("image");

  private final String state;

  private MediaFormat(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }


}
