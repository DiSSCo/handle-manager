package eu.dissco.core.handlemanager.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DigitalOrPhysical {
  DIGITAL("digital"),
  PHYSICAL("physical");

  private final String state;

  private DigitalOrPhysical(@JsonProperty("digitalOrPhysical") String state){
    this.state = state;
  }

  public String getType(){
    return state;
  }

}
