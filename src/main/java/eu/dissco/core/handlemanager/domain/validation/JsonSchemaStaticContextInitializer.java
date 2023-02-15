package eu.dissco.core.handlemanager.domain.validation;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaStaticContextInitializer {
  @Autowired
  private SchemaGeneratorConfig config;

  @PostConstruct
  private void initSchemaGenerator(){
    JsonSchemaGenerator.setConfig(config);
  }

}
