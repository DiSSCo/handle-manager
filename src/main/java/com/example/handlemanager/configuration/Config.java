package com.example.handlemanager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  // Create singleton object, create only once to avoid additional work
  // instantiate ObjectMapper as bean

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

  //Todo make random bean - it will complain


}
