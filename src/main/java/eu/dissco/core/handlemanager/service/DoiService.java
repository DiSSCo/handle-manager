package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MEDIA_OBJECT;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
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
      ObjectMapper mapper, ProfileProperties profileProperties,
      DataCiteService dataCiteService) {
    super(pidRepository, fdoRecordService, pidNameGeneratorService, mapper, profileProperties);
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
          "An error has occurred parsing a record in request. More information: "
              + e.getMessage());
    }
    log.info("Persisting new dois to db");
    pidRepository.postAttributesToDb(Instant.now().getEpochSecond(), handleAttributes);
    log.info("Publishing to DataCite");
    publishToDataCite(handleAttributes, EventType.CREATE, type);
    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, type));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var type = getObjectTypeFromJsonNode(requests);
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
              new DataCiteEvent(jsonFormatSingleRecord(value), eventType));
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
