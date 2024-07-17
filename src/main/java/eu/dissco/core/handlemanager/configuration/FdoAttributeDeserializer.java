package eu.dissco.core.handlemanager.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FdoAttributeDeserializer extends JsonDeserializer<FdoAttribute> {


  @Override
  public FdoAttribute deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) {
    return null;
  }

}
