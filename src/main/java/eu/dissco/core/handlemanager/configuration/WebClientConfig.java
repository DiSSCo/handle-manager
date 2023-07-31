package eu.dissco.core.handlemanager.configuration;

import eu.dissco.core.handlemanager.properties.DataCiteConnectionProperties;
import eu.dissco.core.handlemanager.web.DataCiteStatusCodeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
  private final DataCiteConnectionProperties properties;

  @Bean(name = "pidResolverClient")
  public WebClient pidResolverClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .build();
  }

  @Bean(name = "dataCite")
  public WebClient dataCiteClient() {
    ExchangeFilterFunction errorResponseFilter = ExchangeFilterFunction
        .ofResponseProcessor(DataCiteStatusCodeHandler::exchangeFilterResponseProcessor);
    return WebClient.builder()
        .baseUrl(properties.getEndpoint())
        .filter(errorResponseFilter)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json")
        .defaultHeaders(
            header -> header.setBasicAuth(properties.getRepositoryId(), properties.getPassword()))
        .build();
  }




}
