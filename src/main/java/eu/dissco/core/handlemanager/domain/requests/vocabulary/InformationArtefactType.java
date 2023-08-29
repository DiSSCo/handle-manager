package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum InformationArtefactType {
  @JsonProperty("DCMI:Image") IMAGE("DCMI:Image"),
  @JsonProperty("DCMI:MovingImage") MOVING_IMG("DCMI:MovingImage"),
  @JsonProperty("DCMI:PhysicalObject") PHYSICAL_OBJECT("DCMI:PhysicalObject"),
  @JsonProperty("DCMI:Sound") SOUND("Sound"),
  @JsonProperty("DCMI:3DResourceType") THREE_DIMENSIONAL("DCMI:3DResourceType");

  private final String state;

  private InformationArtefactType(String state) {
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
