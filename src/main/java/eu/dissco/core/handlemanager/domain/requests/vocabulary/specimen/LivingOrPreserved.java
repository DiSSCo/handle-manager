package eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum LivingOrPreserved {
  @JsonProperty("Preserved") PRESERVED("Preserved"),
  @JsonProperty("Living") LIVING("Living");

  private final String state;

  private LivingOrPreserved(@JsonProperty("livingOrPreserved") String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }

  public byte[] getBytes() {
    return state.getBytes(StandardCharsets.UTF_8);
  }


}
