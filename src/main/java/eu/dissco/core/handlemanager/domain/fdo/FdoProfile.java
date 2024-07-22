package eu.dissco.core.handlemanager.domain.fdo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
  TOMBSTONED_TEXT("ods:tombstonedText", 30),
  HAS_RELATED_PID("hasRelatedPID", 31),
  TOMBSTONED_DATE("tombstonedDate", 32),

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
  NORMALISED_SPECIMEN_OBJECT_ID("normalisedPrimarySpecimenObjectId", 205),
  SPECIMEN_OBJECT_ID_ABSENCE_REASON("specimenObjectIdAbsenceReason", 206),
  OTHER_SPECIMEN_IDS("otherSpecimenIds", 207),
  TOPIC_ORIGIN("topicOrigin", 208),
  TOPIC_DOMAIN("topicDomain", 209),
  TOPIC_DISCIPLINE("topicDiscipline", 210),
  TOPIC_CATEGORY("topicCategory", 211),
  LIVING_OR_PRESERVED("livingOrPreserved", 212),
  BASE_TYPE_OF_SPECIMEN("baseTypeOfSpecimen", 213),
  INFORMATION_ARTEFACT_TYPE("informationArtefactType", 214),
  MATERIAL_SAMPLE_TYPE("materialSampleType", 215),
  MATERIAL_OR_DIGITAL_ENTITY("materialOrDigitalEntity", 216),
  MARKED_AS_TYPE("markedAsType", 217),
  WAS_DERIVED_FROM_ENTITY("wasDerivedFromEntity", 218),
  CATALOG_IDENTIFIER("catalogIdentifier", 219),

  // Media
  MEDIA_HOST("mediaHost", 400),
  MEDIA_HOST_NAME("mediaHostName", 401),
  IS_DERIVED_FROM_SPECIMEN("isDerivedFromSpecimen", 403),
  LINKED_DO_PID("linkedDigitalObjectPid", 404),
  LINKED_DO_TYPE("linkedDigitalObjectType", 405),
  LINKED_ATTRIBUTE("linkedAttribute", 406),
  PRIMARY_MEDIA_ID("primaryMediaId", 407),
  PRIMARY_MO_ID_TYPE("primaryMediaObjectIdType", 408),
  PRIMARY_MO_ID_NAME("primaryMediaObjectIdName", 409),
  DCTERMS_TYPE("dcterms:type", 410),
  DCTERMS_SUBJECT("dcterms:subject", 411),
  DCTERMS_FORMAT("dcterms:format", 412),
  DERIVED_FROM_ENTITY("derivedFromEntity", 413),
  LICENSE_NAME("licenseName", 414),
  LICENSE_URL("licenseUrl", 415),
  RIGHTSHOLDER_NAME("rightsholderName", 416),
  RIGHTSHOLDER_PID("rightsholderPid", 417),
  RIGHTSHOLDER_PID_TYPE("rightsholderPidType", 418),
  DC_TERMS_CONFORMS("dcterms:conformsTo", 419),

  // Annotation
  TARGET_PID("targetPid", 500),
  TARGET_TYPE("targetType", 501),
  MOTIVATION("motivation", 502),
  ANNOTATION_HASH("annotationHash", 505),

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
