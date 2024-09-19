package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
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
    var handleList = hf.generateNewHandles(requests.size());
    var handles = handleList.iterator();
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
        case DOI -> fdoRecords = createDoi(requestAttributes, handles);
        case HANDLE -> fdoRecords = createHandle(requestAttributes, handles);
        case DATA_MAPPING -> fdoRecords = createDataMapping(requestAttributes, handles);
        case MAS -> fdoRecords = createMas(requestAttributes, handles);
        case ORGANISATION -> fdoRecords = createOrganisation(requestAttributes, handles);
        case SOURCE_SYSTEM -> fdoRecords = createSourceSystem(requestAttributes, handles);
        default -> throw new UnsupportedOperationException("Type " + fdoType.getDigitalObjectName()
            + " is not permitted for the Handle endpoint. Please use DOI endpoint");
      }
      fdoDocuments = toMongoDbDocument(fdoRecords);
    } catch (JsonProcessingException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    mongoRepository.postHandleRecords(fdoDocuments);
    log.info("Persisted {} new handles to Document Store", handleList.size());
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<PatchRequest> requests, boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequestData = requests.stream()
        .map(PatchRequest::data).toList();
    var fdoType = getFdoTypeFromRequest(
        updateRequestData.stream().map(PatchRequestData::type).toList());
    var fdoRecordMap = processUpdateRequest(
        updateRequestData.stream().map(PatchRequestData::id).toList());
    List<FdoRecord> fdoRecords;
    List<Document> fdoDocuments;
    try {
      switch (fdoType) {
        case ANNOTATION ->
            fdoRecords = updateAnnotation(updateRequestData, fdoRecordMap, incrementVersion);
        case DOI -> fdoRecords = updateDoi(updateRequestData, fdoRecordMap, incrementVersion);
        case HANDLE -> fdoRecords = updateHandle(updateRequestData, fdoRecordMap, incrementVersion);
        case DATA_MAPPING ->
            fdoRecords = updateDataMapping(updateRequestData, fdoRecordMap, incrementVersion);
        case MAS -> fdoRecords = updateMas(updateRequestData, fdoRecordMap, incrementVersion);
        case ORGANISATION -> fdoRecords = updateOrganisation(updateRequestData, fdoRecordMap,
            incrementVersion);
        case SOURCE_SYSTEM -> fdoRecords = updateSourceSystem(updateRequestData, fdoRecordMap,
            incrementVersion);
        default -> throw new UnsupportedOperationException("Type " + fdoType.getDigitalObjectName()
            + " is not permitted for the Handle endpoint. Please use DOI endpoint");
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

  private List<FdoRecord> updateAnnotation(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          AnnotationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedAnnotationRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateDoi(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          DoiKernelRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDoiRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateHandle(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          HandleRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedHandleRecord(requestObject, HANDLE, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateDataMapping(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          DataMappingRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedDataMappingRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateMas(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          MasRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedMasRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateOrganisation(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          OrganisationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedOrganisationRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
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

  private List<FdoRecord> updateSourceSystem(List<PatchRequestData> updateRequests,
      Map<String, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : updateRequests) {
      var requestObject = mapper.treeToValue(request.attributes(),
          SourceSystemRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareUpdatedSourceSystemRecord(requestObject, timestamp,
              previousVersionMap.get(request.id()), incrementVersion));
    }
    return fdoRecords;
  }

}

