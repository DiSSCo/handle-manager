package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType.TOMBSTONE;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;

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
import eu.dissco.core.handlemanager.domain.requests.objects.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MappingRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MasRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
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
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private static final String INVALID_TYPE_ERROR = "Invalid request. Reason: unrecognized type. Check: ";
  private static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  private final HandleRepository handleRep;
  private final FdoRecordService fdoRecordService;
  private final HandleGeneratorService hf;
  private final ObjectMapper mapper;

  // Resolve Record
  public JsonApiWrapperReadSingle resolveSingleRecord(byte[] handle, String path)
      throws PidResolutionException {
    var dbRecord = handleRep.resolveHandleAttributes(handle);
    verifyHandleResolution(List.of(handle), dbRecord);
    var recordAttributeList = formatRecords(dbRecord).get(0);
    var dataNode = wrapData(recordAttributeList, "PID");
    var linksNode = new JsonApiLinks(path);
    return new JsonApiWrapperReadSingle(linksNode, dataNode);
  }

  public JsonApiWrapperRead resolveBatchRecord(List<byte[]> handles, String path)
      throws PidResolutionException {
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    var dbRecords = handleRep.resolveHandleAttributes(handles);
    verifyHandleResolution(handles, dbRecords);
    var recordAttributeList = formatRecords(dbRecords);
    for (JsonNode recordAttributes : recordAttributeList) {
      dataList.add(wrapData(recordAttributes, "PID"));
    }
    return new JsonApiWrapperRead(new JsonApiLinks(path), dataList);
  }

  private void verifyHandleResolution(List<byte[]> handles, List<HandleAttribute> dbRecords) throws PidResolutionException {
    var resolvedHandles = dbRecords.stream()
        .map(HandleAttribute::handle)
        .map(handle -> new String(handle, StandardCharsets.UTF_8))
        .collect(Collectors.toSet());
    if (handles.size()==resolvedHandles.size()){
      return;
    }
    var handlesString = handles.stream().map(handle -> new String(handle, StandardCharsets.UTF_8)).collect(
        Collectors.toSet());
    handlesString.removeAll(resolvedHandles);
    log.error("Unable to resolve the following handles: {}", handlesString);
    throw new PidResolutionException("Handles not found: "+ handlesString);
  }


  // Response Formatting
  private List<JsonNode> formatRecords(List<HandleAttribute> dbRecord) {
    var handleMap = mapRecords(dbRecord);
    List<JsonNode> rootNodeList = new ArrayList<>();
    for (var handleRecord : handleMap.entrySet()) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord.getValue()));
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
    String pidLink = recordAttributes.get(PID.get()).asText();
    String pidName = getPidName(pidLink);
    var handleLink = new JsonApiLinks(pidLink);
    return new JsonApiDataLinks(pidName, recordType, recordAttributes, handleLink);
  }

  private JsonNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    dbRecord.forEach(
        row -> rootNode.put(row.type(), new String(row.data(), StandardCharsets.UTF_8)));
    return rootNode;
  }

  // Search by Physical Specimen Identifier
  public JsonApiWrapperWrite searchByPhysicalSpecimenId(String physicalId,
      PhysicalIdType physicalIdType, String specimenHostPid)
      throws PidResolutionException, InvalidRequestException {

    var physicalIdentifier = setPhysicalId(physicalId, physicalIdType, specimenHostPid);
    var returnedRows = handleRep.searchByNormalisedPhysicalIdentifierFullRecord(List.of(physicalIdentifier));
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
    List<T> digitalSpecimenList = new ArrayList<>();

    List<HandleAttribute> handleAttributes = new ArrayList<>();
    Map<String, ObjectType> recordTypes = new HashMap<>();

    for (var request : requests) {
      ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
      ObjectType type = ObjectType.fromString(dataNode.get(NODE_TYPE).asText());
      recordTypes.put(new String(handles.get(0), StandardCharsets.UTF_8), type);
      try {
        switch (type) {
          case HANDLE -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                HandleRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareHandleRecordAttributes(requestObject, handles.remove(0), type));
          }
          case DOI -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DoiRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareDoiRecordAttributes(requestObject, handles.remove(0), type));
          }
          case DIGITAL_SPECIMEN -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DigitalSpecimenRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareDigitalSpecimenRecordAttributes(requestObject,
                    handles.remove(0), type));
            digitalSpecimenList.add((T) requestObject);
          }
          case MEDIA_OBJECT -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MediaObjectRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMediaObjectAttributes(requestObject, handles.remove(0), type));
          }
          case ANNOTATION -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                AnnotationRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareAnnotationAttributes(requestObject, handles.remove(0), type));
          }
          case MAPPING -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MappingRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMappingAttributes(requestObject, handles.remove(0), type));
          }
          case SOURCE_SYSTEM -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                SourceSystemRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareSourceSystemAttributes(requestObject, handles.remove(0), type));
          }
          case ORGANISATION -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                OrganisationRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareOrganisationAttributes(requestObject, handles.remove(0), type));
          }
          case MAS -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES), MasRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMasRecordAttributes(requestObject, handles.remove(0), type));
          }
          default -> throw new InvalidRequestException(INVALID_TYPE_ERROR + type);
        }
      } catch (JsonProcessingException | UnprocessableEntityException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }

    if (!digitalSpecimenList.isEmpty()) {
      var requestPhysicalIds = getPhysicalIdsFromRequests(digitalSpecimenList);
      verifyNoInternalDuplicatePhysicalSpecimenObjectId(digitalSpecimenList, requestPhysicalIds);
      verifyNoRegisteredSpecimens(getPhysIdBytes(requestPhysicalIds));
    }
    log.info("Persisting new handles to db");
    handleRep.postAttributesToDb(recordTimestamp, handleAttributes);

    var postedRecordAttributes = formatRecords(handleAttributes);
    var dataList = postedRecordAttributes.stream().map(
        recordAttributes -> wrapData(recordAttributes,
            getRecordTypeFromTypeList(recordAttributes, recordTypes))).toList();
    return new JsonApiWrapperWrite(dataList);
  }

  private <T extends DigitalSpecimenRequest> void verifyNoInternalDuplicatePhysicalSpecimenObjectId(
      List<T> requests, Set<String> physicalIds)
      throws InvalidRequestException {
    if (physicalIds.size() < requests.size()) {
      throw new InvalidRequestException(
          "Bad Request. Some PhysicalSpecimenObjectIds are duplicated in request body");
    }
  }

  private void verifyNoRegisteredSpecimens(List<byte[]> physicalIds)
      throws PidCreationException {
    var registeredSpecimens = handleRep.searchByNormalisedPhysicalIdentifierFullRecord(physicalIds);
    if (!registeredSpecimens.isEmpty()) {
      var registeredHandles = listHandleNamesReturnedFromQuery(registeredSpecimens);
      throw new PidCreationException(
          "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
              + registeredHandles);
    }
  }

  public JsonApiWrapperWrite upsertDigitalSpecimens(List<JsonNode> requests)
      throws JsonProcessingException, UnprocessableEntityException, PidResolutionException,
      InvalidRequestException, PidServiceInternalError {
    var digitalSpecimenRequests = jsonNodeToDigitalSpecimenRequest(requests);

    var physicalIds = getPhysicalIdsFromRequests(digitalSpecimenRequests);
    var physicalIdsBytes = getPhysIdBytes(physicalIds);
    var upsertRequests = getRegisteredSpecimensUpsert(digitalSpecimenRequests, physicalIdsBytes);
    var upsertAttributes = prepareUpsertAttributes(upsertRequests);
    logUpdates(upsertRequests);

    var createRequests = getCreateRequests(upsertRequests, digitalSpecimenRequests);
    var newHandles = hf.genHandleList(createRequests.size());
    if (!newHandles.isEmpty()){
      log.info("Successfully minted {} new handle(s)", newHandles.size());
    }
    var createAttributes = getCreateAttributes(createRequests, newHandles);

    var allRequests = Stream.concat(
        createRequests.stream(),
        upsertRequests.stream().map(UpsertDigitalSpecimen::request)).toList();
    verifyNoInternalDuplicatePhysicalSpecimenObjectId(allRequests, physicalIds);

    var recordTimestamp = Instant.now().getEpochSecond();

    log.info("Persisting upserts to db.");
    handleRep.postAndUpdateHandles(recordTimestamp, createAttributes, upsertAttributes);

    var concatAttributes = concatHandleAttributes(createAttributes, upsertAttributes);

    return concatAndFormatUpsertResponse(concatAttributes);
  }

  private List<HandleAttribute> concatHandleAttributes(List<HandleAttribute> createAttributes, List<List<HandleAttribute>> upsertAttributes){
    List<HandleAttribute> upsertListFlat = new ArrayList<>();
    for (var upsertRecord : upsertAttributes){
      upsertListFlat.addAll(upsertRecord);
    }
    return Stream.concat(createAttributes.stream(), upsertListFlat.stream()).toList();
  }

  private JsonApiWrapperWrite concatAndFormatUpsertResponse(List<HandleAttribute> records) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var row: records){
      if (row.type().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())){
        String h = new String(row.handle(), StandardCharsets.UTF_8);
        String pidLink = HANDLE_DOMAIN + h;
        var node = mapper.createObjectNode();
        node.put(PRIMARY_SPECIMEN_OBJECT_ID.get(), new String(row.data(), StandardCharsets.UTF_8));
        dataLinksList.add(new JsonApiDataLinks(h, DIGITAL_SPECIMEN.toString(), node, new JsonApiLinks(pidLink)));
      }
    }
    return new JsonApiWrapperWrite(dataLinksList);
  }

  private void logUpdates(List<UpsertDigitalSpecimen> upsertRequests){
    var registeredHandles = upsertRequests.stream().map(UpsertDigitalSpecimen::handle).toList();
    if (!registeredHandles.isEmpty()){
      log.info("Some specimens already have handles. Updating the following PID Records {}", registeredHandles);
    }
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

  private List<byte[]> getPhysIdBytes(Set<String> physIds) {
    return physIds.stream()
        .map(physId -> physId.getBytes(StandardCharsets.UTF_8))
        .toList();
  }

  private <T extends DigitalSpecimenRequest> Set<String> getPhysicalIdsFromRequests(
      List<T> digitalSpecimenRequests) {
    return digitalSpecimenRequests.stream()
        .map(ServiceUtils::setUniquePhysicalIdentifierId)
        .collect(Collectors.toSet());
  }

  private List<UpsertDigitalSpecimen> getRegisteredSpecimensUpsert(
      List<DigitalSpecimenRequest> requests, List<byte[]> physicalIds) {
    var registeredSpecimensHandleAttributes = new HashSet<>(
        handleRep.searchByNormalisedPhysicalIdentifier(physicalIds));
    if (registeredSpecimensHandleAttributes.isEmpty()) {
      return new ArrayList<>();
    }

    ArrayList<UpsertDigitalSpecimen> upsertDigitalSpecimen = new ArrayList<>();
    for (var row : registeredSpecimensHandleAttributes){
      var targetPhysId = new String(row.data(), StandardCharsets.UTF_8);
      var targetRequest = getRequestFromPhysicalId(requests, targetPhysId);
      requests.remove(targetRequest);
      upsertDigitalSpecimen.add(new UpsertDigitalSpecimen(
          new String(row.handle(), StandardCharsets.UTF_8),
          targetPhysId,
          targetRequest
      ));
    }
    return upsertDigitalSpecimen;
  }

  private DigitalSpecimenRequest getRequestFromPhysicalId(List<DigitalSpecimenRequest> requests,
      String physicalId) {
    for (var request: requests){
      if (setUniquePhysicalIdentifierId(request).equals(physicalId)){
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
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException, PidServiceInternalError {
    var handles = new ArrayList<>(newHandles);
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var digitalSpecimenRequest : digitalSpecimenRequests) {
      handleAttributes.addAll(
          fdoRecordService.prepareDigitalSpecimenRecordAttributes(digitalSpecimenRequest,
              handles.remove(0), DIGITAL_SPECIMEN));
    }
    return handleAttributes;
  }

  private List<List<HandleAttribute>> prepareUpsertAttributes(
      List<UpsertDigitalSpecimen> upsertDigitalSpecimens)
      throws InvalidRequestException, PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
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
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
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

    handleRep.updateRecordBatch(recordTimestamp, attributesToUpdate, incrementVersion);
    return formatUpdates(attributesToUpdate, recordTypes);
  }

  private JsonApiWrapperWrite formatUpdates(List<List<HandleAttribute>> updatedRecords, Map<String, ObjectType> recordTypes){
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var updatedRecord : updatedRecords){
      String handle = new String(updatedRecord.get(0).handle(), StandardCharsets.UTF_8);
      var attributeNode = jsonFormatSingleRecord(updatedRecord);
      var type = recordTypes.get(handle).toString();
      dataList.add(new JsonApiDataLinks(handle, type, attributeNode, new JsonApiLinks(HANDLE_DOMAIN+handle)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  private JsonApiWrapperWrite formatArchives(List<List<HandleAttribute>> archiveRecords){
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (var archiveRecord : archiveRecords){
      String handle = new String(archiveRecord.get(0).handle(), StandardCharsets.UTF_8);
      var attributeNode = jsonFormatSingleRecord(archiveRecord);
      dataList.add(new JsonApiDataLinks(handle, TOMBSTONE.toString(), attributeNode, new JsonApiLinks(HANDLE_DOMAIN+handle)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  private String getRecordTypeFromTypeList(JsonNode recordAttributes,
      Map<String, ObjectType> recordTypes) {
    String pid = getPidName(recordAttributes.get(PID.get()).asText());
    return recordTypes.get(pid).toString();
  }


  private void checkHandlesWritable(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);

    Set<byte[]> handlesExist = new HashSet<>(handleRep.checkHandlesWritable(handles));
    if (handlesExist.size() < handles.size()) {
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = handlesToUpdate.stream()
          .map(h -> new String(h, StandardCharsets.UTF_8)).collect(
              Collectors.toSet());
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
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<HandleAttribute> archiveAttributesFlat = new ArrayList<>();
    List<List<HandleAttribute>> archiveAttributes = new ArrayList<>();

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

    handleRep.archiveRecords(recordTimestamp, archiveAttributesFlat,
        handles.stream().map(h -> new String(h, StandardCharsets.UTF_8)).toList());

    return formatArchives(archiveAttributes);
  }

  public void rollbackHandles(List<String> handles) {
    handleRep.rollbackHandles(handles);
  }

  public void rollbackHandlesFromPhysId(List<String> physicalIds){
    var physicalIdsBytes = physicalIds.stream().map(id -> id.getBytes(StandardCharsets.UTF_8)).toList();
    var handles = handleRep.searchByNormalisedPhysicalIdentifier(physicalIdsBytes).stream()
        .map(HandleAttribute::handle).map(handle -> new String(handle, StandardCharsets.UTF_8)).toList();
    handleRep.rollbackHandles(handles);
  }


}

