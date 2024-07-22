package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import java.util.List;
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
    var handles = hf.generateNewHandles(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.data().attributes())
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream()
        .map(request -> request.data().type())
        .toList());
    List<Document> fdoDocuments;
    List<FdoRecord> fdoRecords;
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> fdoRecords = createDigitalSpecimen(requestAttributes, handles);
        case DIGITAL_MEDIA -> fdoRecords = createDigitalMedia(requestAttributes, handles);
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, fdoType.getDigitalObjectName()));
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException | PidResolutionException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: "
              + e.getMessage());
    }
    log.info("Persisting new DOIs to Document Store");
    mongoRepository.postHandleRecords(fdoDocuments);
    log.info("Publishing to DataCite");
    publishToDataCite(fdoRecords, EventType.CREATE);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var updateRequests = requests.stream()
        .map(request -> request.get(NODE_DATA)).toList();
    var fdoRecordMap = processUpdateRequest(
        updateRequests.stream()
            .map(request -> request.get(NODE_ID).asText()).toList());
    var fdoType = getObjectTypeFromJsonNode(requests);
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN ->
            fdoRecords = updateDigitalSpecimen(updateRequests, fdoRecordMap, incrementVersion);
        case DIGITAL_MEDIA ->
            fdoRecords = updateDigitalMedia(updateRequests, fdoRecordMap, incrementVersion);
        default -> throw new UnsupportedOperationException(
            String.format(TYPE_ERROR_MESSAGE, fdoType.getDigitalObjectName()));
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
      mongoRepository.updateHandleRecords(fdoDocuments);
      publishToDataCite(fdoRecords, EventType.UPDATE);
      return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
    } catch (JsonProcessingException e) {
      log.error("An error has occurred processing JSON data", e);
      throw new UnprocessableEntityException("Json Processing Error");
    }
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
        throw new UnprocessableEntityException("Unable to publish datacite event to queue");
      }
    }
  }
}
