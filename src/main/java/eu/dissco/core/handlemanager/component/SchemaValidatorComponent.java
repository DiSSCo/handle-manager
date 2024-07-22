package eu.dissco.core.handlemanager.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.validation.SchemaLibrary;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaValidatorComponent {

  private final SchemaLibrary library;

  public void validatePost(List<PostRequest> requests) throws InvalidRequestException {
    for (var request : requests) {
      validateSchema(request.data().type(), request.data().attributes());
    }
  }

  public void validatePatch(List<PatchRequest> requests) throws InvalidRequestException {
    for (var request : requests) {
      validateSchema(request.data().type(), request.data().attributes());
    }
  }

  private void validateSchema(FdoType fdoType, JsonNode attributes) throws InvalidRequestException {
    var schema = selectSchema(fdoType);
    var errors = schema.validate(attributes);
    if (!errors.isEmpty()) {
      var detail = setErrorMessage(errors, fdoType, attributes);
      log.error(detail);
      throw new InvalidRequestException(detail);
    }
  }

  private JsonSchema selectSchema(FdoType fdoType) {
    switch (fdoType) {
      case HANDLE -> {
        return library.handleSchema();
      }
      case DOI -> {
        return library.doiSchema();
      }
      case ANNOTATION -> {
        return library.annotationSchema();
      }
      case DATA_MAPPING -> {
        return library.dataMappingSchema();
      }
      case DIGITAL_MEDIA -> {
        return library.digitalMediaSchema();
      }
      case DIGITAL_SPECIMEN -> {
        return library.digitalSpecimenSchema();
      }
      case MAS -> {
        return library.masSchema();
      }
      case ORGANISATION -> {
        return library.organisationSchema();
      }
      case SOURCE_SYSTEM -> {
        return library.sourceSystemSchema();
      }
      default -> {
        log.error("Unknown fdo type {}", fdoType);
        throw new UnsupportedOperationException("Invalid type");
      }
    }
  }

  private String setErrorMessage(Set<ValidationMessage> validationErrors, FdoType fdoType,
      JsonNode requestAttributes) {
    var errorBuilder = new StringBuilder()
        .append("Invalid request body for request type ")
        .append(fdoType.getDigitalObjectName());
    for (var validationError : validationErrors) {
      if (validationError.getType().equals("required")) {
        errorBuilder.append("\nMissing attributes: ")
            .append(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("additionalProperties")) {
        errorBuilder.append("\nUnrecognized attributes: ")
            .append(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("enum")) {
        errorBuilder.append("\nEnum errors: ")
            .append(validationError.getMessage())
            .append(". Invalid value:")
            .append(getProblemEnumValue(requestAttributes, validationError.getPath()));
      } else {
        errorBuilder.append("\nOther errors: ")
            .append(validationError.getMessage());
      }
    }
    return errorBuilder.toString();
  }

  private String getProblemEnumValue(JsonNode request, String path) {
    path = path.replace("$.", "");
    try {
      return request.get(path).asText();
    } catch (NullPointerException npe) {
      return "Unable to parse problem enum value";
    }
  }


}
