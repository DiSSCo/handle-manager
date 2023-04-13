package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum LivingOrPreserved {
  @JsonProperty("preserved") PRESERVED("preserved"),
  @JsonProperty("living") LIVING("living");

  private final String state;

  private LivingOrPreserved(@JsonProperty("preservedOrLiving") String state) {
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
