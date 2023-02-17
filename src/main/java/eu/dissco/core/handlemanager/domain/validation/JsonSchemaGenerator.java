package eu.dissco.core.handlemanager.domain.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.CreateRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.UpdateRequest;
import lombok.Getter;

public class JsonSchemaGenerator {

  private JsonSchemaGenerator() {
    throw new IllegalStateException("Utility class");
  }

  public static void init(SchemaGeneratorConfig createRequestConfig,
      SchemaGeneratorConfig updateRequestConfig) {
    
    setJsonNodesJacksonProperties(createRequestConfig);
    setJsonNodesLenientRequirements(updateRequestConfig);
    setJsonSchemas();
  }

  private static void setJsonNodesJacksonProperties(SchemaGeneratorConfig createRequestConfig) {
    var schemaGenerator = new SchemaGenerator(createRequestConfig);
    handleCreateReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiCreateReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenCreateReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenRequest.class);
    digitalSpecimenBotanyCreateReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectCreateReqJsonNode = schemaGenerator.generateSchema(
        MediaObjectRequest.class);
    tombstoneReqJsonNode = schemaGenerator.generateSchema(TombstoneRecordRequest.class);
    createReqJsonNode = schemaGenerator.generateSchema(CreateRequest.class);
    updateReqJsonNode = schemaGenerator.generateSchema(UpdateRequest.class);
  }

  private static void setJsonNodesLenientRequirements(SchemaGeneratorConfig updateRequestConfig) {
    var schemaGenerator = new SchemaGenerator(updateRequestConfig);
    handleUpdateReqJsonNode = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiUpdateReqJsonNode = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenUpdateReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenRequest.class);
    digitalSpecimenBotanyUpdateReqJsonNode = schemaGenerator.generateSchema(
        DigitalSpecimenBotanyRequest.class);
    mediaObjectUpdateReqJsonNode = schemaGenerator.generateSchema(
        MediaObjectRequest.class);
  }

  private static void setJsonSchemas() {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
    createReqSchema = factory.getSchema(createReqJsonNode);
    updateReqSchema = factory.getSchema(updateReqJsonNode);
    handleCreateReqSchema = factory.getSchema(handleCreateReqJsonNode);
    doiCreateReqSchema = factory.getSchema(doiCreateReqJsonNode);
    digitalSpecimenCreateReqSchema = factory.getSchema(digitalSpecimenCreateReqJsonNode);
    digitalSpecimenBotanyCreateReqSchema = factory.getSchema(digitalSpecimenBotanyCreateReqJsonNode);
    mediaObjectCreateReqSchema = factory.getSchema(mediaObjectCreateReqJsonNode);
    handleUpdateReqSchema = factory.getSchema(handleUpdateReqJsonNode);
    doiUpdateReqSchema = factory.getSchema(doiUpdateReqJsonNode);
    digitalSpecimenUpdateReqSchema = factory.getSchema(digitalSpecimenUpdateReqJsonNode);
    digitalSpecimenBotanyUpdateReqSchema = factory.getSchema(digitalSpecimenBotanyUpdateReqJsonNode);
    mediaObjectUpdateReqSchema = factory.getSchema(mediaObjectUpdateReqJsonNode);
    tombstoneReqSchema = factory.getSchema(tombstoneReqJsonNode);

  }

  private static JsonNode handleCreateReqJsonNode;

  private static JsonNode handleUpdateReqJsonNode;

  private static JsonNode doiCreateReqJsonNode;

  private static JsonNode doiUpdateReqJsonNode;

  private static JsonNode digitalSpecimenCreateReqJsonNode;

  private static JsonNode digitalSpecimenUpdateReqJsonNode;

  private static JsonNode digitalSpecimenBotanyCreateReqJsonNode;

  private static JsonNode digitalSpecimenBotanyUpdateReqJsonNode;

  private static JsonNode tombstoneReqJsonNode;

  private static JsonNode createReqJsonNode;

  private static JsonNode updateReqJsonNode;

  private static JsonNode mediaObjectCreateReqJsonNode;

  private static JsonNode mediaObjectUpdateReqJsonNode;
  @Getter
  private static JsonSchema createReqSchema;
  @Getter
  private static JsonSchema updateReqSchema;
  @Getter
  private static JsonSchema handleCreateReqSchema;
  @Getter
  private static JsonSchema handleUpdateReqSchema;
  @Getter
  private static JsonSchema doiCreateReqSchema;
  @Getter
  private static JsonSchema doiUpdateReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenCreateReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenUpdateReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenBotanyCreateReqSchema;
  @Getter
  private static JsonSchema digitalSpecimenBotanyUpdateReqSchema;
  @Getter
  private static JsonSchema mediaObjectCreateReqSchema;
  @Getter
  private static JsonSchema mediaObjectUpdateReqSchema;
  @Getter
  private static JsonSchema tombstoneReqSchema;


}
