package eu.dissco.core.handlemanager.domain.requests.validation;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaStaticContextInitializer {
  @Autowired
  @Qualifier("JacksonModuleRequestConfig")
  private SchemaGeneratorConfig createRequestSchemaConfig;

  @Autowired
  @Qualifier("RequestConfig")
  private SchemaGeneratorConfig updateRequestSchemaConfig;

  @PostConstruct
  private void initSchemaGenerator(){
    JsonSchemaLibrary.init(createRequestSchemaConfig, updateRequestSchemaConfig);
  }

}
