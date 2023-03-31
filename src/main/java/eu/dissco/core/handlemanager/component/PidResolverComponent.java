package eu.dissco.core.handlemanager.component;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PidResolverComponent {

  private final WebClient webClient;

  @Cacheable("pid")
  public JsonNode resolveExternalPid(String pid)
      throws UnprocessableEntityException, PidResolutionException {
    String url = "https://hdl.handle.net/" + pid;
    log.info("querying the following: {}", url);
    var responseSpec = webClient.get().uri(url).retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            r -> r.bodyToMono(String.class).map(PidResolutionException::new));
    var response = responseSpec.bodyToMono(JsonNode.class);
    JsonNode responseVal;

    try {
      responseVal = response.toFuture().get();
    } catch (InterruptedException e) {
      log.warn("Interrupted connection. Unable to resolve the following: {}", pid);
      Thread.currentThread().interrupt();
      return null;
    } catch (ExecutionException e) {
      if (e.getCause().getClass().equals(PidResolutionException.class)) {
        throw new PidResolutionException("Given PID not found: " + pid);
      }
      throw new UnprocessableEntityException("Unable to parse identifier " + pid + " to JSON.");
    }
    return responseVal;
  }

}
