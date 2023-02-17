package eu.dissco.core.handlemanager.domain.validation;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaStaticContextInitializer {
  @Autowired
  @Qualifier("createRequestConfig")
  private SchemaGeneratorConfig createRequestSchemaConfig;

  @Autowired
  @Qualifier("updateRequestConfig")
  private SchemaGeneratorConfig updateRequestSchemaConfig;

  @PostConstruct
  private void initSchemaGenerator(){
    JsonSchemaGenerator.init(createRequestSchemaConfig, updateRequestSchemaConfig);
  }

}
