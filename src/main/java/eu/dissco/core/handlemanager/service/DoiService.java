package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DOI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.upsert.UpsertMediaResult;
import eu.dissco.core.handlemanager.domain.upsert.UpsertSpecimenResult;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(Profiles.DOI)
@Slf4j
@Service
public class DoiService extends PidService {

  private final DataCiteService dataCiteService;
  private static final Set<FdoType> VALID_FDO_TYPES = Set.of(DIGITAL_SPECIMEN, DIGITAL_MEDIA, DOI);

  public DoiService(FdoRecordService fdoRecordService,
      PidNameGeneratorService pidNameGeneratorService,
      ObjectMapper mapper, ProfileProperties profileProperties,
      DataCiteService dataCiteService, MongoRepository mongoRepository) {
    super(fdoRecordService, pidNameGeneratorService, mapper, profileProperties,
        mongoRepository);
    this.dataCiteService = dataCiteService;
  }

  private static final String TYPE_ERROR_MESSAGE = "Error creating DOI for object of Type %s. Only Digital Specimens and Media Objects use DOIs.";

  @Override
  public JsonApiWrapperWrite createRecords(List<PostRequest> requests, boolean isDraft)
      throws InvalidRequestException, UnprocessableEntityException {
    var requestAttributes = requests.stream()
        .map(request -> request.data().attributes())
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream()
        .map(request -> request.data().type())
        .toList(), VALID_FDO_TYPES);
    log.info("Processing {} {} requests", requestAttributes.size(), fdoType.getDigitalObjectName());
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> {
          return processDigitalSpecimenRequests(requestAttributes, isDraft);
        }
        case DIGITAL_MEDIA -> {
          return processDigitalMediaRequests(requestAttributes, isDraft);
        }
        case DOI -> {
          return createDoi(requestAttributes, isDraft);
        }
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, fdoType.getDigitalObjectName()));
      }
    } catch (JsonProcessingException | PidResolutionException e) {
      log.error(REQUEST_PROCESSING_ERR, e);
      throw new InvalidRequestException(REQUEST_PROCESSING_ERR);
    }
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<PatchRequest> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var fdoType = getFdoTypeFromRequest(requests.stream().map(r -> r.data().type()).toList(),
        VALID_FDO_TYPES);
    var previousVersionMap = getPreviousVersionsMap(requests);
    List<FdoRecord> fdoRecords;
    log.info("Processing {} {} update requests", requests.size(), fdoType.getDigitalObjectName());
    switch (fdoType) {
      case DIGITAL_SPECIMEN ->
          fdoRecords = processSpecimenUpdateRequests(previousVersionMap, incrementVersion);
      case DIGITAL_MEDIA ->
          fdoRecords = processMediaUpdateRequests(previousVersionMap, incrementVersion);
      case DOI -> {
        return updateExistingDoiRecord(previousVersionMap, incrementVersion);
      }
      default ->
          throw new IllegalStateException(); // This case is handled by the getFdoTypeFromRequest check
    }
    log.info("Publishing to DataCite");
    publishToDataCite(fdoRecords, EventType.UPDATE);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
  }

  // Upsert
  private JsonApiWrapperWrite processDigitalSpecimenRequests(List<JsonNode> requestAttributes,
      boolean isDraft)
      throws JsonProcessingException, InvalidRequestException, UnprocessableEntityException {
    var specimenRequests = new ArrayList<DigitalSpecimenRequestAttributes>();
    for (var request : requestAttributes) {
      specimenRequests.add(mapper.treeToValue(request, DigitalSpecimenRequestAttributes.class));
    }
    var timestamp = Instant.now();
    var processResult = processUpsertRequestSpecimen(specimenRequests);
    var updateRecords = updateExistingSpecimenRecords(processResult.updateRequests(), timestamp,
        true);
    var newRecords = createNewSpecimens(processResult.newSpecimenRequests(), timestamp, isDraft);
    var fdoRecords = Stream.concat(updateRecords.stream(), newRecords.stream()).toList();
    if (!isDraft) {
      publishToDataCite(newRecords, EventType.CREATE);
      publishToDataCite(updateRecords, EventType.UPDATE);
    }
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, DIGITAL_SPECIMEN));
  }

  protected JsonApiWrapperWrite processDigitalMediaRequests(List<JsonNode> requestAttributes,
      boolean isDraft)
      throws JsonProcessingException, InvalidRequestException, UnprocessableEntityException {
    var mediaRequests = new ArrayList<DigitalMediaRequestAttributes>();
    for (var request : requestAttributes) {
      mediaRequests.add(mapper.treeToValue(request, DigitalMediaRequestAttributes.class));
    }
    if (mediaRequests.isEmpty()) {
      return new JsonApiWrapperWrite(null);
    }
    var timestamp = Instant.now();
    var processResult = processUpsertRequestMedia(mediaRequests);
    var updateRecords = updateExistingMediaRecords(processResult.updateMediaRequests(), timestamp,
        true);
    var newRecords = createNewMedia(processResult.newMediaRequests(), timestamp, isDraft);
    var fdoRecords = Stream.concat(updateRecords.stream(), newRecords.stream()).toList();
    if (!isDraft) {
      publishToDataCite(newRecords, EventType.CREATE);
      publishToDataCite(updateRecords, EventType.UPDATE);
    }
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, DIGITAL_MEDIA));
  }

  private UpsertMediaResult processUpsertRequestMedia(
      List<DigitalMediaRequestAttributes> mediaRequests) throws JsonProcessingException {
    var existingMediaMap = getExistingRecordsFromNormalisedIds(mediaRequests
        .stream()
        .map(ServiceUtils::normalizeMediaId)
        .toList(), PRIMARY_MEDIA_ID.get()
    );
    var newMedia = new ArrayList<DigitalMediaRequestAttributes>();
    var updateMedia = new HashMap<DigitalMediaRequestAttributes, FdoRecord>();
    mediaRequests.forEach(media -> {
          if (existingMediaMap.containsKey(media.getPrimaryMediaId())) {
            updateMedia.put(media, existingMediaMap.get(media.getPrimaryMediaId()));
          } else {
            newMedia.add(media);
          }
        }
    );
    return new UpsertMediaResult(newMedia, updateMedia);
  }

  private UpsertSpecimenResult processUpsertRequestSpecimen(
      List<DigitalSpecimenRequestAttributes> specimenRequests) throws JsonProcessingException {
    var existingSpecimenMap = getExistingRecordsFromNormalisedIds(specimenRequests
        .stream()
        .map(DigitalSpecimenRequestAttributes::getNormalisedPrimarySpecimenObjectId)
        .toList(), NORMALISED_SPECIMEN_OBJECT_ID.get()
    );
    var newSpecimens = new ArrayList<DigitalSpecimenRequestAttributes>();
    var updateSpecimens = new HashMap<DigitalSpecimenRequestAttributes, FdoRecord>();
    specimenRequests.forEach(specimen -> {
          if (existingSpecimenMap.containsKey(specimen.getNormalisedPrimarySpecimenObjectId())) {
            updateSpecimens.put(specimen,
                existingSpecimenMap.get(specimen.getNormalisedPrimarySpecimenObjectId()));
          } else {
            newSpecimens.add(specimen);
          }
        }
    );
    return new UpsertSpecimenResult(newSpecimens, updateSpecimens);
  }

  // Create
  private JsonApiWrapperWrite createDoi(List<JsonNode> requestAttributes, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    var handleIterator = hf.generateNewHandles(requestAttributes.size()).iterator();
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, DoiKernelRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewDoiRecord(requestObject, handleIterator.next(), timestamp,
              isDraft));
    }
    updateDocuments(fdoRecords);
    // We don't publish DOIs to DataCite
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, DOI));
  }

  private JsonApiWrapperWrite updateExistingDoiRecord(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        DoiKernelRequestAttributes.class);
    var allFdoRecords = new ArrayList<FdoRecord>();
    var newFdoRecords = new ArrayList<FdoRecord>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion = fdoRecordService.prepareUpdatedDoiRecord(updateRequest.getKey(), timestamp,
          updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    updateDocuments(newFdoRecords);
    return new JsonApiWrapperWrite(formatFdoRecord(allFdoRecords, DOI));
  }


  private List<FdoRecord> createNewSpecimens(
      List<DigitalSpecimenRequestAttributes> digitalSpecimenRequests, Instant timestamp,
      boolean isDraft)
      throws InvalidRequestException {
    var handleIterator = hf.generateNewHandles(digitalSpecimenRequests.size()).iterator();
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var request : digitalSpecimenRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalSpecimenRecord(request, handleIterator.next(),
              timestamp, isDraft));
    }
    createDocuments(fdoRecords);
    return fdoRecords;
  }

  private List<FdoRecord> createNewMedia(
      List<DigitalMediaRequestAttributes> digitalMediaRequests, Instant timestamp, boolean isDraft)
      throws InvalidRequestException {
    var handleIterator = hf.generateNewHandles(digitalMediaRequests.size()).iterator();
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var request : digitalMediaRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalMediaRecord(request, handleIterator.next(),
              timestamp, isDraft));
    }
    createDocuments(fdoRecords);
    return fdoRecords;
  }

  // Update
  private List<FdoRecord> processSpecimenUpdateRequests(
      Map<PatchRequestData, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws InvalidRequestException {
    Map<DigitalSpecimenRequestAttributes, FdoRecord> specimenVersionMap =
        convertPatchRequestDataToAttributesClass(previousVersionMap,
            DigitalSpecimenRequestAttributes.class);
    var timestamp = Instant.now();
    return updateExistingSpecimenRecords(specimenVersionMap, timestamp, incrementVersion);
  }

  private List<FdoRecord> processMediaUpdateRequests(
      Map<PatchRequestData, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws InvalidRequestException {
    Map<DigitalMediaRequestAttributes, FdoRecord> mediaVersionMap =
        convertPatchRequestDataToAttributesClass(previousVersionMap,
            DigitalMediaRequestAttributes.class);
    var timestamp = Instant.now();
    return updateExistingMediaRecords(mediaVersionMap, timestamp, incrementVersion);
  }

  protected List<FdoRecord> updateExistingSpecimenRecords(
      Map<DigitalSpecimenRequestAttributes, FdoRecord> updateRequests, Instant timestamp,
      boolean updateVersion)
      throws InvalidRequestException {
    var allFdoRecords = new ArrayList<FdoRecord>();
    var newFdoRecords = new ArrayList<FdoRecord>();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion = fdoRecordService.prepareUpdatedDigitalSpecimenRecord(
          updateRequest.getKey(), timestamp,
          updateRequest.getValue(), updateVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    updateDocuments(newFdoRecords);
    return allFdoRecords;
  }

  private List<FdoRecord> updateExistingMediaRecords(
      Map<DigitalMediaRequestAttributes, FdoRecord> updateRequests, Instant timestamp,
      boolean incrementVersion)
      throws InvalidRequestException {
    if (updateRequests.isEmpty()) {
      return Collections.emptyList();
    }
    var allFdoRecords = new ArrayList<FdoRecord>();
    var newFdoRecords = new ArrayList<FdoRecord>();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion = fdoRecordService.prepareUpdatedDigitalMediaRecord(
          updateRequest.getKey(), timestamp,
          updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    updateDocuments(newFdoRecords);
    return allFdoRecords;
  }

  @Override
  public JsonApiWrapperWrite tombstoneRecords(List<PatchRequest> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    var result = super.tombstoneRecords(requests);
    for (var request : requests) {
      try {
        var tombstoneAttributes = mapper.treeToValue(request.data().attributes(),
            TombstoneRequestAttributes.class);
        dataCiteService.tombstoneDataCite(request.data().id(),
            tombstoneAttributes.getHasRelatedPid());
      } catch (JsonProcessingException e) {
        log.error("Unable to tombstone doi {} with datacite", request.data().id());
      }
    }
    return result;
  }

  protected Map<String, FdoRecord> getExistingRecordsFromNormalisedIds(List<String> normalisedIds,
      String localIdName)
      throws JsonProcessingException {
    var existingHandles = mongoRepository
        .searchByPrimaryLocalId(localIdName, normalisedIds)
        .stream()
        .toList();
    if (!existingHandles.isEmpty()) {
      var handleMap = existingHandles.stream()
          .collect(Collectors.toMap(
              FdoRecord::primaryLocalId,
              f -> f));
      log.info(
          "Some handles already exist. Updating the following records :{}",
          handleMap.values().stream().map(FdoRecord::primaryLocalId).toList());
      return handleMap;
    }
    return Map.of();
  }

  private void publishToDataCite(List<FdoRecord> fdoRecords, EventType eventType)
      throws UnprocessableEntityException {
    var eventList = fdoRecords.stream()
        .map(fdoRecord -> new DataCiteEvent(jsonFormatSingleRecord(fdoRecord.values()),
            eventType)).toList();
    for (var event : eventList) {
      try {
        dataCiteService.publishToDataCite(event, fdoRecords.get(0).fdoType());
      } catch (JsonProcessingException e) {
        log.error("Critical error: Unable to publish datacite event to queue", e);
        if (eventType.equals(EventType.CREATE)) {
          log.info("Rolling back handles");
          rollbackHandles(fdoRecords.stream().map(FdoRecord::handle).toList());
        }
        log.error("Unable to publish datacite event to queue", e);
        throw new UnprocessableEntityException("Unable to publish datacite event to queue");
      }
    }
  }
}
