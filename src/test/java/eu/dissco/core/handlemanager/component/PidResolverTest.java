package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.EXTERNAL_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.loadResourceFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
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
class PidResolverTest {

  private static MockWebServer mockServer;
  private PidResolver pidResolver;

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
    pidResolver = new PidResolver(webClient, MAPPER);
  }

  @AfterAll
  static void destroy() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void testResolveExternalPidHandle() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecord.json"));
    var expected = expectedResponse.get("name").asText();
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID, false);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveRor() throws Exception {
    var expected = "Naturalis Biodiversity Center";
    var response = MAPPER.readTree("""
        {
         "names": [
            {
              "lang": "nl",
              "types": [
                "label"
              ],
              "value": "Nederlands Centrum voor Biodiversiteit Naturalis"
            },
            {
              "lang": "en",
              "types": [
                "ror_display",
                "label"
              ],
              "value": "Naturalis Biodiversity Center"
            }
            ]
        }
        """);
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(response))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var result = pidResolver.getObjectName(HANDLE, true);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testResolveRorNoEnglish() throws Exception {
    var expected = "Nederlands Centrum voor Biodiversiteit Naturalis";
    var response = MAPPER.readTree("""
        {
         "names": [
            {
              "lang": "nl",
              "types": [
                "label"
              ],
              "value": "Nederlands Centrum voor Biodiversiteit Naturalis"
            }
            ]
        }
        """);
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(response))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var result = pidResolver.getObjectName(HANDLE, true);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testResolveRorUnexpected() throws Exception {
    var response = MAPPER.readTree("""
        {
         "names": [
            {
              "language": "nl",
              "types": [
                "label"
              ],
              "value": "Nederlands Centrum voor Biodiversiteit Naturalis"
            }
            ]
        }
        """);
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(response))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var result = pidResolver.getObjectName(HANDLE, true);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void testResolveQid() throws Exception {
    // Given
    var expected = "Naturalis Biodiversity Center";
    var expectedResponse = MAPPER.readTree("""
        {
          "type": "item",
          "labels": {
            "la": "Museum Historiae Naturalis Lugduno-Batavum",
            "ca": "Naturalis",
            "de": "Naturalis",
            "en": "Naturalis Biodiversity Center"
          }
        }
        """);
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.resolveQid("Q641676");

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveQidFails() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree("""
        {
          "type": "item"
          }
        }
        """);
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // then
    assertThrowsExactly(PidResolutionException.class, () -> pidResolver.resolveQid("qid"));
  }

  @Test
  void testResolveQidNotFound() throws Exception {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.NOT_FOUND.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.resolveQid("Qid");

    // Then
    assertThat(response).isEqualTo("NOT FOUND");
  }

  @Test
  void testResolveExternalPidNoNameHandle() throws Exception {
    // Given
    var expectedResponse = MAPPER.readTree(loadResourceFile("pidrecord/pidRecordNoName.json"));
    var expected = "";
    mockServer.enqueue(new MockResponse()
        .setBody(MAPPER.writeValueAsString(expectedResponse))
        .setResponseCode(HttpStatus.OK.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID, false);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testResolveExternalPidNotFoundHandle() throws Exception {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.NOT_FOUND.value())
        .addHeader("Content-Type", "application/json"));

    // When
    var response = pidResolver.getObjectName(EXTERNAL_PID, false);

    // Then
    assertThat(response).isEqualTo("NOT FOUND");
  }

  @Test
  void testOther4xxError() {
    // Given
    mockServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.BAD_REQUEST.value())
        .addHeader("Content-Type", "application/json"));

    // Then
    assertThrowsExactly(PidResolutionException.class, () -> pidResolver.getObjectName(EXTERNAL_PID,
        true));
  }

  @Test
  void testRetriesSuccessHandle() throws Exception {
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
    var response = pidResolver.getObjectName(EXTERNAL_PID, false);

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(mockServer.getRequestCount() - requestCount).isEqualTo(2);
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
    assertThrowsExactly(PidResolutionException.class,
        () -> pidResolver.getObjectName(EXTERNAL_PID, true));
    assertThat(mockServer.getRequestCount() - requestCount).isEqualTo(4);
  }


  @Test
  void testRedirectHandle() throws Exception {
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
    var response = pidResolver.getObjectName(EXTERNAL_PID, false);

    // Then
    assertThat(response).isEqualTo(expected);
  }

}
