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
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@ControllerAdvice
@PreAuthorize("isAuthenticated()")
@Slf4j
// @Profile(Profiles.WEB) Is there a digitalspecimenprofile Profile class?
public class HandleController {

  private final HandleService service;
  
  @PostMapping(value = "/createRecordBatch", params = "pidType=handle")
  public ResponseEntity<List<HandleRecordResponse>> createHandleRecordBatch(
      @RequestBody List<HandleRecordRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecordBatch(request));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=doi")
  public ResponseEntity<List<DoiRecordResponse>> createDoiRecordBatch(
      @RequestBody List<DoiRecordRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecordBatch(request));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimen")
  public ResponseEntity<List<DigitalSpecimenResponse>> createDigitalSpecimenBatch(
      @RequestBody List<DigitalSpecimenRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBatch(request));
  }

  @PostMapping(value = "/createRecordBatch", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<List<DigitalSpecimenBotanyResponse>> createDigitalSpecimenBotanyBatch(
      @RequestBody List<DigitalSpecimenBotanyRequest> request)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, PidCreationException, TransformerException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createDigitalSpecimenBotanyBatch(request));
  }

  // Create Single Record 
  @PostMapping(value = "/createRecord", params = "pidType=handle")
  public ResponseEntity<HandleRecordResponse> createHandleRecord(
      @RequestBody HandleRecordRequest hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createHandleRecord(hdl));
  }

  @PostMapping(value = "/createRecord", params = "pidType=doi")
  public ResponseEntity<DoiRecordResponse> createDoiRecord(
      @RequestBody DoiRecordRequest hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDoiRecord(hdl));
  }

  @PostMapping(value = "/createRecord", params = "pidType=digitalSpecimen")
  public ResponseEntity<DigitalSpecimenResponse> createDigitalSpecimen(
      @RequestBody DigitalSpecimenRequest hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimen(hdl));
  }

  @PostMapping(value = "/createRecord", params = "pidType=digitalSpecimenBotany")
  public ResponseEntity<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotany(
      @RequestBody DigitalSpecimenBotanyRequest hdl)
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createDigitalSpecimenBotany(hdl));
  }

  // Hellos and getters

  //TODO this shouldnt need to be authenticated in future (nor reads)
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

  // Authentication
  private String getNameFromToken(Authentication authentication) {
    KeycloakPrincipal<? extends KeycloakSecurityContext> principal =
        (KeycloakPrincipal<?>) authentication.getPrincipal();
    AccessToken token = principal.getKeycloakSecurityContext().getToken();
    return token.getSubject();
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
