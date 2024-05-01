package eu.dissco.core.handlemanager.domain.fdo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum FdoType {
  @JsonProperty("https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6") HANDLE(
      "Handle Kernel",
      "https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6",
      "https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6"),
  @JsonProperty("https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c") DOI(
      "DOI Kernel",
      "https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c",
      "https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c"),
  @JsonProperty("https://hdl.handle.net/21.T11148/894b1e6cad57e921764e") DIGITAL_SPECIMEN(
      "DigitalSpecimen",
      "https://hdl.handle.net/21.T11148/894b1e6cad57e921764e",
      "https://hdl.handle.net/21.T11148/894b1e6cad57e921764e"),
  @JsonProperty("https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115") MEDIA_OBJECT(
      "MediaObject",
      "https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115",
      "https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115"),
  @JsonProperty("https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f") ANNOTATION(
      "Annotation",
      "https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f",
      "https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f"),
  @JsonProperty("https://hdl.handle.net/21.T11148/417a4f472f60f7974c12") SOURCE_SYSTEM(
      "sourceSystem",
      "https://hdl.handle.net/21.T11148/417a4f472f60f7974c12",
      "https://hdl.handle.net/21.T11148/417a4f472f60f7974c12"),
  @JsonProperty("https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e") MAPPING(
      "Mapping",
      "https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e",
      "https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e"),
  @JsonProperty("https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0") ORGANISATION(
      "Organisation",
      "https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0",
      "https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0"),
  @JsonProperty("https://hdl.handle.net/21.T11148/d7570227982f70256af3") TOMBSTONE(
      "Tombstone",
      "https://hdl.handle.net/21.T11148/d7570227982f70256af3",
      "https://hdl.handle.net/21.T11148/d7570227982f70256af3"),
  @JsonProperty("https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa") MAS(
      "Machine Annotation Service",
      "https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa",
      "https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa");

  private final String digitalObjectName;
  private final String digitalObjectType;
  private final String fdoProfile;

  FdoType(@JsonProperty("type") String digitalObjectName, String digitalObjectType,
      String fdoProfile) {
    this.digitalObjectName = digitalObjectName;
    this.digitalObjectType = digitalObjectType;
    this.fdoProfile = fdoProfile;
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
