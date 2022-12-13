package eu.dissco.core.handlemanager.controller;


import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_TOMBSTONE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
  public ResponseEntity<JsonApiWrapper> resolveSingleHandle(
      @RequestBody byte[] handle
  ) throws PidResolutionException, PidServiceInternalError {
    JsonApiWrapper node = service.resolveSingleRecord(handle);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/records")
  public ResponseEntity<List<JsonApiWrapper>> resolveBatchHandle(
      @RequestBody List<String> handleStrings
  ) throws PidResolutionException, PidServiceInternalError {

    List<byte[]> handles = new ArrayList<>();
    for (String hdlStr : handleStrings) {
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
    }
    List<JsonApiWrapper> node = service.resolveBatchRecord(handles);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @PostMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> createRecordBatch(
      @RequestBody List<ObjectNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRecordInput {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createRecordBatch(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=handle")
  public ResponseEntity<List<JsonApiWrapper>> createHandleRecordJsonBatch(
      @RequestBody List<HandleRecordRequest> requests)
      throws PidResolutionException, PidServiceInternalError {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createHandleRecordBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=doi")
  public ResponseEntity<List<JsonApiWrapper>> createDoiRecordJsonBatch(
      @RequestBody List<DoiRecordRequest> requests)
      throws PidResolutionException, PidServiceInternalError {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDoiRecordBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=digitalSpecimen")
  public ResponseEntity<List<JsonApiWrapper>> createDigitalSpecimenJsonBatch(
      @RequestBody List<DigitalSpecimenRequest> requests)
      throws PidResolutionException, PidServiceInternalError {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<List<JsonApiWrapper>> createDigitalSpecimenBotanyJsonBatch(
      @RequestBody List<DigitalSpecimenBotanyRequest> requests)
      throws PidResolutionException, PidServiceInternalError {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyBatchJson(requests));
  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> createRecord(
      @RequestBody ObjectNode requestRoot)
      throws PidResolutionException, PidServiceInternalError, JsonProcessingException, InvalidRecordInput {

    JsonNode request = requestRoot.get("data");
    String type = request.get("type").asText();
    switch(type){
      case RECORD_TYPE_HANDLE -> {
        HandleRecordRequest requestAttributes = mapper.treeToValue(request.get("attributes"), HandleRecordRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordJson(requestAttributes));
      }
      case RECORD_TYPE_DOI -> {
        DoiRecordRequest requestAttributes = mapper.treeToValue(request.get("attributes"), DoiRecordRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordJson(requestAttributes));
      }
      case RECORD_TYPE_DS -> {
        DigitalSpecimenRequest requestAttributes = mapper.treeToValue(request.get("attributes"), DigitalSpecimenRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenJson(requestAttributes));
      }
      case RECORD_TYPE_DS_BOTANY -> {
        DigitalSpecimenBotanyRequest requestAttributes = mapper.treeToValue(request.get("attributes"), DigitalSpecimenBotanyRequest.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenBotanyJson(requestAttributes));
      }
      default -> throw new InvalidRecordInput("INVALID INPUT. Unrecognized Type");
    }

  }

  // Update
  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> updateRecord(
      @RequestBody ObjectNode request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    JsonNode data = request.get("data");
    byte[] handle = data.get("id").asText().getBytes(StandardCharsets.UTF_8);
    String recordType = data.get("type").asText();
    JsonNode requestAttributes = data.get("attributes");
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.updateRecord(requestAttributes, handle, recordType));
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> updateRecords(
      @RequestBody List<ObjectNode> request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecordBatch(request));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> archiveRecord(
      @RequestBody ObjectNode request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {
    JsonNode data = request.get("data");
    byte[] handle = data.get("id").asText().getBytes(StandardCharsets.UTF_8);
    log.info(data.toString());
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecord(data, handle));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> archiveRecords(
      @RequestBody List<ObjectNode> request)
      throws InvalidRecordInput, PidResolutionException, PidServiceInternalError {
    return ResponseEntity.status(HttpStatus.OK).body(service.archiveRecordBatch(request));
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

  //Error Handling

  @ExceptionHandler(PidServiceInternalError.class)
  private ResponseEntity<String> pidCreationException(PidServiceInternalError e) {
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
