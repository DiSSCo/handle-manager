package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.TOMBSTONE;
import static eu.dissco.core.handlemanager.service.ServiceUtils.getField;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.PidStatus;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRequestData;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
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
import org.bson.Document;

@Slf4j
@RequiredArgsConstructor
public abstract class PidService {

  protected final FdoRecordService fdoRecordService;
  protected final PidNameGeneratorService hf;
  protected final ObjectMapper mapper;
  protected final ProfileProperties profileProperties;
  protected final MongoRepository mongoRepository;

  protected JsonNode jsonFormatSingleRecord(List<FdoAttribute> fdoAttributes) {
    ObjectNode rootNode = mapper.createObjectNode();
    fdoAttributes.stream()
        .filter(attribute -> attribute.getIndex() != HS_ADMIN.index())
        .forEach(attribute -> setNodeData(attribute, rootNode));
    return rootNode;
  }

  protected JsonNode jsonFormatSingleRecord(List<FdoAttribute> fdoAttributes,
      List<FdoProfile> keyAttributes) {
    ObjectNode rootNode = mapper.createObjectNode();
    var indexList = keyAttributes.stream().map(FdoProfile::index).toList();
    fdoAttributes.stream()
        .filter(attribute -> indexList.contains(attribute.getIndex()))
        .forEach(attribute -> setNodeData(attribute, rootNode));
    return rootNode;
  }

  private void setNodeData(FdoAttribute attribute, ObjectNode rootNode) {
    if (attribute.getValue() == null) {
      rootNode.set(attribute.getType(), mapper.nullNode());
    } else {
      try {
        var nodeData = mapper.readTree(attribute.getValue());
        rootNode.set(attribute.getType(), nodeData);
      } catch (JsonProcessingException ignored) {
        rootNode.put(attribute.getType(), attribute.getValue());
      }
    }
  }

  protected List<JsonApiDataLinks> formatFdoRecord(List<FdoRecord> fdoRecords,
      FdoType fdoType) {
    switch (fdoType) {
      case ANNOTATION -> {
        return formatAnnotationResponse(fdoRecords);
      }
      case DIGITAL_SPECIMEN -> {
        return formatSpecimenResponse(fdoRecords);
      }
      case DIGITAL_MEDIA -> {
        return formatMediaResponse(fdoRecords);
      }
      default -> {
        return formatFullRecordResponse(fdoRecords);
      }
    }
  }

