package eu.dissco.core.handlemanager.domain;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
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
  public static final String LOC = "10230/loc";
  public static final String ISSUE_DATE = "issueDate";
  public static final String ISSUE_NUMBER = "issueNumber";
  public static final String PID_STATUS = "pidStatus";
  public static final String PID_KERNEL_METADATA_LICENSE = "pidKernelMetadataLicense";
  // Doi
  public static final String REFERENT_DOI_NAME = "referentDoiName";
  public static final String REFERENT = "referent";
  // Digital Specimen
  public static final String DIGITAL_OR_PHYSICAL = "digitalOrPhysical";
  public static final String SPECIMEN_HOST = "specimenHost";
  public static final String IN_COLLECTION_FACILITY = "inCollectionFacility";
  // Digital Specimen Botany
  public static final String OBJECT_TYPE = "objectType";
  public static final String PRESERVED_OR_LIVING = "preservedOrLiving";
  // Tombstone
  public static final String TOMBSTONE_TEXT = "tombstoneText";
  public static final String TOMBSTONE_PIDS = "tombstonePids";
  // Record types
  public static final String RECORD_TYPE_HANDLE = "handle";
  public static final String RECORD_TYPE_DOI = "doi";
  public static final String RECORD_TYPE_DS = "digitalSpecimen";
  public static final String RECORD_TYPE_DS_BOTANY = "digitalSpecimenBotany";
  public static final String RECORD_TYPE_TOMBSTONE = "tombstone";
  // Fields for requests
  public static final String PID_ISSUER_REQ = "pidIssuerPid";
  public static final String DIGITAL_OBJECT_TYPE_REQ = "digitalObjectTypePid";
  public static final String DIGITAL_OBJECT_SUBTYPE_REQ = "digitalObjectSubtypePid";
  public static final String LOC_REQ = "locations";
  public static final String REFERENT_DOI_NAME_REQ = "referentDoiNamePid";
  public static final String SPECIMEN_HOST_REQ = "specimenHostPid";
  public static final String IN_COLLECTION_FACILITY_REQ = "inCollectionFacilityPid";

  public static final String NODE_ATTRIBUTES = "attributes";
  public static final String NODE_DATA = "data";
  public static final String NODE_ID = "id";
  public static final String NODE_TYPE = "type";
  // Permitted fields for each record type
  public static final Set<String> HANDLE_RECORD_FIELDS;
  public static final Set<String> DOI_RECORD_FIELDS;
  public static final Set<String> DIGITAL_SPECIMEN_FIELDS;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY_FIELDS;
  public static final Set<String> TOMBSTONE_RECORD_FIELDS;
  public static final Set<byte[]> TOMBSTONE_RECORD_FIELDS_BYTES;
  // Fields for request type (For checking update requests)
  public static final Set<String> HANDLE_RECORD_REQ;
  public static final Set<String> DOI_RECORD_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_REQ;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY_REQ;

  public static final Set<String> FIELD_IS_PID_RECORD;

  public static final Map<String, Integer> FIELD_IDX;

  public static final Map<String, Integer> REQ_FIELD_IDX;


  static { // Handle Record Fields
    Set<String> tmp = new HashSet<>();
    tmp.add(PID);
    tmp.add(PID_ISSUER);
    tmp.add(DIGITAL_OBJECT_TYPE);
    tmp.add(DIGITAL_OBJECT_SUBTYPE);
    tmp.add(LOC);
    tmp.add(ISSUE_DATE);
    tmp.add(ISSUE_NUMBER);
    tmp.add(PID_STATUS);
    tmp.add(PID_KERNEL_METADATA_LICENSE);
    HANDLE_RECORD_FIELDS = Collections.unmodifiableSet(tmp);
  }

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
    for (String field : TOMBSTONE_RECORD_FIELDS){
      tmp.add(field.getBytes(StandardCharsets.UTF_8));
    }
    TOMBSTONE_RECORD_FIELDS_BYTES = Collections.unmodifiableSet(tmp);
  }

  // Request fields
  static {
    Set<String> tmp = new HashSet<>();
    tmp.add(PID_ISSUER_REQ);
    tmp.add(DIGITAL_OBJECT_TYPE_REQ);
    tmp.add(DIGITAL_OBJECT_TYPE_REQ);
    tmp.add(LOC_REQ);
    HANDLE_RECORD_REQ = Collections.unmodifiableSet(tmp);
  }

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

  // Fields with specific formatting requirements
  static {
    Set<String> tmp = new HashSet<>();
    tmp.add(PID_ISSUER);
    tmp.add(DIGITAL_OBJECT_TYPE);
    tmp.add(DIGITAL_OBJECT_SUBTYPE);
    tmp.add(TOMBSTONE_PIDS);
    tmp.add(SPECIMEN_HOST);
    tmp.add(IN_COLLECTION_FACILITY);
    tmp.add(REFERENT_DOI_NAME);
    FIELD_IS_PID_RECORD = Collections.unmodifiableSet(tmp);
  }

  static {
    // Keys = field names, values = indexes
    // Seems counterintuitive, but it's likely specific indexes will be overloaded

    Map<String, Integer> tmp = new HashMap<>();
    tmp.put(HS_ADMIN, 100);
    tmp.put(PID, 1);
    tmp.put(PID_ISSUER, 2);
    tmp.put(DIGITAL_OBJECT_TYPE, 3);
    tmp.put(DIGITAL_OBJECT_SUBTYPE, 4);
    tmp.put(LOC, 5);
    tmp.put(ISSUE_DATE, 6);
    tmp.put(ISSUE_NUMBER, 7);
    tmp.put(PID_STATUS, 8);
    tmp.put(TOMBSTONE_TEXT, 9);
    tmp.put(TOMBSTONE_PIDS, 10);
    tmp.put(PID_KERNEL_METADATA_LICENSE, 11);
    tmp.put(REFERENT_DOI_NAME, 12);
    tmp.put(REFERENT, 13);
    tmp.put(DIGITAL_OR_PHYSICAL, 14);
    tmp.put(SPECIMEN_HOST, 15);
    tmp.put(IN_COLLECTION_FACILITY, 16);
    tmp.put(OBJECT_TYPE, 17);
    tmp.put(PRESERVED_OR_LIVING, 18);
    FIELD_IDX = Collections.unmodifiableMap(tmp);
  }

  // Indexes of modifiable fields
  static {
    Map<String, Integer> tmp = new HashMap<>();
    tmp.put(PID_ISSUER_REQ, FIELD_IDX.get(PID_ISSUER));
    tmp.put(DIGITAL_OBJECT_TYPE_REQ, FIELD_IDX.get(DIGITAL_OBJECT_TYPE));
    tmp.put(DIGITAL_OBJECT_SUBTYPE_REQ, FIELD_IDX.get(DIGITAL_OBJECT_SUBTYPE));
    tmp.put(LOC_REQ, FIELD_IDX.get(LOC));
    tmp.put(REFERENT_DOI_NAME_REQ, FIELD_IDX.get(REFERENT_DOI_NAME));
    tmp.put(REFERENT, FIELD_IDX.get(REFERENT));
    tmp.put(DIGITAL_OR_PHYSICAL, FIELD_IDX.get(DIGITAL_OR_PHYSICAL));
    tmp.put(IN_COLLECTION_FACILITY_REQ, FIELD_IDX.get(IN_COLLECTION_FACILITY));
    tmp.put(OBJECT_TYPE, FIELD_IDX.get(OBJECT_TYPE));
    tmp.put(PRESERVED_OR_LIVING, FIELD_IDX.get(PRESERVED_OR_LIVING));
    REQ_FIELD_IDX = Collections.unmodifiableMap(tmp);
  }

  private PidRecords() {
    throw new IllegalStateException("Utility class");
  }


}
