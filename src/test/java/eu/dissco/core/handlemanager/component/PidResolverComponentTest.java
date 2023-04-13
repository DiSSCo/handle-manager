package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.EXTERNAL_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.loadResourceFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PidResolverComponentTest {

  @Mock
  private WebClient client;
  @Mock
  private RequestHeadersUriSpec uriSpec;
  @Mock
  private RequestHeadersSpec headerSpec;
  @Mock
  private ResponseSpec responseSpec;
  @Mock
  private Mono<JsonNode> jsonNodeMono;
  @Mock
  private CompletableFuture<JsonNode> jsonFuture;
  private PidResolverComponent pidResolver;

  @BeforeEach
  void setup() {
    pidResolver = new PidResolverComponent(client);
  }

  @Test
  void testResolveExternalPid() throws Exception {
    // Given
    givenWebclient();
    var expected = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    given(jsonFuture.get()).willReturn(expected);

    // When
    var response = pidResolver.resolveExternalPid(EXTERNAL_PID, null);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveExternalPidNotFound() throws Exception {
    // Given
    givenWebclient();
    var exception = mock(ExecutionException.class);
    given(exception.getCause()).willReturn(new PidResolutionException("pid not found"));
    given(jsonFuture.get()).willThrow(exception);

    // Then
    assertThrows(PidResolutionException.class, () -> pidResolver.resolveExternalPid(EXTERNAL_PID,
        null));
  }

  @Test
  void testResolveExternalPidNotProcessable() throws Exception {
    // Given
    givenWebclient();
    var exception = mock(ExecutionException.class);
    // Can be any exception except PidResolutionException
    given(exception.getCause()).willReturn(new Exception());
    given(jsonFuture.get()).willThrow(exception);

    // Then
    assertThrows(UnprocessableEntityException.class, () -> pidResolver.resolveExternalPid(EXTERNAL_PID,
        null));
  }

  private void givenWebclient() {
    given(client.get()).willReturn(uriSpec);
    given(uriSpec.uri(anyString())).willReturn(headerSpec);
    given(headerSpec.retrieve()).willReturn(responseSpec);
    given(responseSpec.bodyToMono(any(Class.class))).willReturn(jsonNodeMono);
    given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
    given(jsonNodeMono.toFuture()).willReturn(jsonFuture);
  }
}
