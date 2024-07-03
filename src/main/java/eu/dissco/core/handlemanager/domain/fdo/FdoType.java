package eu.dissco.core.handlemanager.domain.fdo;

import static eu.dissco.core.handlemanager.service.FdoRecordService.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.service.FdoRecordService.HANDLE_DOMAIN;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum FdoType {
  @JsonProperty("https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6") HANDLE(
      "Handle Kernel",
      "https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6",
      "https://hdl.handle.net/21.T11148/532ce6796e2828dd2be6",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c") DOI(
      "DOI Kernel",
      "https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c",
      "https://hdl.handle.net/21.T11148/527856fd709ec8c5bc8c",
      DOI_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/894b1e6cad57e921764e") DIGITAL_SPECIMEN(
      "DigitalSpecimen",
      "https://hdl.handle.net/21.T11148/894b1e6cad57e921764e",
      "https://hdl.handle.net/21.T11148/894b1e6cad57e921764e",
      DOI_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115") MEDIA_OBJECT(
      "MediaObject",
      "https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115",
      "https://hdl.handle.net/21.T11148/bbad8c4e101e8af01115",
      DOI_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f") ANNOTATION(
      "Annotation",
      "https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f",
      "https://hdl.handle.net/21.T11148/cf458ca9ee1d44a5608f",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/417a4f472f60f7974c12") SOURCE_SYSTEM(
      "sourceSystem",
      "https://hdl.handle.net/21.T11148/417a4f472f60f7974c12",
      "https://hdl.handle.net/21.T11148/417a4f472f60f7974c12",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e") MAPPING(
      "Mapping",
      "https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e",
      "https://hdl.handle.net/21.T11148/ce794a6f4df42eb7e77e",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0") ORGANISATION(
      "Organisation",
      "https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0",
      "https://hdl.handle.net/21.T11148/413c00cbd83ae33d1ac0",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/d7570227982f70256af3") TOMBSTONE(
      "Tombstone",
      "https://hdl.handle.net/21.T11148/d7570227982f70256af3",
      "https://hdl.handle.net/21.T11148/d7570227982f70256af3",
      HANDLE_DOMAIN),
  @JsonProperty("https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa") MAS(
      "Machine Annotation Service",
      "https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa",
      "https://hdl.handle.net/21.T11148/22e71a0015cbcfba8ffa",
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
