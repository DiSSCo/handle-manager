package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DOI;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(Profiles.HANDLE)
public class HandleService extends PidService {

  public HandleService(PidRepository pidRepository, FdoRecordService fdoRecordService,
      PidNameGeneratorService hf, ObjectMapper mapper, ProfileProperties profileProperties) {
    super(pidRepository, fdoRecordService, hf, mapper, profileProperties);
  }

  // Pid Record Creation
  @Override
  public JsonApiWrapperWrite createRecords(List<JsonNode> requests) throws InvalidRequestException {
    var handles = hf.genHandleList(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_ATTRIBUTES)).toList();
    var type = getObjectTypeFromJsonNode(requests);
    List<HandleAttribute> handleAttributes;
    try {
      switch (type) {
        case ANNOTATION -> handleAttributes = createAnnotation(requestAttributes, handles);
        case DIGITAL_SPECIMEN ->
            handleAttributes = createDigitalSpecimen(requestAttributes, handles);
        case DOI -> handleAttributes = createDoi(requestAttributes, handles);
        case HANDLE -> handleAttributes = createHandle(requestAttributes, handles);
        case MAPPING -> handleAttributes = createMapping(requestAttributes, handles);
        case MAS -> handleAttributes = createMas(requestAttributes, handles);
        case DIGITAL_MEDIA -> handleAttributes = createDigitalMedia(requestAttributes, handles);
        case ORGANISATION -> handleAttributes = createOrganisation(requestAttributes, handles);
        case SOURCE_SYSTEM -> handleAttributes = createSourceSystem(requestAttributes, handles);
        default -> throw new UnsupportedOperationException("Unrecognized type");
      }
    } catch (JsonProcessingException | PidResolutionException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    log.info("Persisting new handles to db");
    pidRepository.postAttributesToDb(Instant.now().getEpochSecond(), handleAttributes);
    return new JsonApiWrapperWrite(formatCreateRecords(handleAttributes, type));
  }

  private List<HandleAttribute> createAnnotation(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, AnnotationRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareAnnotationAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createDoi(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, DoiRecordRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareDoiRecordAttributes(requestObject, thisHandle, DOI));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createHandle(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, HandleRecordRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareHandleRecordAttributes(requestObject, thisHandle, HANDLE));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createMapping(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, MappingRequest.class);
      handleAttributes.addAll(fdoRecordService.prepareMappingAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createMas(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, MasRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareMasRecordAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createOrganisation(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, OrganisationRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareOrganisationAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

  private List<HandleAttribute> createSourceSystem(List<JsonNode> requestAttributes,
      Iterator<byte[]> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<HandleAttribute> handleAttributes = new ArrayList<>();
    for (var request : requestAttributes) {
      var thisHandle = handleIterator.next();
      var requestObject = mapper.treeToValue(request, SourceSystemRequest.class);
      handleAttributes.addAll(
          fdoRecordService.prepareSourceSystemAttributes(requestObject, thisHandle));
    }
    return handleAttributes;
  }

}

