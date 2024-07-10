package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DOI;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;

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
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidMongoRepository;
import eu.dissco.core.handlemanager.repository.PidRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(Profiles.HANDLE)
public class HandleService extends PidService {

  public HandleService(PidRepository pidRepository, FdoRecordService fdoRecordService,
      PidNameGeneratorService hf, ObjectMapper mapper, ProfileProperties profileProperties,
      PidMongoRepository mongoRepository) {
    super(pidRepository, fdoRecordService, hf, mapper, profileProperties, mongoRepository);
  }

  // Pid Record Creation
  @Override
  public JsonApiWrapperWrite createRecords(List<JsonNode> requests) throws InvalidRequestException {
    var handles = hf.genHandleList(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.get(NODE_DATA).get(NODE_ATTRIBUTES)).toList();
    var type = getObjectTypeFromJsonNode(requests);
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (type) {
        case ANNOTATION -> fdoRecords = createAnnotation(requestAttributes, handles);
        case DIGITAL_SPECIMEN -> fdoRecords = createDigitalSpecimen(requestAttributes, handles);
        case DOI -> fdoRecords = createDoi(requestAttributes, handles);
        case HANDLE -> fdoRecords = createHandle(requestAttributes, handles);
        case MAPPING -> fdoRecords = createMapping(requestAttributes, handles);
        case MAS -> fdoRecords = createMas(requestAttributes, handles);
        case MEDIA_OBJECT -> fdoRecords = createMediaObject(requestAttributes, handles);
        case ORGANISATION -> fdoRecords = createOrganisation(requestAttributes, handles);
        case SOURCE_SYSTEM -> fdoRecords = createSourceSystem(requestAttributes, handles);
        default -> throw new UnsupportedOperationException("Unrecognized type");
      }
      fdoDocuments = fdoRecordService.toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException | PidResolutionException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    log.info("Persisting new handles to Document Store");
    mongoRepository.postBatchHandleRecord(fdoDocuments);
    return new JsonApiWrapperWrite(formatCreateRecord(fdoRecords, type));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests)
      throws UnprocessableEntityException, InvalidRequestException {
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
        case ANNOTATION -> fdoRecords = updateAnnotation(updateRequests, fdoRecordMap);
        case DIGITAL_SPECIMEN -> fdoRecords = updateDigitalSpecimen(updateRequests, fdoRecordMap);
        case DOI -> fdoRecords = updateDoi(updateRequests, fdoRecordMap);
        case HANDLE -> fdoRecords = updateHandle(updateRequests, fdoRecordMap);
        case MAPPING -> fdoRecords = updateDataMapping(updateRequests, fdoRecordMap);
        case MAS -> fdoRecords = updateMas(updateRequests, fdoRecordMap);
        case MEDIA_OBJECT -> fdoRecords = updateDigitalMedia(updateRequests, fdoRecordMap);
        case ORGANISATION -> fdoRecords = updateOrganisation(updateRequests, fdoRecordMap);
        case SOURCE_SYSTEM -> fdoRecords = updateSourceSystem(updateRequests, fdoRecordMap);
        default -> throw new UnsupportedOperationException("Unrecognized type");
      }
      fdoDocuments = fdoRecordService.toMongoDbDocument(fdoRecords);
      var fdoType = getObjectTypeFromJsonNode(requests);
      mongoRepository.updateHandleRecord(fdoDocuments, handles);
      // todo figure out response
      return new JsonApiWrapperWrite(formatCreateRecord(fdoRecords, fdoType));
    } catch (JsonProcessingException e) {
      log.error("An error has occurred processing JSON data", e);
      throw new InvalidRequestException();
    }
  }

  private List<FdoRecord> createAnnotation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, AnnotationRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareNewAnnotationRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateAnnotation(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES), AnnotationRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedAnnotationFdoRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createDoi(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, DoiRecordRequest.class);
      fdoRecords.add(fdoRecordService.prepareNewDoiDocument(requestObject, handleIterator.next(),
          DOI, timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateDoi(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES), DoiRecordRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDoiFdoRecord(requestObject, DOI, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createHandle(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, HandleRecordRequest.class);
      fdoRecords.add(fdoRecordService.prepareNewHandleRecord(requestObject, handleIterator.next(),
          HANDLE, timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateHandle(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          HandleRecordRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedHandleRecord(requestObject, HANDLE, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createMapping(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, MappingRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareNewDataMappingRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateDataMapping(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES), MappingRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDataMappingRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createMas(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, MasRequest.class);
      fdoRecords.add(fdoRecordService.prepareNewMasRecord(requestObject, handleIterator.next(),
          timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateMas(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES), MasRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedMasRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createOrganisation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, OrganisationRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareNewOrganisationRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateOrganisation(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          OrganisationRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedOrganisationRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createSourceSystem(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, SourceSystemRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareNewSourceSystemRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateSourceSystem(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          SourceSystemRequest.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedSourceSystemRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), true));
    }
    return fdoRecords;
  }

}

