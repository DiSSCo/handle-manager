package eu.dissco.core.handlemanager.web;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
@Slf4j
public class PidResolver {

  @Qualifier("pidResolver")
  private final WebClient webClient;

  private JsonNode resolveExternalPid(String url)
      throws UnprocessableEntityException, PidResolutionException {
    var response = webClient.get()
        .uri(url)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            r -> Mono.error(new PidResolutionException("Given PID not found: " + url)))
        .onStatus(HttpStatusCode::is4xxClientError,
            r -> Mono.error(
                new UnprocessableEntityException("a fatal client-side error has occurred")))
        .bodyToMono(JsonNode.class)
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
            .filter(this::is5xxServerError)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                new UnprocessableEntityException(
                    "External Service failed to process after max retries")
            ));

    try {
      return response.toFuture().get();
    } catch (InterruptedException | ExecutionException e) {
      log.warn("Interrupted connection. Unable to resolve the following: {}", url);
      Thread.currentThread().interrupt();
      if (e.getCause().getClass().equals(PidResolutionException.class)) {
        throw new PidResolutionException(e.getMessage());
      }
      throw new UnprocessableEntityException(e.getMessage());
    }
  }

  public boolean is5xxServerError(Throwable throwable) {
    return throwable instanceof WebClientResponseException webClientResponseException
        && webClientResponseException.getStatusCode().is5xxServerError();
  }


  @Cacheable("pidName")
  public String getObjectName(String pid)
      throws UnprocessableEntityException, PidResolutionException {
    log.info("getting Pid name for: {}", pid);
    var pidRecord = resolveExternalPid(pid);
    if (pidRecord.get("name") != null) {
      return pidRecord.get("name").asText();
    }
    log.warn("Given pid {} resolves, but does not include a name attribute", pid);
    return "";
  }
}
