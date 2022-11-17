package eu.dissco.core.handlemanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

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
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.repositoryobjects.Handles;
import eu.dissco.core.handlemanager.testUtils.TestUtils;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandleServiceTest {

  @Mock
  HandleRepository handleRep;

  @Mock
  private PidTypeService pidTypeService;

  @Mock
  private HandleGeneratorService hf;

  @Mock
  private Clock mockClock;

  @InjectMocks
  private HandleService service;

  private List<byte[]> handlesList;
  private List<byte[]> handlesSingle;


  @BeforeEach
  void init() throws PidResolutionException, JsonProcessingException {
    // Pid type record
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(TestUtils.PTR_HANDLE_RECORD);

    // Generating list of handles
    initHandles();

    // Return empty list to indicate handle is not taken
    given(handleRep.checkDuplicateHandles(handlesList)).willReturn(new ArrayList<>());

    //Date and time
    initTime();
  }

  @Test
  void createHandleRecordTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    byte[] handle = handlesList.get(0);
    HandleRecordRequest request = TestUtils.generateTestHandleRequest();
    HandleRecordResponse response_expected = TestUtils.generateTestHandleResponse(handle);
    List<Handles> recordTest = TestUtils.generateTestHandleRecord(handle);
    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.genHandleList(1)).willReturn(handlesList);

    // When
    HandleRecordResponse response_received = service.createRecord(request, "hdl");

    // Then
    assertThat(response_received).isEqualTo(response_expected);
  }

  @Test
  void CreateDoiRecordTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    byte[] handle = handlesList.get(0);
    DoiRecordRequest request = TestUtils.generateTestDoiRequest();
    DoiRecordResponse response_expected = TestUtils.generateTestDoiResponse(handle);
    List<Handles> recordTest = TestUtils.generateTestDoiRecord(handle);
    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.genHandleList(1)).willReturn(handlesList);

    // When
    HandleRecordResponse response_received = service.createRecord(request, "doi");

    // Then
    assertThat(response_received).isEqualTo(response_expected);
  }

  @Test
  void CreateDigitalSpecimenTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenRequest request = TestUtils.generateTestDigitalSpecimenRequest();
    DigitalSpecimenResponse response_expected = TestUtils.generateTestDigitalSpecimenResponse(handle);
    List<Handles> recordTest = TestUtils.generateTestDigitalSpecimenRecord(handle);
    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.genHandleList(1)).willReturn(handlesList);

    // When
    HandleRecordResponse response_received = service.createRecord(request, "ds");

    // Then
    assertThat(response_received).isEqualTo(response_expected);
  }

  @Test
  void CreateDigitalSpecimenBotanyTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenBotanyRequest request = TestUtils.generateTestDigitalSpecimenBotanyRequest();
    DigitalSpecimenBotanyResponse response_expected = TestUtils.generateTestDigitalSpecimenBotanyResponse(
        handle);
    List<Handles> recordTest = TestUtils.generateTestDigitalSpecimenBotanyRecord(handle);

    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.genHandleList(1)).willReturn(handlesList);

    // When
    HandleRecordResponse response_received = service.createRecord(request, "dsB");

    // Then
    assertThat(response_received).isEqualTo(response_expected);
  }

  @Test
  void createBatchHandleRecordTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    List<HandleRecordRequest> request = generateBatchHandleRequest();
    List<HandleRecordResponse> responseExpected = generateBatchHandleResponse();
    List<Handles> recordTest = generateBatchHandleList();

    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.newHandle(2)).willReturn(handlesList);


    // When
    List<HandleRecordResponse> responseReceived = service.createHandleRecordBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void createBatchDoiRecordTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    List<DoiRecordRequest> request = generateBatchDoiRequest();
    List<DoiRecordResponse> responseExpected = generateBatchDoiResponse();
    List<Handles> recordTest = generateBatchDoiList();

    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.newHandle(2)).willReturn(handlesList);


    // When
    List<DoiRecordResponse> responseReceived = service.createDoiRecordBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void createBatchDigitalSpecimenTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    List<DigitalSpecimenRequest> request = generateBatchDigitalSpecimenRequest();
    List<DigitalSpecimenResponse> responseExpected = generateBatchDigitalSpecimenResponse();
    List<Handles> recordTest = generateBatchDigitalSpecimenList();

    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.newHandle(2)).willReturn(handlesList);


    // When
    List<DigitalSpecimenResponse> responseReceived = service.createDigitalSpecimenBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void createBatchDigitalSpecimenBotanyTest()
      throws PidCreationException, PidResolutionException, JsonProcessingException {
    // Given
    List<DigitalSpecimenBotanyRequest> request = generateBatchDigitalSpecimenBotanyRequest();
    List<DigitalSpecimenBotanyResponse> responseExpected = generateBatchDigitalSpecimenBotanyResponse();
    List<Handles> recordTest = generateBatchDigitalSpecimenBotanyList();

    given(handleRep.saveAll(recordTest)).willReturn(recordTest);
    given(hf.newHandle(2)).willReturn(handlesList);


    // When
    List<DigitalSpecimenBotanyResponse> responseReceived = service.createDigitalSpecimenBotanyBatch(
        request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  private List<HandleRecordRequest> generateBatchHandleRequest() {
    List<HandleRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(TestUtils.generateTestHandleRequest());
    }
    return requestList;
  }

  private List<HandleRecordResponse> generateBatchHandleResponse() {
    List<HandleRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(TestUtils.generateTestHandleResponse(h));
    }
    return responseList;
  }

  private List<Handles> generateBatchHandleList() {
    List<Handles> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(TestUtils.generateTestHandleRecord(h));
    }
    return handleList;
  }


  private List<DoiRecordRequest> generateBatchDoiRequest() {
    List<DoiRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(TestUtils.generateTestDoiRequest());
    }
    return requestList;
  }

  private List<DoiRecordResponse> generateBatchDoiResponse() {
    List<DoiRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(TestUtils.generateTestDoiResponse(h));
    }
    return responseList;
  }

  private List<Handles> generateBatchDoiList() {
    List<Handles> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(TestUtils.generateTestDoiRecord(h));
    }
    return handleList;
  }

  private List<DigitalSpecimenRequest> generateBatchDigitalSpecimenRequest() {
    List<DigitalSpecimenRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(TestUtils.generateTestDigitalSpecimenRequest());
    }
    return requestList;
  }

  private List<DigitalSpecimenResponse> generateBatchDigitalSpecimenResponse() {
    List<DigitalSpecimenResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(TestUtils.generateTestDigitalSpecimenResponse(h));
    }
    return responseList;
  }

  private List<Handles> generateBatchDigitalSpecimenList() {
    List<Handles> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(TestUtils.generateTestDigitalSpecimenRecord(h));
    }
    return handleList;
  }

  private List<DigitalSpecimenBotanyRequest> generateBatchDigitalSpecimenBotanyRequest() {
    List<DigitalSpecimenBotanyRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(TestUtils.generateTestDigitalSpecimenBotanyRequest());
    }
    return requestList;
  }

  private List<DigitalSpecimenBotanyResponse> generateBatchDigitalSpecimenBotanyResponse() {
    List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(TestUtils.generateTestDigitalSpecimenBotanyResponse(h));
    }
    return responseList;
  }

  private List<Handles> generateBatchDigitalSpecimenBotanyList() {
    List<Handles> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(TestUtils.generateTestDigitalSpecimenBotanyRecord(h));
    }
    return handleList;
  }


  private void initTime() {
    Clock fixedClock = Clock.fixed(TestUtils.CREATED, ZoneOffset.UTC);
    doReturn(fixedClock.instant()).when(mockClock).instant();
    doReturn(fixedClock.getZone()).when(mockClock).getZone();
  }

  private void initHandles() {
    handlesList = new ArrayList<>();
    handlesList.add(TestUtils.HANDLE.getBytes());
    handlesList.add(TestUtils.HANDLE_ALT.getBytes());

    handlesSingle = new ArrayList<>();
    handlesList.add(TestUtils.HANDLE.getBytes());
  }





  /*
   * createHandleRecordBatch
   * createDoiRecordBatch
   * createDigitalSpecimenBatch
   * createDigitalSpecimenBotanyBatch
   *
   * createRecord
   * 		recordType = "hdl"
   * 		recordType = "doi"
   * 		recordType = "ds"
   * 		recordType = "dsB"
   *
   */


}
