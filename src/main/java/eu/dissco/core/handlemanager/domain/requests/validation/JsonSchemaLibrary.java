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
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.PutRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSchemaLibrary {

  @Getter
  private static JsonNode handlePostReqJsonNode;
  @Getter
  private static JsonNode handlePatchReqJsonNode;
  private static JsonNode doiPostReqJsonNode;
  private static JsonNode doiPatchReqJsonNode;
  private static JsonNode digitalSpecimenPostReqJsonNode;
  private static JsonNode digitalSpecimenPatchReqJsonNode;
  private static JsonNode digitalSpecimenBotanyPostReqJsonNode;
  private static JsonNode digitalSpecimenBotanyPatchReqJsonNode;
  private static JsonNode tombstoneReqJsonNode;
  @Getter
  private static JsonNode postReqJsonNode;
  private static JsonNode patchReqJsonNode;
  private static JsonNode putReqJsonNode;
  private static JsonNode mediaObjectPostReqJsonNode;
  private static JsonNode mediaObjectPatchReqJsonNode;
  private static JsonSchema postReqSchema;
  private static JsonSchema patchReqSchema;
  private static JsonSchema putReqSchema;

  private static JsonSchema handlePostReqSchema;

  private static JsonSchema handlePatchReqSchema;

  private static JsonSchema doiPostReqSchema;

  private static JsonSchema doiPatchReqSchema;

  private static JsonSchema digitalSpecimenPostReqSchema;

  private static JsonSchema digitalSpecimenPatchReqSchema;

  private static JsonSchema digitalSpecimenBotanyPostReqSchema;

  private static JsonSchema digitalSpecimenBotanyPatchReqSchema;

  private static JsonSchema mediaObjectPostReqSchema;

  private static JsonSchema mediaObjectPatchReqSchema;

  private static JsonSchema tombstoneReqSchema;

  private JsonSchemaLibrary() {
    throw new IllegalStateException("Utility class");
  }

  public static void init(SchemaGeneratorConfig postRequestConfig,
      SchemaGeneratorConfig patchRequestConfig,
      SchemaGeneratorConfig requestConfig) {
    setJsonNodesJacksonProperties(postRequestConfig);
    setJsonNodesLenientRequirements(patchRequestConfig);
    setJsonNodesRequests(requestConfig);
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

  private static void setJsonNodesRequests(SchemaGeneratorConfig requestConfig){
    var schemaGenerator = new SchemaGenerator(requestConfig);
    postReqJsonNode = schemaGenerator.generateSchema(PostRequest.class);
    patchReqJsonNode = schemaGenerator.generateSchema(PatchRequest.class);
    putReqJsonNode = schemaGenerator.generateSchema(PutRequest.class);
  }

  private static void setJsonSchemas() {
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

  public static void validatePostRequest(JsonNode requestRoot) throws InvalidRecordInput {
    var validationErrors = postReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput(
          "Invalid Request. Reason: " + validationErrors + "from POST request");
    }
    String type = requestRoot.get(NODE_DATA).get(NODE_TYPE).asText();
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    switch (type) {
      case RECORD_TYPE_HANDLE ->
          validateRequestAttributes(attributes, handlePostReqSchema, type);
      case RECORD_TYPE_DOI -> validateRequestAttributes(attributes, doiPostReqSchema, type);
      case RECORD_TYPE_DS ->
          validateRequestAttributes(attributes, digitalSpecimenPostReqSchema, type);
      case RECORD_TYPE_DS_BOTANY ->
          validateRequestAttributes(attributes, digitalSpecimenBotanyPostReqSchema, type);
      case RECORD_TYPE_MEDIA ->
          validateRequestAttributes(attributes, mediaObjectPostReqSchema, type);
      default -> throw new InvalidRecordInput("Invalid Request. Reason: Invalid type");
    }
  }

  public static void validatePutRequest(JsonNode requestRoot) throws InvalidRecordInput {
    var validationErrors = putReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput(
          "Invalid Request. Reason: " + validationErrors + "from POST request");
    }
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    validateRequestAttributes(attributes, tombstoneReqSchema, RECORD_TYPE_TOMBSTONE);

  }

  public static void validatePatchRequest(JsonNode requestRoot) throws InvalidRecordInput {
    var validationErrors = patchReqSchema.validate(requestRoot);
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput(
          "Invalid Request. Reason: " + validationErrors + "from PATCH request");
    }
    String type = requestRoot.get(NODE_DATA).get(NODE_TYPE).asText();
    var attributes = requestRoot.get(NODE_DATA).get(NODE_ATTRIBUTES);
    switch (type) {
      case RECORD_TYPE_HANDLE ->
          validateRequestAttributes(attributes, handlePatchReqSchema, type);
      case RECORD_TYPE_DOI -> validateRequestAttributes(attributes, doiPatchReqSchema, type);
      case RECORD_TYPE_DS ->
          validateRequestAttributes(attributes, digitalSpecimenPatchReqSchema, type);
      case RECORD_TYPE_DS_BOTANY ->
          validateRequestAttributes(attributes, digitalSpecimenBotanyPatchReqSchema, type);
      case RECORD_TYPE_MEDIA ->
          validateRequestAttributes(attributes, mediaObjectPatchReqSchema, type);
      default -> throw new InvalidRecordInput("Invalid Request. Reason: Invalid type");
    }
  }
  
  private static void validateRequestAttributes(JsonNode requestAttributes, JsonSchema schema,
      String type) throws InvalidRecordInput {
    var validationErrors = schema.validate(requestAttributes);
    log.info("validating request attributes: " + requestAttributes.toPrettyString());
    if (!validationErrors.isEmpty()) {
      throw new InvalidRecordInput(
          "Really Invalid Request. Reason: " + validationErrors + " for record type " + type);
    }
  }



}
