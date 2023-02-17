package eu.dissco.core.handlemanager.configuration;

import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import eu.dissco.core.handlemanager.domain.requests.PostRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.PatchRequest;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSchemaConfig {

  private static final String BASE_ID = "https://sandbox.dissco.tech/schema/";
  private static final String HANDLE_ID = BASE_ID + "handle";
  private static final String DOI_ID = BASE_ID + "doi";
  private static final String DS_ID = BASE_ID + "digital-specimen";
  private static final String DS_BOT_ID = BASE_ID + "digital-specimen-botany";
  private static final String TOMB_ID = BASE_ID + "tombstone";
  private static final String POST_REQ_ID = BASE_ID + "post-request";
  private static final String MEDIA_ID = BASE_ID + "media-object";

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
            scope -> scope.getType().getErasedType() == HandleRecordRequest.class ? HANDLE_ID
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? DOI_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? DS_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? DS_BOT_ID
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == PostRequest.class ? POST_REQ_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == PatchRequest.class ? POST_REQ_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == TombstoneRecordRequest.class ? TOMB_ID
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == MediaObjectRequest.class ? MEDIA_ID : null);

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
            scope -> scope.getType().getErasedType() == HandleRecordRequest.class ? HANDLE_ID
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? DOI_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? DS_ID : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? DS_BOT_ID
                : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(
            scope -> scope.getType().getErasedType() == MediaObjectRequest.class ? MEDIA_ID : null);

    return configBuilder.build();
  }

}
