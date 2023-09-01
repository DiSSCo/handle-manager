package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TopicCategory {

  @JsonProperty("Human remains") HUMAN("Human remains"),
  @JsonProperty("Hominid remains") HOMINID("Hominid remains"),
  @JsonProperty("Mycology") MYCOLOGY("Mycology"),
  @JsonProperty("Algae") ALGAE("Algae"),
  @JsonProperty("Bryophytes") BRYO("Bryophytes"),
  @JsonProperty("Pteridophytes") PTERID("Pteridophytes"),
  @JsonProperty("Seed plants") SEED("Seed plants"),
  @JsonProperty("PlantGeneticResources") PLANT_GENE("PlantGeneticResources"),
  @JsonProperty("Other Botany Objects") OTHER_BOTANY("Other Botany Objects"),
  @JsonProperty("Insects") INSECTS("Insects"),
  @JsonProperty("Arachnids") ARACHNIDS("Arachnids"),
  @JsonProperty("Crustaceans & Myriapods") CRUSTACEANS("Crustaceans & Myriapods"),
  @JsonProperty("Porifera") SPONGES("Porifera"),
  @JsonProperty("Mollusca") MOLLUSCA("Mollusca"),
  @JsonProperty("Cnidaria") CNIDARIA("Cnidaria"),
  @JsonProperty("Echinodermata") ECHINODERM("Echinodermata"),
  @JsonProperty("Fishes") FISHES("Fishes"),
  @JsonProperty("Amphibians") AMPHIBIANS("Amphibians"),
  @JsonProperty("Reptiles") REPTILES("Reptiles"),
  @JsonProperty("Birds") BIRDS("Birds"),
  @JsonProperty("Mammals") MAMMALS("Mammals"),
  @JsonProperty("AnimalGeneticResources") ANIMAL_GENE("AnimalGeneticResources"),
  @JsonProperty("Other Zoology Objects") OTHER_ZOO("Other Zoology Objects"),
  @JsonProperty("Phages") PHAGES("Phages"),
  @JsonProperty("Bacteria & Archaea") BACTERIA("Bacteria & Archaea"),
  @JsonProperty("Plasmids") PLASMIDS("Plasmids"),
  @JsonProperty("Protozoa") PROTOZOA("Protozoa"),
  @JsonProperty("Eukaryotic microorganisms") MICRO_EUKS("Eukaryotic microorganisms"),
  @JsonProperty("Viruses") VIRUSES("Viruses"),
  @JsonProperty("Mircofungi") MICRO_FUNGI("Mircofungi"),
  @JsonProperty("Other Microbiology Objects") OTHER_MICRO("Other Microbiology Objects"),
  @JsonProperty("Botany Fossils") BOTANY_FOSSILS("Botany Fossils"),
  @JsonProperty("Invertebrate Fossils") INVERT_FOSSILS("Invertebrate Fossils"),
  @JsonProperty("Vertebrate Fossils") VERT_FOSSILS("Vertebrate Fossils"),
  @JsonProperty("Other Palaeontology Objects") OTHER_PALEO("Other Palaeontology Objects"),
  @JsonProperty("Minerals and Gems") MINERALS("Minerals and Gems"),
  @JsonProperty("Rocks") ROCKS("Rocks"),
  @JsonProperty("Loose Sediment Sample") LOOSE_SEDIMENT("Loose Sediment Sample"),
  @JsonProperty("Mixed Solid Mater Sample") MIXED_SOLID("Mixed Solid Mater Sample"),
  @JsonProperty("Water-Ice Sample") ICE("Water-Ice Sample"),
  @JsonProperty("Liquid or Gaseous Matter Sample") LIQUID_GAS("Liquid or Gaseous Matter Sample"),
  @JsonProperty("Mixed Geology Objects") MIXED_GEO("Mixed Geology Objects"),
  @JsonProperty("Terrestrial Finds/Falls") FINDS("Terrestrial Finds/Falls"),
  @JsonProperty("Terrestrial Impacta") IMPACTS("Terrestrial Impacta"),
  @JsonProperty("Sample Returns") SAMPLE_RETURNS("Sample Returns");

  private final String state;

  private TopicCategory(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
