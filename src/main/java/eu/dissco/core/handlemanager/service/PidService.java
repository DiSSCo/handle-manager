package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidMongoRepository;
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
  protected final PidMongoRepository mongoRepository;

  private List<JsonNode> formatRecords(List<HandleAttribute> dbRecord) {
    var handleMap = mapRecords(dbRecord);
    return handleMap.values().stream().map(this::jsonFormatSingleRecordOld).toList();
  }

  // todo Delete
  protected JsonNode jsonFormatSingleRecordOld(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    for (var row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        var rowData = new String(row.getData(), StandardCharsets.UTF_8);
        try {
          var nodeData = mapper.readTree(rowData);
          rootNode.set(row.getType(), nodeData);
        } catch (JsonProcessingException ignored) {
          rootNode.put(row.getType(), rowData);
        }
      }
    }
    return rootNode;
  }

  protected JsonNode jsonFormatSingleRecord(List<FdoAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    for (var row : dbRecord) {
      if (row.getIndex() != HS_ADMIN.index()) {
        try {
          var nodeData = mapper.readTree(row.getValue());
          rootNode.set(row.getType(), nodeData);
        } catch (JsonProcessingException ignored) {
          rootNode.put(row.getType(), row.getValue());
        }
      }
    }
    return rootNode;
  }

  protected JsonNode jsonFormatSingleRecord(List<FdoAttribute> dbRecord,
      List<FdoProfile> keyAttributes) {
    ObjectNode rootNode = mapper.createObjectNode();
    var indexList = keyAttributes.stream().map(FdoProfile::index).toList();
    for (var row : dbRecord) {
      if (indexList.contains(row.getIndex())) {
        try {
          var nodeData = mapper.readTree(row.getValue());
          rootNode.set(row.getType(), nodeData);
        } catch (JsonProcessingException ignored) {
          rootNode.put(row.getType(), row.getValue());
        }
      }
    }
    return rootNode;
  }


  protected Map<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {
    return flatList.stream()
        .collect(Collectors.groupingBy(row -> new String(row.getHandle(), StandardCharsets.UTF_8)));
  }

  private JsonApiDataLinks wrapResolvedData(JsonNode recordAttributes, String recordType) {
    String pidLink = recordAttributes.get(PID.get()).asText();
    String pidName = getPidName(pidLink);
    var handleLink = new JsonApiLinks(pidLink);
    return new JsonApiDataLinks(pidName, recordType, recordAttributes, handleLink);
  }

  private String getPidName(String pidLink) {
    return pidLink.substring(profileProperties.getDomain().length());
  }

  protected List<JsonApiDataLinks> formatCreateRecord(List<FdoRecord> fdoRecords,
      FdoType fdoType) {
    switch (fdoType) {
      case ANNOTATION -> {
        return formatCreateRecordsAnnotation(fdoRecords);
      }
      case DIGITAL_SPECIMEN -> {
        return formatCreateRecordsSpecimen(fdoRecords);
      }
      case MEDIA_OBJECT -> {
        return formatCreateRecordsMedia(fdoRecords);
      }
      default -> {
        return formatCreateRecordsDefault(fdoRecords, fdoType);
      }
    }
  }

  private List<JsonApiDataLinks> formatCreateRecordsAnnotation(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
          List.of(ANNOTATION_HASH));
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), ANNOTATION.getDigitalObjectType(),
              attributeNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsSpecimen(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
          List.of(PRIMARY_SPECIMEN_OBJECT_ID));
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), DIGITAL_SPECIMEN.getDigitalObjectType(),
              attributeNode, new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsMedia(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
          List.of(PRIMARY_SPECIMEN_OBJECT_ID, LINKED_DO_PID));
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), DIGITAL_SPECIMEN.getDigitalObjectType(),
              attributeNode, new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatCreateRecordsDefault(List<FdoRecord> fdoRecords,
      FdoType fdoType) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var rootNode = jsonFormatSingleRecord(handleRecord.attributes());
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), fdoType.getDigitalObjectType(), rootNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private JsonApiWrapperWrite formatArchives(List<List<HandleAttribute>> archiveRecords) {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var archiveRecord : archiveRecords) {
      String handle = new String(archiveRecord.get(0).getHandle(), StandardCharsets.UTF_8);
      var attributeNode = jsonFormatSingleRecordOld(archiveRecord);
      dataList.add(
          new JsonApiDataLinks(handle, FdoType.TOMBSTONE.getDigitalObjectType(), attributeNode,
              new JsonApiLinks(profileProperties.getDomain() + handle)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  public JsonApiWrapperReadSingle resolveSingleRecord(byte[] handle, String path)
      throws PidResolutionException {
    var dbRecord = pidRepository.resolveHandleAttributes(handle);
    verifyHandleResolution(List.of(handle), dbRecord);
    var recordAttributeList = formatRecords(dbRecord).get(0);
    var dataNode = wrapResolvedData(recordAttributeList, getRecordTypeFromResolvedRecord(dbRecord));
    var linksNode = new JsonApiLinks(path);
    return new JsonApiWrapperReadSingle(linksNode, dataNode);
  }

  private String getRecordTypeFromResolvedRecord(List<HandleAttribute> dbRecord) {
    var type = dbRecord.stream().filter(row -> row.getType().equals(DIGITAL_OBJECT_TYPE.get()))
        .map(val -> new String(val.getData(), StandardCharsets.UTF_8)).findFirst();
    return type.orElse(FdoType.HANDLE.getDigitalObjectType());
  }

  public JsonApiWrapperRead resolveBatchRecord(List<byte[]> handles, String path)
      throws PidResolutionException {
    var dbRecords = pidRepository.resolveHandleAttributes(handles);
    verifyHandleResolution(handles, dbRecords);
    var recordAttributeList = formatRecords(dbRecords);
    var dataList = recordAttributeList.stream().map(
        recordAttributes -> wrapResolvedData(recordAttributes,
            getRecordTypeFromResolvedRecord(dbRecords))).toList();
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
    var handleNames = returnedRows.stream()
        .map(row -> new String(row.getHandle(), StandardCharsets.UTF_8))
        .collect(Collectors.toSet());
    if (handleNames.size() > 1) {
      throw new PidResolutionException(
          "More than one handle record corresponds to the provided collection facility and physical identifier.");
    }
    List<JsonApiDataLinks> dataNode = new ArrayList<>();

    var jsonFormattedRecord = jsonFormatSingleRecordOld(returnedRows);
    dataNode.add(wrapResolvedData(jsonFormattedRecord, DIGITAL_SPECIMEN.getDigitalObjectType()));
    return new JsonApiWrapperWrite(dataNode);
  }

  // Create
  public abstract JsonApiWrapperWrite createRecords(List<JsonNode> requests)
      throws InvalidRequestException, UnprocessableEntityException;

  protected List<FdoRecord> getPreviousVersions(List<String> handles)
      throws InvalidRequestException {
    List<FdoRecord> previousVersions;
    try {
      previousVersions = mongoRepository.getHandleRecords(handles);
    } catch (JsonProcessingException e) {
      throw new InvalidRequestException("Unable to process handles resolution");
    }
    if (previousVersions.size() < handles.size()) {
      throw new InvalidRequestException("Unable to resolve all handles");
    }
    return previousVersions;
  }

  // Update
  public abstract JsonApiWrapperWrite updateRecords(List<JsonNode> requests)
      throws InvalidRequestException, UnprocessableEntityException;

  protected void checkHandlesWritableFullRecord(List<FdoRecord> previousVersions)
      throws InvalidRequestException {
    for (var fdoRecord : previousVersions) {
      var pidStatus = getField(fdoRecord.attributes(), PID_STATUS);
      if ("ARCHIVED".equals(pidStatus)) {
        log.error("Attempting to update a FDO record that has been archived");
        throw new InvalidRequestException();
      }
    }
  }

  protected static FdoAttribute getField(List<FdoAttribute> fdoAttributes, FdoProfile targetField) {
    for (var attribute : fdoAttributes) {
      if (attribute.getIndex() == targetField.index()) {
        return attribute;
      }
    }
    log.error("Unable to find field {} in record {}", targetField, fdoAttributes);
    throw new IllegalStateException();
  }

  protected FdoType getObjectTypeFromJsonNode(List<JsonNode> requests) {
    var types = requests.stream().map(request -> request.get(NODE_DATA).get(NODE_TYPE).asText())
        .collect(Collectors.toSet());
    var type = types.stream().findFirst();
    if (type.isEmpty() || types.size() != 1) {
      throw new UnsupportedOperationException("Requests must all be of the same type");
    }
    return FdoType.fromString(type.get());
  }

  protected List<FdoRecord> createDigitalSpecimen(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    var specimenRequests = new ArrayList<DigitalSpecimenRequest>();
    for (var request : requestAttributes) {
      specimenRequests.add(mapper.treeToValue(request, DigitalSpecimenRequest.class));
    }
    if (specimenRequests.isEmpty()) {
      return Collections.emptyList();
    }
    verifyObjectsAreNew(specimenRequests
        .stream()
        .map(DigitalSpecimenRequest::getNormalisedPrimarySpecimenObjectId)
        .toList());
    var fdoRecords = new ArrayList<FdoRecord>();
    var timestamp = Instant.now();
    for (var request : specimenRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalSpecimenRecord(request, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  protected List<FdoRecord> updateDigitalSpecimen(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          DigitalSpecimenRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDigitalSpecimenRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  protected List<FdoRecord> createMediaObject(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    List<MediaObjectRequest> mediaRequests = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      mediaRequests.add(mapper.treeToValue(request, MediaObjectRequest.class));
    }
    if (mediaRequests.isEmpty()) {
      return Collections.emptyList();
    }
    verifyObjectsAreNew(mediaRequests
        .stream()
        .map(MediaObjectRequest::getPrimaryMediaId)
        .toList());
    for (var mediaRequest : mediaRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalMediaRecord(mediaRequest, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }


  protected List<FdoRecord> updateDigitalMedia(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          MediaObjectRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDigitalMediaRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private void verifyObjectsAreNew(List<String> normalisedIds)
      throws InvalidRequestException, JsonProcessingException {
    var existingHandles = mongoRepository
        .searchByPrimaryLocalId(NORMALISED_SPECIMEN_OBJECT_ID.get(), normalisedIds);
    if (!existingHandles.isEmpty()) {
      log.error("Unable to create new handles, as ");
      var handleMap = existingHandles.stream()
          .collect(Collectors.toMap(
              FdoRecord::handle,
              FdoRecord::primaryLocalId));
      log.error(
          "Unable to create new handles, as they already exist. Verify the following identifiers: {}",
          handleMap);
      throw new InvalidRequestException(
          "Attempting to create handle records for specimens already in system");
    }
  }

  // Update
  public JsonApiWrapperWrite updateRecords(List<List<HandleAttribute>> attributesToUpdate,
      boolean incrementVersion, FdoType recordType) throws InvalidRequestException {
    var recordTimestamp = Instant.now().getEpochSecond();
    var handles = attributesToUpdate.stream().map(pidRecord -> pidRecord.get(0).getHandle())
        .toList();
    //checkInternalDuplicates(handles);
    //checkHandlesWritable(handles);
    log.info("Writing updates to db");
    pidRepository.updateRecordBatch(recordTimestamp, attributesToUpdate, incrementVersion);
    return formatUpdates(handles.stream().map(h -> new String(h, StandardCharsets.UTF_8)).toList(),
        recordType);
  }

  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    List<List<HandleAttribute>> attributesToUpdate = getAttributesToUpdate(requests);
    var recordType = getObjectTypeFromJsonNode(requests);
    return updateRecords(attributesToUpdate, incrementVersion, recordType);
  }

  protected List<List<HandleAttribute>> getAttributesToUpdate(List<JsonNode> requests)
      throws InvalidRequestException {
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      FdoType type = FdoType.fromString(data.get(NODE_TYPE).asText());
      var attributes = fdoRecordService.prepareUpdateAttributes(handle, requestAttributes, type);
      attributesToUpdate.add(attributes);
    }
    return attributesToUpdate;
  }

  protected void checkInternalDuplicates(List<String> handles) throws InvalidRequestException {
    Set<String> handlesToUpdate = new HashSet<>(handles);
    if (handlesToUpdate.size() < handles.size()) {
      Set<String> duplicateHandles = findDuplicates(handles, handlesToUpdate);
      throw new InvalidRequestException(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }

  private Set<String> findDuplicates(List<String> handles, Set<String> handlesToUpdate) {
    Set<String> duplicateHandles = new HashSet<>();
    for (var handle : handles) {
      if (!handlesToUpdate.add(handle)) {
        duplicateHandles.add(handle);
      }
    }
    return duplicateHandles;
  }

  protected JsonApiWrapperWrite formatUpdates(List<String> handles, FdoType type) {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var handle : handles) {
      dataList.add(new JsonApiDataLinks(handle, type.getDigitalObjectType(), null,
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
      throws InvalidRequestException {
    var recordTimestamp = Instant.now().getEpochSecond();
    var handles = new ArrayList<String>();
    var archiveAttributesFlat = new ArrayList<HandleAttribute>();
    var archiveAttributes = new ArrayList<List<HandleAttribute>>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      var handle = data.get(NODE_ID).asText();
      handles.add(handle);
      var recordAttributes = fdoRecordService.prepareTombstoneAttributes(handle.getBytes(
          StandardCharsets.UTF_8), requestAttributes);
      archiveAttributesFlat.addAll(recordAttributes);
      archiveAttributes.add(recordAttributes);
    }

    checkInternalDuplicates(handles);
//    checkHandlesWritable(handles);

    pidRepository.archiveRecords(recordTimestamp, archiveAttributesFlat, handles);

    return formatArchives(archiveAttributes);
  }

  public void rollbackHandles(List<String> handles) {
    pidRepository.rollbackHandles(handles);
  }

  public void rollbackHandlesFromPhysId(List<String> physicalIds) {
    var physicalIdsBytes = physicalIds.stream().map(id -> id.getBytes(StandardCharsets.UTF_8))
        .toList();
    var handles = pidRepository.searchByNormalisedPhysicalIdentifier(physicalIdsBytes).stream()
        .map(ha -> new String(ha.getHandle(), StandardCharsets.UTF_8)).toList();
    pidRepository.rollbackHandles(handles);
  }

}
