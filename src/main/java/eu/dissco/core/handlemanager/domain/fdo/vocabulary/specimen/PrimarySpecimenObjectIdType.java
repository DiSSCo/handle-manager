package eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum PrimarySpecimenObjectIdType {
  @JsonProperty("Global") GLOBAL("Global"),
  @JsonProperty("Resolvable") RESOLVABLE("Resolvable"),
  @JsonProperty("Local") LOCAL("Local");

  private final String state;

  private PrimarySpecimenObjectIdType(@JsonProperty("primarySpecimenObjectIdType") String state) {
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
