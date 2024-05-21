package eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType.*;
import static eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory.*;

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
