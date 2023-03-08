package eu.dissco.core.handlemanager.domain.requests.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum PhysicalIdType {

  @JsonProperty("cetaf") CETAF("cetaf"),
  @JsonProperty("combined") COMBINED("combined");

  private final String state;

  private PhysicalIdType(@JsonProperty("physicalIdType") String state) {
    this.state = state;
  }

  public String getType() {
    return state;
  }

  public byte[] getBytes() {
    return state.getBytes(StandardCharsets.UTF_8);
  }
}
