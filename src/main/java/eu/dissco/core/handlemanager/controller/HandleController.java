package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.VALID_PID_STATUS;
import static eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaLibrary.validatePutRequest;
import static eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaLibrary.validateResolveRequest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaLibrary;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaStaticContextInitializer;
import eu.dissco.core.handlemanager.exceptions.ExceptionResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
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

  private static final String SANDBOX_URI = "https://sandbox.dissco.tech/";
  private final HandleService service;
  @Autowired
  private final JsonSchemaStaticContextInitializer initializer;

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
  public ResponseEntity<JsonApiWrapperRead> resolvePid(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, HttpServletRequest r) throws PidResolutionException {
    String path = SANDBOX_URI + r.getRequestURI();
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);

    var node = service.resolveSingleRecord(handle, path);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/{prefix}/{suffix}/{version}")
  public ResponseEntity<JsonApiWrapperRead> resolvePidVersion(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @PathVariable("version") String version,
      HttpServletRequest r) throws PidResolutionException {
    String path = SANDBOX_URI + r.getRequestURI();
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);

    var node = service.resolveSingleRecord(handle, path);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @PostMapping("/records")
  public ResponseEntity<JsonApiWrapperRead> resolvePids(@RequestBody List<JsonNode> requests,
      HttpServletRequest r) throws PidResolutionException, InvalidRecordInput {

    String path = SANDBOX_URI + r.getRequestURI();
    List<byte[]> handles = new ArrayList<>();

    for (JsonNode request : requests) {
      validateResolveRequest(request);
      handles.add(request.get(NODE_DATA).get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8));
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handles, path));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> createRecord(@RequestBody JsonNode request)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {
    JsonSchemaLibrary.validatePostRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/batch")
  public ResponseEntity<JsonApiWrapperWrite> createRecords(@RequestBody List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {

    for (JsonNode request : requests) {
      JsonSchemaLibrary.validatePostRequest(request);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(requests));
  }

  // Update
  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    JsonSchemaLibrary.validatePatchRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleData = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);

    if (!Arrays.equals(handle, handleData)) {
      throw new InvalidRecordInput("Handle in request URL does not match id in request body.");
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    for (JsonNode request : requests) {
      JsonSchemaLibrary.validatePatchRequest(request);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests));
  }

  //@PreAuthorize("isAuthenticated()")
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody JsonNode request)
      throws InvalidRecordInput, PidResolutionException {

    validatePutRequest(request);

    JsonNode data = request.get(NODE_DATA);
    byte[] handle = (prefix + "/" + suffix).getBytes(StandardCharsets.UTF_8);
    byte[] handleRequest = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
    if (!Arrays.equals(handle, handleRequest)) {
      throw new InvalidRecordInput(
          "Handle in request URL does not match id in request body. URL: " + handle + ", body: "
              + handleRequest);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(List.of(request)));
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping(value = "")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(@RequestBody List<JsonNode> requests)
      throws InvalidRecordInput, PidResolutionException {
    for (JsonNode request : requests) {
      validatePutRequest(request);
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(requests));
  }

  //Exception Handling
  @ExceptionHandler(PidServiceInternalError.class)
  private ResponseEntity<ExceptionResponse> pidServiceInternalError(PidServiceInternalError e) {
    String message;
    if (e.getCause() != null) {
      message = e.getMessage() + ". Cause: " + e.getCause().toString() + "\n " + e.getCause()
          .getLocalizedMessage();
    } else {
      message = e.getMessage();
    }
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY),
        e.getClass().getSimpleName(), message
    );
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponse);
  }

  @ExceptionHandler(InvalidRecordInput.class)
  private ResponseEntity<ExceptionResponse> invalidRecordInputException(InvalidRecordInput e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.BAD_REQUEST),
        e.getClass().getSimpleName(), e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
  }

  @ExceptionHandler(PidResolutionException.class)
  private ResponseEntity<ExceptionResponse> pidResolutionException(PidResolutionException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        String.valueOf(HttpStatus.NOT_FOUND),
        e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
  }
}