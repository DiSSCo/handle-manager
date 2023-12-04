package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType.MEDIA_OBJECT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(Profiles.DOI)
@Slf4j
@Service
public class DoiService extends PidService {

  public DoiService(PidRepository pidRepository,
      FdoRecordService fdoRecordService, PidNameGeneratorService pidNameGeneratorService,
      ObjectMapper mapper,
      ProfileProperties profileProperties) {
    super(pidRepository, fdoRecordService, pidNameGeneratorService, mapper, profileProperties);
  }

  private static final String TYPE_ERROR_MESSAGE = "Error creating DOI for object of Type %s. Only Digital Specimens and Media Objects use DOIs.";

  @Override
  public JsonApiWrapperWrite createRecords(List<JsonNode> requests)
      throws InvalidRequestException, PidCreationException {
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
    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, type));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var types = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_TYPE).asText())
        .filter(type -> !type.equals(MEDIA_OBJECT.toString())
            || !type.equals(DIGITAL_SPECIMEN.toString()))
        .collect(Collectors.toSet());

    if (!types.isEmpty()) {
      throw new InvalidRequestException(String.format(TYPE_ERROR_MESSAGE, types));
    }
    return super.updateRecords(requests, incrementVersion);
  }


}
