package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_SPECIMEN_BOTANY_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_SPECIMEN_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.DOI_RECORD_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.HANDLE_RECORD_FIELDS;
import static eu.dissco.core.handlemanager.domain.PidRecords.HANDLE_RECORD_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

  @GetMapping("/record")
  public ResponseEntity<JsonApiWrapper> resolveSingleHandle(
      @RequestBody byte[] handle
  ) throws JsonProcessingException, PidResolutionException {
    JsonApiWrapper node = service.resolveSingleRecord(handle);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/records")
  public ResponseEntity<List<JsonApiWrapper>> resolveBatchHandle(
      @RequestBody List<String> handleStrings
  ) throws JsonProcessingException, PidResolutionException {

    List<byte[]> handles = new ArrayList<>();
    for (String hdlStr : handleStrings) {
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
    }
    List<JsonApiWrapper> node = service.resolveBatchRecord(handles);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=handle")
  public ResponseEntity<List<JsonApiWrapper>> createHandleRecordJsonBatch(
      @RequestBody List<HandleRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createHandleRecordBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=doi")
  public ResponseEntity<List<JsonApiWrapper>> createDoiRecordJsonBatch(
      @RequestBody List<DoiRecordRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDoiRecordBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=digitalSpecimen")
  public ResponseEntity<List<JsonApiWrapper>> createDigitalSpecimenJsonBatch(
      @RequestBody List<DigitalSpecimenRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBatchJson(requests));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/records", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<List<JsonApiWrapper>> createDigitalSpecimenBotanyJsonBatch(
      @RequestBody List<DigitalSpecimenBotanyRequest> requests)
      throws PidResolutionException, ParserConfigurationException, JsonProcessingException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyBatchJson(requests));
  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record", params = "pidType=handle")
  public ResponseEntity<JsonApiWrapper> createHandleRecordJson(
      @RequestBody HandleRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordJson(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record", params = "pidType=doi")
  public ResponseEntity<JsonApiWrapper> createDoiRecordJson(
      @RequestBody DoiRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordJson(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record", params = "pidType=digitalSpecimen")
  public ResponseEntity<JsonApiWrapper> createDigitalSpecimenJson(
      @RequestBody DigitalSpecimenRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenJson(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/record", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<JsonApiWrapper> createDigitalSpecimenBotanyJson(
      @RequestBody DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, PidCreationException, ParserConfigurationException, JsonProcessingException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyJson(request));
  }

  // Update
  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/record")
  public ResponseEntity<JsonApiWrapper> updateHandleRecord(
      @RequestBody ObjectNode request)
      throws InvalidRecordInput, PidResolutionException, IOException, ParserConfigurationException, TransformerException, PidCreationException {

    JsonNode data = request.get("data");
    byte[] handle = data.get("id").asText().getBytes(StandardCharsets.UTF_8);
    String recordType = data.get("type").asText();
    JsonNode requestAttributes = data.get("attributes");
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.updateRecord(requestAttributes, handle, recordType));
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping(value = "/records")
  public ResponseEntity<List<JsonApiWrapper>> updateHandleRecords(
      @RequestBody List<ObjectNode> request)
      throws InvalidRecordInput, PidResolutionException, IOException, ParserConfigurationException, TransformerException, PidCreationException {

    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecordBatch(request));
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

  @ExceptionHandler(PidCreationException.class)
  private ResponseEntity<String> pidCreationException(PidCreationException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  @ExceptionHandler(InvalidRecordInput.class)
  private ResponseEntity<String> invalidRecordCreationException(PidCreationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(PidResolutionException.class)
  private ResponseEntity<String> pidResolutionException(PidResolutionException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(JsonProcessingException.class)
  private ResponseEntity<String> jsonProcessingException(JsonProcessingException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
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

  //@ExceptionHandler(NullPointerException.class)
  private ResponseEntity<String> missingArgument(NullPointerException e) {

    String message = String.format(
        "Invalid request body. Missing argument : %s",
        e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
  }

}
