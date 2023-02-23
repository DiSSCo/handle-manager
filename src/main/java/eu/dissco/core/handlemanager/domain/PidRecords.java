package eu.dissco.core.handlemanager.domain;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PidRecords {

  // Handle
  public static final String HS_ADMIN = "HS_ADMIN";
  public static final String PID = "pid";
  public static final String PID_ISSUER = "pidIssuer";
  public static final String DIGITAL_OBJECT_TYPE = "digitalObjectType";
  public static final String DIGITAL_OBJECT_SUBTYPE = "digitalObjectSubtype";
  public static final String LOC = "10320/loc";
  public static final String ISSUE_DATE = "issueDate";
  public static final String ISSUE_NUMBER = "issueNumber";
  public static final String PID_STATUS = "pidStatus";
  public static final String PID_KERNEL_METADATA_LICENSE = "pidKernelMetadataLicense";
  // Doi
  public static final String REFERENT_DOI_NAME = "referentDoiName";
  public static final String REFERENT = "referent";
  // Media Object
  public static final String MEDIA_HASH = "mediaHash";
  public static final String MEDIA_URL = "mediaUrl";
  // Digital Specimen
  public static final String DIGITAL_OR_PHYSICAL = "digitalOrPhysical";
  public static final String SPECIMEN_HOST = "specimenHost";
  public static final String IN_COLLECTION_FACILITY = "inCollectionFacility";
  // Digital Specimen Botany
  public static final String OBJECT_TYPE = "objectType";
  public static final String PRESERVED_OR_LIVING = "preservedOrLiving";
  public static final String PHYSICAL_IDENTIFIER = "physicalIdentifier";
  // Tombstone
  public static final String TOMBSTONE_TEXT = "tombstoneText";
  public static final String TOMBSTONE_PIDS = "tombstonePids";
  // Record types
  public static final String RECORD_TYPE_HANDLE = "handle";
  public static final String RECORD_TYPE_DOI = "doi";
  public static final String RECORD_TYPE_DS = "digitalSpecimen";
  public static final String RECORD_TYPE_DS_BOTANY = "digitalSpecimenBotany";
  public static final String RECORD_TYPE_TOMBSTONE = "tombstone";
  public static final String RECORD_TYPE_MEDIA = "mediaObject";
  public static final Set<String> RECORD_TYPES = (Set.of(RECORD_TYPE_HANDLE, RECORD_TYPE_DOI,
      RECORD_TYPE_DS, RECORD_TYPE_DS_BOTANY, RECORD_TYPE_TOMBSTONE, RECORD_TYPE_MEDIA));
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

  // Pid Status
  public static final Set<String> VALID_PID_STATUS = (Set.of("ACTIVE", "ARCHIVED", "DRAFT",
      "RESERVED", "TEST", "TEST2", "ALL"));

  // Permitted fields for each record type
  public static final Set<String> HANDLE_RECORD_FIELDS = Set.of(PID, PID_ISSUER,
      DIGITAL_OBJECT_TYPE, DIGITAL_OBJECT_SUBTYPE, LOC, ISSUE_DATE, ISSUE_NUMBER, PID_STATUS,
      PID_KERNEL_METADATA_LICENSE);
  public static final Set<String> DOI_RECORD_FIELDS;
  public static final Set<String> DIGITAL_SPECIMEN_FIELDS;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY_FIELDS;
  public static final Set<String> TOMBSTONE_RECORD_FIELDS;
  public static final Set<byte[]> TOMBSTONE_RECORD_FIELDS_BYTES;
  // Fields for request type (For checking update requests)
  public static final Set<String> HANDLE_RECORD_REQ = Set.of(PID_ISSUER_REQ, LOC_REQ);
  public static final Set<String> DOI_RECORD_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY_REQ;

  public static final Set<String> FIELD_IS_PID_RECORD = Set.of(PID_ISSUER, DIGITAL_OBJECT_TYPE,
      DIGITAL_OBJECT_SUBTYPE, TOMBSTONE_PIDS, SPECIMEN_HOST, IN_COLLECTION_FACILITY,
      REFERENT_DOI_NAME);

  public static final Map<String, Integer> FIELD_IDX = Map.ofEntries(Map.entry(HS_ADMIN, 100),
      Map.entry(PID, 1), Map.entry(PID_ISSUER, 2), Map.entry(DIGITAL_OBJECT_TYPE, 3),
      Map.entry(DIGITAL_OBJECT_SUBTYPE, 4), Map.entry(LOC, 5), Map.entry(ISSUE_DATE, 6),
      Map.entry(ISSUE_NUMBER, 7), Map.entry(PID_STATUS, 8), Map.entry(TOMBSTONE_TEXT, 9),
      Map.entry(TOMBSTONE_PIDS, 10), Map.entry(PID_KERNEL_METADATA_LICENSE, 11),
      Map.entry(REFERENT_DOI_NAME, 12), Map.entry(REFERENT, 13), Map.entry(DIGITAL_OR_PHYSICAL, 14),
      Map.entry(SPECIMEN_HOST, 15), Map.entry(IN_COLLECTION_FACILITY, 16),
      Map.entry(PHYSICAL_IDENTIFIER, 17), Map.entry(OBJECT_TYPE, 18),
      Map.entry(PRESERVED_OR_LIVING, 19), Map.entry(MEDIA_HASH, 14), Map.entry(MEDIA_URL, 15));

  static { // Doi Record Fields
    Set<String> tmp = new HashSet<>(HANDLE_RECORD_FIELDS);
    tmp.add(REFERENT_DOI_NAME);
    tmp.add(REFERENT);
    DOI_RECORD_FIELDS = Collections.unmodifiableSet(tmp);
  }

  static { // Digital Specimen Fields
    Set<String> tmp = new HashSet<>(DOI_RECORD_FIELDS);
    tmp.add(DIGITAL_OR_PHYSICAL);
    tmp.add(SPECIMEN_HOST);
    tmp.add(IN_COLLECTION_FACILITY);
    DIGITAL_SPECIMEN_FIELDS = Collections.unmodifiableSet(tmp);
  }

  static { // Digital Specimen Botany Fields
    Set<String> tmp = new HashSet<>(DIGITAL_SPECIMEN_FIELDS);
    tmp.add(OBJECT_TYPE);
    tmp.add(PRESERVED_OR_LIVING);
    DIGITAL_SPECIMEN_BOTANY_FIELDS = Collections.unmodifiableSet(tmp);
  }

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
    tmp.add(DIGITAL_OR_PHYSICAL);
    tmp.add(SPECIMEN_HOST_REQ);
    tmp.add(IN_COLLECTION_FACILITY_REQ);
    DIGITAL_SPECIMEN_REQ = Collections.unmodifiableSet(tmp);
  }

  static {
    Set<String> tmp = new HashSet<>(DIGITAL_SPECIMEN_REQ);
    tmp.add(OBJECT_TYPE);
    tmp.add(PRESERVED_OR_LIVING);
    DIGITAL_SPECIMEN_BOTANY_REQ = Collections.unmodifiableSet(tmp);
  }

  // Indexes of modifiable fields
  private PidRecords() {
    throw new IllegalStateException("Utility class");
  }

}
