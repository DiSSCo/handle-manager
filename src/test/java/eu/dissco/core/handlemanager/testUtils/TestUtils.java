package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_SUBTYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OR_PHYSICAL;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IS_PID_RECORD;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.IN_COLLECTION_FACILITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_KERNEL_METADATA_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRESERVED_OR_LIVING;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_TOMBSTONE;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_TEXT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalOrPhysical;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.domain.requests.attributes.PidTypeName;
import eu.dissco.core.handlemanager.domain.requests.attributes.PreservedOrLiving;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

@Slf4j
public class TestUtils {

  public static final Instant CREATED = Instant.parse("2022-11-01T09:59:24.00Z");
  public static final String ISSUE_DATE_TESTVAL = "2022-11-01";
  public static final String HANDLE = "20.5000.1025/QRS-321-ABC";
  public static final String PREFIX = "20.5000.1025";
  public static final String SUFFIX = "QRS-321-ABC";
  public static final String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";
  public static final List<String> HANDLE_LIST_STR;
  public static final String PID_ISSUER_PID = "20.5000.1025/PID-ISSUER";
  public static final String DIGITAL_OBJECT_TYPE_PID = "20.5000.1025/DIGITAL-SPECIMEN";

  // Request Vars
  // Handles
  public static final String DIGITAL_OBJECT_SUBTYPE_PID = "20.5000.1025/BOTANY-SPECIMEN";
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String[] LOC_ALT_TESTVAL = {"naturalis.nl"};
  public static final String PID_STATUS_TESTVAL = "TEST";
  public static final String PID_KERNEL_METADATA_LICENSE_TESTVAL = "https://creativecommons.org/publicdomain/zero/1.0/";
  //DOIs
  public static final String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
  public static final String REFERENT_TESTVAL = "";
  //Digital Specimens
  public static final DigitalOrPhysical DIGITAL_OR_PHYSICAL_TESTVAL = DigitalOrPhysical.PHYSICAL;
  public static final String SPECIMEN_HOST_PID = "20.5000.1025/OTHER-TRIPLET";
  public static final PidTypeName SPECIMEN_HOST_TESTVAL = new PidTypeName(
      SPECIMEN_HOST_PID,
      "ROR",
      "Host institution"
  );
  public static final String IN_COLLECTION_FACILITY_TESTVAL = "20.5000.1025/OTHER-TRIPLET";
  //Botany Specimens
  public static final String OBJECT_TYPE_TESTVAL = "Herbarium Sheet";
  public static final PreservedOrLiving PRESERVED_OR_LIVING_TESTVAL = PreservedOrLiving.PRESERVED;
  // Media Objects
  public static final String MEDIA_URL_TESTVAL = "https://naturalis.nl/media/123";
  public static final String MEDIA_HASH_TESTVAL = "47bce5c74f589f48";
  public static final String PTR_TYPE = "handle";
  public static final String PTR_PRIMARY_NAME = "DiSSCo";
  public static final String PTR_PID_DOI = "http://doi.org/" + PID_ISSUER_PID;
  public static final String PTR_TYPE_DOI = "doi";
  public static final String PTR_REGISTRATION_DOI_NAME = "Registration Agency";
  public final static PhysicalIdentifier PHYSICAL_IDENTIFIER_OBJ = new PhysicalIdentifier(
      "BOTANICAL.QRS.123",
      "physicalSpecimenId"
  );
  public final static String PHYSICAL_IDENTIFIER_TO_STR;
  // Tombstone Record vals
  public final static String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Pid Type Record vals
  private final static String HANDLE_URI = "https://hdl.handle.net/";
  public static final String PTR_PID = HANDLE_URI + PID_ISSUER_PID;
  public final static String PTR_HANDLE_RECORD = genPtrHandleRecord(false);
  public final static String PTR_DOI_RECORD = genPtrHandleRecord(true);
  public static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

  static {
    HANDLE_LIST_STR = List.of(HANDLE, HANDLE_ALT);
  }

