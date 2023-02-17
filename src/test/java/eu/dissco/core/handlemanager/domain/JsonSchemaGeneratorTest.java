package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaGenerator;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaStaticContextInitializer;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class JsonSchemaGeneratorTest {

  private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  @Autowired
  JsonSchemaStaticContextInitializer initializer;

  @BeforeEach
  void setup() {

  }

  @Test
  void testValidCreateRequest(){
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), "handle");
    var schema = JsonSchemaGenerator.getCreateReqSchema();

    // When
    var validationMessages = schema.validate(request);

    // Then
    assertThat(validationMessages.size()).isZero();
  }

  @Test
  void testValidUpdateRequest(){
    var requestAttributes = genUpdateRequestAltLoc();
    var request = mapper.createObjectNode();

    var schema = JsonSchemaGenerator.getCreateReqSchema();

    // When
    var validationMessages = schema.validate(request);

    // Then
    assertThat(validationMessages.size()).isZero();
  }


  @Test
  void testValidCreateHandleRequest(){
    // Given
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), "handle").get("data").get("attributes");
    var schema = JsonSchemaGenerator.getHandleCreateReqSchema();

    // When
    var validationMessages = schema.validate(request);

    // Then
    assertThat(validationMessages.size()).isZero();
  }


  @Test
  void testNotJackson() {
    SchemaGeneratorConfigBuilder configBuilderNotJackson = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
    configBuilderNotJackson.forTypesInGeneral()
        .withAdditionalPropertiesResolver((scope) -> {
          if (scope.getType().isInstanceOf(Map.class)) {
            return scope.getTypeParameterFor(Map.class, 1);
          }
          return null;
        });

    SchemaGeneratorConfig configNotJackson = configBuilderNotJackson.build();
    SchemaGenerator generatorNotJackson = new SchemaGenerator(configNotJackson);

    var handleSchemaNotJackson = generatorNotJackson.generateSchema(HandleRecordRequest.class);
    log.info(handleSchemaNotJackson.toPrettyString());
  }

}
