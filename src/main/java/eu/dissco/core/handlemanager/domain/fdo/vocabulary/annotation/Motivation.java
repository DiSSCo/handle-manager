package eu.dissco.core.handlemanager.domain.fdo.vocabulary.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Motivation {

  @JsonProperty("ods:adding") ADDING("ods:adding"),
  @JsonProperty("oa:assessing") ASSESSING("oa:assessing"),
  @JsonProperty("oa:editing") EDITING("oa:editing"),
  @JsonProperty("oa:commenting") COMMENTING("oa:commenting");
  private final String state;

  private Motivation(String s) {
    this.state = s;
  }

  @Override
  public String toString() {
    return state;
  }

}
