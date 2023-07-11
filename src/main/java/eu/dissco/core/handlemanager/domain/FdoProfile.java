package eu.dissco.core.handlemanager.domain;

import java.util.Arrays;

public enum FdoProfile {
  // Kernel
  FDO_PROFILE("fdoProfile", 1),
  FDO_RECORD_LICENSE("fdoRecordLicense", 2),
  DIGITAL_OBJECT_TYPE("digitalObjectType", 3),
  DIGITAL_OBJECT_NAME("digitalObjectName", 4),
  PID("pid", 5),
  PID_ISSUER("pidIssuer", 6),
  PID_ISSUER_NAME("pidIssuerName", 7),
  ISSUED_FOR_AGENT("issuedForAgent", 8),
  ISSUED_FOR_AGENT_NAME("issuedForAgentName", 9),
  PID_RECORD_ISSUE_DATE("pidRecordIssueDate", 10),
  PID_RECORD_ISSUE_NUMBER("pidRecordIssueNumber", 11),
  STRUCTURAL_TYPE("structuralType", 12),
  PID_STATUS("pidStatus", 13),

  // Tombstone
  TOMBSTONE_TEXT("tombstoneText", 30),
  TOMBSTONE_PIDS("tombstonePids", 31),

  // DOI
  REFERENT_TYPE("referentType", 40),
  REFERENT_DOI_NAME("referentDoiName", 41),
  REFERENT_NAME("referentName", 42),
  PRIMARY_REFERENT_TYPE("primaryReferentType", 43),

  // Digital Specimen
  SPECIMEN_HOST("specimenHost", 200),
  SPECIMEN_HOST_NAME("specimenHostName", 201),
  PRIMARY_SPECIMEN_OBJECT_ID("primarySpecimenObjectId", 202),
  PRIMARY_SPECIMEN_OBJECT_ID_TYPE("primarySpecimenObjectIdType", 203),
  PRIMARY_SPECIMEN_OBJECT_ID_NAME("primarySpecimenObjectIdName", 204),
  NORMALISED_SPECIMEN_OBJECT_ID("normalisedSpecimenObjectId", 205),
  SPECIMEN_OBJECT_ID_ABSENCE_REASON("specimenObjectIdAbsenceReason", 206),
  OTHER_SPECIMEN_IDS("otherSpecimenIds", 207),
  TOPIC_ORIGIN("topicOrigin", 208),
  TOPIC_DOMAIN("topicDomain", 209),
  TOPIC_DISCIPLINE("topicDiscipline", 210),
  TOPIC_CATEGORY("topicCategory", 211),
  LIVING_OR_PRESERVED("livingOrPreserved", 212),
  BASE_TYPE_OF_SPECIMEN("baseTypeOfSpecimen", 213),
  INFORMATION_ARTEFACT_TYPE("informationArtefactType", 241),
  MATERIAL_SAMPLE_TYPE("materialSampleType", 215),
  MATERIAL_OR_DIGITAL_ENTITY("materialOrDigitalEntity", 216),
  MARKED_AS_TYPE("markedAsType", 217),
  WAS_DERIVED_FROM_ENTITY("wasDerivedFromEntity", 218),

  // Media
  MEDIA_URL("mediaUrl", 403),
  MEDIA_OBJECT_TYPE("mediaObjectType", 405),
  MEDIA_HOST("mediaHost", 406),
  SUBJECT_LOCAL_ID("subjectLocalId", 407),
  SUBJECT_PID("subjectPid", 408),


  // Annotation
  SUBJECT_DIGITAL_OBJECT_IDS("subjectDigitalObjectIds", 500),
  ANNOTATION_TOPIC("annotationTopic", 501),
  REPLACE_OR_APPEND("replaceOrAppend", 502),
  ACCESS_RESTRICTED("accessRestricted", 503),
  LINKED_OBJECT_URL("linkedObjectUrl", 504),
  LINKED_OBJECT_IS_PID("linkedObjectIsPid", 505),

  // Agent
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


  private final String attribute;
  private final int index;


  private FdoProfile(String attribute, int index) {
    this.attribute = attribute;
    this.index = index;
  }

  public String get() {
    return this.attribute;
  }

  public int index() {
    return this.index;
  }

  public static int retrieveIndex(String searchAttribute) {
    var fdoProfile = Arrays.stream(FdoProfile.values())
        .filter(fdoRow -> fdoRow.attribute.equals(searchAttribute))
        .findFirst();
    if (fdoProfile.isPresent()) {
      return fdoProfile.get().index;
    }
    throw new IllegalStateException(
        "Unable to locate index for requested attribute " + searchAttribute);
  }

}