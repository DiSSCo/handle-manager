package eu.dissco.core.handlemanager.domain.validation;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.TOMBSTONE;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DataMappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalMediaRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.fdo.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonSchemaValidator {

  // JsonNodes
  private JsonNode postReqJsonNode;
  private JsonNode patchReqJsonNode;
  private JsonNode putReqJsonNode;
  private JsonNode handleJsonNode;
  private JsonNode doiJsonNode;
  private JsonNode digitalSpecimenJsonNode;
  private JsonNode digitalMediaJsonNode;
  private JsonNode tombstoneReqJsonNode;
  private JsonNode annotationJsonNode;
  private JsonNode mappingJsonNode;
  private JsonNode sourceSystemJsonNode;
  private JsonNode organisationJsonNode;
  private JsonNode masJsonNode;

  // Schemas
  private JsonSchema postReqSchema;
  private JsonSchema patchReqSchema;
  private JsonSchema putReqSchema;
  private JsonSchema handleSchema;
  private JsonSchema doiSchema;
  private JsonSchema digitalSpecimenSchema;
  private JsonSchema digitalMediaSchema;
  private JsonSchema tombstoneReqSchema;
  private JsonSchema annotationSchema;
  private JsonSchema dataMappingSchema;
  private JsonSchema sourceSystemSchema;
  private JsonSchema organisationSchema;
  private JsonSchema masSchema;

  public JsonSchemaValidator() {
    setPostRequestAttributesJsonNodes();
    setRequestJsonNodes();
    setJsonSchemas();
  }

  private void setPostRequestAttributesJsonNodes() {
    var schemaGenerator = new SchemaGenerator(jacksonModuleSchemaConfig());
    handleJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenJsonNode = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalMediaJsonNode = schemaGenerator.generateSchema(DigitalMediaRequest.class);
    tombstoneReqJsonNode = schemaGenerator.generateSchema(TombstoneRecordRequest.class);
    annotationJsonNode = schemaGenerator.generateSchema(AnnotationRequest.class);
    mappingJsonNode = schemaGenerator.generateSchema(DataMappingRequest.class);
    sourceSystemJsonNode = schemaGenerator.generateSchema(SourceSystemRequest.class);
    organisationJsonNode = schemaGenerator.generateSchema(OrganisationRequest.class);
    masJsonNode = schemaGenerator.generateSchema(MasRequest.class);
  }

  private SchemaGeneratorConfig jacksonModuleSchemaConfig() {
    // Uses Jackson Annotations to  generate schemas for objects like HandleRecordRequest, DoiRecordRequest, etc
    // Specific for POST request attributes, where all fields are required

    JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
        JacksonOption.FLATTENED_ENUMS_FROM_JSONPROPERTY);

    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON).with(
            Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT)
        .with(module);

    // Restrict enum values
    configBuilder.forTypesInGeneral()
        .withEnumResolver(scope -> scope.getType().getErasedType().isEnum()
            ? Stream.of(scope.getType().getErasedType().getEnumConstants())
            .map(v -> ((Enum) v).name()).toList()
            : null);

    // Min items in array must be 1
    configBuilder.forFields()
        .withArrayMinItemsResolver(field -> field
            .getAnnotationConsideringFieldAndGetterIfSupported(NotEmpty.class) == null ? null : 1);

    // Array items must be unique
    configBuilder.forTypesInGeneral()
        .withArrayUniqueItemsResolver(
            scope -> scope.getType().isInstanceOf(String[].class) ? true : null);

    // Allow null if specified
    configBuilder.forFields()
        .withNullableCheck(
            field -> field.getAnnotationConsideringFieldAndGetter(Nullable.class) != null);

    return configBuilder.build();
  }

  private void setRequestJsonNodes() {
    var schemaGenerator = new SchemaGenerator(requestSchemaConfig());
    postReqJsonNode = schemaGenerator.generateSchema(PostRequest.class);
    patchReqJsonNode = schemaGenerator.generateSchema(PatchRequest.class);
    putReqJsonNode = schemaGenerator.generateSchema(TombstoneRequest.class);
  }

  public SchemaGeneratorConfig requestSchemaConfig() {
    // Used for top-level requests, e.g. PatchRequest, PostRequest, PutRequest Objects
    // These schemas allow unknown fields to accommodate the attributes field (which could be one of several schemas)
    // Attribute schemas are checked using one of the two other schema configs

    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
        .with(Option.FLATTENED_ENUMS_FROM_TOSTRING);

    configBuilder.forFields()
        .withRequiredCheck(
            field -> field.getAnnotationConsideringFieldAndGetter(Nullable.class) == null);

    configBuilder.forFields()
        .withAdditionalPropertiesResolver(field -> field.getType().getErasedType() == JsonNode.class
            ? null : Void.class);

    configBuilder.forTypesInGeneral()
        .withEnumResolver(scope -> scope.getType().getErasedType().isEnum()
            ? Stream.of(scope.getType().getErasedType().getEnumConstants())
            .map(v -> ((Enum) v).name()).toList()
            : null);

    return configBuilder.build();
  }

  private void setJsonSchemas() {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
    postReqSchema = factory.getSchema(postReqJsonNode);
    patchReqSchema = factory.getSchema(patchReqJsonNode);
    putReqSchema = factory.getSchema(putReqJsonNode);
    try {
      handleSchema = factory.getSchema(new URI(
          "https://schemas.dissco.tech/schemas/fdo-profile/handle-kernel/latest/handle-request.json"));
    } catch (Exception e) {
      log.error("!", e);
    }
    doiSchema = factory.getSchema(doiJsonNode);
    digitalSpecimenSchema = factory.getSchema(digitalSpecimenJsonNode);
    digitalMediaSchema = factory.getSchema(digitalMediaJsonNode);
    annotationSchema = factory.getSchema(annotationJsonNode);
    dataMappingSchema = factory.getSchema(mappingJsonNode);
    sourceSystemSchema = factory.getSchema(sourceSystemJsonNode);
    organisationSchema = factory.getSchema(organisationJsonNode);
    masSchema = factory.getSchema(masJsonNode);
    tombstoneReqSchema = factory.getSchema(tombstoneReqJsonNode);
  }

  public void validatePostRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = postReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(setErrorMessage(validationErrors, "CREATE"));
    }
    var fdoType = FdoType.fromString(requestRoot.get(NODE_DATA).get(NODE_TYPE).asText());
    if (fdoType.equals(FdoType.HANDLE)) {
      var errors = handleSchema.validate(requestRoot);
      log.info("Errors: {}", errors);
    }
    validateAttributes(requestRoot, fdoType);
  }

  public void validatePatchRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = patchReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(
          setErrorMessage(validationErrors, "UPDATE"));
    }
    var fdoType = FdoType.fromString(requestRoot.get(NODE_DATA).get(NODE_TYPE).asText());
    validateAttributes(requestRoot, fdoType);
  }

  public void validatePutRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = putReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(
          setErrorMessage(validationErrors, "TOMBSTONE"));
    }
    validateAttributes(requestRoot, TOMBSTONE);
  }

  private void validateAttributes(JsonNode requestRoot, FdoType fdoType)
      throws InvalidRequestException {
    var requestAttributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    JsonSchema schema;
    switch (fdoType) {
      case HANDLE -> {
        return;
      }
      case DOI -> schema = doiSchema;
      case DIGITAL_SPECIMEN -> schema = digitalSpecimenSchema;
      case DIGITAL_MEDIA -> schema = digitalMediaSchema;
      case ANNOTATION -> schema = annotationSchema;
      case DATA_MAPPING -> schema = dataMappingSchema;
      case SOURCE_SYSTEM -> schema = sourceSystemSchema;
      case ORGANISATION -> schema = organisationSchema;
      case MAS -> schema = masSchema;
      case TOMBSTONE -> schema = tombstoneReqSchema;
      default ->
          throw new InvalidRequestException("Invalid Request. Reason: Invalid type: " + fdoType);
    }
    var validationErrors = schema.validate(requestAttributes);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(
          setErrorMessage(validationErrors, fdoType.getDigitalObjectName(),
              requestAttributes));
    }
  }

  private String setErrorMessage(Set<ValidationMessage> validationErrors, String type) {
    return setErrorMessage(validationErrors, type, null);
  }

  private String setErrorMessage(Set<ValidationMessage> validationErrors, String type,
      JsonNode requestAttributes) {
    Set<String> missingAttributes = new HashSet<>();
    Set<String> unrecognizedAttributes = new HashSet<>();
    Set<String> enumErrors = new HashSet<>();
    Set<String> otherErrors = new HashSet<>();

    for (var validationError : validationErrors) {
      if (validationError.getType().equals("required")) {
        missingAttributes.add(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("additionalProperties")) {
        unrecognizedAttributes.add(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("enum")) {
        var badEnumValue = getProblemEnumValue(requestAttributes, validationError.getPath());
        enumErrors.add(validationError.getMessage() + ". invalid value: " + badEnumValue);
        log.error("Bad enum val: {}", badEnumValue);
      } else {
        otherErrors.add(validationError.getMessage());
      }
    }
    String message = "Invalid request body for request type " + type + ".";
    if (!missingAttributes.isEmpty()) {
      message = message + "\nMissing attributes: " + missingAttributes;
    }
    if (!unrecognizedAttributes.isEmpty()) {
      message = message + "\nUnrecognized attributes: " + unrecognizedAttributes;
    }
    if (!enumErrors.isEmpty()) {
      message = message + "\nEnum errors: " + enumErrors;
    }
    if (!otherErrors.isEmpty()) {
      message = message + "\nOther errors: " + otherErrors;
    }
    log.error("Json Schema Validation error." + message);
    return message;
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
