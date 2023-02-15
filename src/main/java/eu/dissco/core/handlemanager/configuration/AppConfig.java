package eu.dissco.core.handlemanager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.GeneralRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRecordRequest;
import java.util.Random;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public DocumentBuilderFactory documentBuilderFactory() throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    return dbf;
  }

  @Bean
  public TransformerFactory transformerFactory() throws TransformerConfigurationException {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    return tf;
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper().findAndRegisterModules();
  }

  @Bean
  public Random random() {
    return new Random();
  }

  @Bean
  public SchemaGeneratorConfig schemaGeneratorConfig () {

    String baseId = "https://sandbox.dissco.tech/schema/";
    String handleId = baseId + "handle";
    String doiId = baseId + "doi";
    String dsId = baseId + "digital-specimen";
    String dsBotId = baseId + "digital-specimen-botany";
    String tombId = baseId + "tombstone";
    String reqId = baseId + "request";
    String mediaId = baseId + "media-object";

    JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
    SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
        SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
        .with(module);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == HandleRecordRequest.class ?  handleId: null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DoiRecordRequest.class ? doiId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DigitalSpecimenRequest.class ? dsId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == DigitalSpecimenBotanyRequest.class ? dsBotId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == GeneralRequest.class ? reqId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == TombstoneRecordRequest.class ? tombId : null);

    configBuilder.forTypesInGeneral()
        .withIdResolver(scope -> scope.getType().getErasedType() == MediaObjectRequest.class ? mediaId : null);

    return configBuilder.build();
  }

}
