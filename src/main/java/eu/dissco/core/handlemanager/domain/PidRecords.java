package eu.dissco.core.handlemanager.domain;

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

  public static final Set<String> HANDLE_RECORD;
  public static final Set<String> DOI_RECORD;
  public static final Set<String> DIGITAL_SPECIMEN;
  public static final Set<String> DIGITAL_SPECIMEN_BOTANY;
  public static final Set<String> TOMBSTONE_RECORD;

  public static final Set<String> FIELD_IS_PID_RECORD;

  public static final Map<String, Integer> FIELD_IDX;


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
    HANDLE_RECORD = Collections.unmodifiableSet(tmp);
  }

  static { // Doi Record Fields
    Set<String> tmp = new HashSet<>(HANDLE_RECORD);
    tmp.add(REFERENT_DOI_NAME);
    tmp.add(REFERENT);
    DOI_RECORD = Collections.unmodifiableSet(tmp);
  }

  static { // Digital Specimen Fields
    Set<String> tmp = new HashSet<>(DOI_RECORD);
    tmp.add(DIGITAL_OR_PHYSICAL);
    tmp.add(SPECIMEN_HOST);
    tmp.add(IN_COLLECTION_FACILITY);
    DIGITAL_SPECIMEN = Collections.unmodifiableSet(tmp);
  }

  static { // Digital Specimen Botany Fields
    Set<String> tmp = new HashSet<>(DIGITAL_SPECIMEN);
    tmp.add(OBJECT_TYPE);
    tmp.add(PRESERVED_OR_LIVING);
    DIGITAL_SPECIMEN_BOTANY = Collections.unmodifiableSet(tmp);
  }

  static {
    Set<String> tmp = new HashSet<>(HANDLE_RECORD);
    tmp.add(TOMBSTONE_TEXT);
    tmp.add(TOMBSTONE_PIDS);
    TOMBSTONE_RECORD = Collections.unmodifiableSet(tmp);
  }

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

  private PidRecords(){
    throw new IllegalStateException("Utility class");
  }


}
