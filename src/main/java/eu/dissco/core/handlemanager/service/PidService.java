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
import eu.dissco.core.handlemanager.domain.requests.UpsertDigitalSpecimen;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
      Map<String, ObjectType> recordTypes) {
    var handleMap = mapRecords(dbRecord);
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      var type = recordTypes.get(handleRecord.getKey());
      var subRecord = handleRecord.getValue();
      if (type.equals(ObjectType.MEDIA_OBJECT)) {
        subRecord = subRecord.stream().filter(
                row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
                    .equals(LINKED_DO_PID.get()))
            .toList();
      } else if (type.equals(DIGITAL_SPECIMEN)) {
        subRecord = subRecord.stream()
            .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())).toList();
      } else if (type.equals(ANNOTATION)) {
        var hashRow = subRecord.stream().filter(row -> row.getType().equals(ANNOTATION_HASH.get()))
            .findFirst();
        if (hashRow.isPresent()) {
          subRecord = List.of(hashRow.get());
        }
      }
      var rootNode = jsonFormatSingleRecord(subRecord);
      String pidLink = profileProperties.getDomain() + handleRecord.getKey();
      dataLinksList.add(new JsonApiDataLinks(handleRecord.getKey(), type.toString(), rootNode,
          new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatUpsertResponse(List<HandleAttribute> records) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var row : records) {
      if (row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())) {
        String h = new String(row.getHandle(), StandardCharsets.UTF_8);
        String pidLink = profileProperties.getDomain() + h;
        var node = mapper.createObjectNode();
        node.put(PRIMARY_SPECIMEN_OBJECT_ID.get(),
            new String(row.getData(), StandardCharsets.UTF_8));
        dataLinksList.add(
            new JsonApiDataLinks(h, DIGITAL_SPECIMEN.toString(), node, new JsonApiLinks(pidLink)));
      }
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
        .map(val -> new String(val.getData(), StandardCharsets.UTF_8))
        .findFirst();
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
    var resolvedHandles = dbRecords.stream()
        .map(HandleAttribute::getHandle)
        .map(handle -> new String(handle, StandardCharsets.UTF_8))
        .collect(Collectors.toSet());
    if (handles.size() == resolvedHandles.size()) {
      return;
    }
    var handlesString = handles.stream()
        .map(handle -> new String(handle, StandardCharsets.UTF_8))
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
  public abstract JsonApiWrapperWrite createRecords(
      List<JsonNode> requests)
      throws PidResolutionException, InvalidRequestException, PidCreationException;

  protected <T extends DigitalSpecimenRequest> Set<String> getPhysicalIdsFromRequests(
      List<T> digitalSpecimenRequests) {
    return digitalSpecimenRequests.stream()
        .map(DigitalSpecimenRequest::getNormalisedPrimarySpecimenObjectId)
        .collect(Collectors.toSet());
  }

  protected List<byte[]> getPhysIdBytes(Set<String> physIds) {
    return physIds.stream()
        .map(physId -> physId.getBytes(StandardCharsets.UTF_8))
        .toList();
  }

  // Digital Specimen Validation
  protected void validateDigitalSpecimens(List<DigitalSpecimenRequest> digitalSpecimenList)
      throws InvalidRequestException, PidCreationException {
    if (!digitalSpecimenList.isEmpty()) {
      var requestPhysicalIds = getPhysicalIdsFromRequests(digitalSpecimenList);
      verifyNoInternalDuplicatePhysicalSpecimenObjectId(digitalSpecimenList, requestPhysicalIds);
      verifyNoRegisteredSpecimens(getPhysIdBytes(requestPhysicalIds));
    }
  }

  protected <T extends DigitalSpecimenRequest> void verifyNoInternalDuplicatePhysicalSpecimenObjectId(
      List<T> requests, Set<String> physicalIds)
      throws InvalidRequestException {
    if (physicalIds.size() < requests.size()) {
      throw new InvalidRequestException(
          "Bad Request. Some PhysicalSpecimenObjectIds are duplicated in request body");
    }
  }

  protected void verifyNoRegisteredSpecimens(List<byte[]> physicalIds)
      throws PidCreationException {
    var registeredSpecimens = pidRepository.searchByNormalisedPhysicalIdentifierFullRecord(
        physicalIds);
    if (!registeredSpecimens.isEmpty()) {
      var registeredHandles = listHandleNamesReturnedFromQuery(registeredSpecimens);
      throw new PidCreationException(
          "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
              + registeredHandles);
    }
  }

  // Upsert
  public JsonApiWrapperWrite upsertDigitalSpecimens(List<JsonNode> requests)
      throws JsonProcessingException, UnprocessableEntityException, PidResolutionException,
      InvalidRequestException {
    var digitalSpecimenRequests = jsonNodeToDigitalSpecimenRequest(requests);

    var physicalIds = getPhysicalIdsFromRequests(digitalSpecimenRequests);
    var physicalIdsBytes = getPhysIdBytes(physicalIds);
    var upsertRequests = getRegisteredSpecimensUpsert(digitalSpecimenRequests, physicalIdsBytes);
    var upsertAttributes = prepareUpsertAttributes(upsertRequests);
    logUpdates(upsertRequests);

    var createRequests = getCreateRequests(upsertRequests, digitalSpecimenRequests);
    var newHandles = hf.genHandleList(createRequests.size());
    if (!newHandles.isEmpty()) {
      log.info("Successfully minted {} new identifiers(s)", newHandles.size());
    }
    var createAttributes = getCreateAttributes(createRequests, newHandles);

    var allRequests = Stream.concat(
        createRequests.stream(),
        upsertRequests.stream().map(UpsertDigitalSpecimen::request)).toList();
    verifyNoInternalDuplicatePhysicalSpecimenObjectId(allRequests, physicalIds);

    var recordTimestamp = Instant.now().getEpochSecond();

    log.info("Persisting upserts to db.");
    pidRepository.postAndUpdateHandles(recordTimestamp, createAttributes, upsertAttributes);

    var concatAttributes = concatHandleAttributes(createAttributes, upsertAttributes);

    return new JsonApiWrapperWrite(
        formatUpsertResponse(concatAttributes));
  }

  private List<DigitalSpecimenRequest> jsonNodeToDigitalSpecimenRequest(List<JsonNode> requests)
      throws JsonProcessingException {
    ArrayList<DigitalSpecimenRequest> digitalSpecimenRequests = new ArrayList<>();
    for (var request : requests) {
      digitalSpecimenRequests.add(mapper.treeToValue(request.get(NODE_DATA).get(NODE_ATTRIBUTES),
          DigitalSpecimenRequest.class));
    }
    return digitalSpecimenRequests;
  }

  private List<UpsertDigitalSpecimen> getRegisteredSpecimensUpsert(
      List<DigitalSpecimenRequest> requests, List<byte[]> physicalIds) {
    var registeredSpecimensHandleAttributes = new HashSet<>(
        pidRepository.searchByNormalisedPhysicalIdentifier(physicalIds));
    if (registeredSpecimensHandleAttributes.isEmpty()) {
      return new ArrayList<>();
    }

    ArrayList<UpsertDigitalSpecimen> upsertDigitalSpecimen = new ArrayList<>();
    for (var row : registeredSpecimensHandleAttributes) {
      var targetPhysId = new String(row.getData(), StandardCharsets.UTF_8);
      var targetRequest = getRequestFromPhysicalId(requests, targetPhysId);
      requests.remove(targetRequest);
      upsertDigitalSpecimen.add(new UpsertDigitalSpecimen(
          new String(row.getHandle(), StandardCharsets.UTF_8),
          targetPhysId,
          targetRequest
      ));
    }
    return upsertDigitalSpecimen;
  }

  private void logUpdates(List<UpsertDigitalSpecimen> upsertRequests) {
    var registeredHandles = upsertRequests.stream().map(UpsertDigitalSpecimen::handle).toList();
    if (!registeredHandles.isEmpty()) {
      log.debug("Some specimens already have identifiers. Updating the following PID Records {}",
          registeredHandles);
    }
  }

  private DigitalSpecimenRequest getRequestFromPhysicalId(List<DigitalSpecimenRequest> requests,
      String physicalId) {
    for (var request : requests) {
      if (physicalId.equals(request.getNormalisedPrimarySpecimenObjectId())) {
        return request;
      }
    }
    throw new IllegalStateException("Physical Identifier not found");
  }

  private List<DigitalSpecimenRequest> getCreateRequests(List<UpsertDigitalSpecimen> upsertRequests,
      List<DigitalSpecimenRequest> digitalSpecimenRequests) {
    var upsertRequestsSet = upsertRequests
        .stream()
        .map(UpsertDigitalSpecimen::request).collect(Collectors.toSet());
    return digitalSpecimenRequests
        .stream()
        .filter(r -> (!upsertRequestsSet.contains(r)))
        .toList();
  }

  private List<HandleAttribute> getCreateAttributes(
      List<DigitalSpecimenRequest> digitalSpecimenRequests, List<byte[]> newHandles)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var handles = new ArrayList<>(newHandles);
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var digitalSpecimenRequest : digitalSpecimenRequests) {
      handleAttributes.addAll(
          fdoRecordService.prepareDigitalSpecimenRecordAttributes(digitalSpecimenRequest,
              handles.remove(0), DIGITAL_SPECIMEN));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> concatHandleAttributes(List<HandleAttribute> createAttributes,
      List<List<HandleAttribute>> upsertAttributes) {
    List<HandleAttribute> upsertListFlat = new ArrayList<>();
    for (var upsertRecord : upsertAttributes) {
      upsertListFlat.addAll(upsertRecord);
    }
    return Stream.concat(createAttributes.stream(), upsertListFlat.stream()).toList();
  }

  private List<List<HandleAttribute>> prepareUpsertAttributes(
      List<UpsertDigitalSpecimen> upsertDigitalSpecimens)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException {
    List<List<HandleAttribute>> upsertAttributes = new ArrayList<>();
    for (var upsertRequest : upsertDigitalSpecimens) {
      ArrayList<HandleAttribute> upsertAttributeSingleSpecimen = new ArrayList<>(fdoRecordService
          .prepareUpdateAttributes(upsertRequest.handle().getBytes(StandardCharsets.UTF_8),
              mapper.valueToTree(upsertRequest.request()), DIGITAL_SPECIMEN));
      upsertAttributes.add(upsertAttributeSingleSpecimen);
    }
    return upsertAttributes;
  }

  // Update
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests,
      boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    Map<String, ObjectType> recordTypes = new HashMap<>();
    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      ObjectType type = ObjectType.fromString(data.get(NODE_TYPE).asText());
      recordTypes.put(new String(handle, StandardCharsets.UTF_8), type);
      var attributes = fdoRecordService.prepareUpdateAttributes(handle, requestAttributes, type);
      attributesToUpdate.add(attributes);
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    pidRepository.updateRecordBatch(recordTimestamp, attributesToUpdate, incrementVersion);
    return formatUpdates(attributesToUpdate, recordTypes);
  }

  protected void checkInternalDuplicates(List<byte[]> handles) throws InvalidRequestException {
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

  protected JsonApiWrapperWrite formatUpdates(List<List<HandleAttribute>> updatedRecords,
      Map<String, ObjectType> recordTypes) {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var updatedRecord : updatedRecords) {
      String handle = new String(updatedRecord.get(0).getHandle(), StandardCharsets.UTF_8);
      var attributeNode = jsonFormatSingleRecord(updatedRecord);
      var type = recordTypes.get(handle).toString();
      dataList.add(new JsonApiDataLinks(handle, type, attributeNode,
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
          .map(h -> new String(h, StandardCharsets.UTF_8)).collect(
              Collectors.toSet());
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