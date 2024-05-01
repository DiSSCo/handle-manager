package eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MaterialSampleType {

  @JsonProperty("Whole organism specimen") WHOLE_ORG("Whole organism specimen"),
  @JsonProperty("Organism part") ORG_PART("Organism part"),
  @JsonProperty("Organism product") ORG_PRODUCT("Organism product"),
  @JsonProperty("Biome aggregation") AGGR_BIOME("Biome aggregation"),
  @JsonProperty("Bundle biome aggregation") BUNDLE_BIOME("Bundle biome aggregation"),
  @JsonProperty("Fossil") FOSSIL("Fossil"),
  @JsonProperty("Any biological specimen") ANY_BIO("Any biological specimen"),
  @JsonProperty("Aggregation") AGGR("Aggregation"),
  @JsonProperty("Slurry biome aggregation") SLURRY_BIOME("Slurry biome aggregation"),
  @JsonProperty("Other solid object") OTHER_SOLID("Other solid object"),
  @JsonProperty("Fluid in container") FLUID("Fluid in container"),
  @JsonProperty("Anthropogenic aggregation") ANTHRO_AGGR("Anthropogenic aggregation"),
  @JsonProperty("Artefact") ARTEFACT("Artefact"),
  @JsonProperty("Any aggregation specimen") ANY_AGGR("Any aggregation specimen");


  private final String state;

  MaterialSampleType(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }

}
