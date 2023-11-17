package eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MaterialSampleType {

  @JsonProperty("Whole organism specimen") WHOLE_ORG("Whole organism specimen"),
  @JsonProperty("Organism part") ORG_PART("Organism part"),
  @JsonProperty("Organism product") ORG_PRODUCT("Organism product"),
  @JsonProperty("Biome aggegation") AGGR_BIOME("Biome aggegation"),
  @JsonProperty("Bundle biome aggregation") BUNDLE_BIOME("Bundle biome aggregation"),
  @JsonProperty("Fossil") FOSSIL("Fossil"),
  @JsonProperty("Any biological specimen") ANY_BIO("Any biological specimen"),
  @JsonProperty("Aggregation") AGGR("Aggregation"),
  @JsonProperty("Slurry biome aggegation") SLURRY_BIOME("Slurry biome aggegation"),
  @JsonProperty("Other solid object") OTHER_SOLID("Other solid object"),
  @JsonProperty("Fluid in container") FLUID("Fluid in container"),
  @JsonProperty("Anthropogenic aggregation") ANTHRO_AGGR("Anthropogenic aggregation"),
  @JsonProperty("Artefact") ARTEFACT("Artefact"),
  @JsonProperty("Any aggregation specimen") ANY_AGGR("Any aggregation specimen");


  private final String state;

  private MaterialSampleType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }

}
