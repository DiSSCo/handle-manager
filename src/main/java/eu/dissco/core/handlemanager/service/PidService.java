package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.MEDIA_OBJECT;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.TOMBSTONE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenUpdateWrapper;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.ProcessedDigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class PidService {

  protected final PidRepository pidRepository;
  protected final FdoRecordService fdoRecordService;
  protected final PidNameGeneratorService hf;
  protected final ObjectMapper mapper;
  protected final ProfileProperties profileProperties;

  private List<JsonNode> formatRecords(List<HandleAttribute> dbRecord) {
    var handleMap = mapRecords(dbRecord);
    return handleMap.values().stream().map(this::jsonFormatSingleRecord).toList();
  }

  private JsonNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    for (var row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        rootNode.put(row.getType(), new String(row.getData(), StandardCharsets.UTF_8));
      }
    }
    return rootNode;
  }

  private Map<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {
    return flatList.stream()
        .collect(Collectors.groupingBy(row -> new String(row.getHandle(), StandardCharsets.UTF_8)));
  }

  private JsonApiDataLinks wrapData(JsonNode recordAttributes, String recordType) {
    String pidLink = recordAttributes.get(PID.get()).asText();
    String pidName = getPidName(pidLink);
    var handleLink = new JsonApiLinks(pidLink);
    return new JsonApiDataLinks(pidName, recordType, recordAttributes, handleLink);
  }

  private String getPidName(String pidLink) {
    return pidLink.substring(profileProperties.getDomain().length());
  }

  protected List<JsonApiDataLinks> formatCreateRecords(List<HandleAttribute> dbRecord,
      ObjectType objectType) {
    var handleMap = mapRecords(dbRecord);
    switch (objectType) {
      case ANNOTATION -> {
        return formatCreateRecordsAnnotation(handleMap);
      }
      case DIGITAL_SPECIMEN -> {
        return formatCreateRecordsSpecimen(handleMap);
      }
      case MEDIA_OBJECT -> {
        return formatCreateRecordsMedia(handleMap);
      }
      default -> {
        return formatCreateRecordsDefault(handleMap, objectType);
      }
    }
  }

  private List<JsonApiDataLinks> formatCreateRecordsAnnotation(
      Map<String, List<HandleAttribute>> handleMap) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      var hashRow = handleRecord.getValue().stream()
          .filter(row -> row.getType().equals(ANNOTATION_HASH.get())).findFirst();
      var subRecord = hashRow.map(List::of).orElse(handleRecord.getValue());
      var rootNode = jsonFormatSingleRecord(subRecord);
      String pidLink = profileProperties.getDomain() + handleRecord.getKey();
      dataLinksList.add(new JsonApiDataLinks(handleRecord.getKey(), ANNOTATION.toString(), rootNode,
          new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsSpecimen(
      Map<String, List<HandleAttribute>> handleMap) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      var subRecord = handleRecord.getValue().stream()
          .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())).toList();
      var rootNode = jsonFormatSingleRecord(subRecord);
      String pidLink = profileProperties.getDomain() + handleRecord.getKey();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.getKey(), DIGITAL_SPECIMEN.toString(), rootNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsMedia(
      Map<String, List<HandleAttribute>> handleMap) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      var subRecord = handleRecord.getValue().stream().filter(
          row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
              .equals(LINKED_DO_PID.get())).toList();
      var rootNode = jsonFormatSingleRecord(subRecord);
      String pidLink = profileProperties.getDomain() + handleRecord.getKey();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.getKey(), MEDIA_OBJECT.toString(), rootNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsDefault(
      Map<String, List<HandleAttribute>> handleMap, ObjectType objectType) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      var rootNode = jsonFormatSingleRecord(handleRecord.getValue());
      String pidLink = profileProperties.getDomain() + handleRecord.getKey();
      dataLinksList.add(new JsonApiDataLinks(handleRecord.getKey(), objectType.toString(), rootNode,
          new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private JsonApiWrapperWrite formatArchives(List<List<HandleAttribute>> archiveRecords) {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var archiveRecord : archiveRecords) {
      String handle = new String(archiveRecord.get(0).getHandle(), StandardCharsets.UTF_8);
      var attributeNode = jsonFormatSingleRecord(archiveRecord);
      dataList.add(new JsonApiDataLinks(handle, TOMBSTONE.toString(), attributeNode,
          new JsonApiLinks(profileProperties.getDomain() + handle)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  public JsonApiWrapperReadSingle resolveSingleRecord(byte[] handle, String path)
      throws PidResolutionException {
    var dbRecord = pidRepository.resolveHandleAttributes(handle);
    verifyHandleResolution(List.of(handle), dbRecord);
    var recordAttributeList = formatRecords(dbRecord).get(0);
    var dataNode = wrapData(recordAttributeList, getRecordType(dbRecord));
    var linksNode = new JsonApiLinks(path);
    return new JsonApiWrapperReadSingle(linksNode, dataNode);
  }

  private String getRecordType(List<HandleAttribute> dbRecord) {
    var type = dbRecord.stream().filter(row -> row.getType().equals(REFERENT_TYPE.get()))
        .map(val -> new String(val.getData(), StandardCharsets.UTF_8)).findFirst();
    return type.orElse(ObjectType.HANDLE.toString());
  }

  private String getRecordType(JsonNode attributes) {
    if (attributes.get(REFERENT_TYPE.get()) != null) {
      return attributes.get(REFERENT_TYPE.get()).asText();
    }
    return ObjectType.HANDLE.toString();
  }

  public JsonApiWrapperRead resolveBatchRecord(List<byte[]> handles, String path)
      throws PidResolutionException {
    var dbRecords = pidRepository.resolveHandleAttributes(handles);
    verifyHandleResolution(handles, dbRecords);
    var recordAttributeList = formatRecords(dbRecords);
    var dataList = recordAttributeList.stream()
        .map(recordAttributes -> wrapData(recordAttributes, getRecordType(recordAttributes)))
        .toList();
    return new JsonApiWrapperRead(new JsonApiLinks(path), dataList);
  }

  // Getters
  public List<String> getHandlesPaged(int pageNum, int pageSize, byte[] pidStatus) {
    return pidRepository.getAllHandles(pidStatus, pageNum, pageSize);
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return pidRepository.getAllHandles(pageNum, pageSize);
  }

  private void verifyHandleResolution(List<byte[]> handles, List<HandleAttribute> dbRecords)
      throws PidResolutionException {
    var resolvedHandles = dbRecords.stream().map(HandleAttribute::getHandle)
        .map(handle -> new String(handle, StandardCharsets.UTF_8)).collect(Collectors.toSet());
    if (handles.size() == resolvedHandles.size()) {
      return;
    }
    var handlesString = handles.stream().map(handle -> new String(handle, StandardCharsets.UTF_8))
        .collect(Collectors.toSet());
    handlesString.removeAll(resolvedHandles);
    log.error("Unable to resolve the following identifiers: {}", handlesString);
    throw new PidResolutionException("PIDs not found: " + handlesString);
  }

  public JsonApiWrapperWrite searchByPhysicalSpecimenId(String normalisedPhysicalId)
      throws PidResolutionException {
    var returnedRows = pidRepository.searchByNormalisedPhysicalIdentifierFullRecord(
        List.of(normalisedPhysicalId.getBytes(StandardCharsets.UTF_8)));
    var handleNames = listHandleNamesReturnedFromQuery(returnedRows);
    if (handleNames.size() > 1) {
      throw new PidResolutionException(
          "More than one handle record corresponds to the provided collection facility and physical identifier.");
    }
    List<JsonApiDataLinks> dataNode = new ArrayList<>();

    var jsonFormattedRecord = jsonFormatSingleRecord(returnedRows);
    dataNode.add(wrapData(jsonFormattedRecord, DIGITAL_SPECIMEN.toString()));
    return new JsonApiWrapperWrite(dataNode);
  }

  private Set<String> listHandleNamesReturnedFromQuery(List<HandleAttribute> rows) {
    Set<String> handles = new HashSet<>();
    rows.forEach(row -> handles.add((new String(row.getHandle(), StandardCharsets.UTF_8))));
    return handles;
  }

  // Create
  public abstract JsonApiWrapperWrite createRecords(List<JsonNode> requests)
      throws PidResolutionException, InvalidRequestException, PidCreationException;

  protected ObjectType getObjectType(List<JsonNode> requests) {
    var types = requests.stream().map(request -> request.get(NODE_DATA).get(NODE_TYPE).asText())
        .collect(Collectors.toSet());
    var type = types.stream().findFirst();
    if (type.isEmpty() || types.size() != 1) {
      throw new UnsupportedOperationException("Requests must all be of the same type");
    }
    return ObjectType.fromString(type.get());
  }

  protected JsonApiWrapperWrite upsertDigitalSpecimen(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator)
      throws InvalidRequestException, JsonProcessingException, PidResolutionException, PidCreationException {
    var specimenRequests = new ArrayList<DigitalSpecimenRequest>();
    for (var request : requestAttributes) {
      specimenRequests.add(mapper.treeToValue(request, DigitalSpecimenRequest.class));
    }
    var processResult = processSpecimenRequests(specimenRequests);
    var recordTimeStamp = Instant.now().getEpochSecond();
    var pidAttributes = createNewDigitalSpecimenRecords(processResult.newRequests(), handleIterator,
        recordTimeStamp);
    pidAttributes.addAll(updateDigitalSpecimen(processResult.updateRequests(), recordTimeStamp));
    return new JsonApiWrapperWrite(formatCreateRecordsSpecimen(mapRecords(pidAttributes)));
  }

  private ArrayList<HandleAttribute> createNewDigitalSpecimenRecords(
      List<DigitalSpecimenRequest> specimenRequests, Iterator<byte[]> handleIterator,
      long recordTimestamp)
      throws PidResolutionException, InvalidRequestException, PidCreationException {
    if (specimenRequests.isEmpty()) {
      return new ArrayList<>();
    }
    var handleAttributes = new ArrayList<HandleAttribute>();
    for (var request : specimenRequests) {
      var thisHandle = handleIterator.next();
      handleAttributes.addAll(
          fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, thisHandle));
    }
    log.info("Posting {} new digital specimen fdo records to db", specimenRequests.size());
    pidRepository.postAttributesToDb(recordTimestamp, handleAttributes);
    return handleAttributes;
  }

  protected List<HandleAttribute> updateDigitalSpecimen(
      List<DigitalSpecimenUpdateWrapper> updateRequests, long recordTimestamp)
      throws InvalidRequestException, PidResolutionException {
    if (updateRequests.isEmpty()) {
      return Collections.emptyList();
    }
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    for (var request : updateRequests) {
      var requestAttributes = mapper.valueToTree(request.digitalSpecimenRequest());
      flatList.addAll(fdoRecordService.prepareUpdateAttributes(
          request.handle().getBytes(StandardCharsets.UTF_8), requestAttributes, DIGITAL_SPECIMEN));
      attributesToUpdate.add(flatList);
    }
    log.info("Updating {} digital specimen fdo records to db", updateRequests.size());
    pidRepository.updateRecordBatch(recordTimestamp, attributesToUpdate, true);
    return flatList;
  }

  private ProcessedDigitalSpecimenRequest processSpecimenRequests(
      ArrayList<DigitalSpecimenRequest> specimenRequests) {
    var physicalIds = specimenRequests.stream().map(
            request -> request.getNormalisedPrimarySpecimenObjectId().getBytes(StandardCharsets.UTF_8))
        .toList();
    var registeredPhysicalIdentifiers = pidRepository.searchByNormalisedPhysicalIdentifier(
        physicalIds);
    var registeredPhysicalIdentiferMap = registeredPhysicalIdentifiers.stream().collect(
        Collectors.toMap(row -> new String(row.getData(), StandardCharsets.UTF_8),
            row -> new String(row.getHandle(), StandardCharsets.UTF_8)));
    var updates = specimenRequests.stream().filter(
        request -> registeredPhysicalIdentiferMap.containsKey(
            request.getNormalisedPrimarySpecimenObjectId())).map(
        request -> new DigitalSpecimenUpdateWrapper(
            registeredPhysicalIdentiferMap.get(request.getNormalisedPrimarySpecimenObjectId()),
            request)).toList();
    if (!updates.isEmpty()) {
      log.debug("Existing records: {}",
          updates.stream().map(DigitalSpecimenUpdateWrapper::handle).toList());
    }
    specimenRequests.removeAll(
        updates.stream().map(DigitalSpecimenUpdateWrapper::digitalSpecimenRequest).toList());

    return new ProcessedDigitalSpecimenRequest(specimenRequests, updates);
  }

  protected List<HandleAttribute> createMediaObject(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator)
      throws InvalidRequestException, JsonProcessingException, PidResolutionException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, MediaObjectRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareMediaObjectAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

  // Update
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    var recordType = getObjectType(requests);
    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      ObjectType type = ObjectType.fromString(data.get(NODE_TYPE).asText());
      var attributes = fdoRecordService.prepareUpdateAttributes(handle, requestAttributes, type);
      attributesToUpdate.add(attributes);
    }

    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);
    pidRepository.updateRecordBatch(recordTimestamp, attributesToUpdate, incrementVersion);
    return formatUpdates(handles.stream().map(h -> new String(h, StandardCharsets.UTF_8)).toList(),
        recordType);
  }

  protected void checkInternalDuplicates(List<byte[]> handles) throws InvalidRequestException {
    Set<String> handlesToUpdateStr = handles.stream()
        .map(h -> new String(h, StandardCharsets.UTF_8)).collect(Collectors.toSet());
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

  protected JsonApiWrapperWrite formatUpdates(List<String> handles, ObjectType type) {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var handle : handles) {
      dataList.add(new JsonApiDataLinks(handle, type.toString(), null,
          new JsonApiLinks(profileProperties.getDomain() + handle)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  protected void checkHandlesWritable(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);
    Set<byte[]> handlesExist = new HashSet<>(pidRepository.checkHandlesWritable(handles));
    if (handlesExist.size() < handles.size()) {
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = handlesToUpdate.stream()
          .map(h -> new String(h, StandardCharsets.UTF_8)).collect(Collectors.toSet());
      throw new PidResolutionException(
          "INVALID INPUT. One or more identifiers in request do not exist or are archived. Verify the following handle(s): "
              + handlesDontExist);
    }
  }

  // Archive
  public JsonApiWrapperWrite archiveRecordBatch(List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    var archiveAttributesFlat = new ArrayList<HandleAttribute>();
    var archiveAttributes = new ArrayList<List<HandleAttribute>>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      var handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      var recordAttributes = fdoRecordService.prepareTombstoneAttributes(handle, requestAttributes);
      archiveAttributesFlat.addAll(recordAttributes);
      archiveAttributes.add(recordAttributes);
    }

    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    pidRepository.archiveRecords(recordTimestamp, archiveAttributesFlat,
        handles.stream().map(h -> new String(h, StandardCharsets.UTF_8)).toList());

    return formatArchives(archiveAttributes);
  }

  public void rollbackHandles(List<String> handles) {
    pidRepository.rollbackHandles(handles);
  }

  public void rollbackHandlesFromPhysId(List<String> physicalIds) {
    var physicalIdsBytes = physicalIds.stream().map(id -> id.getBytes(StandardCharsets.UTF_8))
        .toList();
    var handles = pidRepository.searchByNormalisedPhysicalIdentifier(physicalIdsBytes).stream()
        .map(HandleAttribute::getHandle).map(handle -> new String(handle, StandardCharsets.UTF_8))
        .toList();
    pidRepository.rollbackHandles(handles);
  }

}
