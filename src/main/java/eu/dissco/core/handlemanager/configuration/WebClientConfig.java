package eu.dissco.core.handlemanager.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Bean(name = "pidResolver")
  public WebClient pidResolverClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .build();
  }

  @Bean(name = "dataCite")
  public WebClient dataCiteClient() {
    return WebClient.builder()
        .baseUrl("https://api.datacite.org/dois")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json")
        .build();
  }
}
