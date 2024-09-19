package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRequest;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(Profiles.DOI)
@Slf4j
@Service
public class DoiService extends PidService {

  private final DataCiteService dataCiteService;

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
  public JsonApiWrapperWrite createRecords(List<PostRequest> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    var requestAttributes = requests.stream()
        .map(request -> request.data().attributes())
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream()
        .map(request -> request.data().type())
        .toList());
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> {
          return processDigitalSpecimenRequests(requestAttributes, true);
        }
        case DIGITAL_MEDIA -> {
          return processDigitalMediaRequests(requestAttributes, true);
        }
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, fdoType.getDigitalObjectName()));
      }
    } catch (JsonProcessingException | PidResolutionException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: "
              + e.getMessage());
    }
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<PatchRequest> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var updateRequestsAttributes = requests.stream()
        .map(PatchRequest::data)
        .map(PatchRequestData::attributes)
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream().map(r -> r.data().type()).toList());
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> {
          return processDigitalSpecimenRequests(updateRequestsAttributes, incrementVersion);
        }
        case DIGITAL_MEDIA -> {
          return processDigitalMediaRequests(updateRequestsAttributes, incrementVersion);
        }
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, fdoType.getDigitalObjectName()));
      }
    } catch (JsonProcessingException e) {
      log.error("An error has occurred processing JSON data", e);
      throw new UnprocessableEntityException("Json Processing Error");
    }
  }

  // Upsert
  private JsonApiWrapperWrite processDigitalSpecimenRequests(List<JsonNode> requestAttributes,
      boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException, UnprocessableEntityException {
    var specimenRequests = new ArrayList<DigitalSpecimenRequestAttributes>();
    for (var request : requestAttributes) {
      specimenRequests.add(mapper.treeToValue(request, DigitalSpecimenRequestAttributes.class));
    }
    var timestamp = Instant.now();
    var processResult = processUpsertRequestSpecimen(specimenRequests);
    var updateRecords = updateExistingSpecimenRecords(processResult.updateRequests(), timestamp,
        incrementVersion);
    var newRecords = createNewSpecimens(processResult.newSpecimenRequests(), timestamp);
    var fdoRecords = Stream.concat(updateRecords.stream(), newRecords.stream()).toList();
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, DIGITAL_SPECIMEN));
  }

  protected JsonApiWrapperWrite processDigitalMediaRequests(List<JsonNode> requestAttributes,
      boolean incrementVersion)
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
        incrementVersion);
    var newRecords = createNewMedia(processResult.newMediaRequests(), timestamp);
    var fdoRecords = Stream.concat(updateRecords.stream(), newRecords.stream()).toList();
    publishToDataCite(newRecords, EventType.CREATE);
    publishToDataCite(updateRecords, EventType.UPDATE);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, DIGITAL_SPECIMEN));
  }

  private UpsertMediaResult processUpsertRequestMedia(
      List<DigitalMediaRequestAttributes> mediaRequests) throws JsonProcessingException {
    var existingSpecimenMap = identifyExistingRecords(mediaRequests
        .stream()
        .map(DigitalMediaRequestAttributes::getPrimaryMediaId)
        .toList()
    );
    var newMedia = new ArrayList<DigitalMediaRequestAttributes>();
    var updateMedia = new HashMap<DigitalMediaRequestAttributes, FdoRecord>();
    mediaRequests.forEach(media -> {
          if (existingSpecimenMap.containsKey(media.getPrimaryMediaId())) {
            updateMedia.put(media, existingSpecimenMap.get(media.getPrimaryMediaId()));
          } else {
            newMedia.add(media);
          }
        }
    );
    return new UpsertMediaResult(newMedia, updateMedia);
  }

  private UpsertSpecimenResult processUpsertRequestSpecimen(
      List<DigitalSpecimenRequestAttributes> specimenRequests) throws JsonProcessingException {
    var existingSpecimenMap = identifyExistingRecords(specimenRequests
        .stream()
        .map(DigitalSpecimenRequestAttributes::getNormalisedPrimarySpecimenObjectId)
        .toList()
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
  private List<FdoRecord> createNewSpecimens(
      List<DigitalSpecimenRequestAttributes> digitalSpecimenRequests, Instant timestamp)
      throws InvalidRequestException {
    var handleIterator = hf.generateNewHandles(digitalSpecimenRequests.size()).iterator();
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var request : digitalSpecimenRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalSpecimenRecord(request, handleIterator.next(),
              timestamp));
    }
    createDocuments(fdoRecords);
    return fdoRecords;
  }

  private List<FdoRecord> createNewMedia(
      List<DigitalMediaRequestAttributes> digitalMediaRequests, Instant timestamp)
      throws InvalidRequestException {
    var handleIterator = hf.generateNewHandles(digitalMediaRequests.size()).iterator();
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var request : digitalMediaRequests) {
      fdoRecords.add(
          fdoRecordService.prepareNewDigitalMediaRecord(request, handleIterator.next(),
              timestamp));
    }
    createDocuments(fdoRecords);
    return fdoRecords;
  }

  private void createDocuments(List<FdoRecord> fdoRecords)
      throws InvalidRequestException {
    List<Document> fdoDocuments;
    try {
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    mongoRepository.postHandleRecords(fdoDocuments);
    log.info("Successfully posted {} new specimen fdo records to database", fdoDocuments.size());
  }

  // Update

  protected List<FdoRecord> updateExistingSpecimenRecords(
      Map<DigitalSpecimenRequestAttributes, FdoRecord> updateRequests, Instant timestamp,
      boolean updateVersion)
      throws InvalidRequestException {
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var updateRequest : updateRequests.entrySet()) {
      fdoRecords.add(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(
          updateRequest.getKey(), timestamp,
          updateRequest.getValue(), updateVersion));
    }
    updateDocuments(fdoRecords);
    return fdoRecords;
  }

  private List<FdoRecord> updateExistingMediaRecords(
      Map<DigitalMediaRequestAttributes, FdoRecord> updateRequests, Instant timestamp,
      boolean incrementVersion)
      throws InvalidRequestException {
    var fdoRecords = new ArrayList<FdoRecord>();
    for (var updateRequest : updateRequests.entrySet()) {
      fdoRecords.add(fdoRecordService.prepareUpdatedDigitalMediaRecord(
          updateRequest.getKey(), timestamp,
          updateRequest.getValue(), incrementVersion));
    }
    updateDocuments(fdoRecords);
    return fdoRecords;
  }

  private void updateDocuments(List<FdoRecord> fdoRecords)
      throws InvalidRequestException {
    List<Document> fdoDocuments;
    try {
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    mongoRepository.updateHandleRecords(fdoDocuments);
    log.info("Successfully updated {} specimens fdo records to database", fdoDocuments.size());

  }

  private Map<String, FdoRecord> identifyExistingRecords(List<String> normalisedIds)
      throws JsonProcessingException {
    var existingHandles = mongoRepository
        .searchByPrimaryLocalId(NORMALISED_SPECIMEN_OBJECT_ID.get(), normalisedIds)
        .stream().filter(PidService::handlesAreActive)
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


  @Override
  public JsonApiWrapperWrite tombstoneRecords(List<TombstoneRequest> requests)
      throws InvalidRequestException {
    var result = super.tombstoneRecords(requests);
    for (var request : requests) {
      try {
        dataCiteService.tombstoneDataCite(request.data().id(),
            request.data().attributes().getHasRelatedPid());
      } catch (JsonProcessingException e) {
        log.error("Unable to tombstone doi {} with datacite", request.data().id());
      }
    }
    return result;
  }

  private void publishToDataCite(List<FdoRecord> fdoRecords, EventType eventType)
      throws UnprocessableEntityException {
    var eventList = fdoRecords.stream()
        .map(fdoRecord -> new DataCiteEvent(jsonFormatSingleRecord(fdoRecord.attributes()),
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
