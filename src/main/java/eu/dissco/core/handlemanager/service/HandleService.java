package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.component.FdoRecordBuilder;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private static final String INVALID_TYPE_ERROR = "Invalid request. Reason: unrecognized type. Check: ";
  private final HandleRepository handleRep;
  private final FdoRecordBuilder fdoRecordBuilder;
  private final HandleGeneratorService hf;
  private final ObjectMapper mapper;

  // Resolve Record
  public JsonApiWrapperReadSingle resolveSingleRecord(byte[] handle, String path)
      throws PidResolutionException {
    var recordAttributeList = resolveAndFormatRecords(List.of(handle)).get(0);
    var dataNode = wrapData(recordAttributeList, "PID");
    var linksNode = new JsonApiLinks(path);
    return new JsonApiWrapperReadSingle(linksNode, dataNode);
  }

  public JsonApiWrapperRead resolveBatchRecord(List<byte[]> handles, String path)
      throws PidResolutionException {
    List<JsonApiDataLinks> dataList = new ArrayList<>();

    var recordAttributeList = resolveAndFormatRecords(handles);
    for (JsonNode recordAttributes : recordAttributeList) {
      dataList.add(wrapData(recordAttributes, "PID"));
    }
    return new JsonApiWrapperRead(new JsonApiLinks(path), dataList);
  }

  private List<JsonNode> resolveAndFormatRecords(List<byte[]> handles)
      throws PidResolutionException {
    var dbRecord = handleRep.resolveHandleAttributes(handles);
    var handleMap = mapRecords(dbRecord);
    Set<String> resolvedHandles = new HashSet<>();

    List<JsonNode> rootNodeList = new ArrayList<>();

    for (var handleRecord : handleMap.entrySet()) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord.getValue()));
      resolvedHandles.add(
          new String(handleRecord.getValue().get(0).handle(), StandardCharsets.UTF_8));
    }

    if (handles.size() > resolvedHandles.size()) {
      Set<String> unresolvedHandles = handles.stream()
          .filter(h -> !resolvedHandles.contains(new String(h, StandardCharsets.UTF_8)))
          .map(h -> new String(h, StandardCharsets.UTF_8)).collect(Collectors.toSet());

      throw new PidResolutionException(
          "Unable to resolve the following handles: " + unresolvedHandles);
    }
    return rootNodeList;
  }

  // Getters

  public List<String> getHandlesPaged(int pageNum, int pageSize, byte[] pidStatus) {
    return handleRep.getAllHandles(pidStatus, pageNum, pageSize);
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return handleRep.getAllHandles(pageNum, pageSize);
  }

  // Response Formatting

  private String getPidName(String pidLink) {
    return pidLink.substring(pidLink.length() - 24);
  }

  private HashMap<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {

    HashMap<String, List<HandleAttribute>> handleMap = new HashMap<>();

    for (HandleAttribute row : flatList) {
      String handle = new String(row.handle(), StandardCharsets.UTF_8);
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

  private JsonApiDataLinks wrapData(JsonNode recordAttributes, String recordType) {
    String pidLink = recordAttributes.get(PID).asText();
    String pidName = getPidName(pidLink);
    var handleLink = new JsonApiLinks(pidLink);
    return new JsonApiDataLinks(pidName, recordType, recordAttributes, handleLink);
  }

  private JsonNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    dbRecord.forEach(row -> rootNode.put(row.type(), new String(row.data(), StandardCharsets.UTF_8)));
    return rootNode;
  }

  // Search by Physical Specimen Identifier
  public JsonApiWrapperWrite searchByPhysicalSpecimenId(String physicalId,
      PhysicalIdType physicalIdType, String specimenHostPid)
      throws PidResolutionException, InvalidRequestException {

    var physicalIdentifier = setPhysicalId(physicalId, physicalIdType, specimenHostPid);
    var returnedRows = handleRep.searchByPhysicalIdentifier(List.of(physicalIdentifier));
    var handleNames = listHandleNamesReturnedFromQuery(returnedRows);
    if (handleNames.size() > 1) {
      throw new PidResolutionException(
          "More than one handle record corresponds to the provided collection facility and physical identifier.");
    }
    List<JsonApiDataLinks> dataNode = new ArrayList<>();

    var jsonFormattedRecord = jsonFormatSingleRecord(returnedRows);
    dataNode.add(wrapData(jsonFormattedRecord, "PID"));
    return new JsonApiWrapperWrite(dataNode);
  }

  private byte[] setPhysicalId(String physicalIdentifier, PhysicalIdType physicalIdType,
      String specimenHostPid)
      throws InvalidRequestException {
    if (physicalIdType.equals(PhysicalIdType.COMBINED)) {
      if (specimenHostPid == null) {
        throw new InvalidRequestException("Missing specimen host ID.");
      }
      var hostIdArr = specimenHostPid.split("/");
      return (physicalIdentifier + ":" + hostIdArr[hostIdArr.length - 1]).getBytes(
          StandardCharsets.UTF_8);
    }
    return physicalIdentifier.getBytes(StandardCharsets.UTF_8);
  }

  private <T extends DigitalSpecimenRequest> void verifyNoRegisteredSpecimens(List<T> requests)
      throws PidCreationException {
    List<byte[]> physicalIds = new ArrayList<>();
    for (var request : requests) {
      physicalIds.add(setUniquePhysicalIdentifierId(request));
    }
    var registeredSpecimens = handleRep.searchByPhysicalIdentifier(physicalIds);
    if (!registeredSpecimens.isEmpty()) {
      var registeredHandles = listHandleNamesReturnedFromQuery(registeredSpecimens);
      throw new PidCreationException(
          "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
              + registeredHandles);
    }
  }

  private Set<String> listHandleNamesReturnedFromQuery(List<HandleAttribute> rows) {
    Set<String> handles = new HashSet<>();
    rows.forEach(row -> handles.add((new String(row.handle(), StandardCharsets.UTF_8))));
    return handles;
  }

  // Pid Record Creation
  public <T extends DigitalSpecimenRequest> JsonApiWrapperWrite createRecords(
      List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {

    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<byte[]> handlesPost = new ArrayList<>(handles);
    List<T> digitalSpecimenList = new ArrayList<>();

    List<HandleAttribute> handleAttributes = new ArrayList<>();
    Map<String, String> recordTypes = new HashMap<>();

    for (var request : requests) {
      ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
      ObjectType type = ObjectType.fromString(dataNode.get(NODE_TYPE).asText());
      recordTypes.put(new String(handles.get(0), StandardCharsets.UTF_8), type.toString());
      try {
        switch (type) {
          case HANDLE -> {
            HandleRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                HandleRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordBuilder.prepareHandleRecordAttributes(requestObject, handles.remove(0)));
          }
          case DOI -> {
            DoiRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DoiRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordBuilder.prepareDoiRecordAttributes(requestObject, handles.remove(0)));
          }
          case DIGITAL_SPECIMEN -> {
            DigitalSpecimenRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DigitalSpecimenRequest.class);
            handleAttributes.addAll(
                fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(requestObject,
                    handles.remove(0)));
            digitalSpecimenList.add((T) requestObject);
          }
          case DIGITAL_SPECIMEN_BOTANY -> {
            DigitalSpecimenBotanyRequest requestObject = mapper.treeToValue(
                dataNode.get(NODE_ATTRIBUTES), DigitalSpecimenBotanyRequest.class);
            handleAttributes.addAll(
                fdoRecordBuilder.prepareDigitalSpecimenBotanyRecordAttributes(requestObject,
                    handles.remove(0)));
            digitalSpecimenList.add((T) requestObject);
          }
          case MEDIA_OBJECT -> {
            MediaObjectRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MediaObjectRequest.class);
            handleAttributes.addAll(
                fdoRecordBuilder.prepareMediaObjectAttributes(requestObject, handles.remove(0)));
          }
          default -> throw new InvalidRequestException(INVALID_TYPE_ERROR + type);
        }
      } catch (JsonProcessingException | UnprocessableEntityException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }

    verifyNoRegisteredSpecimens(digitalSpecimenList);
    handleRep.postAttributesToDb(recordTimestamp, handleAttributes);

    var postedRecordAttributes = resolveAndFormatRecords(handlesPost);
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (JsonNode recordAttributes : postedRecordAttributes) {
      dataList.add(
          wrapData(recordAttributes, getRecordTypeFromTypeList(recordAttributes, recordTypes)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  // Update Records
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    Map<String, String> recordTypes = new HashMap<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      String recordType = data.get(NODE_TYPE).asText();
      recordTypes.put(new String(handle, StandardCharsets.UTF_8), recordType);

      var attributes = fdoRecordBuilder.prepareUpdateAttributes(handle, requestAttributes);
      attributesToUpdate.add(attributes);
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.updateRecordBatch(recordTimestamp, attributesToUpdate);
    var updatedRecords = resolveAndFormatRecords(handles);

    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (JsonNode updatedRecord : updatedRecords) {
      dataList.add(wrapData(updatedRecord, getRecordTypeFromTypeList(updatedRecord, recordTypes)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  private String getRecordTypeFromTypeList(JsonNode recordAttributes,
      Map<String, String> recordTypes) {
    String pid = getPidName(recordAttributes.get(PID).asText());
    return recordTypes.get(pid);
  }


  private void checkHandlesWritable(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);

    Set<byte[]> handlesExist = new HashSet<>(handleRep.checkHandlesWritable(handles));
    if (handlesExist.size() < handles.size()) {
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = new HashSet<>();
      for (byte[] handle : handlesToUpdate) {
        handlesDontExist.add(new String(handle, StandardCharsets.UTF_8));
      }
      throw new PidResolutionException(
          "INVALID INPUT. One or more handles in request do not exist or are archived. Verify the following handle(s): "
              + handlesDontExist);
    }
  }

  private void checkInternalDuplicates(List<byte[]> handles) throws InvalidRequestException {
    Set<String> handlesToUpdateStr = new HashSet<>();
    for (byte[] handle : handles) {
      handlesToUpdateStr.add(new String(handle, StandardCharsets.UTF_8));
    }

    if (handlesToUpdateStr.size() < handles.size()) {
      Set<String> duplicateHandles = findDuplicates(handles, handlesToUpdateStr);
      throw new InvalidRequestException(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }

  private Set<String> findDuplicates(List<byte[]> handles, Set<String> handlesToUpdate) {
    Set<String> duplicateHandles = new HashSet<>();
    for (byte[] handle : handles) {
      if (!handlesToUpdate.add(new String(handle, StandardCharsets.UTF_8))) {
        duplicateHandles.add(new String(handle, StandardCharsets.UTF_8));
      }
    }
    return duplicateHandles;
  }

  // Archive
  public JsonApiWrapperWrite archiveRecordBatch(List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<HandleAttribute> archiveAttributes = new ArrayList<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      archiveAttributes.addAll(fdoRecordBuilder.prepareUpdateAttributes(handle, requestAttributes));
      archiveAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
          "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.archiveRecords(recordTimestamp, archiveAttributes);
    var archivedRecords = resolveAndFormatRecords(handles);

    List<JsonApiDataLinks> dataList = new ArrayList<>();

    for (JsonNode updatedRecord : archivedRecords) {
      String pidLink = updatedRecord.get(PID).asText();
      String pidName = getPidName(pidLink);
      dataList.add(new JsonApiDataLinks(pidName, ObjectType.TOMBSTONE.toString(), updatedRecord,
          new JsonApiLinks(pidLink)));
    }
    return new JsonApiWrapperWrite(dataList);
  }
}

