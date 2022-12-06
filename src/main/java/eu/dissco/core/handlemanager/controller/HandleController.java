package eu.dissco.core.handlemanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
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
// @Profile(Profiles.WEB) Is there a digitalspecimenprofile Profile class?
public class HandleController {

  private final HandleService service;

  @GetMapping("/resolve")
  public ResponseEntity<JsonApiWrapper> resolveSingleHandle(
      @RequestBody byte[] handle
  ) throws JsonProcessingException, PidResolutionException {
    JsonApiWrapper node = service.resolveSingleRecord(handle);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  @GetMapping("/resolveList")
  public ResponseEntity<List<JsonApiWrapper>> resolveBatchHandle(
      @RequestBody List<String> handleStrings
  ) throws JsonProcessingException {

    List<byte[]> handles = new ArrayList<>();
    for (String hdlStr : handleStrings){
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
    }
    List<JsonApiWrapper> node = service.resolveBatchRecord(handles);
    return ResponseEntity.status(HttpStatus.OK).body(node);
  }

  //@PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/createRecordJson", params = "pidType=handle")
  public ResponseEntity<JsonApiWrapper> createHandleRecordJson(
      @RequestBody HandleRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordJson(request));
  }

  @PostMapping(value = "/createRecordJson", params = "pidType=doi")
  public ResponseEntity<JsonApiWrapper> createDoiRecordJson(
      @RequestBody DoiRecordRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordJson(request));
  }

  @PostMapping(value = "/createRecordJson", params = "pidType=digitalSpecimen")
  public ResponseEntity<JsonApiWrapper> createDigitalSpecimenJson(
      @RequestBody DigitalSpecimenRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenJson(request));
  }

  @PostMapping(value = "/createRecordJson", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<JsonApiWrapper> createDigitalSpecimenBotanyJson(
      @RequestBody DigitalSpecimenBotanyRequest request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenBotanyJson(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/createRecordBatch", params = "pidType=handle")
  public ResponseEntity<List<HandleRecordResponse>> createHandleRecordBatch(
      @RequestBody List<HandleRecordRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordBatch(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/createRecordBatch", params = "pidType=doi")
  public ResponseEntity<List<DoiRecordResponse>> createDoiRecordBatch(
      @RequestBody List<DoiRecordRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordBatch(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimen")
  public ResponseEntity<List<DigitalSpecimenResponse>> createDigitalSpecimenBatch(
      @RequestBody List<DigitalSpecimenRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBatch(request));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<List<DigitalSpecimenBotanyResponse>> createDigitalSpecimenBotanyBatch(
      @RequestBody List<DigitalSpecimenBotanyRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyBatch(request));
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
      @RequestParam(name = "pidStatus") String pidStatus,
      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "100") int pageSize)
      throws PidResolutionException {

    List<String> handleList = service.getHandlesPaged(pidStatus, pageNum, pageSize);
    if (handleList.isEmpty()) {
      throw new PidResolutionException("Unable to resolve pids");
    }
    return ResponseEntity.ok(handleList);
  }

  //Error Handling

  @ExceptionHandler(PidCreationException.class)
  private ResponseEntity<String> pidCreationException(PidCreationException e) {
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

}
