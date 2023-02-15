package eu.dissco.core.handlemanager.testUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaGenerator;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaStaticContextInitializer;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(properties="spring.main.lazy-initialization=true")
class JsonSchemaGeneratorTest {
  JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
  SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
      .with(module);
  SchemaGeneratorConfig config = configBuilder.build();
  SchemaGenerator generator = new SchemaGenerator(config);

  private static final String baseId = "https://sandbox.dissco.tech/schema/";
  private static final String handleId = baseId + "handle";
  private static final String doiId = baseId + "doi";
  private static final String dsId = baseId + "digital-specimen";
  private static final String dsBotId = baseId + "digital-specimen-botany";
  private static final String tombId = baseId + "tombstone";
  private static final String reqId = baseId + "request";
  private static final String mediaId = baseId + "media-object";

  @Autowired
  JsonSchemaStaticContextInitializer initializer;

  @BeforeEach
  void setup(){
    configBuilder.forFields()
        .withArrayMinItemsResolver(field -> field
            .getAnnotationConsideringFieldAndGetterIfSupported(NotEmpty.class) == null ? null : 1);

    // Array items must be unique
    configBuilder.forTypesInGeneral()
        .withArrayUniqueItemsResolver(scope -> scope.getType().isInstanceOf(String[].class) ? true : null);

  }

  @Test
  void testHandleRecordSchema()  {
    // Given
    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == HandleRecordRequest.class ? handleId : null);
    JsonNode expectedResponse = generator.generateSchema(HandleRecordRequest.class);

    // When
    JsonNode receivedResponse = JsonSchemaGenerator.getHandleRequestSchema();

    log.info(receivedResponse.toPrettyString());

    // Then
    assertThat(receivedResponse).isEqualTo(expectedResponse);
  }
  
  @Test
  void testDoiRecordRecordSchema()  {
    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? doiId : null);
    JsonNode expectedResponse = generator.generateSchema(DoiRecordRequest.class);

    // When
    JsonNode receivedResponse = JsonSchemaGenerator.getDoiRequestSchema();
    log.info(receivedResponse.toPrettyString());

    // Then
    assertThat(receivedResponse).isEqualTo(expectedResponse);
  }

  @Test
  void testDigitalSpecimenSchema()  {
    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? dsId : null);
    JsonNode expectedResponse = generator.generateSchema(DigitalSpecimenRequest.class);

    // When
    JsonNode receivedResponse = JsonSchemaGenerator.getDigitalSpecimenRequestSchema();
    log.info(receivedResponse.toPrettyString());

    // Then
    assertThat(receivedResponse).isEqualTo(expectedResponse);
  }

  @Test
  void testDigitalSpecimenBotanySchema()  {
    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? dsBotId : null);
    JsonNode expectedResponse = generator.generateSchema(DigitalSpecimenBotanyRequest.class);

    // When
    JsonNode receivedResponse = JsonSchemaGenerator.getDigitalSpecimenBotanyRequestSchema();
    log.info(receivedResponse.toPrettyString());

    // Then
    assertThat(receivedResponse).isEqualTo(expectedResponse);
  }

  @Test
  void Tombstone()  {
    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == TombstoneRecordRequest.class ? tombId : null);
    JsonNode expectedResponse = generator.generateSchema(TombstoneRecordRequest.class);

    // When
    JsonNode receivedResponse = JsonSchemaGenerator.getTombstoneRequestSchema();
    log.info(receivedResponse.toPrettyString());

    // Then
    assertThat(receivedResponse).isEqualTo(expectedResponse);
  }


}
