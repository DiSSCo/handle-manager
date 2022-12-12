package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.*;
import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiData;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.*;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private final HandleRepository handleRep;
  private final PidTypeService pidTypeService;
  private final HandleGeneratorService hf;
  private final DocumentBuilderFactory dbf;
  private final ObjectMapper mapper;
  private final TransformerFactory tf;

  private final String INVALID_FIELD_ERROR = "Invalid request. Attempting to add forbidden fields to record type %s. Forbidden field: %s";

  public JsonApiWrapper resolveSingleRecord(byte[] handle)
      throws JsonProcessingException, PidResolutionException {
    ObjectNode recordAttributes = handleRep.resolveSingleRecord(handle);
    JsonApiData jsonData = new JsonApiData(new String(handle), "PID", recordAttributes);
    String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
    JsonApiLinks links = new JsonApiLinks(pidLink);
    return new JsonApiWrapper(links, jsonData);
  }

  public List<JsonApiWrapper> resolveBatchRecord(List<byte[]> handles)
      throws JsonProcessingException, PidResolutionException {
    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();
    var recordAttributeList = handleRep.resolveBatchRecord(handles);

    for (ObjectNode recordAttributes : recordAttributeList) {
      String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
      String pidName = getPidName(pidLink);
      jsonData = new JsonApiData(pidName, "PID", recordAttributes);
      links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  private String getPidName(String pidLink) {
    return pidLink.substring(pidLink.length() - 25, pidLink.length() - 1);
  }

  // Batch Creation Json

  public List<JsonApiWrapper> createHandleRecordBatchJson(List<HandleRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(prepareHandleRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    List<ObjectNode> postedRecordAttributes = handleRep.createRecordBatchJson(handles,
        recordTimestamp, handleAttributes);

    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (ObjectNode recordAttributes : postedRecordAttributes) {
      String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
      String pidName = getPidName(pidLink);
      jsonData = new JsonApiData(pidName, RECORD_TYPE_HANDLE, recordAttributes);
      links = new JsonApiLinks(pidLink);

      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public List<JsonApiWrapper> createDoiRecordBatchJson(List<DoiRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(prepareDoiRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    List<ObjectNode> postedRecordAttributes = handleRep.createRecordBatchJson(handles,
        recordTimestamp, handleAttributes);

    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (ObjectNode recordAttributes : postedRecordAttributes) {
      String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
      String pidName = getPidName(pidLink);
      jsonData = new JsonApiData(pidName, RECORD_TYPE_DOI, recordAttributes);
      links = new JsonApiLinks(pidLink);

      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public List<JsonApiWrapper> createDigitalSpecimenBatchJson(List<DigitalSpecimenRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(
          prepareDigitalSpecimenRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    List<ObjectNode> postedRecordAttributes = handleRep.createRecordBatchJson(handles,
        recordTimestamp, handleAttributes);

    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (ObjectNode recordAttributes : postedRecordAttributes) {
      String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
      String pidName = getPidName(pidLink);
      jsonData = new JsonApiData(pidName, RECORD_TYPE_DS, recordAttributes);
      links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public List<JsonApiWrapper> createDigitalSpecimenBotanyBatchJson(
      List<DigitalSpecimenBotanyRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      handleAttributes.addAll(
          prepareDigitalSpecimenBotanyRecordAttributes(requests.get(i), handles.get(i)));
    }
    var recordTimestamp = Instant.now();
    List<ObjectNode> postedRecordAttributes = handleRep.createRecordBatchJson(
        handles, recordTimestamp, handleAttributes);

    JsonApiData jsonData;
    JsonApiLinks links;
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (ObjectNode recordAttributes : postedRecordAttributes) {
      String pidLink = mapper.writeValueAsString(recordAttributes.get("pid"));
      String pidName = getPidName(pidLink);
      jsonData = new JsonApiData(pidName, RECORD_TYPE_DS_BOTANY, recordAttributes);
      links = new JsonApiLinks(pidLink);

      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  // Create Single Record

  // Json
  public JsonApiWrapper createHandleRecordJson(HandleRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

    ObjectNode postedRecordAttributes = handleRep.createRecord(handle, recordTimestamp,
        handleRecord);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_HANDLE,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(postedRecordAttributes.get("pid")));

    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDoiRecordJson(DoiRecordRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

    ObjectNode postedRecordAttributes = handleRep.createRecord(handle, recordTimestamp,
        handleRecord);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DOI,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(postedRecordAttributes.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDigitalSpecimenJson(DigitalSpecimenRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

    ObjectNode postedRecordAttributes = handleRep.createRecord(handle, recordTimestamp,
        handleRecord);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DS,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(postedRecordAttributes.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDigitalSpecimenBotanyJson(DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenBotanyRecordAttributes(request,
        handle);
    var recordTimestamp = Instant.now();

    ObjectNode postedRecordAttributes = handleRep.createRecord(handle,
        recordTimestamp, handleRecord);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DS_BOTANY,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(postedRecordAttributes.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }

  // Update
  public List<JsonApiWrapper> updateRecordBatch(List<ObjectNode> requests)
      throws InvalidRecordInput, PidResolutionException, ParserConfigurationException, IOException, TransformerException {
    var recordTimestamp = Instant.now();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    List<String> recordTypes = new ArrayList<>();

    for (JsonNode root : requests){
      JsonNode data = root.get("data");
      JsonNode requestAttributes = data.get("attributes");
      String recordType = data.get("type").asText();
      recordTypes.add(recordType);
      byte[] handle = data.get("id").asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      requestAttributes = validateRequestData(requestAttributes, recordType);
      attributesToUpdate.add(prepareUpdateAttributes(handle, requestAttributes));
    }
    checkInternalDuplicates(handles);
    checkHandlesExist(handles);

    List<ObjectNode> updatedRecords = handleRep.updateRecordBatch(handles, recordTimestamp, attributesToUpdate);

    List<JsonApiWrapper> wrapperList = new ArrayList<>();
    int i = 0;

    for (ObjectNode updatedRecord : updatedRecords) {
      String pidLink = mapper.writeValueAsString(updatedRecord.get("pid"));
      String pidName = getPidName(pidLink);
      JsonApiData jsonData = new JsonApiData(pidName, recordTypes.get(i++), updatedRecord);
      JsonApiLinks links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public List<JsonApiWrapper> archiveRecordBatch(List<ObjectNode> requests)
      throws InvalidRecordInput, PidResolutionException, ParserConfigurationException, IOException, TransformerException, PidCreationException {
    var recordTimestamp = Instant.now();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToArchive = new ArrayList<>();
    List<HandleAttribute> newFields = new ArrayList<>();
    for (JsonNode root : requests){
      JsonNode data = root.get("data");
      JsonNode requestAttributes = data.get("attributes");
      byte[] handle = data.get("id").asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      requestAttributes = validateRequestData(requestAttributes, RECORD_TYPE_TOMBSTONE);
      List<HandleAttribute> singleTombstoneRecord = prepareUpdateAttributes(handle, requestAttributes);
      newFields.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS, "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
      attributesToArchive.add(new ArrayList<>(singleTombstoneRecord));
    }
    checkInternalDuplicates(handles);
    checkHandlesExist(handles);

    checkInternalDuplicates(handles);
    checkHandlesExist(handles);

    handleRep.updateRecordBatch(handles, recordTimestamp, attributesToArchive);
    //handleRep.createRecordBatchJson(handles, recordTimestamp, newFields);
    var archivedRecords = handleRep.resolveBatchRecord(handles);

    List<JsonApiWrapper> wrapperList = new ArrayList<>();
    int i = 0;

    for (ObjectNode updatedRecord : archivedRecords) {
      String pidLink = mapper.writeValueAsString(updatedRecord.get("pid"));
      String pidName = getPidName(pidLink);
      JsonApiData jsonData = new JsonApiData(pidName, RECORD_TYPE_TOMBSTONE, updatedRecord);
      JsonApiLinks links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public JsonApiWrapper archiveRecord(JsonNode request, byte[] handle)
      throws InvalidRecordInput, PidResolutionException, ParserConfigurationException, IOException, TransformerException, PidCreationException {
    var recordTimestamp = Instant.now();
    validateRequestData(request, RECORD_TYPE_TOMBSTONE);
    checkHandlesExist(List.of(handle));

    List<HandleAttribute> tombstoneAttributes = prepareUpdateAttributes(handle, request);

    tombstoneAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS, "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    ObjectNode archivedRecord = handleRep.updateRecord(recordTimestamp, tombstoneAttributes);

    // Package response
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_TOMBSTONE, archivedRecord);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(archivedRecord.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }


  public JsonApiWrapper updateRecord(JsonNode request, byte[] handle, String recordType)
      throws InvalidRecordInput, PidResolutionException, IOException, ParserConfigurationException, TransformerException, PidCreationException {

    var recordTimestamp = Instant.now();

    request = validateRequestData(request, recordType);
    checkHandlesExist(List.of(handle));
    List<HandleAttribute> attributesToUpdate = prepareUpdateAttributes(handle, request);

    // Update record
    ObjectNode updatedRecord = handleRep.updateRecord(recordTimestamp, attributesToUpdate);

    // Package response
    JsonApiData jsonData = new JsonApiData(new String(handle), recordType, updatedRecord);
    JsonApiLinks links = new JsonApiLinks(
        mapper.writeValueAsString(updatedRecord.get("pid")));
    return new JsonApiWrapper(links, jsonData);
  }

  private JsonNode validateRequestData(JsonNode request, String recordType)
      throws ParserConfigurationException, IOException, TransformerException, InvalidRecordInput, PidResolutionException {
    List<String> keys = new ArrayList<>();
    Iterator<String> fieldItr = request.fieldNames();
    fieldItr.forEachRemaining(keys::add);

    JsonNode requestUpdated = request.deepCopy();

    // Data Ingestion Checks
    Set<String> recordFields = getRecordFields(recordType);
    checkFields(keys, recordFields, recordType);

    // Format 10320/loc
    if (keys.contains(LOC_REQ)) {
      requestUpdated = setLocationFromJson(request);
    }
    return requestUpdated;
  }

  private Set<String> getRecordFields(String recordType) throws InvalidRecordInput {
    switch (recordType) {
      case RECORD_TYPE_HANDLE -> {
        return HANDLE_RECORD_REQ;
      }
      case RECORD_TYPE_DOI -> {
        return DOI_RECORD_REQ;
      }
      case RECORD_TYPE_DS -> {
        return  DIGITAL_SPECIMEN_REQ;
      }
      case RECORD_TYPE_DS_BOTANY -> {
        return DIGITAL_SPECIMEN_BOTANY_REQ;
      }
      case RECORD_TYPE_TOMBSTONE -> {
        return TOMBSTONE_RECORD_FIELDS;
      }
      default -> {
        throw new InvalidRecordInput("Invalid request. Reason: unknown record type.");
      }
    }
  }

  private void checkFields(List<String> fields, Set<String> recordFields, String recordType)
      throws InvalidRecordInput {
    List<String> invalidFields = new ArrayList<>();

    for (String field : fields) {
      if (!recordFields.contains(field)) {
        invalidFields.add(field);
      }
    }
    if (!invalidFields.isEmpty()) {
      throw new InvalidRecordInput(String.format(INVALID_FIELD_ERROR, recordType, invalidFields));
    }
  }

  private void checkHandlesExist(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);

    Set<byte[]> handlesExist = new HashSet<>(handleRep.checkHandlesExist(handles));
    if (handlesExist.size() < handles.size()){
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = new HashSet<>();
      for (byte[] handle: handlesToUpdate){
        handlesDontExist.add(new String(handle));
      }
      throw new PidResolutionException("INVALID INPUT. One or more handles in request do not exist. Verify the following handle(s): " + handlesDontExist);
    }
  }

  private void checkInternalDuplicates(List<byte[]> handles) throws InvalidRecordInput {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);
    Set<String> handlesToUpdateStr = new HashSet<>();
    for (byte[] handle : handlesToUpdate){
      handlesToUpdateStr.add(new String(handle));
    }

    if (handlesToUpdateStr.size() < handles.size()) {
      Set<String> duplicateHandles = findDuplicates(handles, handlesToUpdateStr);
      throw new InvalidRecordInput(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }


  private Set<String> findDuplicates(List<byte[]> handles, Set<String> handlesToUpdate){
    Set<String> duplicateHandles = new HashSet<>();
    for(byte[] handle : handles ){
      if (!handlesToUpdate.add(new String(handle))){
        duplicateHandles.add(new String(handle));
      }
    }
    return duplicateHandles;
  }

  private JsonNode setLocationFromJson(JsonNode request)
      throws ParserConfigurationException, TransformerException, IOException {
    ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
    });
    JsonNode locNode = request.get(LOC_REQ);
    ObjectNode requestObjectNode = request.deepCopy();
    if (locNode.isArray()) {
      List<String> locList = reader.readValue(locNode);
      String[] locArr = locList.toArray(new String[0]);
      requestObjectNode.put(LOC, new String(setLocations(locArr)));
      requestObjectNode.remove(LOC_REQ);
    }
    return requestObjectNode;
  }


  private List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode request)
      throws PidResolutionException, JsonProcessingException {
    Map<String, String> updateRecord = mapper.convertValue(request, new TypeReference<Map<String, String>>() {});
    List<HandleAttribute> attributesToUpdate = new ArrayList<>();

    for (Map.Entry<String, String> requestField : updateRecord.entrySet()) {
      String type = requestField.getKey().replace("Pid", "");
      byte[] data = requestField.getValue().getBytes(StandardCharsets.UTF_8);

      // Resolve data if it's a pid
      if (FIELD_IS_PID_RECORD.contains(type)) {
        data = (pidTypeService.resolveTypePid(new String(data))).getBytes(StandardCharsets.UTF_8);
      }
      attributesToUpdate.add(new HandleAttribute(FIELD_IDX.get(type), handle, type, data));
    }
    return attributesToUpdate;
  }

  // Getters

  public List<String> getHandlesPaged(int pageNum, int pageSize, String pidStatus) {
    return handleRep.getAllHandles(pidStatus.getBytes(StandardCharsets.UTF_8), pageNum, pageSize);
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return handleRep.getAllHandles(pageNum, pageSize);
  }

  // Prepare Attribute lists

  private List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle)).getBytes(StandardCharsets.UTF_8);
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 2: PidIssuer
    String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER,
            pidIssuer.getBytes(StandardCharsets.UTF_8)));

    // 3: Digital Object Type
    String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            digitalObjectType.getBytes(StandardCharsets.UTF_8)));

    // 4: Digital Object Subtype
    String digitalObjectSubtype = pidTypeService.resolveTypePid(
        request.getDigitalObjectSubtypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_SUBTYPE), handle, DIGITAL_OBJECT_SUBTYPE,
            digitalObjectSubtype.getBytes(StandardCharsets.UTF_8)));

    // 5: 10320/loc
    byte[] loc = setLocations(request.getLocations());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 6: Issue Date
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUE_DATE), handle, ISSUE_DATE,
            getDate().getBytes(StandardCharsets.UTF_8)));

    // 7: Issue number
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUE_NUMBER), handle, ISSUE_NUMBER,
            "1".getBytes(StandardCharsets.UTF_8)));

    // 8: PidStatus
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
            "TEST".getBytes(StandardCharsets.UTF_8)));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PID_KERNEL_METADATA_LICENSE), handle,
            PID_KERNEL_METADATA_LICENSE, pidKernelMetadataLicense));

    return handleRecord;
  }


  private List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);

    // 12: Referent DOI Name
    String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiNamePid());

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME,
            referentDoiName.getBytes(StandardCharsets.UTF_8)));

    // 13: Referent -> NOTE: Referent is blank currently until we have a model
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        request.getReferent().getBytes(StandardCharsets.UTF_8)));
    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OR_PHYSICAL), handle, DIGITAL_OR_PHYSICAL,
            request.getDigitalOrPhysical().getBytes(StandardCharsets.UTF_8)));

    // 15: specimenHost
    String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST,
        specimenHost.getBytes(StandardCharsets.UTF_8)));

    // 16: In collectionFacility
    String inCollectionFacility = pidTypeService.resolveTypePid(
        request.getInCollectionFacilityPid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(IN_COLLECTION_FACILITY), handle, IN_COLLECTION_FACILITY,
            inCollectionFacility.getBytes(StandardCharsets.UTF_8)));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request,
      byte[] handle)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);

    // 17: ObjectType
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE,
            request.getObjectType().getBytes(StandardCharsets.UTF_8)));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRESERVED_OR_LIVING), handle, PRESERVED_OR_LIVING,
            request.getPreservedOrLiving().getBytes(StandardCharsets.UTF_8)));

    return handleRecord;
  }

  private String getDate() {
    DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        .withZone(ZoneId.of("UTC"));
    Instant instant = Instant.now();
    return dt.format(instant);
  }

  public byte[] setLocations(String[] objectLocations)
      throws TransformerException, ParserConfigurationException {

    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQ);
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

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }


}

