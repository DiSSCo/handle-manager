package eu.dissco.core.handlemanager.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final WebClient webClient;
  private final ObjectMapper mapper;

  @Cacheable("pidName")
  public String getObjectName(String pid)
      throws PidResolutionException {
    log.info("getting Pid name for: {}", pid);
    var pidRecord = resolveExternalPid(pid);
    if (pidRecord.get("name") != null) {
      return pidRecord.get("name").asText();
    }
    log.warn("Given pid {} resolves, but does not include a name attribute", pid);
    return "";
  }

  @Cacheable("qid")
  public String resolveQid(String qid) {
    try {
      var qidRecord = resolveExternalPid(qid);
      return qidRecord.get("labels").get("en").asText();
    } catch (NullPointerException e) {
      log.error("Given Qid {} resolves, but does not include a name attribute. ", qid);
      throw new PidResolutionException("Given QID " + qid + "resolves but does not include a name");
    } catch (PidResolutionException e) {
      log.error("An error has occurred in resolving a QID ", e);
      throw new PidResolutionException(
          "An error has occured in resolving a QID: " + e.getMessage());
    }
  }

  private JsonNode resolveExternalPid(String url)
      throws PidResolutionException {
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
        log.error("Unable to resolve pid. Putting placeholder", e);
        return mapper.createObjectNode()
            .put("name", "NOT FOUND")
            .set("labels", mapper.createObjectNode().put("en", "NOT FOUND"));
      }
      throw new PidResolutionException(e.getMessage());
    }
  }

  public boolean is5xxServerError(Throwable throwable) {
    return throwable instanceof WebClientResponseException webClientResponseException
        && webClientResponseException.getStatusCode().is5xxServerError();
  }

}
