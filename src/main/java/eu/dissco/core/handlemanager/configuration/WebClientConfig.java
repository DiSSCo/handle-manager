package eu.dissco.core.handlemanager.configuration;


import eu.dissco.core.handlemanager.properties.DataCiteProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  private DataCiteProperties properties;

  @Bean(name = "pidResolver")
  public WebClient pidResolverClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .build();
  }

  @Bean(name = "dataCite")
  public WebClient dataCiteClient() {
    return WebClient.builder()
        .baseUrl(properties.getEndpoint())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json")
        .defaultHeaders(header -> header.setBasicAuth(properties.getRepositoryId(), properties.getPassword()))
        .build();
  }
}
