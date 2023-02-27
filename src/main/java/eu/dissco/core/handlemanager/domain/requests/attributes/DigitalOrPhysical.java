package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum DigitalOrPhysical {
  @JsonProperty("digital") DIGITAL("digital"),
  @JsonProperty("physical") PHYSICAL("physical");

  private final String state;

  private DigitalOrPhysical(@JsonProperty("digitalOrPhysical") String state) {
    this.state = state;
  }

  public String getType() {
    return state;
  }

  public byte[] getBytes() {
    return state.getBytes(StandardCharsets.UTF_8);
  }

}
