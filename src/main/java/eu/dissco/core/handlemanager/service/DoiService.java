package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.MEDIA_OBJECT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.exceptions.DatabaseCopyException;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(Profiles.DOI)
@Slf4j
@Service
public class DoiService extends PidService {

  private final DataCiteService dataCiteService;

  public DoiService(PidRepository pidRepository,
      FdoRecordService fdoRecordService, PidNameGeneratorService pidNameGeneratorService,
      ObjectMapper mapper, ProfileProperties profileProperties, DataCiteService dataCiteService) {
    super(pidRepository, fdoRecordService, pidNameGeneratorService, mapper, profileProperties);
    this.dataCiteService = dataCiteService;
  }

  private static final String TYPE_ERROR_MESSAGE = "Error creating DOI for object of Type %s. Only Digital Specimens and Media Objects use DOIs.";

  @Override
  public JsonApiWrapperWrite createRecords(List<JsonNode> requests)
      throws InvalidRequestException, DatabaseCopyException {
    var handles = hf.genHandleList(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_ATTRIBUTES)).toList();
    var type = getObjectType(requests);
    List<HandleAttribute> handleAttributes;
    try {
      switch (type) {
        case DIGITAL_SPECIMEN ->
            handleAttributes = createDigitalSpecimen(requestAttributes, handles);
        case MEDIA_OBJECT -> handleAttributes = createMediaObject(requestAttributes, handles);
        default -> throw new UnsupportedOperationException(
            type + " is not an appropriate Type for DOI endpoint.");
      }
    } catch (JsonProcessingException | PidResolutionException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    log.info("Persisting new dois to db");
    pidRepository.postAttributesToDb(Instant.now().getEpochSecond(), handleAttributes);
    log.info("Publishing to DataCite");
    publishToDataCite(handleAttributes, EventType.CREATE, type);
    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, type));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var type = getObjectType(requests);
    if (!DIGITAL_SPECIMEN.equals(type) && !MEDIA_OBJECT.equals(type)) {
      throw new InvalidRequestException(TYPE_ERROR_MESSAGE);
    }
    var attributesToUpdate = getAttributesToUpdate(requests);
    var response = updateRecords(attributesToUpdate, incrementVersion, type);
    log.info("Publishing to datacite");
    var flatList = attributesToUpdate.stream().flatMap(List::stream).toList();
    publishToDataCite(flatList, EventType.UPDATE, type);
    return response;
  }

  private void publishToDataCite(List<HandleAttribute> handleAttributes, EventType eventType,
      ObjectType objectType) throws UnprocessableEntityException {
    var handleMap = mapRecords(handleAttributes);
    var pidRecords = new ArrayList<JsonNode>();
    handleMap.forEach((key, value) -> pidRecords.add(jsonFormatSingleRecord(value)));
    var event = new DataCiteEvent(pidRecords, eventType);
    try {
      dataCiteService.publishToDataCite(event, objectType);
    } catch (JsonProcessingException e) {
      log.error("Critical error: Unable to publish datacite event to queue", e);
      dataCiteService.dlqDois(handleMap.keySet());
      log.info("Rolling back handles");
      if (eventType.equals(EventType.CREATE)) {
        rollbackHandles(new ArrayList<>(handleMap.keySet()));
      }
      throw new UnprocessableEntityException("Unable to publish PIDs to datacite queue");
    }
  }
}
