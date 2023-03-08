package eu.dissco.core.handlemanager.domain.requests.validation;

import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_TOMBSTONE;

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
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.PutRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
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
  private JsonNode handlePostReqJsonNode;
  private JsonNode handlePatchReqJsonNode;
  private JsonNode doiPostReqJsonNode;
  private JsonNode doiPatchReqJsonNode;
  private JsonNode digitalSpecimenPostReqJsonNode;
  private JsonNode digitalSpecimenPatchReqJsonNode;
  private JsonNode digitalSpecimenBotanyPostReqJsonNode;
  private JsonNode digitalSpecimenBotanyPatchReqJsonNode;
  private JsonNode mediaObjectPostReqJsonNode;
  private JsonNode mediaObjectPatchReqJsonNode;
  private JsonNode tombstoneReqJsonNode;
  // Schemas
  private JsonSchema postReqSchema;
  private JsonSchema patchReqSchema;
  private JsonSchema putReqSchema;
  private JsonSchema handlePostReqSchema;
  private JsonSchema handlePatchReqSchema;
  private JsonSchema doiPostReqSchema;
  private JsonSchema doiPatchReqSchema;
  private JsonSchema digitalSpecimenPostReqSchema;
  private JsonSchema digitalSpecimenPatchReqSchema;
  private JsonSchema digitalSpecimenBotanyPostReqSchema;
  private JsonSchema digitalSpecimenBotanyPatchReqSchema;
  private JsonSchema mediaObjectPostReqSchema;
  private JsonSchema mediaObjectPatchReqSchema;
  private JsonSchema tombstoneReqSchema;

  public JsonSchemaValidator() {
    setPostRequestAttributesJsonNodes();
    setPatchRequestAttributesJsonNodes();
    setRequestJsonNodes();
    setJsonSchemas();
  }

  private void setPostRequestAttributesJsonNodes() {
    var schemaGenerator = new SchemaGenerator(jacksonModuleSchemaConfig());
    handlePostReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiPostReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenPostReqJsonNode = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalSpecimenBotanyPostReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectPostReqJsonNode = schemaGenerator.generateSchema(MediaObjectRequest.class);
    tombstoneReqJsonNode = schemaGenerator.generateSchema(TombstoneRecordRequest.class);
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

    return configBuilder.build();
  }

  private void setPatchRequestAttributesJsonNodes() {
    var schemaGenerator = new SchemaGenerator( attributesSchemaConfig() );
    handlePatchReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiPatchReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenPatchReqJsonNode = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalSpecimenBotanyPatchReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectPatchReqJsonNode = schemaGenerator.generateSchema(MediaObjectRequest.class);
  }

  private SchemaGeneratorConfig attributesSchemaConfig() {
    // Secondary Configuration for schemas of Request Objects
    // In these schemas not every field is required, but no unknown properties are allowed
    // e.g. PATCH update attributes

    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON).with(
        Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT);

    configBuilder.forTypesInGeneral()
        .withEnumResolver(scope -> scope.getType().getErasedType().isEnum()
            ? Stream.of(scope.getType().getErasedType().getEnumConstants())
            .map(v -> ((Enum) v).name()).toList()
            : null);

    // Min
    configBuilder.forFields()
        .withArrayMinItemsResolver(field -> field
            .getAnnotationConsideringFieldAndGetterIfSupported(NotEmpty.class) == null ? null : 1);

    return configBuilder.build();
  }

  private void setRequestJsonNodes() {
    var schemaGenerator = new SchemaGenerator(requestSchemaConfig());
    postReqJsonNode = schemaGenerator.generateSchema(PostRequest.class);
    patchReqJsonNode = schemaGenerator.generateSchema(PatchRequest.class);
    putReqJsonNode = schemaGenerator.generateSchema(PutRequest.class);
  }

  public SchemaGeneratorConfig requestSchemaConfig() {
    // Used for top-level requests, e.g. PatchRequest, PostRequest, PutRequest Objects
    // These schemas allow unknown fields to accommodate the attributes field (which could be one of several schemas)
    // Attribute schemas are checked using one of the two other schema configs

    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);

    configBuilder.forFields()
        .withRequiredCheck(
            field -> field.getAnnotationConsideringFieldAndGetter(Nullable.class) == null);

    configBuilder.forFields()
        .withAdditionalPropertiesResolver(field -> field.getType().getErasedType() == JsonNode.class
            ? null : Void.class);

    return configBuilder.build();
  }

  private void setJsonSchemas() {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);

    postReqSchema = factory.getSchema(postReqJsonNode);
    patchReqSchema = factory.getSchema(patchReqJsonNode);
    putReqSchema = factory.getSchema(putReqJsonNode);

    handlePostReqSchema = factory.getSchema(handlePostReqJsonNode);
    doiPostReqSchema = factory.getSchema(doiPostReqJsonNode);
    digitalSpecimenPostReqSchema = factory.getSchema(digitalSpecimenPostReqJsonNode);
    digitalSpecimenBotanyPostReqSchema = factory.getSchema(digitalSpecimenBotanyPostReqJsonNode);
    mediaObjectPostReqSchema = factory.getSchema(mediaObjectPostReqJsonNode);

    handlePatchReqSchema = factory.getSchema(handlePatchReqJsonNode);
    doiPatchReqSchema = factory.getSchema(doiPatchReqJsonNode);
    digitalSpecimenPatchReqSchema = factory.getSchema(digitalSpecimenPatchReqJsonNode);
    digitalSpecimenBotanyPatchReqSchema = factory.getSchema(digitalSpecimenBotanyPatchReqJsonNode);
    mediaObjectPatchReqSchema = factory.getSchema(mediaObjectPatchReqJsonNode);

    tombstoneReqSchema = factory.getSchema(tombstoneReqJsonNode);
  }

  public void validatePostRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = postReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(setErrorMessage(validationErrors, "POST"));
    }
    String type = requestRoot.get(NODE_DATA).get(NODE_TYPE).asText();
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    switch (type) {
      case RECORD_TYPE_HANDLE -> validateRequestAttributes(attributes, handlePostReqSchema, type);
      case RECORD_TYPE_DOI -> validateRequestAttributes(attributes, doiPostReqSchema, type);
      case RECORD_TYPE_DS ->
          validateRequestAttributes(attributes, digitalSpecimenPostReqSchema, type);
      case RECORD_TYPE_DS_BOTANY ->
          validateRequestAttributes(attributes, digitalSpecimenBotanyPostReqSchema, type);
      case RECORD_TYPE_MEDIA ->
          validateRequestAttributes(attributes, mediaObjectPostReqSchema, type);
      default -> throw new InvalidRequestException("Invalid Request. Reason: Invalid type: " + type);
    }
  }

  public void validatePutRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = putReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(setErrorMessage(validationErrors, "PUT (tombstone)"));
    }
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    validateRequestAttributes(attributes, tombstoneReqSchema, RECORD_TYPE_TOMBSTONE);
  }

  public void validatePatchRequest(JsonNode requestRoot) throws InvalidRequestException {
    var validationErrors = patchReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(setErrorMessage(validationErrors, "PATCH (update)"));
    }
    String type = requestRoot.get(NODE_DATA).get(NODE_TYPE).asText();
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    switch (type) {
      case RECORD_TYPE_HANDLE -> validateRequestAttributes(attributes, handlePatchReqSchema, type);
      case RECORD_TYPE_DOI -> validateRequestAttributes(attributes, doiPatchReqSchema, type);
      case RECORD_TYPE_DS ->
          validateRequestAttributes(attributes, digitalSpecimenPatchReqSchema, type);
      case RECORD_TYPE_DS_BOTANY ->
          validateRequestAttributes(attributes, digitalSpecimenBotanyPatchReqSchema, type);
      case RECORD_TYPE_MEDIA ->
          validateRequestAttributes(attributes, mediaObjectPatchReqSchema, type);
      default -> throw new InvalidRequestException("Invalid Request. Reason: Invalid type: " + type);
    }
  }

  private void validateRequestAttributes(JsonNode requestAttributes, JsonSchema schema,
      String type) throws InvalidRequestException {
    var validationErrors = schema.validate(requestAttributes);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRequestException(setErrorMessage(validationErrors, type));
    }
  }

  private String setErrorMessage(Set<ValidationMessage> validationErrors, String type) {
    Set<String> missingAttributes = new HashSet<>();
    Set<String> unrecognizedAttributes = new HashSet<>();
    Set<String> enumErrors = new HashSet<>();
    Set<String> otherErrors = new HashSet<>();

    for (var validationError : validationErrors) {
      if (validationError.getType().equals("required")) {
        missingAttributes.add(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("additionalProperties")) {
        unrecognizedAttributes.add(Arrays.toString(validationError.getArguments()));
      } else if (validationError.getType().equals("enum")){
        enumErrors.add(validationError.getMessage());
      }
      else {
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

}
