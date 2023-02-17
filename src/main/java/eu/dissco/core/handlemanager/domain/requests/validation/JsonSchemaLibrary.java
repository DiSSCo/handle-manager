package eu.dissco.core.handlemanager.domain.requests.validation;

import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_MEDIA;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import lombok.Getter;

public class JsonSchemaLibrary {

  private static JsonNode handlePostReqJsonNode;
  private static JsonNode handlePatchReqJsonNode;
  private static JsonNode doiPostReqJsonNode;
  private static JsonNode doiPatchReqJsonNode;
  private static JsonNode digitalSpecimenPostReqJsonNode;
  private static JsonNode digitalSpecimenPatchReqJsonNode;
  private static JsonNode digitalSpecimenBotanyPostReqJsonNode;
  private static JsonNode digitalSpecimenBotanyPatchReqJsonNode;
  private static JsonNode tombstoneReqJsonNode;
  private static JsonNode postReqJsonNode;
  private static JsonNode patchReqJsonNode;
  private static JsonNode mediaObjectPostReqJsonNode;
  private static JsonNode mediaObjectPatchReqJsonNode;
  @Getter
  private static JsonSchema postReqSchema;
  @Getter
  private static JsonSchema patchReqSchema;
  @Getter
  private static JsonSchema handlePostReqSchema;
  @Getter
  private static JsonSchema handlePatchReqSchema;
  @Getter
  private static JsonSchema doiPostReqSchema;
  @Getter
  private static JsonSchema doiPatchReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenPostReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenPatchReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenBotanyPostReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenBotanyPatchReqSchema;
  @Getter
  private static JsonSchema mediaObjectPostReqSchema;
  @Getter
  private static JsonSchema mediaObjectPatchReqSchema;
  @Getter
  private static JsonSchema tombstoneReqSchema;

  private JsonSchemaLibrary() {
    throw new IllegalStateException("Utility class");
  }

  public static void init(SchemaGeneratorConfig postRequestConfig,
      SchemaGeneratorConfig patchRequestConfig) {
    setJsonNodesJacksonProperties(postRequestConfig);
    setJsonNodesLenientRequirements(patchRequestConfig);
    setJsonSchemas();
  }

  private static void setJsonNodesJacksonProperties(SchemaGeneratorConfig postRequestConfig) {
    var schemaGenerator = new SchemaGenerator(postRequestConfig);
    handlePostReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiPostReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenPostReqJsonNode = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalSpecimenBotanyPostReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectPostReqJsonNode = schemaGenerator.generateSchema(MediaObjectRequest.class);
    tombstoneReqJsonNode = schemaGenerator.generateSchema(TombstoneRecordRequest.class);
    postReqJsonNode = schemaGenerator.generateSchema(PostRequest.class);
    patchReqJsonNode = schemaGenerator.generateSchema(PatchRequest.class);
  }

  private static void setJsonNodesLenientRequirements(SchemaGeneratorConfig patchRequestConfig) {
    var schemaGenerator = new SchemaGenerator(patchRequestConfig);
    handlePatchReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiPatchReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenPatchReqJsonNode = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalSpecimenBotanyPatchReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectPatchReqJsonNode = schemaGenerator.generateSchema(MediaObjectRequest.class);
  }

  private static void setJsonSchemas() {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
    postReqSchema = factory.getSchema(postReqJsonNode);
    patchReqSchema = factory.getSchema(patchReqJsonNode);
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

  public static void validatePostRequest(JsonNode requestRoot) throws InvalidRecordInput {
    var validationErrors = postReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput("Invalid Request. Reason: " + validationErrors + "from POST request");
    }
    String type = requestRoot.get(NODE_DATA).get(NODE_TYPE).asText();
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    switch (type) {
      case RECORD_TYPE_HANDLE -> validatePostRequestAttributes(attributes, handlePostReqSchema, type);
      case RECORD_TYPE_DOI -> validatePostRequestAttributes(attributes, doiPostReqSchema, type);
      case RECORD_TYPE_DS ->
          validatePostRequestAttributes(attributes, digitalSpecimenPostReqSchema, type);
      case RECORD_TYPE_DS_BOTANY ->
          validatePostRequestAttributes(attributes, digitalSpecimenBotanyPostReqSchema, type);
      case RECORD_TYPE_MEDIA -> validatePostRequestAttributes(attributes, mediaObjectPostReqSchema, type);
      default -> throw new InvalidRecordInput("Invalid Request. Reason: Invalid type");
    }
  }

  private static void validatePostRequestAttributes(JsonNode requestAttributes, JsonSchema schema, String type)
      throws InvalidRecordInput {
    var validationErrors = schema.validate(requestAttributes);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput("Invalid Request. Reason: " + validationErrors + " for record type " + type);
    }
  }

}
