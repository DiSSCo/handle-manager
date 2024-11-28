package eu.dissco.core.handlemanager.domain.fdo;

import static eu.dissco.core.handlemanager.service.FdoRecordService.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.service.FdoRecordService.HANDLE_DOMAIN;

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
      "https://doi.org/21.T11148/894b1e6cad57e921764e",
      DOI_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/bbad8c4e101e8af01115")
  @JsonAlias("https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115")
  DIGITAL_MEDIA(
      "MediaObject",
      "https://doi.org/21.T11148/bbad8c4e101e8af01115",
      "https://doi.org/21.T11148/bbad8c4e101e8af01115",
      DOI_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/cf458ca9ee1d44a5608f")
  @JsonAlias("https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f")
  ANNOTATION(
      "Annotation",
      "https://doi.org/21.T11148/cf458ca9ee1d44a5608f",
      "https://doi.org/21.T11148/cf458ca9ee1d44a5608f",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/417a4f472f60f7974c12")
  @JsonAlias("https://hdl.handle.net/21.T11148/417a4f472f60f7974c12")
  SOURCE_SYSTEM(
      "sourceSystem",
      "https://doi.org/21.T11148/417a4f472f60f7974c12",
      "https://doi.org/21.T11148/417a4f472f60f7974c12",
      HANDLE_DOMAIN),
  @JsonProperty("https://doi.org/21.T11148/ce794a6f4df42eb7e77e")
  @JsonAlias("https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e")
  DATA_MAPPING(
      "Mapping",
      "https://doi.org/21.T11148/ce794a6f4df42eb7e77e",
      "https://doi.org/21.T11148/ce794a6f4df42eb7e77e",
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
  @JsonProperty("https://doi.org/21.T11148/22e71a0015cbcfba8ffa")
  @JsonAlias("https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa")
  MAS(
      "Machine Annotation Service",
      "https://doi.org/21.T11148/22e71a0015cbcfba8ffa",
      "https://doi.org/21.T11148/22e71a0015cbcfba8ffa",
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
