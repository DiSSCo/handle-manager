package eu.dissco.core.handlemanager.domain;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PidRecords {

  // Handle

  public static final String FDO_PROFILE = "fdoProfile"; // 1
  public static final String FDO_RECORD_LICENSE = "fdoRecordLicense"; //2
  public static final String DIGITAL_OBJECT_TYPE = "digitalObjectType"; //3
  public static final String DIGITAL_OBJECT_NAME = "digitalObjectName"; //4
  public static final String PID = "pid"; //5
  public static final String PID_ISSUER = "pidIssuer"; // 6
  public static final String PID_ISSUER_NAME = "pidIssuerName"; // 7
  public static final String ISSUED_FOR_AGENT = "issuedForAgent"; // 8
  public static final String ISSUED_FOR_AGENT_NAME = "issuedForAgentName"; // 9
  public static final String PID_RECORD_ISSUE_DATE = "pidRecordIssueDate"; // 10
  public static final String PID_RECORD_ISSUE_NUMBER = "pidRecordIssueNumber"; //11
  public static final String STRUCTURAL_TYPE = "structuralType"; // 12
  public static final String PID_STATUS = "pidStatus"; // 13

  // Tombstone
  public static final String TOMBSTONE_TEXT = "tombstoneText"; // 30
  public static final String TOMBSTONE_PIDS = "tombstonePids"; // 31

  // Doi
  public static final String REFERENT_TYPE = "referentType"; // 40
  public static final String REFERENT_DOI_NAME = "referentDoiName"; // 41
  public static final String REFERENT_NAME = "referentName"; // 42
  public static final String PRIMARY_REFERENT_TYPE = "primaryReferentType"; // 43
  public static final String REFERENT = "referent"; // 44

  // Digital Specimen
  public static final String SPECIMEN_HOST = "specimenHost"; // 200
  public static final String SPECIMEN_HOST_NAME = "specimenHostName"; // 201
  public static final String PRIMARY_SPECIMEN_OBJECT_ID = "primarySpecimenObjectId"; // 202
  public static final String PRIMARY_SPECIMEN_OBJECT_ID_TYPE = "primarySpecimenObjectIdType"; // 203
  public static final String PRIMARY_SPECIMEN_OBJECT_ID_NAME = "primarySpecimenObjectIdName"; // 204
  public static final String PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE = "primarySpecimenObjectIdAbsenceReason"; // 205
  public static final String OTHER_SPECIMEN_IDS = "otherSpecimenIds"; // 206
  public static final String TOPIC_ORIGIN = "topicOrigin"; // 207
  public static final String TOPIC_DOMAIN = "topicDomain"; // 208
  public static final String TOPIC_DISCIPLINE = "topicDiscipline"; // 209
  public static final String OBJECT_TYPE = "objectType"; // 210
  public static final String LIVING_OR_PRESERVED = "livingOrPreserved"; // 211
  public static final String BASE_TYPE_OF_SPECIMEN = "baseTypeOfSpecimen"; // 212
  public static final String INFORMATION_ARTEFACT_TYPE = "informationArtefactType"; // 213
  public static final String MATERIAL_SAMPLE_TYPE = "materialSampleType"; // 214
  public static final String MATERIAL_OR_DIGITAL_ENTITY = "materialOrDigitalEntity"; // 215
  public static final String MARKED_AS_TYPE = "markedAsType"; // 216
  public static final String WAS_DERIVED_FROM = "wasDerivedFrom"; // 217


  // Handle Admin
  public static final String HS_ADMIN = "HS_ADMIN"; // 100
  public static final String LOC = "10320/loc"; //101


  // Media Object
  public static final String MEDIA_HASH = "mediaHash";
  public static final String MEDIA_HASH_ALG = "mediaHashAlgorithm";
  public static final String SUBJECT_SPECIMEN_HOST = "subjectSpecimenHost";
  public static final String MEDIA_URL = "mediaUrl";
  public static final String SUBJECT_PHYSICAL_IDENTIFIER = "subjectPrimarySpecimenObjectId";

  // Annotations
  public static final String SUBJECT_DIGITAL_OBJECT_ID = "subjectDigitalObjectId";

  // Source Systems
  public static final String HOST_INSTITUTION = "hostInstitution";

  // Mappings
  public static final String SOURCE_DATA_STANDARD = "sourceDataStandard";

  // Organisations
  public static final String ORGANISATION_ID = "organisationIdentifier";
  public static final String ORGANISATION_ID_TYPE = "organisationIdentifierType";
  public static final String ORGANISATION_NAME = "organisationName";

  // Fields for requests
  public static final String PID_ISSUER_REQ = "pidIssuerPid";
  public static final String LOC_REQ = "locations";
  public static final String REFERENT_DOI_NAME_REQ = "referentDoiNamePid";
  public static final String SPECIMEN_HOST_REQ = "specimenHostPid";
  public static final String IN_COLLECTION_FACILITY_REQ = "inCollectionFacilityPid";
  public static final String NODE_ATTRIBUTES = "attributes";
  public static final String NODE_DATA = "data";
  public static final String NODE_ID = "id";
  public static final String NODE_TYPE = "type";

  // Permitted fields for each record type
  public static final Set<String> HANDLE_RECORD_FIELDS = Set.of(PID, PID_ISSUER,
      DIGITAL_OBJECT_TYPE, LOC, PID_RECORD_ISSUE_DATE,
      PID_RECORD_ISSUE_NUMBER, PID_STATUS, FDO_RECORD_LICENSE);
  public static final Set<byte[]> TOMBSTONE_RECORD_FIELDS_BYTES;
  public static final Set<String> TOMBSTONE_RECORD_FIELDS;
  // Fields for request type (For checking update requests)
  public static final Set<String> HANDLE_RECORD_REQ = Set.of(PID_ISSUER_REQ, LOC_REQ);
  public static final Set<String> DOI_RECORD_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY_REQ;

  public static final Map<String, Integer> FIELD_IDX = Map.ofEntries(Map.entry(FDO_PROFILE, 1),
      Map.entry(FDO_RECORD_LICENSE, 2), Map.entry(DIGITAL_OBJECT_TYPE, 3),
      Map.entry(DIGITAL_OBJECT_NAME, 4), Map.entry(PID, 5), Map.entry(PID_ISSUER, 6),
      Map.entry(PID_ISSUER_NAME, 7), Map.entry(ISSUED_FOR_AGENT, 8),
      Map.entry(ISSUED_FOR_AGENT_NAME, 9), Map.entry(PID_RECORD_ISSUE_DATE, 10),
      Map.entry(PID_RECORD_ISSUE_NUMBER, 11), Map.entry(STRUCTURAL_TYPE, 12),
      Map.entry(PID_STATUS, 13),

      Map.entry(TOMBSTONE_TEXT, 31), Map.entry(TOMBSTONE_PIDS, 32),

      Map.entry(REFERENT_TYPE, 40), Map.entry(REFERENT_DOI_NAME, 41), Map.entry(REFERENT_NAME, 42),
      Map.entry(PRIMARY_REFERENT_TYPE, 43), Map.entry(REFERENT, 44),

      Map.entry(SPECIMEN_HOST, 200), Map.entry(SPECIMEN_HOST_NAME, 201),
      Map.entry(PRIMARY_SPECIMEN_OBJECT_ID, 202), Map.entry(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, 203),
      Map.entry(PRIMARY_SPECIMEN_OBJECT_ID_NAME, 204),
      Map.entry(PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE, 205), Map.entry(OTHER_SPECIMEN_IDS, 206),
      Map.entry(TOPIC_ORIGIN, 207), Map.entry(TOPIC_DOMAIN, 208), Map.entry(TOPIC_DISCIPLINE, 209),
      Map.entry(OBJECT_TYPE, 210), Map.entry(LIVING_OR_PRESERVED, 211),
      Map.entry(BASE_TYPE_OF_SPECIMEN, 212), Map.entry(INFORMATION_ARTEFACT_TYPE, 213),
      Map.entry(MATERIAL_SAMPLE_TYPE, 214), Map.entry(MATERIAL_OR_DIGITAL_ENTITY, 215),
      Map.entry(MARKED_AS_TYPE, 216), Map.entry(WAS_DERIVED_FROM, 217),

      Map.entry(MEDIA_HASH, 400),
      Map.entry(MEDIA_HASH_ALG, 401),
      Map.entry(SUBJECT_SPECIMEN_HOST, 402),
      Map.entry(MEDIA_URL, 403),
      Map.entry(SUBJECT_PHYSICAL_IDENTIFIER, 404),

      Map.entry(SUBJECT_DIGITAL_OBJECT_ID,500),

      Map.entry(HOST_INSTITUTION,600),

      Map.entry(SOURCE_DATA_STANDARD, 700),

      Map.entry(ORGANISATION_ID, 800),
      Map.entry(ORGANISATION_ID_TYPE, 801),
      Map.entry(ORGANISATION_NAME, 802),

      Map.entry(HS_ADMIN, 100), Map.entry(LOC, 101));


  static {
    Set<String> tmp = new HashSet<>(HANDLE_RECORD_FIELDS);
    tmp.add(TOMBSTONE_TEXT);
    tmp.add(TOMBSTONE_PIDS);
    TOMBSTONE_RECORD_FIELDS = Collections.unmodifiableSet(tmp);
  }

  static {
    Set<byte[]> tmp = new HashSet<>();
    for (String field : TOMBSTONE_RECORD_FIELDS) {
      tmp.add(field.getBytes(StandardCharsets.UTF_8));
    }
    TOMBSTONE_RECORD_FIELDS_BYTES = Collections.unmodifiableSet(tmp);
  }

  // Request fields

  static {
    Set<String> tmp = new HashSet<>(HANDLE_RECORD_REQ);
    tmp.add(REFERENT);
    tmp.add(REFERENT_DOI_NAME_REQ);
    DOI_RECORD_REQ = Collections.unmodifiableSet(tmp);
  }

  static {
    Set<String> tmp = new HashSet<>(DOI_RECORD_REQ);
    tmp.add(MATERIAL_OR_DIGITAL_ENTITY);
    tmp.add(SPECIMEN_HOST_REQ);
    tmp.add(IN_COLLECTION_FACILITY_REQ);
    DIGITAL_SPECIMEN_REQ = Collections.unmodifiableSet(tmp);
  }

  static {
    Set<String> tmp = new HashSet<>(DIGITAL_SPECIMEN_REQ);
    tmp.add(OBJECT_TYPE);
    tmp.add(LIVING_OR_PRESERVED);
    DIGITAL_SPECIMEN_BOTANY_REQ = Collections.unmodifiableSet(tmp);
  }

  private PidRecords() {
    throw new IllegalStateException("Utility class");
  }

}
