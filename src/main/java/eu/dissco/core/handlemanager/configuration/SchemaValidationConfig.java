package eu.dissco.core.handlemanager.configuration;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import eu.dissco.core.handlemanager.domain.validation.SchemaLibrary;
import eu.dissco.core.handlemanager.properties.JsonSchemaProperties;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaValidationConfig {

  @Bean
  SchemaLibrary schemaLibrary() throws IOException {
    var factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
    JsonSchemaProperties pathProperties = new JsonSchemaProperties();
    return new SchemaLibrary(
        setSchema(pathProperties.getHandleSchemaPath(), factory),
        setSchema(pathProperties.getDoiSchemaPath(), factory),
        setSchema(pathProperties.getAnnotationSchemaPath(), factory),
        setSchema(pathProperties.getDataMappingSchemaPath(), factory),
        setSchema(pathProperties.getDigitalMediaSchemaPath(), factory),
        setSchema(pathProperties.getDigitalSpecimenSchemaPath(), factory),
        setSchema(pathProperties.getMasSchemaPath(), factory),
        setSchema(pathProperties.getOrganisationSchemaPath(), factory),
        setSchema(pathProperties.getSourceSystemPath(), factory)
    );
  }

  private JsonSchema setSchema(String schema, JsonSchemaFactory factory) throws IOException {
    try (var input = Thread.currentThread().getContextClassLoader().getResourceAsStream(schema)) {
      return factory.getSchema(input);
    }
  }

}
