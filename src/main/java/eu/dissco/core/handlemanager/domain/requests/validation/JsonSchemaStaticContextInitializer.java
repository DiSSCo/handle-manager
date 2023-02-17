package eu.dissco.core.handlemanager.domain.requests.validation;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaStaticContextInitializer {
  @Autowired
  @Qualifier("JacksonModuleAttributes")
  private SchemaGeneratorConfig jacksonAttributesRequestConfig;

  @Autowired
  @Qualifier("RequestAttributes")
  private SchemaGeneratorConfig requestAttributesConfig;

  @Autowired
  @Qualifier("RequestConfig")
  private SchemaGeneratorConfig requestConfig;

  @PostConstruct
  private void initSchemaGenerator(){
    JsonSchemaLibrary.init(jacksonAttributesRequestConfig, requestAttributesConfig, requestConfig);
  }

}
