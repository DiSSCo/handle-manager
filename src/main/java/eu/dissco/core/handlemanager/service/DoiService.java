package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MEDIA_OBJECT;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidMongoRepository;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(Profiles.DOI)
@Slf4j
@Service
public class DoiService extends PidService {

  private final DataCiteService dataCiteService;

  public DoiService(PidRepository pidRepository,
      FdoRecordService fdoRecordService, PidNameGeneratorService pidNameGeneratorService,
      ObjectMapper mapper, ProfileProperties profileProperties,
      DataCiteService dataCiteService, PidMongoRepository mongoRepository) {
    super(pidRepository, fdoRecordService, pidNameGeneratorService, mapper, profileProperties,
        mongoRepository);
    this.dataCiteService = dataCiteService;
  }

  private static final String TYPE_ERROR_MESSAGE = "Error creating DOI for object of Type %s. Only Digital Specimens and Media Objects use DOIs.";

  @Override
  public JsonApiWrapperWrite createRecords(List<JsonNode> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    var handles = hf.genHandleList(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_ATTRIBUTES)).toList();
    var type = getObjectTypeFromJsonNode(requests);
    List<Document> fdoDocuments;
    List<FdoRecord> fdoRecords;
    try {
      switch (type) {
        case DIGITAL_SPECIMEN -> fdoRecords = createDigitalSpecimen(requestAttributes, handles);
        case MEDIA_OBJECT -> fdoRecords = createMediaObject(requestAttributes, handles);
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, type.getDigitalObjectName()));
      }
      fdoDocuments = fdoRecordService.toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException | PidResolutionException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: "
              + e.getMessage());
    }
    log.info("Persisting new DOIs to Document Store");
    mongoRepository.postBatchHandleRecord(fdoDocuments);
    log.info("Publishing to DataCite");
    //todo publishToDataCite(fdoRecords, EventType.CREATE, type);
    return new JsonApiWrapperWrite(formatCreateRecord(fdoRecords, type));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests)
      throws InvalidRequestException, UnprocessableEntityException {
    var updateRequests = requests.stream()
        .map(request -> request.get(NODE_DATA)).toList();
    var handles = updateRequests.stream()
        .map(request -> request.get(NODE_ID).asText()).toList();
    var previousVersions = getPreviousVersions(handles);
    checkInternalDuplicates(handles);
    checkHandlesWritableFullRecord(previousVersions);
    var fdoRecordMap = previousVersions.stream()
        .collect(Collectors.toMap(FdoRecord::handle, f -> f));
    var type = getObjectTypeFromJsonNode(requests);
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (type) {
        case DIGITAL_SPECIMEN -> fdoRecords = updateDigitalSpecimen(updateRequests, fdoRecordMap);
        case MEDIA_OBJECT -> fdoRecords = updateDigitalMedia(updateRequests, fdoRecordMap);
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, type.getDigitalObjectName()));
      }
      fdoDocuments = fdoRecordService.toMongoDbDocument(fdoRecords);
      var fdoType = getObjectTypeFromJsonNode(requests);
      mongoRepository.updateHandleRecord(fdoDocuments, handles);
      // todo figure out response
      return new JsonApiWrapperWrite(formatCreateRecord(fdoRecords, fdoType));
    } catch (JsonProcessingException e) {
      log.error("An error has occurred processing JSON data", e);
      throw new UnprocessableEntityException("Json Processing Error");
    }
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var type = getObjectTypeFromJsonNode(requests);
    if (!DIGITAL_SPECIMEN.equals(type) && !MEDIA_OBJECT.equals(type)) {
      throw new InvalidRequestException(
          String.format(TYPE_ERROR_MESSAGE, type.getDigitalObjectName()));
    }
    var attributesToUpdate = getAttributesToUpdate(requests);
    var response = updateRecords(attributesToUpdate, incrementVersion, type);
    log.info("Publishing to datacite");
    var flatList = attributesToUpdate.stream().flatMap(List::stream).toList();
    publishToDataCite(flatList, EventType.UPDATE, type);
    return response;
  }

  private void publishToDataCite(List<HandleAttribute> handleAttributes, EventType eventType,
      FdoType objectType) throws UnprocessableEntityException {
    var handleMap = mapRecords(handleAttributes);
    var eventList = new ArrayList<DataCiteEvent>();
    handleMap.forEach(
        (key, value) -> {
          if (eventType.equals(EventType.UPDATE)) {
            value.add(
                new HandleAttribute(FdoProfile.PID, key.getBytes(StandardCharsets.UTF_8),
                    key));
          }
          eventList.add(
              new DataCiteEvent(jsonFormatSingleRecordOld(value), eventType));
        });

    for (var event : eventList) {
      try {
        dataCiteService.publishToDataCite(event, objectType);
      } catch (JsonProcessingException e) {
        log.error("Critical error: Unable to publish datacite event to queue", e);
        log.info("Rolling back handles");
        if (eventType.equals(EventType.CREATE)) {
          rollbackHandles(new ArrayList<>(handleMap.keySet()));
        }
        throw new UnprocessableEntityException("Unable to publish datacite event to queue");
      }
    }
  }
}
