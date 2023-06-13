package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ObjectType {
  @JsonProperty("handle") HANDLE("handle"),
  @JsonProperty("doi") DOI("doi"),
  @JsonProperty("digitalSpecimen") DIGITAL_SPECIMEN("digitalSpecimen"),
  @JsonProperty("digitalSpecimenBotany") DIGITAL_SPECIMEN_BOTANY("digitalSpecimenBotany"),
  @JsonProperty("mediaObject") MEDIA_OBJECT("mediaObject"),
  @JsonProperty("annotation") ANNOTATION("annotation"),
  @JsonProperty("sourceSystem") SOURCE_SYSTEM("sourceSystem"),
  @JsonProperty("mapping") MAPPING("mapping"),
  @JsonProperty("translator") TRANSLATOR("translator"),
  @JsonProperty("organisation") ORGANISATION("organisation"),
  @JsonProperty("tombstone") TOMBSTONE("tombstone");

  private final String state;

  private ObjectType(@JsonProperty("type") String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }
  public static ObjectType fromString(String state){
    for (ObjectType type : ObjectType.values()){
      if(type.state.equalsIgnoreCase(state)){
        return type;
      }
    }
    throw new IllegalArgumentException("No object type " + state);
  }
}
