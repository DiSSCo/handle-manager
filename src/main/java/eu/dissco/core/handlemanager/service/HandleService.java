package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.MasRequestAttributes;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(Profiles.HANDLE)
public class HandleService extends PidService {

  public HandleService(FdoRecordService fdoRecordService,
      PidNameGeneratorService hf, ObjectMapper mapper, ProfileProperties profileProperties,
      MongoRepository mongoRepository) {
    super(fdoRecordService, hf, mapper, profileProperties, mongoRepository);
  }

  // Pid Record Creation
  @Override
  public JsonApiWrapperWrite createRecords(List<PostRequest> requests)
      throws InvalidRequestException {
    var handles = hf.generateNewHandles(requests.size()).iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.data().attributes())
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream()
        .map(request -> request.data().type())
        .toList());
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (fdoType) {
        case ANNOTATION -> fdoRecords = createAnnotation(requestAttributes, handles);
        case DIGITAL_SPECIMEN -> fdoRecords = createDigitalSpecimen(requestAttributes, handles);
        case DOI -> fdoRecords = createDoi(requestAttributes, handles);
        case HANDLE -> fdoRecords = createHandle(requestAttributes, handles);
        case DATA_MAPPING -> fdoRecords = createDataMapping(requestAttributes, handles);
        case MAS -> fdoRecords = createMas(requestAttributes, handles);
        case DIGITAL_MEDIA -> fdoRecords = createDigitalMedia(requestAttributes, handles);
        case ORGANISATION -> fdoRecords = createOrganisation(requestAttributes, handles);
        case SOURCE_SYSTEM -> fdoRecords = createSourceSystem(requestAttributes, handles);
        default -> throw new UnsupportedOperationException("Unrecognized type");
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException | PidResolutionException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    log.info("Persisting new handles to Document Store");
    mongoRepository.postHandleRecords(fdoDocuments);
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests, boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequests = requests.stream()
        .map(request -> request.get(NODE_DATA)).toList();
    var fdoRecordMap = processUpdateRequest(
        updateRequests.stream().map(request -> request.get(NODE_ID).asText()).toList());
    var fdoType = getObjectTypeFromJsonNode(requests);
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (fdoType) {
        case ANNOTATION ->
            fdoRecords = updateAnnotation(updateRequests, fdoRecordMap, incrementVersion);
        case DIGITAL_SPECIMEN ->
            fdoRecords = updateDigitalSpecimen(updateRequests, fdoRecordMap, incrementVersion);
        case DOI -> fdoRecords = updateDoi(updateRequests, fdoRecordMap, incrementVersion);
        case HANDLE -> fdoRecords = updateHandle(updateRequests, fdoRecordMap, incrementVersion);
        case DATA_MAPPING ->
            fdoRecords = updateDataMapping(updateRequests, fdoRecordMap, incrementVersion);
        case MAS -> fdoRecords = updateMas(updateRequests, fdoRecordMap, incrementVersion);
        case DIGITAL_MEDIA ->
            fdoRecords = updateDigitalMedia(updateRequests, fdoRecordMap, incrementVersion);
        case ORGANISATION ->
            fdoRecords = updateOrganisation(updateRequests, fdoRecordMap, incrementVersion);
        case SOURCE_SYSTEM ->
            fdoRecords = updateSourceSystem(updateRequests, fdoRecordMap, incrementVersion);
        default -> throw new UnsupportedOperationException("Unrecognized type");
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
      mongoRepository.updateHandleRecords(fdoDocuments);
      return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
    } catch (JsonProcessingException e) {
      log.error("An error has occurred processing JSON data", e);
      throw new InvalidRequestException("Unable to parse FDO Record");
    }
  }

  private List<FdoRecord> createAnnotation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, AnnotationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewAnnotationRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateAnnotation(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          AnnotationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedAnnotationRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createDoi(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, DoiKernelRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewDoiRecord(requestObject, handleIterator.next(), timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateDoi(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          DoiKernelRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDoiRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createHandle(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, HandleRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewHandleRecord(requestObject, handleIterator.next(), timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateHandle(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          HandleRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedHandleRecord(requestObject, HANDLE, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createDataMapping(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, DataMappingRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewDataMappingRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateDataMapping(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          DataMappingRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDataMappingRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createMas(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, MasRequestAttributes.class);
      fdoRecords.add(fdoRecordService.prepareNewMasRecord(requestObject, handleIterator.next(),
          timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateMas(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          MasRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedMasRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createOrganisation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, OrganisationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewOrganisationRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateOrganisation(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          OrganisationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedOrganisationRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

  private List<FdoRecord> createSourceSystem(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator) throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, SourceSystemRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewSourceSystemRecord(requestObject, handleIterator.next(),
              timestamp));
    }
    return fdoRecords;
  }

  private List<FdoRecord> updateSourceSystem(List<JsonNode> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.get(NODE_ATTRIBUTES),
          SourceSystemRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedSourceSystemRecord(requestObject, timestamp,
              previousVersionMap.get(request.get(NODE_ID).asText()), incrementVersion));
    }
    return fdoRecords;
  }

}