  private List<JsonApiDataLinks> formatAnnotationResponse(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      JsonNode attributeNode;
      if (handleRecord.primaryLocalId() == null) {
        attributeNode = jsonFormatSingleRecord(handleRecord.attributes());
      } else {
        attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
            List.of(ANNOTATION_HASH));
      }
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), ANNOTATION.getDigitalObjectType(),
              attributeNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatSpecimenResponse(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
          List.of(NORMALISED_SPECIMEN_OBJECT_ID));
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), DIGITAL_SPECIMEN.getDigitalObjectType(),
              attributeNode, new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatMediaResponse(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var handleRecord : fdoRecords) {
      var attributeNode = jsonFormatSingleRecord(handleRecord.attributes(),
          List.of(PRIMARY_MEDIA_ID, LINKED_DO_PID));
      String pidLink = profileProperties.getDomain() + handleRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(handleRecord.handle(), DIGITAL_MEDIA.getDigitalObjectType(),
              attributeNode, new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  private List<JsonApiDataLinks> formatFullRecordResponse(List<FdoRecord> fdoRecords) {
    List<JsonApiDataLinks> dataLinksList = new ArrayList<>();
    for (var fdoRecord : fdoRecords) {
      var rootNode = jsonFormatSingleRecord(fdoRecord.attributes());
      String pidLink = profileProperties.getDomain() + fdoRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(fdoRecord.handle(), fdoRecord.fdoType().getDigitalObjectType(),
              rootNode,
              new JsonApiLinks(pidLink)));
    }
    return dataLinksList;
  }

  // Getters
  public JsonApiWrapperRead resolveSingleRecord(String handle, String path)
      throws PidResolutionException {
    return resolveBatchRecord(List.of(handle), path);
  }

  public JsonApiWrapperRead resolveBatchRecord(List<String> handles, String path)
      throws PidResolutionException {
    List<FdoRecord> fdoRecords;
    try {
      fdoRecords = mongoRepository.getHandleRecords(handles);
    } catch (JsonProcessingException e) {
      log.error("JsonProcessingException when resolving handles {}", handles, e);
      throw new PidResolutionException("Unable to resolve handle records for handles: " + handles);
    }
    if (fdoRecords.size() < handles.size()) {
      var hdl = new ArrayList<>(handles);
      var missingHandles = hdl.removeAll(fdoRecords.stream().map(FdoRecord::handle).toList());
      log.error("Some handles do not exist: {}", missingHandles);
      throw new PidResolutionException(
          "Attempting to resolve handles that do not exist: \n" + missingHandles);
    }
    return new JsonApiWrapperRead(new JsonApiLinks(path), formatFullRecordResponse(fdoRecords));
  }

  public JsonApiWrapperWrite searchByPhysicalSpecimenId(String normalisedPhysicalId)
      throws PidResolutionException {
    List<FdoRecord> specimen;
    try {
      specimen = mongoRepository.searchByPrimaryLocalId(NORMALISED_SPECIMEN_OBJECT_ID.get(),
          List.of(normalisedPhysicalId));
      if (specimen.size() != 1) {
        if (specimen.size() > 1) {
          log.error("Multiple fdo records found for normalised specimen id {}",
              normalisedPhysicalId);
        }
        throw new PidResolutionException(
            "Unable to resolve specimen with id" + normalisedPhysicalId);
      }
    } catch (JsonProcessingException e) {
      log.error(
          "JsonProcessingException while reading record for specimen with normalised object id {}",
          normalisedPhysicalId, e);
      throw new PidResolutionException(
          "Unable to resolve specimen with with id " + normalisedPhysicalId);
    }
    return new JsonApiWrapperWrite(formatFullRecordResponse(specimen));
  }

  // Create
  public abstract JsonApiWrapperWrite createRecords(List<PostRequest> requests)
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
  public abstract JsonApiWrapperWrite updateRecords(List<PatchRequest> requests,
      boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException;

  protected void checkHandlesWritable(List<FdoRecord> previousVersions)
      throws InvalidRequestException {
    for (var fdoRecord : previousVersions) {
      var pidStatus = getField(fdoRecord.attributes(), PID_STATUS).getValue();
      if (PidStatus.TOMBSTONED.name().equals(pidStatus)) {
        log.error("Attempting to update a FDO record that has been archived");
        throw new InvalidRequestException("This PID has already been archived. It is read-only");
      }
    }
  }

  protected Map<String, FdoRecord> processUpdateRequest(List<String> handles)
      throws InvalidRequestException {
    checkInternalDuplicates(handles);
    var previousVersions = getPreviousVersions(handles);
    checkHandlesWritable(previousVersions);
    return previousVersions.stream()
        .collect(Collectors.toMap(FdoRecord::handle, f -> f));
  }

  protected FdoType getFdoTypeFromRequest(List<FdoType> fdoTypes) {
    var uniqueTypes = new HashSet<>(fdoTypes);
    if (uniqueTypes.size() != 1) {
      throw new UnsupportedOperationException("Requests must all be of the same type");
    }
    return uniqueTypes.iterator().next();
  }

  protected List<FdoRecord> createDigitalSpecimen(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    var specimenRequests = new ArrayList<DigitalSpecimenRequestAttributes>();
    for (var request : requestAttributes) {
      specimenRequests.add(mapper.treeToValue(request, DigitalSpecimenRequestAttributes.class));
    }
    if (specimenRequests.isEmpty()) {
      return Collections.emptyList();
    }
    verifyObjectsAreNew(specimenRequests
        .stream()
        .map(DigitalSpecimenRequestAttributes::getNormalisedPrimarySpecimenObjectId)
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

  protected List<FdoRecord> updateDigitalSpecimen(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          DigitalSpecimenRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDigitalSpecimenRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
    }
    return fdoRecords;
  }

  protected List<FdoRecord> createDigitalMedia(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    List<DigitalMediaRequestAttributes> mediaRequests = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      mediaRequests.add(mapper.treeToValue(request, DigitalMediaRequestAttributes.class));
    }
    if (mediaRequests.isEmpty()) {
      return Collections.emptyList();
    }
    verifyObjectsAreNew(mediaRequests
        .stream()
        .map(DigitalMediaRequestAttributes::getPrimaryMediaId)
        .toList());
    for (var mediaRequest : mediaRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalMediaRecord(mediaRequest, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  protected List<FdoRecord> updateDigitalMedia(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          DigitalMediaRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDigitalMediaRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
    }
    return fdoRecords;
  }

  private void verifyObjectsAreNew(List<String> normalisedIds)
      throws InvalidRequestException, JsonProcessingException {
    var existingHandles = mongoRepository
        .searchByPrimaryLocalId(NORMALISED_SPECIMEN_OBJECT_ID.get(), normalisedIds);
    if (!existingHandles.isEmpty()) {
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

  protected void checkInternalDuplicates(List<String> handles) throws InvalidRequestException {
    Set<String> handlesToUpdate = new HashSet<>(handles);
    if (handlesToUpdate.size() < handles.size()) {
      Set<String> duplicateHandles = handles.stream()
          .filter(i -> Collections.frequency(handles, i) > 1)
          .collect(Collectors.toSet());
      throw new InvalidRequestException(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }

  // Tombstone
  public JsonApiWrapperWrite tombstoneRecords(List<TombstoneRequest> requests)
      throws InvalidRequestException {
    var tombstoneRequestData = requests.stream()
        .map(TombstoneRequest::data).toList();
    var fdoRecordMap = processUpdateRequest(
        tombstoneRequestData.stream().map(TombstoneRequestData::id).toList());
    var fdoRecords = new ArrayList<FdoRecord>();
    var timestamp = Instant.now();
    List<Document> fdoDocuments;
    try {
      for (var requestData : tombstoneRequestData) {
        fdoRecords.add(fdoRecordService.prepareTombstoneRecord(requestData.attributes(), timestamp,
            fdoRecordMap.get(requestData.id())));
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error("JsonProcessingException while tombstoning records", e);
      throw new InvalidRequestException("Unable to read request");
    }
    mongoRepository.updateHandleRecords(fdoDocuments);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, TOMBSTONE));
  }

  public void rollbackHandles(List<String> handles) {
    mongoRepository.rollbackHandles(handles);
  }

  public void rollbackHandlesFromPhysId(List<String> physicalIds) {
    mongoRepository.rollbackHandles(NORMALISED_SPECIMEN_OBJECT_ID.get(), physicalIds);
  }


  protected List<Document> toMongoDbDocument(List<FdoRecord> fdoRecords)
      throws JsonProcessingException {
    var documentList = new ArrayList<Document>();
    for (var fdoRecord : fdoRecords) {
      var doc = Document.parse(mapper.writeValueAsString(fdoRecord));
      addLocalId(fdoRecord, doc);
      documentList.add(doc);
    }
    return documentList;
  }

  private void addLocalId(FdoRecord fdoRecord, Document doc) {
    if (fdoRecord.primaryLocalId() == null) {
      return;
    }
    if (DIGITAL_SPECIMEN.equals(fdoRecord.fdoType())) {
      doc.append(NORMALISED_SPECIMEN_OBJECT_ID.get(), fdoRecord.primaryLocalId());
    } else if (DIGITAL_MEDIA.equals(fdoRecord.fdoType())) {
      doc.append(PRIMARY_MEDIA_ID.get(), fdoRecord.primaryLocalId());
    } else if (ANNOTATION.equals(fdoRecord.fdoType())) {
      doc.append(ANNOTATION_HASH.get(), fdoRecord.primaryLocalId());
    }
  }


}
