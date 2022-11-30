package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleResponse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class HandleControllerTest {

  private final int REQUEST_LEN = 3;
  @Mock
  private HandleService service;
  private HandleController controller;
  private byte[] handle;

  @BeforeEach
  void setup() {
    controller = new HandleController(service);
    handle = HANDLE.getBytes();
  }

  @Test
  void testGetAllHandles() throws PidResolutionException {
    // Given
    int pageSize = 10;
    int pageNum = 1;
    String handle = "20.10/...";
    List<String> expectedHandles = new ArrayList<>();
    for (int i = 0; i < pageSize; i++) {
      expectedHandles.add(handle);
    }
    given(service.getHandlesPaged(pageNum, pageSize)).willReturn(expectedHandles);

    // When
    ResponseEntity<List<String>> response = controller.getAllHandles(pageNum, pageSize);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(expectedHandles).isEqualTo(response.getBody());
  }

  @Test
  void testGetAllHandlesFailure() {
    // Given
    given(service.getHandlesPaged(1, 10)).willReturn(new ArrayList<>());

    // When
    var exception = assertThrowsExactly(PidResolutionException.class,
        () -> controller.getAllHandles(1, 10));

    // Then
    assertThat(exception).hasMessage("Unable to locate handles");
  }

  @Test
  void testHandleRecordCreation()
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    // Given
    HandleRecordRequest request = generateTestHandleRequest();
    HandleRecordResponse responseExpected = generateTestHandleResponse(handle);
    given(service.createHandleRecord(request)).willReturn(responseExpected);

    // When
    ResponseEntity<HandleRecordResponse> responseReceived = controller.createHandleRecord(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDoiRecordCreation()
      throws Exception {
    // Given
    DoiRecordRequest request = generateTestDoiRequest();
    DoiRecordResponse responseExpected = generateTestDoiResponse(handle);
    given(service.createDoiRecord(request)).willReturn(responseExpected);

    // When
    ResponseEntity<DoiRecordResponse> responseReceived = controller.createDoiRecord(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpeciemenCreation()
      throws Exception {
    // Given
    DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
    DigitalSpecimenResponse responseExpected = generateTestDigitalSpecimenResponse(
        handle);
    given(service.createDigitalSpecimen(request)).willReturn(responseExpected);

    // When
    ResponseEntity<DigitalSpecimenResponse> responseReceived = controller.createDigitalSpecimen(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpeciemenBotanyCreation()
      throws Exception {
    // Given
    DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
    DigitalSpecimenBotanyResponse responseExpected = generateTestDigitalSpecimenBotanyResponse(
        handle);
    given(service.createDigitalSpecimenBotany(request)).willReturn(responseExpected);

    // When
    ResponseEntity<DigitalSpecimenBotanyResponse> responseReceived = controller.createDigitalSpecimenBotany(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testHandleRecordCreationBatch()
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    // Given
    List<HandleRecordRequest> request = buildHandleRequestList();
    List<HandleRecordResponse> responseExpected = buildHandleResponseList();
    given(service.createHandleRecordBatch(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<HandleRecordResponse>> responseReceived = controller.createHandleRecordBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDoiRecordCreationBatch()
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    // Given
    List<DoiRecordRequest> request = buildDoiRequestList();
    List<DoiRecordResponse> responseExpected = buildDoiResponseList();
    given(service.createDoiRecordBatch(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<DoiRecordResponse>> responseReceived = controller.createDoiRecordBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpecimenCreationBatch()
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    // Given
    List<DigitalSpecimenRequest> request = buildDigitalSpecimenRequestList();
    List<DigitalSpecimenResponse> responseExpected = buildDigitalSpecimenResponseList();
    given(service.createDigitalSpecimenBatch(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<DigitalSpecimenResponse>> responseReceived = controller.createDigitalSpecimenBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpecimenBotanyCreationBatch()
      throws PidResolutionException, JsonProcessingException, ParserConfigurationException, TransformerException, PidCreationException {
    // Given
    List<DigitalSpecimenBotanyRequest> request = buildDigitalSpecimenBotanyRequestList();
    List<DigitalSpecimenBotanyResponse> responseExpected = buildDigitalSpecimenBotanyResponseList();
    given(service.createDigitalSpecimenBotanyBatch(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<DigitalSpecimenBotanyResponse>> responseReceived = controller.createDigitalSpecimenBotanyBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testHello() {
    //When
    ResponseEntity<String> response = controller.hello();
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testPidCreationException() throws Exception {
    // Given
    HandleRecordRequest request = generateTestHandleRequest();
    given(service.createHandleRecord(request)).willThrow(PidResolutionException.class);

    // When
    var exception = assertThrowsExactly(PidResolutionException.class,
        () -> controller.createHandleRecord(request));

    // Then

  }

  private List<HandleRecordRequest> buildHandleRequestList() {
    List<HandleRecordRequest> requestList = new ArrayList<>();
    HandleRecordRequest request = generateTestHandleRequest();

    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<HandleRecordResponse> buildHandleResponseList() {
    List<HandleRecordResponse> responseList = new ArrayList<>();
    HandleRecordResponse response = generateTestHandleResponse(handle);
    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }

  private List<DoiRecordRequest> buildDoiRequestList() {
    List<DoiRecordRequest> requestList = new ArrayList<>();
    DoiRecordRequest request = generateTestDoiRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<DoiRecordResponse> buildDoiResponseList() {
    List<DoiRecordResponse> responseList = new ArrayList<>();
    DoiRecordResponse response = generateTestDoiResponse(handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }

  // DigitalSpecimens
  private List<DigitalSpecimenRequest> buildDigitalSpecimenRequestList() {
    List<DigitalSpecimenRequest> requestList = new ArrayList<>();
    DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<DigitalSpecimenResponse> buildDigitalSpecimenResponseList() {
    List<DigitalSpecimenResponse> responseList = new ArrayList<>();
    DigitalSpecimenResponse response = generateTestDigitalSpecimenResponse(handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }

  //DigitalSpecimenBotany
  private List<DigitalSpecimenBotanyRequest> buildDigitalSpecimenBotanyRequestList() {
    List<DigitalSpecimenBotanyRequest> responseList = new ArrayList<>();
    DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(request);
    }
    return responseList;
  }

  private List<DigitalSpecimenBotanyResponse> buildDigitalSpecimenBotanyResponseList() {
    List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
    DigitalSpecimenBotanyResponse response = generateTestDigitalSpecimenBotanyResponse(
        handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }
}
