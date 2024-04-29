package eu.dissco.core.handlemanager.domain.requests.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum FdoType {
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/532ce6796e2828dd2be6") HANDLE(
      "Handle Kernel",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/532ce6796e2828dd2be6"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/527856fd709ec8c5bc8c") DOI(
      "DOI Kernel",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/527856fd709ec8c5bc8c"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/894b1e6cad57e921764") DIGITAL_SPECIMEN(
      "DigitalSpecimen",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/894b1e6cad57e921764e"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/bbad8c4e101e8af01115") MEDIA_OBJECT(
      "MediaObject",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/bbad8c4e101e8af01115"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/cf458ca9ee1d44a5608f") ANNOTATION(
      "Annotation",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/cf458ca9ee1d44a5608f"),
  @JsonProperty("https://dtr-test.pidconsortium.eu/objects/21.T11148/417a4f472f60f7974c12") SOURCE_SYSTEM(
      "sourceSystem",
      "https://dtr-test.pidconsortium.eu/objects/21.T11148/417a4f472f60f7974c12"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/ce794a6f4df42eb7e77e") MAPPING(
      "Mapping",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/ce794a6f4df42eb7e77e"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/413c00cbd83ae33d1ac0") ORGANISATION(
      "Organisation",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/413c00cbd83ae33d1ac0"),

  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/d7570227982f70256af3") TOMBSTONE(
      "Tombstone",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/d7570227982f70256af3"),
  @JsonProperty("https://dtr-test.pidconsortium.net/objects/21.T11148/22e71a0015cbcfba8ffa") MAS(
      "Machine Annotation Service",
      "https://dtr-test.pidconsortium.net/objects/21.T11148/22e71a0015cbcfba8ffa");

  private final String digitalObjectName;
  private final String digitalObjectType;

  FdoType(@JsonProperty("type") String digitalObjectName, String digitalObjectType) {
    this.digitalObjectName = digitalObjectName;
    this.digitalObjectType = digitalObjectType;
  }

  @Override
  public String toString() {
    return digitalObjectType;
  }

  public static FdoType fromString(String fdoTypePid) {
    for (FdoType type : FdoType.values()) {
      if (type.digitalObjectType.equalsIgnoreCase(fdoTypePid)) {
        return type;
      }
    }
    log.error("Unable to determine fdo type from {}, is it a PID?", fdoTypePid);
    throw new IllegalArgumentException("No object type exists for " + fdoTypePid);
  }
}
