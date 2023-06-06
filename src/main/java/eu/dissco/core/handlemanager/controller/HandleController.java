package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.attributes.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.service.HandleService;
import io.swagger.v3.oas.annotations.Operation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
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
public class HandleController {

  private static final String SANDBOX_URI = "https://sandbox.dissco.tech";
  private final HandleService service;
  private final JsonSchemaValidator schemaValidator;

  // Getters
  @Operation(summary = "Resolve single PID record")
  @GetMapping("/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperReadSingle> resolvePid(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, HttpServletRequest r)
      throws PidResolutionException {

    String path = SANDBOX_URI + r.getRequestURI();
    String handle = prefix + "/" + suffix;

    if (prefix.equals("20.5000.1025")){
      return resolveInternalPid(handle, path);
    }
    throw new PidResolutionException("Unable to resolve PIDs outside DiSSCo namespace. PID must start with prefix 20.5000.1025");
  }

  private ResponseEntity<JsonApiWrapperReadSingle> resolveInternalPid(String handle, String path)
      throws PidResolutionException {
    var node = service.resolveSingleRecord(handle.getBytes(StandardCharsets.UTF_8), path);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @Operation(summary ="Resolve multiple PID records")
  @GetMapping("/records")
  public ResponseEntity<JsonApiWrapperRead> resolvePids(
      @RequestParam List<String> handles,
      HttpServletRequest r) throws PidResolutionException, InvalidRequestException {
    String path = SANDBOX_URI + r.getRequestURI();
    int maxHandles = 200;

    if (handles.size() > maxHandles){
      throw new InvalidRequestException("Attempting to resolve more than maximum permitted PIDs in a single request. Maximum handles: " + maxHandles);
    }
    List<byte[]> handleBytes = new ArrayList<>();
    handles.forEach(h -> handleBytes.add(h.getBytes(StandardCharsets.UTF_8)));

    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handleBytes, path));
  }

  @Operation(summary ="Given a physical identifier (i.e. local identifier), resolve PID record")
  @GetMapping("/records/primarySpecimenObjectId")
  public ResponseEntity<JsonApiWrapperWrite> searchByPhysicalSpecimenId(
      @RequestParam String physicalIdentifier,
      @RequestParam PhysicalIdType physicalIdentifierType,
      @RequestParam (required = false) String specimenHostPid)
      throws InvalidRequestException, PidResolutionException {
    return ResponseEntity.status(HttpStatus.OK).body(service.searchByPhysicalSpecimenId(physicalIdentifier, physicalIdentifierType, specimenHostPid));
  }

  @Operation(summary ="Create single PID Record")
  @PostMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> createRecord(@RequestBody JsonNode request)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {
    schemaValidator.validatePostRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(List.of(request)));
  }

  @Operation(summary ="Create multiple PID Records at a time.")
  @PostMapping(value = "/batch")
  public ResponseEntity<JsonApiWrapperWrite> createRecords(@RequestBody List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {
    log.info("received batch request");

    for (JsonNode request : requests) {
      schemaValidator.validatePostRequest(request);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(requests));
  }

  // Update
  @Operation(summary ="Update existing PID Record")
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {

    schemaValidator.validatePatchRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleData = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);

    if (!Arrays.equals(handle, handleData)) {
      throw new InvalidRequestException("Handle in request URL does not match id in request body.");
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(List.of(request)));
  }

  @Operation(summary ="Update multiple PID Records")
  @PatchMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {

    for (JsonNode request : requests) {
      schemaValidator.validatePatchRequest(request);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests));
  }

  // Upsert
  @Operation(summary="Create a PID Record; if it already exists, update contents. DigitalSpecimens only.")
  @PatchMapping(value="/upsert")
  public ResponseEntity<JsonApiWrapperWrite> upsertRecord(List<JsonNode> requests)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException, PidServiceInternalError, JsonProcessingException {
    for (var request: requests){
      schemaValidator.validatePostRequest(request);
      if (!request.get(NODE_DATA).get(NODE_ATTRIBUTES).asText().equals(ObjectType.DIGITAL_SPECIMEN.toString())){
        throw new InvalidRequestException("Invalid type. Upsert endpoint only available for type DigitalSpecimen");
      }
    }
    return ResponseEntity.ok(service.upsertDigitalSpecimens(requests));
  }


  @Operation(summary ="Archive given record")
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {

    schemaValidator.validatePutRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleRequest = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    if (!Arrays.equals(handle, handleRequest)) {
      throw new InvalidRequestException(
          "Handle in request URL does not match id in request body. URL: " + handle + ", body: "
              + handleRequest);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(List.of(request)));
  }

  @Operation(summary ="Archive multiple PID records")
  @PutMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {
    for (JsonNode request : requests) {
      schemaValidator.validatePutRequest(request);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(requests));
  }
}