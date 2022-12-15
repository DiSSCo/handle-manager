package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.PidRecords.*;
import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiData;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
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
  public static final String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";
  public static final List<String> HANDLE_LIST_STR;

  static {
    HANDLE_LIST_STR = List.of(HANDLE, HANDLE_ALT);
  }

  // Request Vars
  // Handles

  public static final String PID_ISSUER_PID = "20.5000.1025/PID-ISSUER";
  public static final String DIGITAL_OBJECT_TYPE_PID = "20.5000.1025/DIGITAL-SPECIMEN";
  public static final String DIGITAL_OBJECT_SUBTYPE_PID = "20.5000.1025/BOTANY-SPECIMEN";
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String[] LOC_ALT_TESTVAL = {"naturalis.nl"};
  public static final String PID_STATUS_TESTVAL = "TEST";
  public static final String PID_KERNEL_METADATA_LICENSE_TESTVAL = "https://creativecommons.org/publicdomain/zero/1.0/";
  //DOIs
  public static final String REFERENT_DOI_NAME_PID = "20.5000.1025/OTHER-TRIPLET";
  public static final String REFERENT_TESTVAL = "";
  //Digital Specimens
  public static final String DIGITAL_OR_PHYSICAL_TESTVAL = "physical";
  public static final String SPECIMEN_HOST_PID = "20.5000.1025/OTHER-TRIPLET";
  public static final String IN_COLLECTION_FACILITY_TESTVAL = "20.5000.1025/OTHER-TRIPLET";

  //Botany Specimens
  public static final String OBJECT_TYPE_TESTVAL = "Herbarium Sheet";
  public static final String PRESERVED_OR_LIVING_TESTVAL = "preserved";

  // Pid Type Record vals
  public static final String PTR_PID = "http://hdl.handle.net/" + PID_ISSUER_PID;
  public static final String PTR_TYPE = "handle";
  public static final String PTR_PRIMARY_NAME = "DiSSCo";
  public static final String PTR_PID_DOI = "http://doi.org/" + PID_ISSUER_PID;
  public static final String PTR_TYPE_DOI = "doi";
  public static final String PTR_REGISTRATION_DOI_NAME = "Registration Agency";
  public final static String PTR_HANDLE_RECORD = genPtrHandleRecord(false);
  public final static String PTR_DOI_RECORD = genPtrHandleRecord(true);

  // Tombstone Record vals
  public final static String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";


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
      objectNode.put("primaryNamefromPid", PTR_PRIMARY_NAME);
      objectNode.put("registrationAgencyDoiName", PTR_REGISTRATION_DOI_NAME);
    } else {
      objectNode.put("pid", PTR_PID);
      objectNode.put("pidType", PTR_TYPE);
      objectNode.put("primaryNamefromPid", PTR_PRIMARY_NAME);
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

    // 100: Admin Handle
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

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

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes(StandardCharsets.UTF_8);

    // 14: digitalOrPhysical
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OR_PHYSICAL), handle, DIGITAL_OR_PHYSICAL,
            DIGITAL_OR_PHYSICAL_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 15: specimenHost
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST, ptr_record));

    // 16: In collectionFacility
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(IN_COLLECTION_FACILITY), handle, IN_COLLECTION_FACILITY,
            ptr_record));
    return handleRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenBotanyAttributes(byte[] handle) {
    List<HandleAttribute> handleRecord = genDigitalSpecimenAttributes(handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE,
        OBJECT_TYPE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRESERVED_OR_LIVING), handle, PRESERVED_OR_LIVING,
            PRESERVED_OR_LIVING_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    return handleRecord;
  }
  public static <T extends HandleRecordRequest> ObjectNode genCreateRecordRequest(T request,
      String recordType) {
    ObjectMapper mapper = new ObjectMapper();

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
        IN_COLLECTION_FACILITY_TESTVAL);
  }

  public static DigitalSpecimenBotanyRequest genDigitalSpecimenBotanyRequestObject() {
    return new DigitalSpecimenBotanyRequest(PID_ISSUER_PID,
        DIGITAL_OBJECT_TYPE_PID,
        DIGITAL_OBJECT_SUBTYPE_PID,
        LOC_TESTVAL,
        REFERENT_DOI_NAME_PID,
        DIGITAL_OR_PHYSICAL_TESTVAL,
        SPECIMEN_HOST_PID,
        IN_COLLECTION_FACILITY_TESTVAL,
        OBJECT_TYPE_TESTVAL,
        PRESERVED_OR_LIVING_TESTVAL);
  }

  // Batch Requests

  public static List<HandleRecordRequest> genHandleRecordRequestBatch(List<byte[]> handles) {
    List<HandleRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requestList.add(genDoiRecordRequestObject());
    }
    return requestList;
  }

  public static List<DoiRecordRequest> genDoiRecordRequestBatch(List<byte[]> handles) {
    List<DoiRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requestList.add(genDoiRecordRequestObject());
    }
    return requestList;
  }

  public static List<DigitalSpecimenRequest> genDigitalSpecimenRequestBatch(List<byte[]> handles) {
    List<DigitalSpecimenRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requestList.add(genDigitalSpecimenRequestObject());
    }
    return requestList;
  }

  public static List<DigitalSpecimenBotanyRequest> genDigitalSpecimenBotanyRequestBatch(
      List<byte[]> handles) {
    List<DigitalSpecimenBotanyRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requestList.add(genDigitalSpecimenBotanyRequestObject());
    }
    return requestList;
  }

  // Single JsonApiWrapper Response 
  public static JsonApiWrapper genHandleRecordJsonResponse(byte[] handle)
      throws JsonProcessingException {
    var testDbRecord = genHandleRecordAttributes(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, RECORD_TYPE_HANDLE);
  }

  public static JsonApiWrapper genHandleRecordJsonResponse(byte[] handle, String recordType)
      throws JsonProcessingException {
    var testDbRecord = genHandleRecordAttributes(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, recordType);
  }

  public static JsonApiWrapper genDoiRecordJsonResponse(byte[] handle, String recordType)
      throws JsonProcessingException {
    var testDbRecord = genDoiRecordAttributes(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, recordType);
  }

  public static JsonApiWrapper genDigitalSpecimenJsonResponse(byte[] handle, String recordType)
      throws JsonProcessingException {
    var testDbRecord = genDigitalSpecimenAttributes(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, recordType);
  }

  public static JsonApiWrapper genDigitalSpecimenBotanyJsonResponse(byte[] handle, String recordType)
      throws JsonProcessingException {
    var testDbRecord = genDigitalSpecimenBotanyAttributes(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, recordType);
  }

  public static JsonApiWrapper genGenericRecordJsonResponse(byte[] handle,
      List<HandleAttribute> testDbRecord, String recordType)
      throws JsonProcessingException {
    JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);
    JsonApiData jsonData = new JsonApiData(new String(handle), recordType, recordAttributes);
    JsonApiLinks links = new JsonApiLinks(recordAttributes.get("pid").asText());
    return new JsonApiWrapper(links, jsonData);
  }

  // Batch JsonApiWrapper Response
  public static List<JsonApiWrapper> genHandleRecordJsonResponseBatch(List<byte[]> handles)
      throws JsonProcessingException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genHandleRecordJsonResponse(handle));
    }
    return wrapperList;
  }

  public static List<JsonApiWrapper> genHandleRecordJsonResponseBatch(List<byte[]> handles,
      String recordType)
      throws JsonProcessingException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genHandleRecordJsonResponse(handle, recordType));
    }
    return wrapperList;
  }

  public static List<JsonApiWrapper> genDoiRecordJsonResponseBatch(List<byte[]> handles, String recordType)
      throws JsonProcessingException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genDoiRecordJsonResponse(handle, recordType));
    }
    return wrapperList;
  }

  public static List<JsonApiWrapper> genDigitalSpecimenJsonResponseBatch(List<byte[]> handles, String recordType)
      throws JsonProcessingException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genDigitalSpecimenJsonResponse(handle, recordType));
    }
    return wrapperList;
  }

  public static List<JsonApiWrapper> genDigitalSpecimenBotanyJsonResponseBatch(List<byte[]> handles, String recordType)
      throws JsonProcessingException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genDigitalSpecimenBotanyJsonResponse(handle, recordType));
    }
    return wrapperList;
  }

  // Update test functions
  public static List<JsonApiWrapper> genUpdateAltLocResponseBatch(List<byte[]> handles)
      throws JsonProcessingException, ParserConfigurationException, TransformerException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      wrapperList.add(genHandleRecordJsonResponseAltLoc(handle));
    }
    return wrapperList;
  }

  public static List<JsonApiWrapper> genTombstoneResponseBatch(List<byte[]> handles)
      throws JsonProcessingException, ParserConfigurationException, TransformerException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (byte[] handle : handles) {
      var tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);
      var singleTombstoneRecordResponse = genGenericRecordJsonResponse(handle,
          tombstoneAttributesFull, RECORD_TYPE_TOMBSTONE);
      wrapperList.add(singleTombstoneRecordResponse);
    }
    return wrapperList;
  }

  // Update Requests

  public static List<JsonNode> genUpdateRequestBatch(List<byte[]> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (byte[] handle : handles) {
      requestNodeData.put("type", RECORD_TYPE_HANDLE);
      requestNodeData.put("id", new String(handle));
      requestNodeData.set("attributes", genUpdateRequestAltLoc());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }
    return requestNodeList;
  }

  public static List<JsonNode> genTombstoneRequestBatch() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (String handle : HANDLE_LIST_STR) {
      requestNodeData.put("type", RECORD_TYPE_TOMBSTONE);
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

  public static JsonApiWrapper genHandleRecordJsonResponseAltLoc(byte[] handle)
      throws JsonProcessingException, ParserConfigurationException, TransformerException {
    var testDbRecord = genHandleRecordAttributesAltLoc(handle);
    return genGenericRecordJsonResponse(handle, testDbRecord, RECORD_TYPE_HANDLE);
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
      if (FIELD_IS_PID_RECORD.contains(type)) {
        subNode = mapper.readValue(data, ObjectNode.class);
        rootNode.set(type, subNode);
      }
      else {
        rootNode.put(type, data);
      }
    }
    return rootNode;
  }

  public static List<JsonNode> genObjectNodeRecordBatch(List<List<HandleAttribute>> dbRecords)
      throws JsonProcessingException {
    List<JsonNode> nodeList = new ArrayList<>();
    for (List<HandleAttribute> dbRecord : dbRecords) {
      nodeList.add(genObjectNodeAttributeRecord(dbRecord));
    }
    return nodeList;
  }

  // Other Functions

  private static String getLocString() {
    byte[] loc = "".getBytes(StandardCharsets.UTF_8);
    try {
      loc = setLocations(LOC_TESTVAL);
    } catch (TransformerException | ParserConfigurationException e) {
      e.printStackTrace();
    }
    return new String(loc);
  }

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
