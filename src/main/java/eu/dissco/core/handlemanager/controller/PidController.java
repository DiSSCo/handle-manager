package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.RollbackRequest;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PrimaryObjectIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.service.PidService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pids")
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class PidController {

  private final PidService service;
  private final JsonSchemaValidator schemaValidator;
  private final ApplicationProperties applicationProperties;
  private static final int LOG_LIMIT = 4;


  // Getters
  @Operation(summary = "Resolve single PID record")
  @GetMapping("/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperReadSingle> resolvePid(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, HttpServletRequest r)
      throws PidResolutionException {
    String path = applicationProperties.getUiUrl() + r.getRequestURI();
    String handle = prefix + "/" + suffix;

    if (prefix.equals(applicationProperties.getPrefix())) {
      var node = service.resolveSingleRecord(handle.getBytes(StandardCharsets.UTF_8), path);
      return ResponseEntity.status(HttpStatus.OK).body(node);
    }
    throw new PidResolutionException(
        "Unable to resolve PIDs outside DiSSCo namespace. PID must start with prefix "
            + applicationProperties.getPrefix());
  }

  @Operation(summary = "Resolve multiple PID records")
  @GetMapping("/records")
  public ResponseEntity<JsonApiWrapperRead> resolvePids(
      @RequestParam List<String> handles,
      HttpServletRequest r) throws PidResolutionException, InvalidRequestException {
    String path = applicationProperties.getUiUrl() + r.getRequestURI();

    if (handles.size() > applicationProperties.getMaxHandles()) {
      throw new InvalidRequestException(
          "Attempting to resolve more than maximum permitted PIDs in a single request. Maximum handles: "
              + applicationProperties.getMaxHandles());
    }
    List<byte[]> handleBytes = new ArrayList<>();
    handles.forEach(h -> handleBytes.add(h.getBytes(StandardCharsets.UTF_8)));

    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handleBytes, path));
  }

  @Operation(summary = "Given a physical identifier (i.e. local identifier), resolve PID record")
  @GetMapping("/records/primarySpecimenObjectId")
  public ResponseEntity<JsonApiWrapperWrite> searchByPrimarySpecimenObjectId(
      @RequestParam String primarySpecimenObjectId,
      @RequestParam PrimaryObjectIdType physicalIdentifierType,
      @RequestParam(required = false) String sourceSystemId)
      throws InvalidRequestException, PidResolutionException {
    return ResponseEntity.status(HttpStatus.OK).body(
        service.searchByPhysicalSpecimenId(primarySpecimenObjectId, physicalIdentifierType,
            sourceSystemId));
  }

  @Operation(summary = "Create single PID Record")
  @PostMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> createRecord(@RequestBody JsonNode request,
      Authentication authentication)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {
    log.info("Received single POST request from user {}", authentication.getName());
    schemaValidator.validatePostRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(List.of(request)
    ));
  }

  @Operation(summary = "Create multiple PID Records at a time.")
  @PostMapping(value = "/batch")
  public ResponseEntity<JsonApiWrapperWrite> createRecords(@RequestBody List<JsonNode> requests,
      Authentication authentication)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {
    log.info("Validating batch POST request from user {}", authentication.getName());
    for (JsonNode request : requests) {
      schemaValidator.validatePostRequest(request);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(requests));
  }

  // Update
  @Operation(summary = "Update existing PID Record")
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request,
      Authentication authentication)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    log.info("Received single update request for PID {}/{} from user {}", prefix, suffix,
        authentication.getName());
    schemaValidator.validatePatchRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleData = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);

    if (!Arrays.equals(handle, handleData)) {
      throw new InvalidRequestException(String.format(
          "Handle in request path does not match id in request body. Path: %s, Body: %s",
          new String(handle, StandardCharsets.UTF_8),
          new String(handleData, StandardCharsets.UTF_8)));
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(List.of(request), true));
  }

  @Operation(summary = "Update multiple PID Records")
  @PatchMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<JsonNode> requests,
      Authentication authentication)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    log.info("Validating batch update request from user {}", authentication.getName());
    for (JsonNode request : requests) {
      schemaValidator.validatePatchRequest(request);
    }
    var ids = requests.stream().map(r -> r.get(NODE_DATA).get(NODE_ID).asText()).limit(LOG_LIMIT)
        .toList();
    log.info("Received valid batch update request for {} PIDS {} ...", requests.size(), ids);
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests, true));
  }

  // Upsert
  @Operation(summary = "Create a PID Record; if it already exists, update contents. DigitalSpecimens only.")
  @PatchMapping(value = "/upsert")
  public ResponseEntity<JsonApiWrapperWrite> upsertRecord(@RequestBody List<JsonNode> requests,
      Authentication authentication)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException, PidServiceInternalError, JsonProcessingException {
    log.info("Validating upsert request from user {}", authentication.getName());
    for (var request : requests) {
      schemaValidator.validatePostRequest(request);
      if (!request.get(NODE_DATA).get(NODE_TYPE).asText()
          .equals(ObjectType.DIGITAL_SPECIMEN.toString())) {
        log.error(
            "Attempting to upsert invalid type of PID record. Currently only upsert for Digital Specimen is supported.");
        throw new InvalidRequestException(
            "Invalid type. Upsert endpoint only available for type DigitalSpecimen");
      }
    }
    var ids = getPhysicalSpecimenIds(requests);
    log.info("Received valid upsert request for {} physical specimens: {}... ", requests.size(),
        ids);
    return ResponseEntity.ok(service.upsertDigitalSpecimens(requests));
  }

  @Operation(summary = "Archive given record")
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request,
      Authentication authentication)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    log.info("Received archive request for PID {}/{} from user {}", prefix, suffix,
        authentication.getName());
    schemaValidator.validatePutRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleRequest = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    if (!Arrays.equals(handle, handleRequest)) {
      throw new InvalidRequestException(
          "Handle in request path does not match id in request body. Path: "
              + new String(handle, StandardCharsets.UTF_8)
              + ". Body: " + new String(handleRequest, StandardCharsets.UTF_8));
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(List.of(request)));
  }

  @Operation(summary = "rollback handle creation")
  @DeleteMapping(value = "/rollback")
  public ResponseEntity<Void> rollbackHandleCreation(@RequestBody RollbackRequest request,
      Authentication authentication)
      throws InvalidRequestException {
    log.info("validating rollback creation request from user {}", authentication.getName());
    var ids = request.data().stream().map(d -> d.get(NODE_ID)).toList();
    if (ids.contains(null)) {
      throw new InvalidRequestException("Missing Handles (\"id\") in request");
    }
    var handles = ids.stream().map(JsonNode::asText).toList();

    log.info("Recieved valid rollback creation request for {} handles : {}...", handles.size(),
        handles.stream().limit(LOG_LIMIT).toList());
    service.rollbackHandles(handles);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "rollback handle update")
  @DeleteMapping(value = "/rollback/update")
  public ResponseEntity<JsonApiWrapperWrite> rollbackHandleUpdate(
      @RequestBody List<JsonNode> requests, Authentication authentication)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    log.info("Validating rollback update request from user {}", authentication.getName());
    for (JsonNode request : requests) {
      schemaValidator.validatePatchRequest(request);
    }
    var handles = requests.stream().map(r -> r.get(NODE_DATA).get(NODE_ID).asText())
        .limit(LOG_LIMIT).toList();
    log.info("Received valid rollback update request for {} handles: {}...", requests.size(),
        handles);
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests, false));
  }

  @Operation(summary = "rollback handle update")
  @DeleteMapping(value = "/rollback/physId")
  public ResponseEntity<Void> rollbackHandlePhysId(
      @RequestBody List<String> physicalIds, Authentication authentication) {
    log.info("Validating rollback update request from user {}", authentication.getName());
    service.rollbackHandlesFromPhysId(physicalIds);
    return ResponseEntity.ok().build();
  }


  @Operation(summary = "Archive multiple PID records")
  @PutMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(@RequestBody List<JsonNode> requests,
      Authentication authentication)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError, UnprocessableEntityException {
    log.info("Validating archive request from user {}", authentication.getName());
    for (JsonNode request : requests) {
      schemaValidator.validatePutRequest(request);
    }
    log.info("Received valid archive request");
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(requests));
  }

  private List<String> getPhysicalSpecimenIds(List<JsonNode> requests) {
    return requests.stream().map(r -> r.get(NODE_DATA).get(NODE_ATTRIBUTES)
        .get(PRIMARY_SPECIMEN_OBJECT_ID.get()).asText()).limit(LOG_LIMIT).toList();
  }
}