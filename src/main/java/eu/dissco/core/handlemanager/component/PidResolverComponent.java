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
  public JsonNode resolveExternalPid(String url)
      throws UnprocessableEntityException, PidResolutionException {
    log.info("Querying the following: {}", url);
    var responseSpec = webClient.get().uri(url).retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            r -> r.bodyToMono(String.class).map(PidResolutionException::new));
    var response = responseSpec.bodyToMono(JsonNode.class);

    try {
      return response.toFuture().get();
    } catch (InterruptedException e) {
      log.warn("Interrupted connection. Unable to resolve the following: {}", url);
      Thread.currentThread().interrupt();
      return null;
    } catch (ExecutionException e) {
      if (e.getCause().getClass().equals(PidResolutionException.class)) {
        throw new PidResolutionException("Given PID not found: " + url);
      }
      throw new UnprocessableEntityException("Unable to parse identifier " + url + " to JSON.");
    }
  }


  public String getObjectName(String pid)
      throws UnprocessableEntityException, PidResolutionException {
    var pidRecord = resolveExternalPid(pid);
    if (pidRecord.get("name")!= null){
      return pidRecord.get("name").asText();
    }
    log.warn("Given pid {} resolves, but does not include a name attribute", pid);
    return "";
  }

}
