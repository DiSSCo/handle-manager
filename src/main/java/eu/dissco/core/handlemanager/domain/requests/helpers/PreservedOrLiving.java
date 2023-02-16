package eu.dissco.core.handlemanager.domain.requests.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;

public enum PreservedOrLiving {
  @JsonProperty("preserved") PRESERVED("preserved"),
  @JsonProperty("living") LIVING("living");

  private final String state;

  private PreservedOrLiving(@JsonProperty("preservedOrLiving") String state){
    this.state = state;
  }

  public String getType(){
    return state;
  }

  public byte[] getBytes() {
    return state.getBytes(StandardCharsets.UTF_8);
  }

}
