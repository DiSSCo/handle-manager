package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.TOMBSTONE;
import static eu.dissco.core.handlemanager.service.FdoRecordService.GENERATED_KEYS;
import static eu.dissco.core.handlemanager.service.ServiceUtils.getField;
import static eu.dissco.core.handlemanager.service.ServiceUtils.toSingle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.responses.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestRuntimeException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
  protected static final String REQUEST_PROCESSING_ERR = "An error has occurred parsing a record in request";
  private static final Set<String> MEDIA_KEY_SET = Set.of(PRIMARY_MEDIA_ID.name(),
      LINKED_DO_PID.name());

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
    for (var fdoRecord : fdoRecords) {
      var mediaUrl = getField(fdoRecord.attributes(), PRIMARY_MEDIA_ID.index());
      var digitalSpecimenId = getField(fdoRecord.attributes(), LINKED_DO_PID.index());
      if (mediaUrl == null || digitalSpecimenId == null) {
        log.error("Missing key attributes for media record");
        throw new IllegalStateException();
      }
      var key = mapper.createObjectNode().set("digitalMediaKey",
          mapper.valueToTree(
              new DigitalMediaKey(digitalSpecimenId.getValue(), mediaUrl.getValue())));
      String pidLink = profileProperties.getDomain() + fdoRecord.handle();
      dataLinksList.add(
          new JsonApiDataLinks(fdoRecord.handle(), DIGITAL_MEDIA.getDigitalObjectType(),
              key, new JsonApiLinks(pidLink)));
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
      var missingHandles = new ArrayList<>(handles);
      missingHandles.removeAll(fdoRecords.stream().map(FdoRecord::handle).toList());
      log.error("Some handles do not exist: {}", missingHandles);
      throw new PidResolutionException(
          "Attempting to resolve handles that do not exist: " + missingHandles);
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
  public abstract JsonApiWrapperWrite createRecords(List<PostRequest> requests, boolean isDraft)
      throws InvalidRequestException, UnprocessableEntityException;

  // Activate
  public void activateRecords(List<String> handles) throws InvalidRequestException {
    List<FdoRecord> draftRecords;
    try {
      draftRecords = mongoRepository.getHandleRecords(handles);
    } catch (JsonProcessingException e) {
      log.error("Unable to read PID record", e);
      throw new InvalidRequestException("Unable to read PID record");
    }
    var timestamp = Instant.now();
    var activeRecords = new ArrayList<FdoRecord>();
    for (var draftRecord : draftRecords) {
      activeRecords.add(fdoRecordService.activatePidRecord(draftRecord, timestamp));
    }
    updateDocuments(activeRecords);
  }

  // Update
  public abstract JsonApiWrapperWrite updateRecords(List<PatchRequest> requests,
      boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException;

  protected static FdoType getFdoTypeFromRequest(List<FdoType> fdoTypes,
      Set<FdoType> validFdoTypes) {
    var uniqueTypes = new HashSet<>(fdoTypes);
    if (uniqueTypes.size() != 1) {
      throw new UnsupportedOperationException("Requests must all be of the same type");
    }
    var fdoType = uniqueTypes.iterator().next();
    if (!validFdoTypes.contains(fdoType)) {
      log.error("Type {} is an invalid FDO type for this endpoint.", fdoType);
      throw new UnsupportedOperationException(
          "Type" + fdoType + " is an invalid FDO type for this endpoint.");
    }
    return fdoType;
  }

  // Tombstone
  public JsonApiWrapperWrite tombstoneRecords(List<PatchRequest> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    var prev = getPreviousVersionsMap(requests);
    var map = convertPatchRequestDataToAttributesClass(prev, TombstoneRequestAttributes.class);
    var fdoRecords = new ArrayList<FdoRecord>();
    var timestamp = Instant.now();
    List<Document> fdoDocuments;
    try {
      for (var request : map.entrySet()) {
        fdoRecords.add(fdoRecordService.prepareTombstoneRecord(request.getKey(), timestamp,
            request.getValue()));
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error("JsonProcessingException while tombstoning records", e);
      throw new InvalidRequestException(REQUEST_PROCESSING_ERR);
    }
    mongoRepository.updateHandleRecords(fdoDocuments);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, TOMBSTONE));
  }

  public void rollbackHandles(List<String> handles) {
    mongoRepository.rollbackHandles(handles);
    log.info("Successfully rolled back handles");
    log.debug("Rolled back handles: {}", handles);
  }

  public void rollbackHandlesFromPhysId(List<String> physicalIds) {
    var rollbackCount = mongoRepository.rollbackHandlesFromLocalId(
        NORMALISED_SPECIMEN_OBJECT_ID.get(), physicalIds);
    log.info("Successfully rolled back {} handles", rollbackCount);
    log.debug("Rolled back physical ids: {}", physicalIds);
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

  protected Map<PatchRequestData, FdoRecord> getPreviousVersionsMap(List<PatchRequest> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    List<FdoRecord> previousVersions;
    var patchRequestData = requests.stream().map(PatchRequest::data).toList();
    var handles = patchRequestData.stream().map(PatchRequestData::id).toList();
    try {
      previousVersions = mongoRepository.getHandleRecords(handles);
    } catch (JsonProcessingException e) {
      throw new InvalidRequestException("Unable to process handles resolution");
    }
    if (previousVersions.size() < handles.size()) {
      throw new InvalidRequestException("Unable to resolve all handles");
    }
    try {
      return previousVersions.stream().collect(Collectors.toMap(
          fdo -> patchRequestData.stream().filter(data -> data.id().equals(fdo.handle()))
              .collect(toSingle()),
          Function.identity()
      ));
    } catch (RuntimeException e) {
      throw new UnprocessableEntityException(REQUEST_PROCESSING_ERR);
    }
  }

  protected <T> Map<T, FdoRecord> convertPatchRequestDataToAttributesClass(
      Map<PatchRequestData, FdoRecord> previousVersionMap, Class<T> targetClass)
      throws InvalidRequestException {
    try {
      return previousVersionMap.entrySet().stream()
          .collect(Collectors.toMap(
              e -> convertKeyToAttributes(e.getKey(), targetClass),
              Map.Entry::getValue
          ));
    } catch (InvalidRequestRuntimeException e) {
      throw new InvalidRequestException(REQUEST_PROCESSING_ERR);
    }
  }

  protected <T> T convertKeyToAttributes(PatchRequestData key, Class<T> targetClass) {
    try {
      return mapper.treeToValue(key.attributes(), targetClass);
    } catch (JsonProcessingException e) {
      log.error(REQUEST_PROCESSING_ERR, e);
      throw new InvalidRequestRuntimeException();
    }
  }

  protected static boolean fdoRecordsAreDifferent(FdoRecord newVersion, FdoRecord currentVersion) {
    var currentAttributes = currentVersion.attributes();
    if (newVersion.attributes().size() != currentAttributes.size()) {
      return true;
    }
    for (var newAttribute : newVersion.attributes()) {
      if (!GENERATED_KEYS.contains(newAttribute.getIndex())) {
        var currentAttribute = getField(currentAttributes, newAttribute.getIndex());
        if (currentAttribute == null || !currentAttribute.getValue()
            .equals(newAttribute.getValue())) {
          return true;
        }
      }
    }
    return false;
  }

  protected void createDocuments(List<FdoRecord> fdoRecords)
      throws InvalidRequestException {
    List<Document> fdoDocuments;
    try {
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error(REQUEST_PROCESSING_ERR, e);
      throw new InvalidRequestException(REQUEST_PROCESSING_ERR);
    }
    mongoRepository.postHandleRecords(fdoDocuments);
    log.info("Successfully posted {} fdo records to database", fdoDocuments.size());
  }

  protected void updateDocuments(List<FdoRecord> fdoRecords)
      throws InvalidRequestException {
    List<Document> fdoDocuments;
    try {
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error(REQUEST_PROCESSING_ERR, e);
      throw new InvalidRequestException(
          REQUEST_PROCESSING_ERR);
    }
    mongoRepository.updateHandleRecords(fdoDocuments);
    log.info("Successfully updated {} specimens fdo records to database", fdoDocuments.size());
  }

  private record DigitalMediaKey(
      String digitalSpecimenId, String mediaUrl
  ) {

  }

}
