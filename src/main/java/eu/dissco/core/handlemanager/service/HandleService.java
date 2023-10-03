package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MappingRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MasRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(Profiles.HANDLE)
public class HandleService extends PidService {

  public HandleService(PidRepository pidRepository,
      FdoRecordService fdoRecordService, PidNameGeneratorService hf,
      ObjectMapper mapper,
      ProfileProperties profileProperties) {
    super(pidRepository, fdoRecordService, hf, mapper, profileProperties);
  }

  // Pid Record Creation
  @Override
  public JsonApiWrapperWrite createRecords(
      List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {

    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<DigitalSpecimenRequest> digitalSpecimenList = new ArrayList<>();

    List<HandleAttribute> handleAttributes = new ArrayList<>();
    Map<String, ObjectType> recordTypes = new HashMap<>();

    for (var request : requests) {
      ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
      ObjectType type = ObjectType.fromString(dataNode.get(NODE_TYPE).asText());
      recordTypes.put(new String(handles.get(0), StandardCharsets.UTF_8), type);
      try {
        switch (type) {
          case HANDLE -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                HandleRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareHandleRecordAttributes(requestObject, handles.remove(0),
                    type));
          }
          case DOI -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DoiRecordRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareDoiRecordAttributes(requestObject, handles.remove(0),
                    type));
          }
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
          case ANNOTATION -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                AnnotationRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareAnnotationAttributes(requestObject, handles.remove(0),
                    type));
          }
          case MAPPING -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MappingRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMappingAttributes(requestObject, handles.remove(0), type));
          }
          case SOURCE_SYSTEM -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                SourceSystemRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareSourceSystemAttributes(requestObject, handles.remove(0),
                    type));
          }
          case ORGANISATION -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                OrganisationRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareOrganisationAttributes(requestObject, handles.remove(0),
                    type));
          }
          case MAS -> {
            var requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES), MasRequest.class);
            handleAttributes.addAll(
                fdoRecordService.prepareMasRecordAttributes(requestObject, handles.remove(0),
                    type));
          }
          default -> throw new InvalidRequestException(
              "Invalid request. Unrecognized object type: " + type);
        }
      } catch (JsonProcessingException | UnprocessableEntityException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }

    validateDigitalSpecimens(digitalSpecimenList);
    log.info("Persisting new handles to db");
    pidRepository.postAttributesToDb(recordTimestamp, handleAttributes);

    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, recordTypes));
  }

}

