package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum PhysicalIdType {

  @JsonProperty("global") GLOBAL("global"),
  @JsonProperty("combined") COMBINED("combined");

  private final String state;

  private PhysicalIdType(@JsonProperty("physicalIdType") String state) {
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
