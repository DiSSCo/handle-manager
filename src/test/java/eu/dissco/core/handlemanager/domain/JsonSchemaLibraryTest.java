package eu.dissco.core.handlemanager.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaLibrary;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaStaticContextInitializer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class JsonSchemaLibraryTest {

  @Autowired
  JsonSchemaStaticContextInitializer schemaInitializer;


  @Test
  void attributesJackson(){
    JsonNode jackson = JsonSchemaLibrary.getHandlePostReqJsonNode();
    log.info(jackson.toPrettyString());
  }

  @Test
  void attributesVanilla(){
    JsonNode vanilla = JsonSchemaLibrary.getHandlePatchReqJsonNode();
    log.info(vanilla.toPrettyString());
  }

  @Test
  void request(){
    JsonNode vanilla = JsonSchemaLibrary.getPostReqJsonNode();
    log.info(vanilla.toPrettyString());
  }


  void testNotJackson() {
    SchemaGeneratorConfigBuilder configBuilderNotJackson = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
    configBuilderNotJackson.forTypesInGeneral().withAdditionalPropertiesResolver((scope) -> {
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
