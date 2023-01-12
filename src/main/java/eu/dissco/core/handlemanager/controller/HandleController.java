package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class HandleController {

  private final HandleService service;

  private final ObjectMapper mapper;

  @GetMapping("/record")
  public ResponseEntity<JsonApiWrapper> resolvePid(
      @RequestBody JsonNode request
  ) throws PidResolutionException, InvalidRecordInput {

    checkRequestNodesPresent(request, true, false, true, false);

    byte[] handle = request.get(NODE_DATA).get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);

    JsonApiWrapper node = service.resolveSingleRecord(handle);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/records")
  public ResponseEntity<List<JsonApiWrapper>> resolvePids(
      @RequestBody List<JsonNode> requests
  ) throws PidResolutionException, InvalidRecordInput {

    List<byte[]> handles = new ArrayList<>();

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, false, true, false);
      handles.add(request.get(NODE_DATA).get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8));
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handles));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> createRecord(
      @RequestBody JsonNode request)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput, UnrecognizedPropertyException {

    checkRequestNodesPresent(request, true, true, false, true);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createRecord(request));
  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> createRecords(
      @RequestBody List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, true, false, true);
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createRecordBatch(requests));
  }

  // Update
  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> updateRecord(
      @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    checkRequestNodesPresent(request, true, true, false, true);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    String recordType = data.get(NODE_TYPE).asText();
    JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.updateRecord(requestAttributes, handle, recordType));
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> updateRecords(
      @RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, true, true, true);
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecordBatch(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> archiveRecord(
      @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException {
    checkRequestNodesPresent(request, true, false, false, true);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.archiveRecord(data.get(NODE_ATTRIBUTES), handle));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> archiveRecords(
      @RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException {

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, false, true, true);
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(requests));
  }


  // Hellos and getters
  @GetMapping(value = "/health")
  public ResponseEntity<String> hello() {
    return new ResponseEntity<>("API is running", HttpStatus.OK);
  }

  // List all handle values
  @GetMapping(value = "/all")
  public ResponseEntity<List<String>> getAllHandles(
      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "100") int pageSize)
      throws PidResolutionException {
    List<String> handleList = service.getHandlesPaged(pageNum, pageSize);
    if (handleList.isEmpty()) {
      throw new PidResolutionException("Unable to locate handles");
    }
    return ResponseEntity.ok(handleList);
  }

  @GetMapping(value = "/all", params = {"pidStatus", "pageNum", "pageSize"})
  public ResponseEntity<List<String>> getAllHandlesByPidStatus(
      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
      @RequestParam(name = "pidStatus") String pidStatus)
      throws PidResolutionException {

    List<String> handleList = service.getHandlesPaged(pageNum, pageSize, pidStatus);
    if (handleList.isEmpty()) {
      throw new PidResolutionException("Unable to resolve pids");
    }
    return ResponseEntity.ok(handleList);
  }

  private void checkRequestNodesPresent(JsonNode requestRoot,
      boolean checkData, boolean checkType, boolean checkId, boolean checkAttributes)
      throws InvalidRecordInput {
    String errorMsg = "INVALID INPUT. Missing node \" %s \"";
    if (checkData && !requestRoot.has(NODE_DATA)) {
      throw new InvalidRecordInput(String.format(errorMsg, NODE_DATA));
    }
    JsonNode requestData = requestRoot.get(NODE_DATA);
    if (checkType && !requestData.has(NODE_TYPE)) {
      throw new InvalidRecordInput(String.format(errorMsg, NODE_TYPE));
    }
    if (checkId && !requestData.has(NODE_ID)) {
      throw new InvalidRecordInput(String.format(errorMsg, NODE_ID));
    }
    if (checkAttributes && !requestData.has(NODE_ATTRIBUTES)) {
      throw new InvalidRecordInput(String.format(errorMsg, NODE_ATTRIBUTES));
    }
  }

  //Exception Handling
  @ExceptionHandler(PidServiceInternalError.class)
  private ResponseEntity<String> pidServiceInternalError(PidServiceInternalError e) {
    String message;
    if (e.getExceptionCause() != null) {
      message = e.getMessage() + ". Cause: " + e.getExceptionCause().toString() + "\n "
          + e.getExceptionCause().getLocalizedMessage();
    } else {
      message = e.getMessage();
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
  }

  @ExceptionHandler(InvalidRecordInput.class)
  private ResponseEntity<String> invalidRecordCreationException(InvalidRecordInput e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(PidResolutionException.class)
  private ResponseEntity<String> pidResolutionException(PidResolutionException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(UnrecognizedPropertyException.class)
  private ResponseEntity<String> unrecognizedPropertyException(UnrecognizedPropertyException e) {
    String message = String.format(
        """
            INVALID REQUEST: One or more request fields are inappropriate for the type of PID you are attempting to create.
            Record Type: %s
            Unrecognized Property: %s
            This kind of record accepts these properties: %s
            """,
        e.getReferringClass().getSimpleName(),
        e.getPropertyName(),
        e.getKnownPropertyIds());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(message);
  }
}
