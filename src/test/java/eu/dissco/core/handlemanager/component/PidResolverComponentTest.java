package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.EXTERNAL_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.loadResourceFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

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
    WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .baseUrl(String.format("http://%s:%s", mockServer.getHostName(), mockServer.getPort()))
        .build();
    pidResolver = new PidResolverComponent(webClient);
  }

  @AfterAll
  static void destroy() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void testResolveExternalPid() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    var expected = expectedResponse.get("name").asText();
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveExternalPidNoName() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecordNoName.json"));
    var expected = "";
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID);

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
    assertThrows(PidResolutionException.class, () -> pidResolver.getObjectName(EXTERNAL_PID
    ));
  }

  @Test
  void testOther4xxError() {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.BAD_REQUEST.value())
        .addHeader("Content-Type", "application/json"));

    // Then
    assertThrows(UnprocessableEntityException.class, () -> pidResolver.getObjectName(EXTERNAL_PID
    ));
  }

  @Test
  void testRetriesSuccess() throws Exception {
    // Given
    int requestCount = mockServer.getRequestCount();
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    var expected = expectedResponse.get("name").asText();
    mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value()));
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID);

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
    assertThrows(UnprocessableEntityException.class, () -> pidResolver.getObjectName(EXTERNAL_PID));
    assertThat(mockServer.getRequestCount()-requestCount).isEqualTo(4);
  }


  @Test
  void testRedirect() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    var expected = expectedResponse.get("name").asText();
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.FOUND.value())
        .addHeader("Content-Type", "text/html;charset=utf-8")
        .addHeader("Location", EXTERNAL_PID));

    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID);

    // Then
    assertThat(response).isEqualTo(expected);
  }

}
