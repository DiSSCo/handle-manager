package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class HandleServiceTest {

  @Mock
  private HandleRepository handleRep;

  @Mock(lenient = true)
  private PidTypeService pidTypeService;

  @Mock
  private HandleGeneratorService hgService;

  private ObjectMapper mapper;

  private Instant instant;

  private HandleService service;

  private List<byte[]> handlesList;

  private MockedStatic<Instant> mockedStatic;


  @BeforeEach
  void setup() throws Exception {

    mapper = new ObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();

    service = new HandleService(handleRep, pidTypeService, hgService,
        documentBuilderFactory, mapper, transformerFactory);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);
    initTime();
    initHandleList();
  }

  @AfterEach
  void destroy() {
    mockedStatic.close();
  }

  @Test
  void testResolveSingleRecord() throws JsonProcessingException, PidResolutionException {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> recordAttributeList = generateTestHandleAttributes(handle);
    ObjectNode repositoryResponse =  generateRecordObjectNode(recordAttributeList);
    var responseExpected = generateTestJsonHandleRecordResponse(handle, "PID");

    given(handleRep.resolveSingleRecord(handle)).willReturn(repositoryResponse);

    // When
    var responseReceived = service.resolveSingleRecord(handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchRecord() throws JsonProcessingException {
    // Given
    List<ObjectNode> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handlesList){
      repositoryResponse.add(generateRecordObjectNode(generateTestHandleAttributes(handle)));
    }
    given(handleRep.resolveBatchRecord(handlesList)).willReturn(repositoryResponse);
    var responseExpected = generateTestJsonHandleRecordResponseBatch(handlesList);
    // When
    var responseReceived = service.resolveBatchRecord(handlesList);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  // Json object creation

  @Test
  void testCreateHandleRecordJson() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    HandleRecordRequest request = generateTestHandleRequest();
    List<HandleAttribute> handleRecord = generateTestHandleAttributes(handle);
    var databaseResponse =  generateRecordObjectNode(handleRecord);
    var responseExpected = generateTestJsonHandleRecordResponse(handle);

    given(handleRep.createHandleRecordJson(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createHandleRecordJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordJson() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    DoiRecordRequest request = generateTestDoiRequest();
    List<HandleAttribute> handleRecord = generateTestDoiAttributes(handle);
    var databaseResponse =  generateRecordObjectNode(handleRecord);
    var responseExpected = generateTestJsonDoiRecordResponse(handle);

    given(handleRep.createDoiRecordJson(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createDoiRecordJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenJson() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
    List<HandleAttribute> handleRecord = generateTestDigitalSpecimenAttributes(handle);
    var databaseResponse =  generateRecordObjectNode(handleRecord);
    var responseExpected = generateTestJsonDigitalSpecimenResponse(handle);

    given(handleRep.createDigitalSpecimenJson(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createDigitalSpecimenJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyJson() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
    List<HandleAttribute> handleRecord = generateTestDigitalSpecimenBotanyAttributes(handle);
    var databaseResponse =  generateRecordObjectNode(handleRecord);
    var responseExpected = generateTestJsonDigitalSpecimenBotanyResponse(handle);

    given(handleRep.createDigitalSpecimenBotanyJson(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createDigitalSpecimenBotanyJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  // Response Object Creation
  @Test
  void testCreateHandleRecord()
      throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    HandleRecordRequest request = generateTestHandleRequest();
    HandleRecordResponse responseExpected = generateTestHandleResponse(handle);
    List<HandleAttribute> recordTest = generateTestHandleAttributes(handle);

    given(handleRep.createHandle(handle, instant, recordTest)).willReturn(responseExpected);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    HandleRecordResponse responseReceived = service.createHandleRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord()
      throws Exception {

    // Given
    byte[] handle = handlesList.get(0);
    DoiRecordRequest request = generateTestDoiRequest();
    DoiRecordResponse responseExpected = generateTestDoiResponse(handle);
    List<HandleAttribute> recordTest = generateTestDoiAttributes(handle);

    given(handleRep.createDoi(handle, instant, recordTest)).willReturn(responseExpected);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    DoiRecordResponse responseReceived = service.createDoiRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen()
      throws Exception {

    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenRequest request = generateTestDigitalSpecimenRequest();
    DigitalSpecimenResponse responseExpected = generateTestDigitalSpecimenResponse(handle);
    List<HandleAttribute> recordTest = generateTestDigitalSpecimenAttributes(handle);

    given(handleRep.createDigitalSpecimen(handle, instant, recordTest)).willReturn(
        responseExpected);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    DigitalSpecimenResponse responseReceived = service.createDigitalSpecimen(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotany()
      throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    DigitalSpecimenBotanyRequest request = generateTestDigitalSpecimenBotanyRequest();
    DigitalSpecimenBotanyResponse responseExpected = generateTestDigitalSpecimenBotanyResponse(
        handle);
    List<HandleAttribute> recordTest = generateTestDigitalSpecimenBotanyAttributes(handle);

    given(handleRep.createDigitalSpecimenBotany(handle, instant, recordTest)).willReturn(
        responseExpected);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    DigitalSpecimenBotanyResponse responseReceived = service.createDigitalSpecimenBotany(
        request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateBatchHandleRecord()
      throws Exception {
    // Given
    List<HandleRecordRequest> request = generateBatchHandleRequest();
    List<HandleRecordResponse> responseExpected = generateBatchHandleResponse();
    List<HandleAttribute> handleAttributes = generateBatchHandleAttributeList();

    given(handleRep.createHandleRecordBatch(handlesList, instant, handleAttributes)).willReturn(
        responseExpected);
    given(hgService.genHandleList(2)).willReturn(handlesList);

    // When
    List<HandleRecordResponse> responseReceived = service.createHandleRecordBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void testCreateBatchDoiRecord()
      throws Exception {
    // Given
    List<DoiRecordRequest> request = generateBatchDoiRequest();
    List<DoiRecordResponse> responseExpected = generateBatchDoiResponse();

    List<HandleAttribute> handleAttributes = generateBatchDoiAttributeList();

    given(handleRep.createDoiRecordBatch(handlesList, instant, handleAttributes)).willReturn(
        responseExpected);
    given(hgService.genHandleList(2)).willReturn(handlesList);

    // When
    List<DoiRecordResponse> responseReceived = service.createDoiRecordBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void testCreateBatchDigitalSpecimen()
      throws Exception {
    // Given
    List<DigitalSpecimenRequest> request = generateBatchDigitalSpecimenRequest();
    List<DigitalSpecimenResponse> responseExpected = generateBatchDigitalSpecimenResponse();

    List<HandleAttribute> handleAttributes = generateBatchDigitalSpecimenAttributeList();

    given(handleRep.createDigitalSpecimenBatch(handlesList, instant, handleAttributes)).willReturn(
        responseExpected);
    given(hgService.genHandleList(2)).willReturn(handlesList);

    // When
    List<DigitalSpecimenResponse> responseReceived = service.createDigitalSpecimenBatch(request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }

  @Test
  void testCreateBatchDigitalSpecimenBotany()
      throws Exception {
    // Given
    List<DigitalSpecimenBotanyRequest> request = generateBatchDigitalSpecimenBotanyRequest();
    List<DigitalSpecimenBotanyResponse> responseExpected = generateBatchDigitalSpecimenBotanyResponse();

    List<HandleAttribute> handleAttributes = generateBatchDigitalSpecimenBotanyAttributeList();

    given(handleRep.createDigitalSpecimenBotanyBatch(handlesList, instant,
        handleAttributes)).willReturn(responseExpected);
    given(hgService.genHandleList(2)).willReturn(handlesList);

    // When
    List<DigitalSpecimenBotanyResponse> responseReceived = service.createDigitalSpecimenBotanyBatch(
        request);

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
  }


  private List<HandleRecordRequest> generateBatchHandleRequest() {
    List<HandleRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(generateTestHandleRequest());
    }
    return requestList;
  }

  private List<HandleRecordResponse> generateBatchHandleResponse() {
    List<HandleRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(generateTestHandleResponse(h));
    }
    return responseList;
  }

  private List<HandleAttribute> generateBatchHandleAttributeList() {
    List<HandleAttribute> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(generateTestHandleAttributes(h));
    }
    return handleList;
  }

  private List<HandleAttribute> generateBatchDoiAttributeList() {
    List<HandleAttribute> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(generateTestDoiAttributes(h));
    }
    return handleList;
  }

  private List<HandleAttribute> generateBatchDigitalSpecimenAttributeList() {
    List<HandleAttribute> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(generateTestDigitalSpecimenAttributes(h));
    }
    return handleList;
  }

  private List<HandleAttribute> generateBatchDigitalSpecimenBotanyAttributeList() {
    List<HandleAttribute> handleList = new ArrayList<>();
    for (byte[] h : handlesList) {
      handleList.addAll(generateTestDigitalSpecimenBotanyAttributes(h));
    }
    return handleList;
  }

  private List<DoiRecordRequest> generateBatchDoiRequest() {
    List<DoiRecordRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(generateTestDoiRequest());
    }
    return requestList;
  }

  private List<DoiRecordResponse> generateBatchDoiResponse() {
    List<DoiRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(generateTestDoiResponse(h));
    }
    return responseList;
  }

  private List<DigitalSpecimenRequest> generateBatchDigitalSpecimenRequest() {
    List<DigitalSpecimenRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(generateTestDigitalSpecimenRequest());
    }
    return requestList;
  }

  private List<DigitalSpecimenResponse> generateBatchDigitalSpecimenResponse() {
    List<DigitalSpecimenResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(generateTestDigitalSpecimenResponse(h));
    }
    return responseList;
  }

  private List<DigitalSpecimenBotanyRequest> generateBatchDigitalSpecimenBotanyRequest() {
    List<DigitalSpecimenBotanyRequest> requestList = new ArrayList<>();
    for (int i = 0; i < handlesList.size(); i++) {
      requestList.add(generateTestDigitalSpecimenBotanyRequest());
    }
    return requestList;
  }

  private List<DigitalSpecimenBotanyResponse> generateBatchDigitalSpecimenBotanyResponse() {
    List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
    for (byte[] h : handlesList) {
      responseList.add(generateTestDigitalSpecimenBotanyResponse(h));
    }
    return responseList;
  }


  private void initTime() {
    Clock clock = Clock.fixed(CREATED, ZoneOffset.UTC);
    instant = Instant.now(clock);
    mockedStatic = mockStatic(Instant.class);
    mockedStatic.when(Instant::now).thenReturn(instant);
    mockedStatic.when(() -> Instant.from(any())).thenReturn(instant);
  }

  private void initHandleList() {
    handlesList = new ArrayList<>();
    handlesList.add(HANDLE.getBytes());
    handlesList.add(HANDLE_ALT.getBytes());
  }


}
