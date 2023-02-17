package eu.dissco.core.handlemanager.configuration;

import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import eu.dissco.core.handlemanager.domain.requests.CreateRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.UpdateRequest;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSchemaConfig {

  private static final String baseId = "https://sandbox.dissco.tech/schema/";
  private static final String handleId = baseId + "handle";
  private static final String doiId = baseId + "doi";
  private static final String dsId = baseId + "digital-specimen";
  private static final String dsBotId = baseId + "digital-specimen-botany";
  private static final String tombId = baseId + "tombstone";
  private static final String reqId = baseId + "request";
  private static final String mediaId = baseId + "media-object";

  @Bean
  @Qualifier("createRequestConfig")
  public SchemaGeneratorConfig createRequestSchemaConfig() {

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

    // Add $ids to each schema
    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == HandleRecordRequest.class ? handleId
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? doiId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? dsId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? dsBotId
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == CreateRequest.class ? reqId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == UpdateRequest.class ? reqId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == TombstoneRecordRequest.class ? tombId
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == MediaObjectRequest.class ? mediaId : null);

    return configBuilder.build();
  }

  @Bean
  @Qualifier("updateRequestConfig")
  public SchemaGeneratorConfig updateRequestSchemaConfig() {
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

    // Array items must be unique
    configBuilder.forTypesInGeneral()
        .withArrayUniqueItemsResolver(
            scope -> scope.getType().isInstanceOf(String[].class) ? true : null);

    // Add $ids to each schema
    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == HandleRecordRequest.class ? handleId
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? doiId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? dsId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? dsBotId
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == MediaObjectRequest.class ? mediaId : null);

    return configBuilder.build();
  }

}
