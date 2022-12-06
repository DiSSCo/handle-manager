package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@Slf4j
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
  void testResolveSingleHandle() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    JsonApiWrapper responseExpected = generateTestJsonHandleRecordResponse(handle);
    given(service.resolveSingleRecord(handle)).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolveSingleHandle(handle);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandle() throws Exception {
    // Given
    List<byte[]> handles = new ArrayList<>();
    for (String hdlStr : HANDLE_LIST_STR){
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
    }
    var responseExpected = generateTestJsonHandleRecordResponseBatch(handles);
    given(service.resolveBatchRecord(anyList())).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolveBatchHandle(HANDLE_LIST_STR);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
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

  // JSON
  @Test
  void testHandleRecordCreationJson() throws Exception {
    // Given
    HandleRecordRequest request = generateTestHandleRequest();
    JsonApiWrapper responseExpected = generateTestJsonHandleRecordResponse(handle);
    given(service.createHandleRecordJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.createHandleRecordJson(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDoiRecordCreationJson() throws Exception {
    // Given
    DoiRecordRequest request = generateTestDoiRequest();
    JsonApiWrapper responseExpected = generateTestJsonDoiRecordResponse(handle);
    given(service.createDoiRecordJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.createDoiRecordJson(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpecimenCreationJson() throws Exception {
    // Given
    DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
    JsonApiWrapper responseExpected = generateTestJsonDigitalSpecimenResponse(handle);
    given(service.createDigitalSpecimenJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.createDigitalSpecimenJson(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpecimenBotanyCreationJson() throws Exception {
    // Given
    DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
    JsonApiWrapper responseExpected = generateTestJsonDigitalSpecimenBotanyResponse(handle);
    given(service.createDigitalSpecimenBotanyJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.createDigitalSpecimenBotanyJson(request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  // Response Object


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
      throws Exception {
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
      throws Exception {
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
      throws Exception {
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
    given(service.createHandleRecordJson(request)).willThrow(PidResolutionException.class);

    // When
    var exception = assertThrowsExactly(PidResolutionException.class,
        () -> controller.createHandleRecordJson(request));

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
