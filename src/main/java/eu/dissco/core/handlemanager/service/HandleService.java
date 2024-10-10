package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DATA_MAPPING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ORGANISATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.SOURCE_SYSTEM;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequestData;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.MachineAnnotationServiceRequestAttributes;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(Profiles.HANDLE)
public class HandleService extends PidService {

  private static final Set<FdoType> VALID_FDO_TYPES = Set.of(ANNOTATION, HANDLE, DATA_MAPPING, MAS,
      ORGANISATION, SOURCE_SYSTEM);

  public HandleService(FdoRecordService fdoRecordService,
      PidNameGeneratorService hf, ObjectMapper mapper, ProfileProperties profileProperties,
      MongoRepository mongoRepository) {
    super(fdoRecordService, hf, mapper, profileProperties, mongoRepository);
  }

  // Pid Record Creation
  @Override
  public JsonApiWrapperWrite createRecords(List<PostRequest> requests, boolean isDraft)
      throws InvalidRequestException {
    var handleList = hf.generateNewHandles(requests.size());
    var handles = handleList.iterator();
    var requestAttributes = requests.stream()
        .map(request -> request.data().attributes())
        .toList();
    var fdoType = getFdoTypeFromRequest(requests.stream()
        .map(request -> request.data().type())
        .toList(), VALID_FDO_TYPES);
    List<FdoRecord> fdoRecords;
    try {
      switch (fdoType) {
        case ANNOTATION -> fdoRecords = createAnnotation(requestAttributes, handles, isDraft);
        case HANDLE -> fdoRecords = createHandle(requestAttributes, handles, isDraft);
        case DATA_MAPPING -> fdoRecords = createDataMapping(requestAttributes, handles, isDraft);
        case MAS -> fdoRecords = createMas(requestAttributes, handles, isDraft);
        case ORGANISATION -> fdoRecords = createOrganisation(requestAttributes, handles, isDraft);
        case SOURCE_SYSTEM -> fdoRecords = createSourceSystem(requestAttributes, handles, isDraft);
        default ->
            throw new IllegalStateException(); // This case is handled by the getFdoTypeFromRequest check
      }
    } catch (JsonProcessingException e) {
      log.error("An error has occurred in processing request", e);
      throw new InvalidRequestException(
          "An error has occurred parsing a record in request. More information: " + e.getMessage());
    }
    createDocuments(fdoRecords);
    log.info("Persisted {} new handles to Document Store", handleList.size());
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords, fdoType));
  }

  @Override
  public JsonApiWrapperWrite updateRecords(List<PatchRequest> requests, boolean incrementVersion)
      throws InvalidRequestException, UnprocessableEntityException {
    var fdoType = getFdoTypeFromRequest(
        requests.stream().map(r -> r.data().type()).toList(), VALID_FDO_TYPES);
    Pair<List<FdoRecord>, List<FdoRecord>> fdoRecords;
    var previousVersionMap = getPreviousVersionsMap(requests);
    switch (fdoType) {
      case ANNOTATION -> fdoRecords = updateAnnotation(previousVersionMap, incrementVersion);
      case HANDLE -> fdoRecords = updateHandle(previousVersionMap, incrementVersion);
      case DATA_MAPPING -> fdoRecords = updateDataMapping(previousVersionMap, incrementVersion);
      case MAS -> fdoRecords = updateMas(previousVersionMap, incrementVersion);
      case ORGANISATION -> fdoRecords = updateOrganisation(previousVersionMap,
          incrementVersion);
      case SOURCE_SYSTEM -> fdoRecords = updateSourceSystem(previousVersionMap,
          incrementVersion);
      default -> throw new IllegalStateException();
    }
    updateDocuments(fdoRecords.getLeft());
    return new JsonApiWrapperWrite(formatFdoRecord(fdoRecords.getRight(), fdoType));
  }

  private List<FdoRecord> createAnnotation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, AnnotationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewAnnotationRecord(requestObject, handleIterator.next(),
              timestamp, isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateAnnotation(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion) throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        AnnotationRequestAttributes.class);
    List<FdoRecord> allFdoRecords = new ArrayList<>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion =
          fdoRecordService.prepareUpdatedAnnotationRecord(updateRequest.getKey(), timestamp,
              updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

  private List<FdoRecord> createHandle(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, HandleRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewHandleRecord(requestObject, handleIterator.next(), timestamp,
              isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateHandle(
      Map<PatchRequestData, FdoRecord> previousVersionMap, boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        HandleRequestAttributes.class);
    var allFdoRecords = new ArrayList<FdoRecord>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion =
          fdoRecordService.prepareUpdatedHandleRecord(updateRequest.getKey(), HANDLE, timestamp,
              updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

  private List<FdoRecord> createDataMapping(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, DataMappingRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewDataMappingRecord(requestObject, handleIterator.next(),
              timestamp, isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateDataMapping(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        DataMappingRequestAttributes.class);
    List<FdoRecord> allFdoRecords = new ArrayList<>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion = fdoRecordService.prepareUpdatedDataMappingRecord(updateRequest.getKey(),
          timestamp,
          updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

  private List<FdoRecord> createMas(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request,
          MachineAnnotationServiceRequestAttributes.class);
      fdoRecords.add(fdoRecordService.prepareNewMasRecord(requestObject, handleIterator.next(),
          timestamp, isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateMas(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoRecord> allFdoRecords = new ArrayList<>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        MachineAnnotationServiceRequestAttributes.class);
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion =
          fdoRecordService.prepareUpdatedMasRecord(updateRequest.getKey(), timestamp,
              updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

  private List<FdoRecord> createOrganisation(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, OrganisationRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewOrganisationRecord(requestObject, handleIterator.next(),
              timestamp, isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateOrganisation(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion) throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        OrganisationRequestAttributes.class);
    List<FdoRecord> allFdoRecords = new ArrayList<>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion = fdoRecordService.prepareUpdatedOrganisationRecord(updateRequest.getKey(),
          timestamp, updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

  private List<FdoRecord> createSourceSystem(List<JsonNode> requestAttributes,
      Iterator<String> handleIterator, boolean isDraft)
      throws JsonProcessingException, InvalidRequestException {
    List<FdoRecord> fdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var request : requestAttributes) {
      var requestObject = mapper.treeToValue(request, SourceSystemRequestAttributes.class);
      fdoRecords.add(
          fdoRecordService.prepareNewSourceSystemRecord(requestObject, handleIterator.next(),
              timestamp, isDraft));
    }
    return fdoRecords;
  }

  private Pair<List<FdoRecord>, List<FdoRecord>> updateSourceSystem(
      Map<PatchRequestData, FdoRecord> previousVersionMap,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updateRequests = convertPatchRequestDataToAttributesClass(previousVersionMap,
        SourceSystemRequestAttributes.class);
    List<FdoRecord> allFdoRecords = new ArrayList<>();
    List<FdoRecord> newFdoRecords = new ArrayList<>();
    var timestamp = Instant.now();
    for (var updateRequest : updateRequests.entrySet()) {
      var newVersion =
          fdoRecordService.prepareUpdatedSourceSystemRecord(updateRequest.getKey(), timestamp,
              updateRequest.getValue(), incrementVersion);
      allFdoRecords.add(newVersion);
      if (fdoRecordsAreDifferent(newVersion, updateRequest.getValue())) {
        newFdoRecords.add(newVersion);
      }
    }
    return Pair.of(newFdoRecords, allFdoRecords);
  }

}