  static {
    try {
      PHYSICAL_IDENTIFIER_TO_STR = MAPPER.writeValueAsString(PHYSICAL_IDENTIFIER_OBJ);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  // Pid Type Records
  private static String genPtrHandleRecord(boolean isDoi) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    if (isDoi) {
      objectNode.put("pid", PTR_PID_DOI);
      objectNode.put("pidType", PTR_TYPE_DOI);
      objectNode.put("primaryNameFromPid", PTR_PRIMARY_NAME);
      objectNode.put("registrationAgencyDoiName", PTR_REGISTRATION_DOI_NAME);
    } else {
      objectNode.put("pid", PTR_PID);
      objectNode.put("pidType", PTR_TYPE);
      objectNode.put("primaryNameFromPid", PTR_PRIMARY_NAME);
    }
    try {
      return mapper.writeValueAsString(objectNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }

  // Single Handle Attribute Lists
  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle) {

    List<HandleAttribute> handleRecord = new ArrayList<>();
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes(StandardCharsets.UTF_8);

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes(StandardCharsets.UTF_8);
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 2: PidIssuer
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER, ptr_record));

    // 3: Digital Object Type
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            ptr_record));

    // 4: Digital Object Subtype
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_SUBTYPE), handle, DIGITAL_OBJECT_SUBTYPE,
            ptr_record));

    // 5: 10320/loc
    byte[] loc = "".getBytes(StandardCharsets.UTF_8);
    try {
      loc = setLocations(LOC_TESTVAL);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUE_DATE), handle, ISSUE_DATE,
        ISSUE_DATE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 7: Issue number
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUE_NUMBER), handle, ISSUE_NUMBER,
            "1".getBytes(StandardCharsets.UTF_8)));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 11: PidKernelMetadataLicense:
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_KERNEL_METADATA_LICENSE), handle,
            PID_KERNEL_METADATA_LICENSE,
            PID_KERNEL_METADATA_LICENSE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return handleRecord;
  }

  public static List<HandleAttribute> genHandleRecordAttributesAltLoc(byte[] handle)
      throws ParserConfigurationException, TransformerException {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle);

    byte[] locOriginal = setLocations(LOC_TESTVAL);
    var locOriginalAttr = new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locOriginal);

    byte[] locAlt = setLocations(LOC_ALT_TESTVAL);
    var locAltAttr = new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locAlt);

    attributes.set(attributes.indexOf(locOriginalAttr), locAltAttr);

    return attributes;
  }

  public static List<HandleAttribute> genTombstoneRecordFullAttributes(byte[] handle) {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle);
    HandleAttribute oldPidStatus = new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle,
        PID_STATUS, PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8));
    attributes.remove(oldPidStatus);
    attributes.addAll(genTombstoneRecordRequestAttributes(handle));

    return attributes;
  }

  public static List<HandleAttribute> genUpdateRecordAttributesAltLoc(byte[] handle)
      throws ParserConfigurationException, TransformerException {
    byte[] locAlt = setLocations(LOC_ALT_TESTVAL);
    return List.of(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locAlt));
  }

  public static List<HandleAttribute> genTombstoneRecordRequestAttributes(byte[] handle) {
    List<HandleAttribute> tombstoneAttributes = new ArrayList<>();
    tombstoneAttributes.add(
        new HandleAttribute(FIELD_IDX.get(TOMBSTONE_TEXT), handle, TOMBSTONE_TEXT,
            TOMBSTONE_TEXT_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    return tombstoneAttributes;
  }

  public static List<HandleAttribute> genDoiRecordAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes(StandardCharsets.UTF_8);

    // 12: Referent DOI Name
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME,
            ptr_record));
    // 13: Referent
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        REFERENT_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    return handleRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle)
      throws JsonProcessingException {
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes(StandardCharsets.UTF_8);

    // 14: digitalOrPhysical
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OR_PHYSICAL), handle, DIGITAL_OR_PHYSICAL,
            DIGITAL_OR_PHYSICAL_TESTVAL.getBytes()));

    // 15: specimenHost
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST, ptr_record));

    // 16: In collectionFacility
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(IN_COLLECTION_FACILITY), handle, IN_COLLECTION_FACILITY,
            ptr_record));

    var physicalIdentifier = MAPPER.writeValueAsBytes(PHYSICAL_IDENTIFIER_OBJ);
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PHYSICAL_IDENTIFIER), handle, PHYSICAL_IDENTIFIER,
            physicalIdentifier));
    return handleRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenBotanyAttributes(byte[] handle)
      throws JsonProcessingException {
    List<HandleAttribute> handleRecord = genDigitalSpecimenAttributes(handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE,
        OBJECT_TYPE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRESERVED_OR_LIVING), handle, PRESERVED_OR_LIVING,
            PRESERVED_OR_LIVING_TESTVAL.getBytes()));
    return handleRecord;
  }

  public static <T extends HandleRecordRequest> ObjectNode genCreateRecordRequest(T request,
      String recordType) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode dataNode = mapper.createObjectNode();
    ObjectNode attributeNode = mapper.valueToTree(request);

    if (attributeNode.has("referent")) {
      attributeNode.remove("referent");
    }

    dataNode.put("type", recordType);
    dataNode.set("attributes", attributeNode);
    rootNode.set("data", dataNode);

    return rootNode;
  }

  // Single Requests
  public static HandleRecordRequest genHandleRecordRequestObject() {
    return new HandleRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL);
  }

  public static DoiRecordRequest genDoiRecordRequestObject() {
    return new DoiRecordRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID);
  }

  public static DigitalSpecimenRequest genDigitalSpecimenRequestObject() {
    return new DigitalSpecimenRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY_TESTVAL,
        PHYSICAL_IDENTIFIER_OBJ);
  }

  public static DigitalSpecimenBotanyRequest genDigitalSpecimenBotanyRequestObject() {
    return new DigitalSpecimenBotanyRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY_TESTVAL,
        PHYSICAL_IDENTIFIER_OBJ,
        OBJECT_TYPE_TESTVAL,
        PRESERVED_OR_LIVING_TESTVAL);
  }

  public static MediaObjectRequest genMediaRequestObject() {
    return new MediaObjectRequest(
        PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        MEDIA_HASH_TESTVAL,
        MEDIA_URL_TESTVAL,
        PHYSICAL_IDENTIFIER_OBJ
    );
  }

  public static TombstoneRecordRequest genTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(
        TOMBSTONE_TEXT
    );
  }

  public static ObjectNode givenSearchByPhysIdRequest() {
    var request = MAPPER.createObjectNode();
    var dataNode = MAPPER.createObjectNode();
    var attributeNode = MAPPER.createObjectNode();
    attributeNode.set(PHYSICAL_IDENTIFIER, MAPPER.valueToTree(PHYSICAL_IDENTIFIER_OBJ));
    attributeNode.set(SPECIMEN_HOST, MAPPER.valueToTree(SPECIMEN_HOST_TESTVAL));
    dataNode.set(NODE_ATTRIBUTES, attributeNode);
    request.set(NODE_DATA, dataNode);
    return request;
  }

  public static JsonApiWrapperRead givenRecordResponseRead(List<byte[]> handles, String path,
      String recordType)
      throws JsonProcessingException {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }

    var responseLink = new JsonApiLinks(path);
    return new JsonApiWrapperRead(responseLink, dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      String recordType)
      throws JsonProcessingException {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteGeneric(List<byte[]> handles,
      String recordType)
      throws JsonProcessingException {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), "PID",
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      String attributeType, String recordType)
      throws JsonProcessingException {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(attributeType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles)
      throws Exception {
    return givenRecordResponseWriteAltLoc(handles, RECORD_TYPE_HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles,
      String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genHandleRecordAttributesAltLoc(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteArchive(List<byte[]> handles)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genTombstoneRecordFullAttributes(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), RECORD_TYPE_TOMBSTONE,
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  private static List<HandleAttribute> genAttributes(String recordType, byte[] handle)
      throws JsonProcessingException {
    switch (recordType) {
      case RECORD_TYPE_HANDLE, "PID" -> {
        return genHandleRecordAttributes(handle);
      }
      case RECORD_TYPE_DOI -> {
        return genDoiRecordAttributes(handle);
      }
      case RECORD_TYPE_DS -> {
        return genDigitalSpecimenAttributes(handle);
      }
      case RECORD_TYPE_DS_BOTANY -> {
        return genDigitalSpecimenBotanyAttributes(handle);
      }
      default -> {
        return null;
      }
    }
  }

  public static List<JsonNode> genUpdateRequestBatch(List<byte[]> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (byte[] handle : handles) {
      requestNodeData.put("type", RECORD_TYPE_HANDLE);
      requestNodeData.put("id", new String(handle, StandardCharsets.UTF_8));
      requestNodeData.set("attributes", genUpdateRequestAltLoc());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }
    return requestNodeList;
  }

  public static List<JsonNode> genTombstoneRequestBatch(List<String> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (String handle : handles) {
      requestNodeData.put("type", RECORD_TYPE_HANDLE);
      requestNodeData.put("id", handle);
      requestNodeData.set("attributes", genTombstoneRequest());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }

    return requestNodeList;
  }

  public static JsonNode genUpdateRequestAltLoc() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.putArray(LOC_REQ).add(LOC_ALT_TESTVAL[0]);
    return rootNode;
  }

  public static JsonNode genTombstoneRequest() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put(TOMBSTONE_TEXT, TOMBSTONE_TEXT_TESTVAL);
    return rootNode;
  }

  // Handle Attributes as ObjectNode 
  public static JsonNode genObjectNodeAttributeRecord(List<HandleAttribute> dbRecord)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode subNode;

    for (HandleAttribute row : dbRecord) {
      String type = row.type();
      String data = new String(row.data());
      if (row.index() == FIELD_IDX.get(HS_ADMIN)) {
        continue; // We never want HS_ADMIN in our json
      }
      if (FIELD_IS_PID_RECORD.contains(type) || type.equals(PHYSICAL_IDENTIFIER)) {
        subNode = mapper.readValue(data, ObjectNode.class);
        rootNode.set(type, subNode);
      } else {
        rootNode.put(type, data);
      }
    }
    return rootNode;
  }

  // Other Functions

  public static byte[] setLocations(String[] objectLocations)
      throws TransformerException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    for (int i = 0; i < objectLocations.length; i++) {

      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      locs.setAttribute("weight", "0");
      locations.appendChild(locs);
    }
    return documentToString(doc).getBytes(StandardCharsets.UTF_8);
  }

  private static String documentToString(Document document) throws TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

}
