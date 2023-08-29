package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ALGAE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.AMPHIBIANS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ANIMAL_GENE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ARACHNIDS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.BACTERIA;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.BIRDS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.BOTANY_FOSSILS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.BRYO;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.CNIDARIA;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.CRUSTACEANS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ECHINODERM;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.FINDS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.FISHES;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.HOMINID;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.HUMAN;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ICE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.IMPACTS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.INSECTS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.INVERT_FOSSILS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.LIQUID_GAS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.LOOSE_SEDIMENT;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MAMMALS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MICRO_EUKS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MICRO_FUNGI;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MINERALS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MIXED_GEO;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MIXED_SOLID;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MOLLUSCA;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.MYCOLOGY;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.OTHER_BOTANY;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.OTHER_MICRO;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.OTHER_PALEO;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.OTHER_ZOO;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.PHAGES;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.PLANT_GENE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.PLASMIDS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.PROTOZOA;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.PTERID;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.REPTILES;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.ROCKS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.SAMPLE_RETURNS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.SEED;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.SPONGES;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.VERT_FOSSILS;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory.VIRUSES;

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
  @JsonProperty("Other Other Geodiversity") OTHER_GEO("Other Geodiversity"),
  @JsonProperty("Astrogeology") ASTRO("Astrogeology");

  private final String state;
  @Getter
  private final Set<TopicCategory> categories;

  private TopicDiscipline(String state) {
    this.state = state;
    this.categories = setCategories(state);
  }

  public boolean isCorrectCategory(TopicCategory target) {
    return this.categories.contains(target);
  }

  private static Set<TopicCategory> setCategories(String state) {
    switch (state) {
      case "Anthropology" -> {
        return Set.of(HUMAN, HOMINID);
      }
      case "Botany" -> {
        return Set.of(MYCOLOGY, ALGAE, BRYO, PTERID, SEED, PLANT_GENE, OTHER_BOTANY);
      }
      case "Geology" -> {
        return Set.of(MINERALS, ROCKS, LOOSE_SEDIMENT, MIXED_SOLID, ICE, LIQUID_GAS, MIXED_GEO);
      }
      case "Zoology" -> {
        return Set.of(INSECTS, ARACHNIDS, ECHINODERM, CRUSTACEANS, SPONGES, MOLLUSCA, CNIDARIA,
            FISHES, AMPHIBIANS, BIRDS, MAMMALS, REPTILES, ANIMAL_GENE, OTHER_ZOO);
      }
      case "Microbiology" -> {
        return Set.of(PHAGES, PLASMIDS, BACTERIA, PROTOZOA, MICRO_EUKS, VIRUSES,
            MICRO_FUNGI, ALGAE, OTHER_MICRO);
      }
      case "Palaeontology" -> {
        return Set.of(INVERT_FOSSILS, VERT_FOSSILS, OTHER_PALEO, BOTANY_FOSSILS);
      }
      case "Astrogeology" -> {
        return Set.of(FINDS, IMPACTS, SAMPLE_RETURNS);
      }
      default -> {
        return Collections.emptySet();
      }
    }
  }

  @Override
  public String toString() {
    return this.state;
  }
}
