package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import static eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType.ANTHRO_AGGR;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType.ARTEFACT;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType.BUNDLE_BIOME;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType.SLURRY_BIOME;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Set;

public enum TopicDomain {

  @JsonProperty("Life") LIFE("Life"),
  @JsonProperty("Environment") ENV("Environment"),
  @JsonProperty("Earth System") EARTH("Earth System"),
  @JsonProperty("Extraterrestrial") EXTRATERRESTRIAL("Extraterrestrial"),
  @JsonProperty("Cultural Artefacts") CULTURE("Cultural Artefacts"),
  @JsonProperty("Archive Material") ARCHIVE("Archive Material");
  private final String state;

  private Set<MaterialSampleType> materialSampleTypes;

  private TopicDomain(String state) {
    this.state = state;
    setMaterialSampleTypes(state);
  }

  private void setMaterialSampleTypes(String state) {
    switch (state) {
      case "Environment" -> this.materialSampleTypes = Set.of(SLURRY_BIOME, BUNDLE_BIOME);
      case "Cultural artefacts" -> this.materialSampleTypes = Set.of(ANTHRO_AGGR, ARTEFACT);
      case "Archive material" -> this.materialSampleTypes = Set.of(ARTEFACT);
      default -> this.materialSampleTypes = Collections.emptySet();
    }
  }

  public boolean isCorrectMaterialSampleType(MaterialSampleType materialSampleType) {
    return this.materialSampleTypes.contains(materialSampleType);
  }

  @Override
  public String toString() {
    return this.state;
  }

}
