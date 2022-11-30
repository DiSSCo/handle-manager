package eu.dissco.core.handlemanager.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.service.HandleService;
import eu.dissco.core.handlemanager.testUtils.TestUtils;
import java.util.ArrayList;
import java.util.List;
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
    handle = TestUtils.HANDLE.getBytes();
  }

  @Test
  void testHandleRecordCreation()
      throws Exception {
    // Given
    HandleRecordRequest request = TestUtils.generateTestHandleRequest();
    HandleRecordResponse responseExpected = TestUtils.generateTestHandleResponse(handle);
    given(service.createHandleRecord(request)).willReturn(responseExpected);
    // When
    ResponseEntity<HandleRecordResponse> responseReceived = controller.createRecord(request);
    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDoiRecordCreation()
      throws Exception {
    // Given
    DoiRecordRequest request = TestUtils.generateTestDoiRequest();
    DoiRecordResponse responseExpected = TestUtils.generateTestDoiResponse(handle);
    given(service.createDoiRecord(request)).willReturn(responseExpected);
    // When
    ResponseEntity<HandleRecordResponse> responseReceived = controller.createRecord(request);
    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpeciemenCreation()
      throws Exception {
    // Given
    DigitalSpecimenRequest request = TestUtils.generateTestDigitalSpecimenRequest();
    DigitalSpecimenResponse responseExpected = TestUtils.generateTestDigitalSpecimenResponse(
        handle);
    given(service.createDigitalSpecimenRecord(request)).willReturn(responseExpected);
    // When
    ResponseEntity<HandleRecordResponse> responseReceived = controller.createRecord(request);
    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testDigitalSpeciemenBotanyCreation()
      throws Exception {
    // Given
    DigitalSpecimenBotanyRequest request = TestUtils.generateTestDigitalSpecimenBotanyRequest();
    DigitalSpecimenBotanyResponse responseExpected = TestUtils.generateTestDigitalSpecimenBotanyResponse(
        handle);
    given(service.createDigitalSpecimenBotanyRecord(request)).willReturn(responseExpected);
    // When
    ResponseEntity<HandleRecordResponse> responseReceived = controller.createRecord(request);
    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testHandleRecordBatchCreation() throws Exception {
    // Given
    List<HandleRecordRequest> requestList = buildHandleRequestList();
    List<HandleRecordResponse> responseList = buildHandleResponseList();

    given(service.createHandleRecordBatch(requestList)).willReturn(responseList);

    // When
    ResponseEntity<List<HandleRecordResponse>> responseReceived = controller.createHandleRecordBatch(
        requestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseList);
  }

  @Test
  void testDoiRecordBatchCreation() throws Exception {
    // Given
    List<DoiRecordRequest> requestList = buildDoiRequestList();
    List<DoiRecordResponse> responseList = buildDoiResponseList();

    given(service.createDoiRecordBatch(requestList)).willReturn(responseList);

    // When
    ResponseEntity<List<DoiRecordResponse>> responseReceived = controller.createDoiRecordBatch(
        requestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseList);
  }

  @Test
  void testDigitalSpecimenBatchCreation() throws Exception {
    // Given
    List<DigitalSpecimenRequest> requestList = buildDigitalSpecimenRequestList();
    List<DigitalSpecimenResponse> responseList = buildDigitalSpecimenResponseList();

    given(service.createDigitalSpecimenBatch(requestList)).willReturn(responseList);

    // When
    ResponseEntity<List<DigitalSpecimenResponse>> responseReceived = controller.createDigitalSpecimenBatch(
        requestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseList);
  }

  @Test
  void testDigitalSpecimenBotanyBatchCreation() throws Exception {
    // Given
    List<DigitalSpecimenBotanyRequest> requestList = buildDigitalSpecimenBotanyRequestList();
    List<DigitalSpecimenBotanyResponse> responseList = buildDigitalSpecimenBotanyResponseList();

    given(service.createDigitalSpecimenBotanyBatch(requestList)).willReturn(responseList);

    // When
    ResponseEntity<List<DigitalSpecimenBotanyResponse>> responseReceived = controller.createDigitalSpecimenBotanyBatch(
        requestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseList);
  }

  @Test
  void testHello() {
    //When
    ResponseEntity<String> response = controller.hello();
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private List<HandleRecordRequest> buildHandleRequestList() {
    List<HandleRecordRequest> requestList = new ArrayList<>();
    HandleRecordRequest request = TestUtils.generateTestHandleRequest();

    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<HandleRecordResponse> buildHandleResponseList() {
    List<HandleRecordResponse> responseList = new ArrayList<>();
    HandleRecordResponse response = TestUtils.generateTestHandleResponse(handle);
    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }

  private List<DoiRecordRequest> buildDoiRequestList() {
    List<DoiRecordRequest> requestList = new ArrayList<>();
    DoiRecordRequest request = TestUtils.generateTestDoiRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<DoiRecordResponse> buildDoiResponseList() {
    List<DoiRecordResponse> responseList = new ArrayList<>();
    DoiRecordResponse response = TestUtils.generateTestDoiResponse(handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }

  // DigitalSpecimens
  private List<DigitalSpecimenRequest> buildDigitalSpecimenRequestList() {
    List<DigitalSpecimenRequest> requestList = new ArrayList<>();
    DigitalSpecimenRequest request = TestUtils.generateTestDigitalSpecimenRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      requestList.add(request);
    }
    return requestList;
  }

  private List<DigitalSpecimenResponse> buildDigitalSpecimenResponseList() {
    List<DigitalSpecimenResponse> responseList = new ArrayList<>();
    DigitalSpecimenResponse response = TestUtils.generateTestDigitalSpecimenResponse(handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }


  //DigitalSpecimenBotany
  private List<DigitalSpecimenBotanyRequest> buildDigitalSpecimenBotanyRequestList() {
    List<DigitalSpecimenBotanyRequest> responseList = new ArrayList<>();
    DigitalSpecimenBotanyRequest request = TestUtils.generateTestDigitalSpecimenBotanyRequest();
    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(request);
    }
    return responseList;
  }

  private List<DigitalSpecimenBotanyResponse> buildDigitalSpecimenBotanyResponseList() {
    List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
    DigitalSpecimenBotanyResponse response = TestUtils.generateTestDigitalSpecimenBotanyResponse(
        handle);

    for (int i = 0; i < REQUEST_LEN; i++) {
      responseList.add(response);
    }
    return responseList;
  }


}
