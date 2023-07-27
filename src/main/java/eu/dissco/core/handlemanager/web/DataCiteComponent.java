package eu.dissco.core.handlemanager.web;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.requests.datacite.DcRequest;
import eu.dissco.core.handlemanager.exceptions.DataCiteException;
import eu.dissco.core.handlemanager.properties.DataCiteProperties;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataCiteComponent {

  @Qualifier("datacite")
  WebClient webClient;

  DataCiteProperties properties;

  public void sendDoiRequest(DcRequest requestBody) throws DataCiteException {

    var response = webClient.method(HttpMethod.POST)
        .body(BodyInserters.fromValue(requestBody))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new DataCiteException(
            "Unable to create PID. Response from Handle API: " + r.statusCode())))
        .bodyToMono(JsonNode.class)
        .retryWhen(
            Retry.fixedDelay(3, Duration.ofSeconds(2))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new DataCiteException(
                    "External Service failed to process after max retries")));

    try {
      response.toFuture().get();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      log.error("Interrupted exception has occurred.");
      throw new DataCiteException(
          "Interrupted execution: A connection error has occurred in creating a handle.");
    }
  }
}
