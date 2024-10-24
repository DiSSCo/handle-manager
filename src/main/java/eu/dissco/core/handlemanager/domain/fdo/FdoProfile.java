package eu.dissco.core.handlemanager.domain.fdo;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FdoProfile {
  // Kernel
  FDO_PROFILE("fdoProfile", 1),
  FDO_RECORD_LICENSE_ID("fdoRecordLicenseId", 2),
  FDO_RECORD_LICENSE_NAME("fdoRecordLicenseName", 3),
  DIGITAL_OBJECT_TYPE("digitalObjectType", 4),
  DIGITAL_OBJECT_NAME("digitalObjectName", 5),
  PID("pid", 6),
  PID_ISSUER("pidIssuer", 7),
  PID_ISSUER_NAME("pidIssuerName", 8),
  PID_RECORD_ISSUE_DATE("pidRecordIssueDate", 9),
  PID_RECORD_ISSUE_NUMBER("pidRecordIssueNumber", 10),
  PID_STATUS("pidStatus", 11),

  // Tombstone
  TOMBSTONED_TEXT("ods:tombstonedText", 30),
  HAS_RELATED_PID("hasRelatedPID", 31),
  TOMBSTONED_DATE("tombstonedDate", 32),

  // DOI
  ISSUED_FOR_AGENT("issuedForAgent", 40),
  ISSUED_FOR_AGENT_NAME("issuedForAgentName", 41),
  REFERENT_NAME("referentName", 42),

  // Digital Specimen
  SPECIMEN_HOST("specimenHost", 200),
  SPECIMEN_HOST_NAME("specimenHostName", 201),
  NORMALISED_SPECIMEN_OBJECT_ID("normalisedPrimarySpecimenObjectId", 202),
  OTHER_SPECIMEN_IDS("otherSpecimenIds", 203),
  TOPIC_ORIGIN("topicOrigin", 204),
  TOPIC_DOMAIN("topicDomain", 205),
  TOPIC_DISCIPLINE("topicDiscipline", 206),
  TOPIC_CATEGORY("topicCategory", 207),
  LIVING_OR_PRESERVED("livingOrPreserved", 208),
  MATERIAL_SAMPLE_TYPE("materialSampleType", 209),
  MARKED_AS_TYPE("markedAsType", 210),
  CATALOG_IDENTIFIER("catalogIdentifier", 211),

  // Media
  MEDIA_HOST("mediaHost", 400),
  MEDIA_HOST_NAME("mediaHostName", 401),
  LINKED_DO_PID("linkedDigitalObjectPid", 403),
  LINKED_DO_TYPE("linkedDigitalObjectType", 404),
  PRIMARY_MEDIA_ID("primaryMediaId", 405),
  PRIMARY_MEDIA_ID_TYPE("primaryMediaIdType", 406),
  PRIMARY_MEDIA_ID_NAME("primaryMediaIdName", 407),
  MEDIA_TYPE("mediaType", 408),
  MIME_TYPE("mimeType", 409),
  LICENSE_NAME("licenseName", 410),
  LICENSE_ID("licenseId", 411),
  RIGHTS_HOLDER_NAME("rightsHolderName", 412),
  RIGHTS_HOLDER_PID("rightsHolderId", 413),

  // Annotation
  TARGET_PID("targetPid", 500),
  TARGET_TYPE("targetType", 501),
  TARGET_TYPE_NAME("targetTypeName", 502),
  ANNOTATION_HASH("annotationHash", 505),

  // Agent (Organisation, Source System, or MAS)
  SOURCE_SYSTEM_NAME("sourceSystemName", 600),
  ORGANISATION_ID("organisationIdentifier", 601),
  ORGANISATION_ID_TYPE("organisationIdentifierType", 602),
  ORGANISATION_NAME("organisationName", 603),
  MAS_NAME("machineAnnotationServiceName", 604),

  // Mapping
  SOURCE_DATA_STANDARD("sourceDataStandard", 700),

  // Administration
  HS_ADMIN("HS_ADMIN", 100),
  LOC("10320/loc", 101);

  private static final Map<String, FdoProfile> LOOKUP;

  static {
    LOOKUP = new HashMap<>();
    for (var fdoProfileAttribute : FdoProfile.values()) {
      LOOKUP.put(fdoProfileAttribute.attribute, fdoProfileAttribute);
    }
  }

  public static FdoProfile fromString(String attributeName) {
    return LOOKUP.get(attributeName);
  }

  private final String attribute;
  private final int index;


  FdoProfile(String attribute, int index) {
    this.attribute = attribute;
    this.index = index;
  }

  public String get() {
    return this.attribute;
  }

  public int index() {
    return this.index;
  }

}
