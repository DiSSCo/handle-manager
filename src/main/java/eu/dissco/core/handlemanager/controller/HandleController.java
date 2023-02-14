package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.VALID_PID_STATUS;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.service.HandleService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  private final HandleService service;

  private static final String SANDBOX_URI = "https://sandbox.dissco.tech/";


  // Hellos and getters
  @GetMapping(value = "/health")
  public ResponseEntity<String> hello() {
    return new ResponseEntity<>("API is running", HttpStatus.OK);
  }

  // List all handle values
  @GetMapping(value = "/names")
  public ResponseEntity<List<String>> getAllHandlesByPidStatus(
      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
      @RequestParam(name = "pidStatus", defaultValue = "ALL") String pidStatus)
      throws PidResolutionException, InvalidRecordInput {

    if (!VALID_PID_STATUS.contains(pidStatus)) {
      throw new InvalidRecordInput(
          "Invalid Input. Pid Status not recognized. Available Pid Statuses: " + VALID_PID_STATUS);
    }
    List<String> handleList;
    if (pidStatus.equals("ALL")) {
      handleList = service.getHandlesPaged(pageNum, pageSize);
    } else {
      handleList = service.getHandlesPaged(pageNum, pageSize, pidStatus);
    }
    if (handleList.isEmpty()) {
      throw new PidResolutionException("Unable to resolve pids");
    }
    return ResponseEntity.ok(handleList);
  }

  @GetMapping("/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperRead> resolvePid(
      @PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix,
      HttpServletRequest r
  ) throws PidResolutionException {
    String path = SANDBOX_URI + r.getRequestURI();
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);

    var node = service.resolveSingleRecord(handle, path);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/{prefix}/{suffix}/{version}")
  public ResponseEntity<JsonApiWrapperRead> resolvePidVersion(
      @PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix,
      @PathVariable("version") String version,
      HttpServletRequest r
  ) throws PidResolutionException {
    String path = SANDBOX_URI + r.getRequestURI();
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);

    var node = service.resolveSingleRecord(handle, path);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @PostMapping("/records")
  public ResponseEntity<JsonApiWrapperRead> resolvePids(
      @RequestBody List<JsonNode> requests,
      HttpServletRequest r
  ) throws PidResolutionException, InvalidRecordInput {

    String path = SANDBOX_URI + r.getRequestURI();
    List<byte[]> handles = new ArrayList<>();

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, false, true, false);
      handles.add(request.get(NODE_DATA).get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8));
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handles, path));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> createRecord(
      @RequestBody JsonNode request)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {
    checkRequestNodesPresent(request, true, true, false, true);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/batch")
  public ResponseEntity<JsonApiWrapperWrite> createRecords(
      @RequestBody List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, true, false, true);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(requests));
  }

  // Update
  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(
      @PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix,
      @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    checkRequestNodesPresent(request, true, true, false, true);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleData = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);

    if (!Arrays.equals(handle, handleData)) {
      throw new InvalidRecordInput("Handle in request URL does not match id in request body.");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(service.updateRecords(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, true, true, true);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests));
  }

  //@PreAuthorize("isAuthenticated()")
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecord(
      @PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix,
      @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException {
    checkRequestNodesPresent(request, true, false, false, true);
    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleRequest = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    if (!Arrays.equals(handle, handleRequest)) {
      throw new InvalidRecordInput("Handle in request URL does not match id in request body.");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.archiveRecordBatch(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException {
    for (JsonNode request : requests) {
      checkRequestNodesPresent(request, true, false, true, true);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(requests));
  }

  private void checkRequestNodesPresent(JsonNode requestRoot, boolean checkData, boolean checkType,
      boolean checkId, boolean checkAttributes) throws InvalidRecordInput {

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
    if (e.getCause() != null) {
      message = e.getMessage() + ". Cause: " + e.getCause().toString() + "\n "
          + e.getCause().getLocalizedMessage();
    } else {
      message = e.getMessage();
    }
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
  }

  @ExceptionHandler(InvalidRecordInput.class)
  private ResponseEntity<String> invalidRecordCreationException(InvalidRecordInput e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(PidResolutionException.class)
  private ResponseEntity<String> pidResolutionException(PidResolutionException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }
}