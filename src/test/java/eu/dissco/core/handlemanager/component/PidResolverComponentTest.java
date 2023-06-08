package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.EXTERNAL_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.loadResourceFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class PidResolverComponentTest {

  private static MockWebServer mockServer;
  private PidResolverComponent pidResolver;

  @BeforeAll
  static void init() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();
  }

  @BeforeEach
  void setup() {
    WebClient webClient = WebClient.create(
        String.format("http://%s:%s", mockServer.getHostName(), mockServer.getPort()));
    pidResolver = new PidResolverComponent(webClient);
  }

  @AfterAll
  static void destroy() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void testResolveExternalPid() throws Exception {
    // Given
    var expected = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expected))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.resolveExternalPid(EXTERNAL_PID);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveExternalPidNotFound() {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.NOT_FOUND.value())
        .addHeader("Content-Type", "application/json"));

    // Then
    assertThrows(PidResolutionException.class, () -> pidResolver.resolveExternalPid(EXTERNAL_PID
    ));
  }

  @Test
  void testOther4xxError() {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.BAD_REQUEST.value())
        .addHeader("Content-Type", "application/json"));

    // Then
    assertThrows(UnprocessableEntityException.class, () -> pidResolver.resolveExternalPid(EXTERNAL_PID
    ));
  }

  @Test
  void testRetriesSuccess() throws Exception {
    // Given
    int requestCount = mockServer.getRequestCount();
    var expected = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expected))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.resolveExternalPid(EXTERNAL_PID);

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(mockServer.getRequestCount()-requestCount).isEqualTo(2);
  }

  @Test
  void testRetriesFailures() {
    // Given
    int requestCount = mockServer.getRequestCount();
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));

    // Then
    assertThrows(UnprocessableEntityException.class, () -> pidResolver.resolveExternalPid(EXTERNAL_PID));
    assertThat(mockServer.getRequestCount()-requestCount).isEqualTo(4);
  }

}
