package eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen;

import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.AGGR;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.AGGR_BIOME;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.ANY_BIO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.BUNDLE_BIOME;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.FLUID;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.FOSSIL;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.ORG_PART;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.ORG_PRODUCT;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.OTHER_SOLID;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.SLURRY_BIOME;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.WHOLE_ORG;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ALGAE;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.AMPHIBIANS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ANIMAL_GENE;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ARACHNIDS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.BACTERIA;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.BIRDS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.BOTANY_FOSSILS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.BRYO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.CNIDARIA;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.CRUSTACEANS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ECHINODERM;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.FINDS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.FISHES;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.HOMINID;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.HUMAN;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ICE;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.IMPACTS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.INSECTS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.INVERT_FOSSILS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.LIQUID_GAS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.LOOSE_SEDIMENT;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MAMMALS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MICRO_EUKS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MICRO_FUNGI;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MINERALS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MIXED_GEO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MIXED_SOLID;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MOLLUSCA;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.MYCOLOGY;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.OTHER_BOTANY;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.OTHER_MICRO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.OTHER_PALEO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.OTHER_ZOO;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.PHAGES;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.PLANT_GENE;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.PLASMIDS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.PROTOZOA;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.PTERID;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.REPTILES;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.ROCKS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.SAMPLE_RETURNS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.SEED;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.SPONGES;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.VERT_FOSSILS;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.VIRUSES;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;

public enum TopicDiscipline {
  @JsonProperty("Anthropology") ANTHRO("Anthropology"),
  @JsonProperty("Botany") BOTANY("Botany"),
  @JsonProperty("Geology") GEOLOGY("Geology"),
  @JsonProperty("Microbiology") MICRO("Microbiology"),
  @JsonProperty("Palaeontology") PALEO("Palaeontology"),
  @JsonProperty("Zoology") ZOO("Zoology"),
  @JsonProperty("Ecology") ECO("Ecology"),
  @JsonProperty("Other Biodiversity") OTHER_BIO("Other Biodiversity"),
  @JsonProperty("Other Geodiversity") OTHER_GEO("Other Geodiversity"),
  @JsonProperty("Astrogeology") ASTRO("Astrogeology"),
  @JsonProperty("Unclassified") UNCLASSIFIED("Unclassified");

  private final String state;
  @Getter
  private Set<TopicCategory> topicCategories;
  @Getter
  private Set<MaterialSampleType> materialSampleTypes;

  private TopicDiscipline(String state) {
    this.state = state;
    setSubLists(state);
  }

  public boolean isCorrectCategory(TopicCategory target) {
    return this.topicCategories.contains(target);
  }

  public boolean isCorrectMaterialSampleType(MaterialSampleType materialSampleType) {
    return this.materialSampleTypes.contains(materialSampleType);
  }

  private void setSubLists(String state) {
    switch (state) {
      case "Anthropology" -> {
        this.topicCategories = Set.of(HUMAN, HOMINID);
        this.materialSampleTypes = Set.of(WHOLE_ORG, ORG_PART, ORG_PRODUCT);
      }
      case "Botany" -> {
        this.topicCategories = Set.of(MYCOLOGY, ALGAE, BRYO, PTERID, SEED, PLANT_GENE,
            OTHER_BOTANY);
        this.materialSampleTypes = Set.of(WHOLE_ORG, AGGR_BIOME, ORG_PART, ORG_PRODUCT);
      }
      case "Geology" -> {
        this.topicCategories = Set.of(MINERALS, ROCKS, LOOSE_SEDIMENT, MIXED_SOLID, ICE, LIQUID_GAS,
            MIXED_GEO);
        this.materialSampleTypes = Set.of(AGGR, SLURRY_BIOME, OTHER_SOLID, FLUID);
      }
      case "Zoology" -> {
        this.topicCategories = Set.of(INSECTS, ARACHNIDS, ECHINODERM, CRUSTACEANS, SPONGES,
            MOLLUSCA, CNIDARIA,
            FISHES, AMPHIBIANS, BIRDS, MAMMALS, REPTILES, ANIMAL_GENE, OTHER_ZOO);
        this.materialSampleTypes = Set.of(WHOLE_ORG, ORG_PART, ORG_PRODUCT, AGGR_BIOME);
      }
      case "Microbiology" -> {
        this.topicCategories = Set.of(PHAGES, PLASMIDS, BACTERIA, PROTOZOA, MICRO_EUKS, VIRUSES,
            MICRO_FUNGI, ALGAE, OTHER_MICRO);
        this.materialSampleTypes = Set.of(BUNDLE_BIOME, WHOLE_ORG);
      }
      case "Palaeontology" -> {
        this.topicCategories = Set.of(INVERT_FOSSILS, VERT_FOSSILS, OTHER_PALEO, BOTANY_FOSSILS);
        this.materialSampleTypes = Set.of(FOSSIL);
      }
      case "Astrogeology" -> {
        this.topicCategories = Set.of(FINDS, IMPACTS, SAMPLE_RETURNS);
        this.materialSampleTypes = Set.of(AGGR, OTHER_SOLID);
      }
      case "Other Biodiversity" -> {
        this.topicCategories = Collections.emptySet();
        this.materialSampleTypes = Set.of(ANY_BIO);
      }
      default -> {
        this.topicCategories = Collections.emptySet();
        this.materialSampleTypes = Collections.emptySet();
      }
    }
  }

  @Override
  public String toString() {
    return this.state;
  }
}
