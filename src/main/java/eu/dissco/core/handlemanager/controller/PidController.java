package eu.dissco.core.handlemanager.controller;


import eu.dissco.core.handlemanager.domain.openapi.patch.AnnotationPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.DataMappingPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.DigitalMediaPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.DigitalSpecimenPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.DoiPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.HandlePatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.MasPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.OrganisationPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.patch.SourceSystemPatchRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.AnnotationPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.DataMappingPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.DigitalMediaPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.DigitalSpecimenPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.DoiPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.HandlePostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.MasPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.OrganisationPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.post.SourceSystemPostRequest;
import eu.dissco.core.handlemanager.domain.openapi.tombstone.TombstoneRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.service.PidService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  private static final String PREFIX_OAS = "Prefix of target ID";
  private static final String SUFFIX_OAS = "Suffix of target ID";

  // Getters
  @Operation(summary = "Resolve single PID record")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PID successfully retrieved", content = {
          @Content(mediaType = "application/json")})
  })
  @GetMapping("/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperRead> resolvePid(
      @Parameter(description = PREFIX_OAS) @PathVariable("prefix") String prefix,
      @Parameter(description = SUFFIX_OAS) @PathVariable("suffix") String suffix,
      HttpServletRequest r) throws PidResolutionException {
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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PIDs successfully retrieved", content = {
          @Content(mediaType = "application/json")})
  })
  @GetMapping("/records")
  public ResponseEntity<JsonApiWrapperRead> resolvePids(
      @Parameter(description = "Handles to resolve") @RequestParam List<String> handles,
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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PIDs successfully retrieved", content = {
          @Content(mediaType = "application/json")})
  })
  @GetMapping("/records/primarySpecimenObjectId")
  public ResponseEntity<JsonApiWrapperWrite> searchByPrimarySpecimenObjectId(
      @RequestParam String normalisedPrimarySpecimenObjectId) throws PidResolutionException {
    return ResponseEntity.status(HttpStatus.OK).body(
        service.searchByPhysicalSpecimenId(normalisedPrimarySpecimenObjectId));
  }

  @Operation(summary = "Create single PID Record",
      description = """
          Creates a single PID record. Clients may indicate PID Record is a "draft". In that case,
          the PidStatus will be set to "DRAFT", and if it is a DOI, the PID will not be published to DataCite.
          """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          schema = @Schema(oneOf = {
              AnnotationPostRequest.class,
              DataMappingPostRequest.class,
              DigitalMediaPostRequest.class,
              DigitalSpecimenPostRequest.class,
              DoiPostRequest.class,
              HandlePostRequest.class,
              MasPostRequest.class,
              OrganisationPostRequest.class,
              SourceSystemPostRequest.class
          })))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "PID successfully Created", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = JsonApiWrapperWrite.class)),
      }),
      @ApiResponse(responseCode = "204", description = "Received empty request")
  })
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

  @Operation(summary = "Activate draft handles")
  @PostMapping(value = "/activate")
  public ResponseEntity<Void> activateRecords(
      @Parameter(description = "Draft handles to activate") @RequestBody List<String> handles)
      throws InvalidRequestException {
    service.activateRecords(handles);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Create multiple PID Records at a time.",
      description = """
          Creates Multiple PID records. PID records must all be of the same FDO type.
          Clients may indicate PID Records are "draft". In that case,
          the PidStatus will be set to "DRAFT" for all PIDs, and if they are DOI objects, the PIDs will not be published to DataCite.
          """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(
              oneOf = {
                  AnnotationPostRequest.class,
                  DataMappingPostRequest.class,
                  DigitalMediaPostRequest.class,
                  DigitalSpecimenPostRequest.class,
                  DoiPostRequest.class,
                  HandlePostRequest.class,
                  MasPostRequest.class,
                  OrganisationPostRequest.class,
                  SourceSystemPostRequest.class
              }
          ))))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "PIDs successfully Created", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = JsonApiWrapperWrite.class)),
      }),
      @ApiResponse(responseCode = "204", description = "Received empty request")
  })
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

  // Update
  @Operation(summary = "Update existing PID Record", description = "Update exiting PID records")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          schema = @Schema(
              oneOf = {
                  AnnotationPatchRequest.class,
                  DataMappingPatchRequest.class,
                  DigitalMediaPatchRequest.class,
                  DigitalSpecimenPatchRequest.class,
                  DoiPatchRequest.class,
                  HandlePatchRequest.class,
                  MasPatchRequest.class,
                  OrganisationPatchRequest.class,
                  SourceSystemPatchRequest.class
              }
          )))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "PID successfully updated", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = JsonApiWrapperWrite.class)),
      }),
      @ApiResponse(responseCode = "204", description = "Received empty request")
  })
  @PatchMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> updateRecord(
      @Parameter(description = PREFIX_OAS) @PathVariable("prefix") String prefix,
      @Parameter(description = SUFFIX_OAS) @PathVariable("suffix") String suffix,
      @RequestBody PatchRequest request, Authentication authentication)
      throws InvalidRequestException, UnprocessableEntityException {
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

  @Operation(summary = "Update multiple PID Records", description = "Update multiple PID records. PID records must be all of the same FDO type")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(
              schema = @Schema(
                  oneOf = {
                      AnnotationPatchRequest.class,
                      DataMappingPatchRequest.class,
                      DigitalMediaPatchRequest.class,
                      DigitalSpecimenPatchRequest.class,
                      DoiPatchRequest.class,
                      HandlePatchRequest.class,
                      MasPatchRequest.class,
                      OrganisationPatchRequest.class,
                      SourceSystemPatchRequest.class
                  }
              ))))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "PIDs successfully Created", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = JsonApiWrapperWrite.class)),
      }),
      @ApiResponse(responseCode = "204", description = "Received empty request")
  })
  @PatchMapping(value = "/")
  public ResponseEntity<JsonApiWrapperWrite> updateRecords(@RequestBody List<PatchRequest> requests,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch update", authentication.getName());
    log.info("Received valid batch update request for {} PIDS", requests.size());
    var result = service.updateRecords(requests, true);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }


  @Operation(summary = "Tombstone a single PID record", description = """
      Tombstone a given PID record. A client may only tombstone DOIs at the DOI endpoint, and handles at the handle endpoint
      """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(
              schema = @Schema(implementation = TombstoneRequest.class))))
  @PutMapping(value = "/{prefix}/{suffix}")
  public ResponseEntity<JsonApiWrapperWrite> tombstoneRecord(
      @Parameter(description = PREFIX_OAS) @PathVariable("prefix") String prefix,
      @Parameter(description = SUFFIX_OAS) @PathVariable("suffix") String suffix,
      @RequestBody PatchRequest request,
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


  @Operation(summary = "Archive multiple PID records", description = """
      Tombstone a batch of PID records. A client may only tombstone DOIs at the DOI endpoint, and handles at the handle endpoint
      """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(
              schema = @Schema(implementation = TombstoneRequest.class))))
  @PutMapping(value = "/")
  public ResponseEntity<JsonApiWrapperWrite> archiveRecords(
      @RequestBody List<PatchRequest> requests,
      Authentication authentication) throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch tombstone", authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(service.tombstoneRecords(requests));
  }

  @Hidden
  @Operation(summary = "rollback handle creation", description = "Internal use only")
  @DeleteMapping(value = "/rollback/create")
  public ResponseEntity<Void> rollbackHandleCreation(@RequestBody List<String> handles,
      Authentication authentication) {
    log.info(RECEIVED_MSG, "batch rollback create", authentication.getName());
    service.rollbackHandles(handles);
    return ResponseEntity.ok().build();
  }

  @Hidden
  @Operation(summary = "rollback handle update")
  @DeleteMapping(value = "/rollback/update")
  public ResponseEntity<JsonApiWrapperWrite> rollbackHandleUpdate(
      @RequestBody List<PatchRequest> requests, Authentication authentication)
      throws InvalidRequestException, UnprocessableEntityException {
    log.info(RECEIVED_MSG, "batch rollback update", authentication.getName());
    return ResponseEntity.status(HttpStatus.OK).body(service.updateRecords(requests, false));
  }

  @Hidden
  @Operation(summary = "rollback handle update by physical identifier")
  @DeleteMapping(value = "/rollback/physId")
  public ResponseEntity<Void> rollbackHandlePhysId(
      @RequestBody List<String> physicalIds, Authentication authentication) {
    log.info(RECEIVED_MSG, "batch rollback (physical id)", authentication.getName());
    service.rollbackHandlesFromPhysId(physicalIds);
    return ResponseEntity.ok().build();
  }

}