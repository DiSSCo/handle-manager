package eu.dissco.core.handlemanager.controller;


import eu.dissco.core.handlemanager.component.SchemaValidator;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.service.PidService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
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
@RequestMapping
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class PidController {

  private final PidService service;
  private final ApplicationProperties applicationProperties;
  private static final String RECEIVED_MSG = "Received {} request from user {}";

  // Getters
  @Operation(summary = "Resolve single PID record")
  @GetMapping("/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperRead> resolvePid(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, HttpServletRequest r) throws PidResolutionException {
    String link = applicationProperties.getUiUrl() + "/" + r.getRequestURI();
    String handle = prefix + "/" + suffix;
    if (prefix.equals(applicationProperties.getPrefix())) {
      var node = service.resolveSingleRecord(handle, link);
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
      HttpServletRequest r) throws InvalidRequestException {
    String link = applicationProperties.getUiUrl() + "/" + r.getRequestURI();

    if (handles.size() > applicationProperties.getMaxHandles()) {
      throw new InvalidRequestException(
          "Attempting to resolve more than maximum permitted PIDs in a single request. Maximum handles: "
              + applicationProperties.getMaxHandles());
    }

    return ResponseEntity.status(HttpStatus.OK).body(service.resolveBatchRecord(handles, link));
  }

  @Operation(summary = "Given a physical identifier (i.e. local identifier), resolve PID record")
  @GetMapping("/records/primarySpecimenObjectId")
  public ResponseEntity<JsonApiWrapperWrite> searchByPrimarySpecimenObjectId(
      @RequestParam String normalisedPrimarySpecimenObjectId) throws PidResolutionException {
    return ResponseEntity.status(HttpStatus.OK).body(
        service.searchByPhysicalSpecimenId(normalisedPrimarySpecimenObjectId));
  }

  @Operation(summary = "Create single PID Record")
  @PostMapping(value = {"/", "/{draft}"})
  public ResponseEntity<JsonApiWrapperWrite> createRecord(
      @PathVariable Optional<Boolean> draft,
      @RequestBody PostRequest request,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    var isDraft = draft.orElse(false);
    log.info(RECEIVED_MSG, "single create", authentication.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.createRecords(List.of(request), isDraft));
  }

  @Operation(summary = "Create multiple PID Records at a time.")
  @PostMapping(value = {"/batch", "/batch/{draft}"})
  public ResponseEntity<JsonApiWrapperWrite> createRecords(
      @PathVariable Optional<Boolean> draft,
      @RequestBody List<PostRequest> requests,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    if (requests.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    var isDraft = draft.orElse(false);
    log.info(RECEIVED_MSG, "batch create", authentication.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecords(requests, isDraft));
  }

  @PostMapping(value = "/activate")
  public ResponseEntity<Void> activateRecords(@RequestBody List<String> handles)
      throws InvalidRequestException {
    service.activateRecords(handles);
    return ResponseEntity.ok().build();
  }

  // Update
  @Operation(summary = "Update existing PID Record")
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody PatchRequest request,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info("Received single update request for PID {}/{} from user {}", prefix, suffix,
        authentication.getName());
    var handle = (prefix + "/" + suffix);
    var handleData = request.data().id();
    if (!handle.equals(handleData)) {
      throw new InvalidRequestException(String.format(
          "Handle in request path does not match id in request body. Path: %s, Body: %s",
          handle,
          handleData));
    }
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(List.of(request), true));
  }

  @Operation(summary = "Update multiple PID Records")
  @PatchMapping(value = "/")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<PatchRequest> requests,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch update", authentication.getName());
    log.info("Received valid batch update request for {} PIDS", requests.size());
    var result = service.updateRecords(requests, true);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }


  @Operation(summary = "Archive given record")
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecord(@PathVariable("prefix") String prefix,
      @PathVariable("suffix") String suffix, @RequestBody PatchRequest request,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info("Received tombstone request for PID {}/{} from user {}", prefix, suffix,
        authentication.getName());
    var handle = (prefix + "/" + suffix);
    if (!handle.equals(request.data().id())) {
      throw new InvalidRequestException(
          "Handle in request path does not match id in request body. Path: " + handle
              + ". Body: " + request.data().id());
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.tombstoneRecords(List.of(request)));
  }

  @Operation(summary = "rollback handle creation")
  @DeleteMapping(value = "/rollback/create")
  public ResponseEntity<Void> rollbackHandleCreation(@RequestBody List<String> handles,
      Authentication authentication) {
    log.info(RECEIVED_MSG, "batch rollback create", authentication.getName());
    service.rollbackHandles(handles);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "rollback handle update")
  @DeleteMapping(value = "/rollback/update")
  public ResponseEntity<JsonApiWrapperWrite> rollbackHandleUpdate(
      @RequestBody List<PatchRequest> requests, Authentication authentication)
      throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch rollback update", authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests, false));
  }

  @Operation(summary = "rollback handle update")
  @DeleteMapping(value = "/rollback/physId")
  public ResponseEntity<Void> rollbackHandlePhysId(
      @RequestBody List<String> physicalIds, Authentication authentication) {
    log.info(RECEIVED_MSG, "batch rollback (physical id)", authentication.getName());
    service.rollbackHandlesFromPhysId(physicalIds);
    return ResponseEntity.ok().build();
  }


  @Operation(summary = "Archive multiple PID records")
  @PutMapping(value = "/")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(
      @RequestBody List<PatchRequest> requests,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch tombstone", authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(service.tombstoneRecords(requests));
  }

}