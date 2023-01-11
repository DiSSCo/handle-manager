package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.*;
import static eu.dissco.core.handlemanager.utils.Resources.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiData;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.*;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

  private static final String INVALID_FIELD_ERROR = "Invalid request. Attempting to add forbidden fields to record type %s. Forbidden field: %s";
  private static final String INVALID_TYPE_ERROR = "Invalid request. Reason: unrecognized type. Check: ";

  // Resolve Record

  public JsonApiWrapper resolveSingleRecord(byte[] handle)
      throws PidResolutionException {

    var recordAttributes = getRecord(handle);

    JsonApiData jsonData = new JsonApiData(new String(handle), "PID", recordAttributes);
    String pidLink = recordAttributes.get(PID).asText();
    JsonApiLinks links = new JsonApiLinks(pidLink);
    return new JsonApiWrapper(links, jsonData);
  }


  private JsonNode getRecord(byte[] handle) throws PidResolutionException {
    var dbRecord = handleRep.resolveHandleAttributes(handle);
    if (dbRecord.isEmpty()){
      throw new PidResolutionException("Unable to resolve handle");
    }
    return jsonFormatSingleRecord(dbRecord);
  }

  public List<JsonNode> fetchResolvedRecords(List<byte[]> handles) throws PidResolutionException {
    var dbRecord = handleRep.resolveHandleAttributes(handles);
    var handleMap = mapRecords(dbRecord);
    Set<byte[]> resolvedHandles = new HashSet<>();

    List<JsonNode> rootNodeList = new ArrayList<>();

    for (Map.Entry<String, List<HandleAttribute>> handleRecord : handleMap.entrySet()) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord.getValue()));
      resolvedHandles.add(handleRecord.getValue().get(0).handle());
    }

    if (handles.size() > resolvedHandles.size()){
      handles.forEach(resolvedHandles::remove); // Removes handles from resolved handle list, now it only contains unresolved handles

      Set<String> unresolvedHandles = new HashSet<>();
      for (byte[] handle : resolvedHandles){
        unresolvedHandles.add(new String(handle));
      }
      throw new PidResolutionException("Unable to resolve the following handles: " + unresolvedHandles );
    }
    return rootNodeList;
  }

  private HashMap<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {

    HashMap<String, List<HandleAttribute>> handleMap = new HashMap<>();

    for (HandleAttribute row : flatList) {
      String handle = new String(row.handle());
      if (handleMap.containsKey(handle)) {
        List<HandleAttribute> tmpList = new ArrayList<>(handleMap.get(handle));
        tmpList.add(row);
        handleMap.replace(handle, tmpList);
      } else {
        handleMap.put(handle, List.of(row));
      }
    }
    return handleMap;
  }

  public List<JsonApiWrapper> resolveBatchRecord(List<byte[]> handles)
      throws PidResolutionException {
    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    var recordAttributeList = fetchResolvedRecords(handles);

    for (JsonNode recordAttributes : recordAttributeList) {
      wrapperList.add(wrapResponse(recordAttributes, "PID"));
    }
    return wrapperList;
  }

  private String getPidName(String pidLink) {
    return pidLink.substring(pidLink.length() - 24);
  }

  // Batch Creation Json

  public List<JsonApiWrapper> updateRecordBatch(List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {
    var recordTimestamp = Instant.now();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    List<String> recordTypes = new ArrayList<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      String recordType = data.get(NODE_TYPE).asText();
      recordTypes.add(recordType);

      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);

      var keys = getKeys(requestAttributes);
      validateRequestData(recordType, keys);
      if (keys.contains(LOC_REQ)) {
        requestAttributes = setLocationFromJson(requestAttributes);
      }
      attributesToUpdate.add(prepareUpdateAttributes(handle, requestAttributes));
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.updateRecordBatch(recordTimestamp, attributesToUpdate);
    var updatedRecords = fetchResolvedRecords(handles);

    List<JsonApiWrapper> wrapperList = new ArrayList<>();
    int i = 0;

    for (JsonNode updatedRecord : updatedRecords) {
      String pidLink = updatedRecord.get(PID).asText();
      String pidName = getPidName(pidLink);
      JsonApiData jsonData = new JsonApiData(pidName, recordTypes.get(i++), updatedRecord);
      JsonApiLinks links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }



  public JsonApiWrapper updateRecord(JsonNode request, byte[] handle, String recordType)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    var recordTimestamp = Instant.now();

    List<String> keys = getKeys(request);
    validateRequestData(recordType, keys);
    if (keys.contains(LOC_REQ)) {
      request = setLocationFromJson(request);
    }
    checkHandlesWritable(List.of(handle));
    List<HandleAttribute> attributesToUpdate = prepareUpdateAttributes(handle, request);

    // Update record
    handleRep.updateRecord(recordTimestamp, attributesToUpdate);
    var updatedRecord = getRecord(handle);

    // Package response
    JsonApiData jsonData = new JsonApiData(new String(handle), recordType, updatedRecord);
    JsonApiLinks links = new JsonApiLinks(updatedRecord.get(PID).asText());
    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createRecord(JsonNode request) throws InvalidRecordInput, PidResolutionException, PidServiceInternalError, UnrecognizedPropertyException {
    byte[] handle = hf.genHandleList(1).get(0);
    var recordTimestamp = Instant.now();
    ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
    String type = dataNode.get(NODE_TYPE).asText();
    List<HandleAttribute> newRecord;

    try {
      switch (type) {
        case RECORD_TYPE_HANDLE -> {
          HandleRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
              HandleRecordRequest.class);
          newRecord = prepareHandleRecordAttributes(requestObject, handle);
        }
        case RECORD_TYPE_DOI -> {
          DoiRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
              DoiRecordRequest.class);
          newRecord = prepareDoiRecordAttributes(requestObject, handle);
        }
        case RECORD_TYPE_DS -> {
          DigitalSpecimenRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
              DigitalSpecimenRequest.class);
          newRecord = prepareDigitalSpecimenRecordAttributes(requestObject, handle);
        }
        case RECORD_TYPE_DS_BOTANY -> {
          DigitalSpecimenBotanyRequest requestObject = mapper.treeToValue(
              dataNode.get(NODE_ATTRIBUTES), DigitalSpecimenBotanyRequest.class);
          newRecord = prepareDigitalSpecimenBotanyRecordAttributes(requestObject, handle);
        }
        default -> throw new InvalidRecordInput(
            INVALID_TYPE_ERROR + type);
      }
    }
    catch (UnrecognizedPropertyException e){
      throw e;
    }
    catch (JsonProcessingException e) {
      throw new InvalidRecordInput(
          "An error has occurred parsing a record in request. More information: "
              + e.getMessage());
    }

    handleRep.postAttributesToDb(recordTimestamp, newRecord);
    var postedRecordAttributes = getRecord(handle);

    return wrapResponse(postedRecordAttributes, type);

  }

  public List<JsonApiWrapper> createRecordBatch(List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {
    var recordTimestamp = Instant.now();
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<byte[]> handlesPost = new ArrayList<>(handles);
    List<HandleAttribute> handleAttributes = new ArrayList<>();

    for (var request : requests) {
      ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
      String type = dataNode.get(NODE_TYPE).asText();
      try {
        switch (type) {
          case RECORD_TYPE_HANDLE -> {
            HandleRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                HandleRecordRequest.class);

            handleAttributes.addAll(
                prepareHandleRecordAttributes(requestObject, handles.remove(0)));
          }
          case RECORD_TYPE_DOI -> {
            DoiRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DoiRecordRequest.class);
            handleAttributes.addAll(prepareDoiRecordAttributes(requestObject, handles.remove(0)));
          }
          case RECORD_TYPE_DS -> {
            DigitalSpecimenRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DigitalSpecimenRequest.class);
            handleAttributes.addAll(
                prepareDigitalSpecimenRecordAttributes(requestObject, handles.remove(0)));
          }
          case RECORD_TYPE_DS_BOTANY -> {
            DigitalSpecimenBotanyRequest requestObject = mapper.treeToValue(
                dataNode.get(NODE_ATTRIBUTES), DigitalSpecimenBotanyRequest.class);
            handleAttributes.addAll(
                prepareDigitalSpecimenBotanyRecordAttributes(requestObject, handles.remove(0)));
          }
          default -> throw new InvalidRecordInput(
              INVALID_TYPE_ERROR + type);
        }
      } catch (JsonProcessingException e) {
        throw new InvalidRecordInput(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }

    handleRep.postAttributesToDb(recordTimestamp, handleAttributes);
    var postedRecordAttributes = fetchResolvedRecords(handlesPost);

    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (JsonNode recordAttributes : postedRecordAttributes) {
      wrapperList.add(wrapResponse(recordAttributes, "PID"));
    }
    return wrapperList;
  }


  private JsonApiWrapper wrapResponse(JsonNode recordAttributes, String recordType) {
    String pidLink = recordAttributes.get(PID).asText();
    String pidName = getPidName(pidLink);
    var jsonData = new JsonApiData(pidName, recordType, recordAttributes);
    var links = new JsonApiLinks(pidLink);
    return new JsonApiWrapper(links, jsonData);
  }

  // Create Single Record

  // Json
  public JsonApiWrapper createHandleRecordJson(HandleRecordRequest request)
      throws PidResolutionException, PidServiceInternalError {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareHandleRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

    handleRep.postAttributesToDb(recordTimestamp, handleRecord);
    var postedRecordAttributes = getRecord(handle);

    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_HANDLE,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(postedRecordAttributes.get(PID).asText());

    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDoiRecordJson(DoiRecordRequest request)
      throws PidResolutionException, PidServiceInternalError {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDoiRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

    handleRep.postAttributesToDb(recordTimestamp, handleRecord);
    var postedRecordAttributes = getRecord(handle);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DOI,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(postedRecordAttributes.get(PID).asText());
    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDigitalSpecimenJson(DigitalSpecimenRequest request)
      throws PidResolutionException, PidServiceInternalError {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);
    var recordTimestamp = Instant.now();

   handleRep.postAttributesToDb(recordTimestamp, handleRecord);
   var postedRecordAttributes = getRecord(handle);
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DS,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(postedRecordAttributes.get(PID).asText());
    return new JsonApiWrapper(links, jsonData);
  }

  public JsonApiWrapper createDigitalSpecimenBotanyJson(DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, PidServiceInternalError {
    byte[] handle = hf.genHandleList(1).get(0);
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenBotanyRecordAttributes(request,
        handle);
    var recordTimestamp = Instant.now();

    handleRep.postAttributesToDb(recordTimestamp, handleRecord);
    var postedRecordAttributes = getRecord(handle);

    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_DS_BOTANY,
        postedRecordAttributes);
    JsonApiLinks links = new JsonApiLinks(postedRecordAttributes.get(PID).asText());
    return new JsonApiWrapper(links, jsonData);
  }

  // Update

  public List<JsonApiWrapper> archiveRecordBatch(List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException {
    var recordTimestamp = Instant.now();
    List<byte[]> handles = new ArrayList<>();
    List<HandleAttribute> archiveAttributes = new ArrayList<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      List<String> keys = getKeys(requestAttributes);
      validateRequestData(RECORD_TYPE_TOMBSTONE, keys);

      archiveAttributes.addAll(prepareUpdateAttributes(handle, requestAttributes));
      archiveAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
          "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.archiveRecords(recordTimestamp, archiveAttributes, handles);
    var archivedRecords = fetchResolvedRecords(handles);

    List<JsonApiWrapper> wrapperList = new ArrayList<>();

    for (JsonNode updatedRecord : archivedRecords) {
      String pidLink = updatedRecord.get(PID).asText();
      String pidName = getPidName(pidLink);
      JsonApiData jsonData = new JsonApiData(pidName, RECORD_TYPE_TOMBSTONE, updatedRecord);
      JsonApiLinks links = new JsonApiLinks(pidLink);
      wrapperList.add(new JsonApiWrapper(links, jsonData));
    }
    return wrapperList;
  }

  public JsonApiWrapper archiveRecord(JsonNode request, byte[] handle)
      throws InvalidRecordInput, PidResolutionException {
    var recordTimestamp = Instant.now();
    List<String> keys = getKeys(request);

    validateRequestData(RECORD_TYPE_TOMBSTONE, keys);
    checkHandlesWritable(List.of(handle));

    List<HandleAttribute> tombstoneAttributes = prepareUpdateAttributes(handle, request);

    tombstoneAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    handleRep.archiveRecord(recordTimestamp, tombstoneAttributes);

    var archivedRecord = getRecord(handle);

    // Package response
    JsonApiData jsonData = new JsonApiData(new String(handle), RECORD_TYPE_TOMBSTONE,
        archivedRecord);
    JsonApiLinks links = new JsonApiLinks(archivedRecord.get(PID).asText());
    return new JsonApiWrapper(links, jsonData);
  }


  private List<String> getKeys(JsonNode request){
    List<String> keys = new ArrayList<>();
    Iterator<String> fieldItr = request.fieldNames();
    fieldItr.forEachRemaining(keys::add);
    return keys;
  }

  private void validateRequestData(String requestRecordType, List<String> keys)
      throws InvalidRecordInput {

    // Data Ingestion Checks
    Set<String> requestRecordFields = getRecordFields(requestRecordType);
    checkFields(keys, requestRecordFields, requestRecordType);
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
        return DIGITAL_SPECIMEN_REQ;
      }
      case RECORD_TYPE_DS_BOTANY -> {
        return DIGITAL_SPECIMEN_BOTANY_REQ;
      }
      case RECORD_TYPE_TOMBSTONE -> {
        return TOMBSTONE_RECORD_FIELDS;
      }
      default -> throw new InvalidRecordInput("Invalid request. Reason: unknown record type.");
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

  private void checkHandlesWritable(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);

    Set<byte[]> handlesExist = new HashSet<>(handleRep.checkHandlesWritable(handles));
    if (handlesExist.size() < handles.size()) {
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = new HashSet<>();
      for (byte[] handle : handlesToUpdate) {
        handlesDontExist.add(new String(handle));
      }
      throw new PidResolutionException(
          "INVALID INPUT. One or more handles in request do not exist or are archived. Verify the following handle(s): "
              + handlesDontExist);
    }
  }

  private void checkInternalDuplicates(List<byte[]> handles) throws InvalidRecordInput {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);
    Set<String> handlesToUpdateStr = new HashSet<>();
    for (byte[] handle : handlesToUpdate) {
      handlesToUpdateStr.add(new String(handle));
    }

    if (handlesToUpdateStr.size() < handles.size()) {
      Set<String> duplicateHandles = findDuplicates(handles, handlesToUpdateStr);
      throw new InvalidRecordInput(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }


  private Set<String> findDuplicates(List<byte[]> handles, Set<String> handlesToUpdate) {
    Set<String> duplicateHandles = new HashSet<>();
    for (byte[] handle : handles) {
      if (!handlesToUpdate.add(new String(handle))) {
        duplicateHandles.add(new String(handle));
      }
    }
    return duplicateHandles;
  }

  private JsonNode setLocationFromJson(JsonNode request) throws PidServiceInternalError {
    ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
    });
    JsonNode locNode = request.get(LOC_REQ);
    ObjectNode requestObjectNode = request.deepCopy();
    if (locNode.isArray()) {
      try {
        List<String> locList = reader.readValue(locNode);
        String[] locArr = locList.toArray(new String[0]);
        requestObjectNode.put(LOC, new String(setLocations(locArr)));
        requestObjectNode.remove(LOC_REQ);
      } catch (IOException e) {
        throw new PidServiceInternalError(
            "An error has occurred parsing \"locations\" array. " + e.getMessage(), e);
      }

    }
    return requestObjectNode;
  }


  private List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode request)
      throws PidResolutionException {
    Map<String, String> updateRecord = mapper.convertValue(request,
        new TypeReference<Map<String, String>>() {
        });
    List<HandleAttribute> attributesToUpdate = new ArrayList<>();

    for (Map.Entry<String, String> requestField : updateRecord.entrySet()) {
      String type = requestField.getKey().replace("Pid", "");
      byte[] data = requestField.getValue().getBytes(StandardCharsets.UTF_8);
      byte[] pidData;

      // Resolve data if it's a pid
      if (FIELD_IS_PID_RECORD.contains(type)) {
        pidData = (pidTypeService.resolveTypePid(new String(data))).getBytes(
            StandardCharsets.UTF_8);
        data = pidData;
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
      throws PidResolutionException, PidServiceInternalError {
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
      throws PidResolutionException, PidServiceInternalError {
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
      throws PidResolutionException, PidServiceInternalError {
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
      throws PidResolutionException, PidServiceInternalError {
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
      throws PidServiceInternalError {

    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new PidServiceInternalError(e.getMessage(), e);
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQ);
    doc.appendChild(locations);
    for (int i = 0; i < objectLocations.length; i++) {

      var locs = doc.createElement("location");
      locs.setAttribute(NODE_ID, String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      locs.setAttribute("weight", "0");
      locations.appendChild(locs);
    }
    try {
      return documentToString(doc).getBytes(StandardCharsets.UTF_8);
    } catch (TransformerException e) {
      throw new PidServiceInternalError("An internal error has occurred parsing location data", e);
    }
  }

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }




  private JsonNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode subNode;
    String data;
    String type;
    for (HandleAttribute row : dbRecord) {
      type = row.type();
      data = new String(row.data());
      if (FIELD_IS_PID_RECORD.contains(type)) {
        try {
          subNode = mapper.readValue(data, ObjectNode.class);
          rootNode.set(type, subNode);
        } catch (JsonProcessingException e) {
          // Not 100% sure if an exception should be thrown here. We don't want to make a poorly formatted record un-resolvable
          log.warn("Type \"{}\" is noncompliant to the PID kernel model. Invalid data: {}", type,
              data);
        }
      } else {
        rootNode.put(type, data);
      }
    }
    return rootNode;
  }


}

