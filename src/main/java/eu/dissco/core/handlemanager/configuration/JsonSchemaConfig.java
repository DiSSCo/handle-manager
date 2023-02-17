package eu.dissco.core.handlemanager.configuration;

import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSchemaConfig {

  @Bean
  @Qualifier("JacksonModuleRequestConfig")
  public SchemaGeneratorConfig jacksonModuleSchemaConfig() {

    JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
        JacksonOption.FLATTENED_ENUMS_FROM_JSONPROPERTY);
    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
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

  @Bean
  @Qualifier("RequestConfig")
  public SchemaGeneratorConfig schemaConfig() {
    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);

    configBuilder.forTypesInGeneral()
        .withEnumResolver(scope -> scope.getType().getErasedType().isEnum()
            ? Stream.of(scope.getType().getErasedType().getEnumConstants())
            .map(v -> ((Enum) v).name()).toList()
            : null);

    // Min items in array must be 1
    configBuilder.forFields()
        .withArrayMinItemsResolver(field -> field
            .getAnnotationConsideringFieldAndGetterIfSupported(NotEmpty.class) == null ? null : 1);

    return configBuilder.build();
  }

}
