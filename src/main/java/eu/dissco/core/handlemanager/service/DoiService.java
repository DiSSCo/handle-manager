package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public JsonApiWrapperWrite createSpecimenRecord(List<JsonNode> requests)
      throws PidResolutionException, InvalidRequestException, PidCreationException {
    throw new UnsupportedOperationException("");
  }

  @Override
  public JsonApiWrapperWrite createRecords(
      List<JsonNode> requests)
      throws PidResolutionException, InvalidRequestException, PidCreationException {

    List<byte[]> handles = hf.genHandleList(requests.size());
    List<DigitalSpecimenRequest> digitalSpecimenList = new ArrayList<>();

    List<HandleAttribute> handleAttributes = new ArrayList<>();
    Map<String, ObjectType> recordTypes = new HashMap<>();

    for (var request : requests) {
      var dataNode = request.get(NODE_DATA);
      ObjectType type = ObjectType.fromString(dataNode.get(NODE_TYPE).asText());
      recordTypes.put(new String(handles.get(0), StandardCharsets.UTF_8), type);
      try {
        switch (type) {
          case DIGITAL_SPECIMEN -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DigitalSpecimenRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareDigitalSpecimenRecordAttributes(requestObject,
                    handles.remove(0), type));
            digitalSpecimenList.add(requestObject);
          }
          case MEDIA_OBJECT -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MediaObjectRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMediaObjectAttributes(requestObject, handles.remove(0),
                    type));
          }
          default -> throw new InvalidRequestException(String.format(
              TYPE_ERROR_MESSAGE, type));
        }
      } catch (JsonProcessingException | UnprocessableEntityException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }

    validateDigitalSpecimens(digitalSpecimenList);

    log.info("Persisting new DOIs to db");
    var recordTimestamp = Instant.now().getEpochSecond();
    pidRepository.postAttributesToDb(recordTimestamp, handleAttributes);

    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, recordTypes));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    var types = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_TYPE).asText())
        .filter(type -> !type.equals(ObjectType.MEDIA_OBJECT.toString())
            || !type.equals(ObjectType.DIGITAL_SPECIMEN.toString()))
        .collect(Collectors.toSet());

    if (!types.isEmpty()) {
      throw new InvalidRequestException(String.format(TYPE_ERROR_MESSAGE, types));
    }
    return super.updateRecords(requests, incrementVersion);

  }


}
