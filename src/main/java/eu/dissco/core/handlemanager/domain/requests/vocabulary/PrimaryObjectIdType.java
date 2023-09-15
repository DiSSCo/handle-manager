package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum PrimaryObjectIdType {
  @JsonProperty("global") GLOBAL("global"),
  @JsonProperty("resolvable") RESOLVABLE("resolvable"),
  @JsonProperty("local") LOCAL("local");

  private final String state;
  private final boolean isGlobal;

  private PrimaryObjectIdType(@JsonProperty("physicalIdType") String state) {
    this.state = state;
    this.isGlobal = !state.equals("local") && !state.equals("combined");
  }

  @Override
  public String toString() {
    return state;
  }

  public byte[] getBytes() {
    return state.getBytes(StandardCharsets.UTF_8);
  }

  public boolean isGlobal() {
    return this.isGlobal;
  }
}
