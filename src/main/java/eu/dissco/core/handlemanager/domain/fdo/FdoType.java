package eu.dissco.core.handlemanager.domain.fdo;

import static eu.dissco.core.handlemanager.properties.ProfileProperties.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.properties.ProfileProperties.HANDLE_DOMAIN;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum FdoType {
  @JsonProperty("https://doi.org/21.T11148/532ce6796e2828dd2be6")
  @JsonAlias("https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6")
  HANDLE(
      "Handle Kernel",
      "https://doi.org/21.T11148/532ce6796e2828dd2be6",
      "https://doi.org/21.T11148/532ce6796e2828dd2be6",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/527856fd709ec8c5bc8c")
  @JsonAlias("https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c")
  DOI(
      "DOI Kernel",
      "https://doi.org/21.T11148/527856fd709ec8c5bc8c",
      "https://doi.org/21.T11148/527856fd709ec8c5bc8c",
      DOI_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/894b1e6cad57e921764e")
  @JsonAlias("https://hdl.handle.net/21.T11148/894b1e6cad57e921764e")
  DIGITAL_SPECIMEN(
      "DigitalSpecimen",
      "https://doi.org/21.T11148/894b1e6cad57e921764e",
      "https://doi.org/21.T11148/d8de0819e144e4096645",
      DOI_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/bbad8c4e101e8af01115")
  @JsonAlias("https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115")
  DIGITAL_MEDIA(
      "MediaObject",
      "https://doi.org/21.T11148/bbad8c4e101e8af01115",
      "https://doi.org/21.T11148/306452d0867adb910803",
      DOI_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/cf458ca9ee1d44a5608f")
  @JsonAlias("https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f")
  ANNOTATION(
      "Annotation",
      "https://doi.org/21.T11148/cf458ca9ee1d44a5608f",
      "https://doi.org/21.T11148/2e76f544229901c5a942",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/23a63913d0c800609a50")
  @JsonAlias("https://hdl.handle.net/21.T11148/23a63913d0c800609a50")
  SOURCE_SYSTEM(
      "sourceSystem",
      "https://doi.org/21.T11148/23a63913d0c800609a50",
      "https://doi.org/21.T11148/417a4f472f60f7974c12",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/ce794a6f4df42eb7e77e")
  @JsonAlias("https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e")
  DATA_MAPPING(
      "Mapping",
      "https://doi.org/21.T11148/ce794a6f4df42eb7e77e",
      "https://doi.org/21.T11148/def2eba59562958fa4a0",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/413c00cbd83ae33d1ac0")
  @JsonAlias("https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0")
  ORGANISATION(
      "Organisation",
      "https://doi.org/21.T11148/413c00cbd83ae33d1ac0",
      "https://doi.org/21.T11148/413c00cbd83ae33d1ac0",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/d7570227982f70256af3")
  @JsonAlias("https://hdl.handle.net/21.T11148/d7570227982f70256af3")
  TOMBSTONE(
      "Tombstone",
      "https://doi.org/21.T11148/d7570227982f70256af3",
      "https://doi.org/21.T11148/d7570227982f70256af3",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/a369e128df5ef31044d4")
  @JsonAlias("https://hdl.handle.net/21.T11148/a369e128df5ef31044d4")
  MAS(
      "Machine Annotation Service",
      "https://doi.org/21.T11148/a369e128df5ef31044d4",
      "https://doi.org/21.T11148/22e71a0015cbcfba8ffa",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/2ac65a933b7a0361b651")
  @JsonAlias("https://hdl.handle.net/21.T11148/2ac65a933b7a0361b651")
  VIRTUAL_COLLECTION(
      "Virtual Collection",
          "https://hdl.handle.net/21.T11148/2ac65a933b7a0361b651",
          "https://hdl.handle.net/21.T11148/6fc9f9381f875249ff1f",
      HANDLE_DOMAIN);

  private final String digitalObjectName;
  private final String digitalObjectType;
  private final String fdoProfile;
  private final String domain;

  FdoType(@JsonProperty("type") String digitalObjectName, String digitalObjectType,
      String fdoProfile, String domain) {
    this.digitalObjectName = digitalObjectName;
    this.digitalObjectType = digitalObjectType;
    this.fdoProfile = fdoProfile;
    this.domain = domain;
  }

  private static final Map<String, FdoType> LOOKUP;

  static {
    LOOKUP = new HashMap<>();
    for (var fdoType : FdoType.values()) {
      LOOKUP.put(fdoType.digitalObjectType, fdoType);
      LOOKUP.put(fdoType.digitalObjectName, fdoType);
      LOOKUP.put(fdoType.fdoProfile, fdoType);
    }
  }

  public static FdoType fromString(String attributeName) {
    return LOOKUP.get(attributeName);
  }

  @Override
  public String toString() {
    return digitalObjectType;
  }

}
