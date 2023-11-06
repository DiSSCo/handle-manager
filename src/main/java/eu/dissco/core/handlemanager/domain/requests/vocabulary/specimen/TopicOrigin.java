package eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TopicOrigin {
  @JsonProperty("Natural") NATURAL("Natural"),
  @JsonProperty("Human-made") HUMAN_MADE("Human-made"),
  @JsonProperty("Mixed origin") MIXED("Mixed origin");

  private final String state;

  private TopicOrigin(String state) {
    this.state = state;
  }

  public boolean isCorrectMaterialSampleType(MaterialSampleType materialSampleType) {
    return materialSampleType.equals(MaterialSampleType.ANY_AGGR) && this.state.equals(
        "Mixed origin");
  }

  @Override
  public String toString() {
    return this.state;
  }
}
