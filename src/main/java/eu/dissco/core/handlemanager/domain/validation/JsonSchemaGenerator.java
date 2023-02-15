package eu.dissco.core.handlemanager.domain.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.GeneralRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import lombok.Getter;

@Getter
public class JsonSchemaGenerator {
  private JsonSchemaGenerator() {
    throw new IllegalStateException("Utility class");
  }

  public static void setConfig(SchemaGeneratorConfig config){
    var schemaGenerator = new SchemaGenerator(config);
    handleRequestSchema = schemaGenerator.generateSchema(HandleRecordRequest.class);
    doiRequestSchema = schemaGenerator.generateSchema(DoiRecordRequest.class);
    digitalSpecimenRequestSchema = schemaGenerator.generateSchema(DigitalSpecimenRequest.class);
    digitalSpecimenBotanyRequestSchema = schemaGenerator.generateSchema(DigitalSpecimenBotanyRequest.class);
    tombstoneRequestSchema = schemaGenerator.generateSchema(TombstoneRecordRequest.class);
    requestSchema = schemaGenerator.generateSchema(GeneralRequest.class);
    mediaObjectRequestSchema = schemaGenerator.generateSchema(MediaObjectRequest.class);
  }

  @Getter
  private static JsonNode handleRequestSchema;

  @Getter
  private static JsonNode doiRequestSchema;
  @Getter
  private static JsonNode digitalSpecimenRequestSchema;
  @Getter
  private static JsonNode digitalSpecimenBotanyRequestSchema;
  @Getter
  private static JsonNode tombstoneRequestSchema;
  @Getter
  private static JsonNode requestSchema;
  @Getter
  private static JsonNode mediaObjectRequestSchema;
}
