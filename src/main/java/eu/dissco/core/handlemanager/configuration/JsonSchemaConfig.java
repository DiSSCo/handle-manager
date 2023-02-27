package eu.dissco.core.handlemanager.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.Option;
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
import org.springframework.lang.Nullable;

@Configuration
public class JsonSchemaConfig {

  @Bean
  @Qualifier("JacksonModuleAttributes")
  public SchemaGeneratorConfig jacksonModuleSchemaConfig() {
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

  @Bean
  @Qualifier("RequestAttributes")
  public SchemaGeneratorConfig attributesSchemaConfig() {
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

  @Bean
  @Qualifier("RequestConfig")
  public SchemaGeneratorConfig schemaConfig() {
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

}
