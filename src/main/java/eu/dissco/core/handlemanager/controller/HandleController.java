package eu.dissco.core.handlemanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
//Controller, return value of methods will be returned as the response body for an API; handleService is injected as dependency using Autowired
@RequestMapping("/api")
// Defines base URL for all REST APIs; followed by REST endpoints given to each controller methods
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class HandleController {


  private final HandleService service;

  // ** Create Records: handle, doi, digital specimen, botany specimen **

  // Batch creation
  @PostMapping(value = "/createRecordBatch", params = "pidType=handle")
  public ResponseEntity<List<HandleRecordResponse>> createHandleRecordBatch(
      @RequestBody List<HandleRecordRequest> hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordBatch(hdl));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=doi")
  public ResponseEntity<List<DoiRecordResponse>> createDoiRecordBatch(
      @RequestBody List<DoiRecordRequest> doi)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordBatch(doi));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimen")
  public ResponseEntity<List<DigitalSpecimenResponse>> createDigitalSpecimenBatch(
      @RequestBody List<DigitalSpecimenRequest> ds)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenBatch(ds));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<List<DigitalSpecimenBotanyResponse>> createDigitalSpecimenBotanyBatch(
      @RequestBody List<DigitalSpecimenBotanyRequest> dsB)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyBatch(dsB));
  }

  // Create Single Record
  @PostMapping(value = "/createRecord", params = "pidType=handle")
  public ResponseEntity<HandleRecordResponse> createRecord(
      @RequestBody HandleRecordRequest hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecord(hdl));
  }

  @PostMapping(value = "/createRecord", params = "pidType=doi")
  public ResponseEntity<HandleRecordResponse> createRecord(
      @RequestBody DoiRecordRequest doi)
      throws PidResolutionException, JsonProcessingException, TransformerException, ParserConfigurationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecord(doi));
  }

  @PostMapping(value = "/createRecord", params = "pidType=digitalSpecimen")
  public ResponseEntity<HandleRecordResponse> createRecord(
      @RequestBody DigitalSpecimenRequest ds)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenRecord(ds));
  }

  @PostMapping(value = "/createRecord", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<HandleRecordResponse> createRecord(
      @RequestBody DigitalSpecimenBotanyRequest dsB)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyRecord(dsB));
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

  @GetMapping(value = "/subset")
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

  @ExceptionHandler(PidCreationException.class)
  private ResponseEntity<String> pidCreationException(PidCreationException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
