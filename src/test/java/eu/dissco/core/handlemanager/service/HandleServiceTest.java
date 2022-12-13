package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_TOMBSTONE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
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
  void testResolveSingleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle);
    ObjectNode repositoryResponse = genObjectNodeAttributeRecord(recordAttributeList);
    var responseExpected = genHandleRecordJsonResponse(handle, "PID");

    given(handleRep.resolveSingleRecord(handle)).willReturn(repositoryResponse);

    // When
    var responseReceived = service.resolveSingleRecord(handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    List<ObjectNode> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handlesList) {
      repositoryResponse.add(genObjectNodeAttributeRecord(genHandleRecordAttributes(handle)));
    }
    given(handleRep.resolveBatchRecord(handlesList)).willReturn(repositoryResponse);
    var responseExpected = genHandleRecordJsonResponseBatch(handlesList, "PID");
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
    HandleRecordRequest request = genHandleRecordRequestObject();
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genHandleRecordJsonResponse(handle);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
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
    DoiRecordRequest request = genDoiRecordRequestObject();
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genDoiRecordJsonResponse(handle);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
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
    DigitalSpecimenRequest request = genDigitalSpecimenRequestObject();
    List<HandleAttribute> handleRecord = genDigitalSpecimenAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genDigitalSpecimenJsonResponse(handle);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
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
    DigitalSpecimenBotanyRequest request = genDigitalSpecimenBotanyRequestObject();
    List<HandleAttribute> handleRecord = genDigitalSpecimenBotanyAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genDigitalSpecimenBotanyJsonResponse(handle);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createDigitalSpecimenBotanyJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecordBatchJson() throws Exception {
    // Given
    List<HandleRecordRequest> requests = genHandleRecordRequestBatch(handlesList);
    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handlesList) {
      singleRecord = genHandleRecordAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);
    var responseExpected = genHandleRecordJsonResponseBatch(handlesList);

    given(handleRep.createRecords(handlesList, instant, flatList)).willReturn(
        databaseResponse);
    given(hgService.genHandleList(handlesList.size())).willReturn(handlesList);

    // When
    var responseReceived = service.createHandleRecordBatchJson(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordBatchJson() throws Exception {
    // Given
    List<DoiRecordRequest> requests = genDoiRecordRequestBatch(handlesList);
    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handlesList) {
      singleRecord = genDoiRecordAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);
    var responseExpected = genDoiRecordJsonResponseBatch(handlesList);

    given(handleRep.createRecords(handlesList, instant, flatList)).willReturn(
        databaseResponse);
    given(hgService.genHandleList(handlesList.size())).willReturn(handlesList);

    // When
    var responseReceived = service.createDoiRecordBatchJson(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBatchJson() throws Exception {
    // Given
    List<DigitalSpecimenRequest> requests = genDigitalSpecimenRequestBatch(handlesList);
    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handlesList) {
      singleRecord = genDigitalSpecimenAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);
    var responseExpected = genDigitalSpecimenJsonResponseBatch(handlesList);

    given(handleRep.createRecords(handlesList, instant, flatList)).willReturn(
        databaseResponse);
    given(hgService.genHandleList(handlesList.size())).willReturn(handlesList);

    // When
    var responseReceived = service.createDigitalSpecimenBatchJson(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatchJson() throws Exception {
    // Given
    List<DigitalSpecimenBotanyRequest> requests = genDigitalSpecimenBotanyRequestBatch(handlesList);
    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handlesList) {
      singleRecord = genDigitalSpecimenBotanyAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);
    var responseExpected = genDigitalSpecimenBotanyJsonResponseBatch(handlesList);

    given(handleRep.createRecords(handlesList, instant, flatList)).willReturn(
        databaseResponse);
    given(hgService.genHandleList(handlesList.size())).willReturn(handlesList);

    // When
    var responseReceived = service.createDigitalSpecimenBotanyBatchJson(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordBatchLoc() throws Exception {
    // Given
    List<ObjectNode> updateRequest = genUpdateRequestBatch();

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> singleRecord;
    for (byte[] handle : handlesList) {
      singleRecord = genHandleRecordAttributesAltLoc(handle);
      aggrList.add(new ArrayList<>(singleRecord));
    }

    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);

    var responseExpected = genUpdateAltLocResponseBatch(handlesList);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handlesList);
    given(handleRep.resolveBatchRecord(anyList())).willReturn(databaseResponse);

    // When
    var responseReceived = service.updateRecordBatch(updateRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    JsonNode updateRequest = genUpdateRequestAltLoc();
    List<HandleAttribute> updateAttributeList = genUpdateRecordAttributesAltLoc(handle);

    List<HandleAttribute> updateAttributeListFull = genHandleRecordAttributesAltLoc(handle);
    var databaseResponse = genObjectNodeAttributeRecord(updateAttributeListFull);
    JsonApiWrapper responseExpected = genHandleRecordJsonResponseAltLoc(handle);

    given(handleRep.checkHandlesWritable(List.of(handle))).willReturn(List.of(handle));
    given(handleRep.updateRecord(instant, updateAttributeList)).willReturn(databaseResponse);

    // When
    var responseReceived = service.updateRecord(updateRequest, handle, RECORD_TYPE_HANDLE);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    JsonNode archiveRequest = genTombstoneRequest();
    List<HandleAttribute> tombstoneAttributes = genTombstoneRecordRequestAttributes(handle);
    List<HandleAttribute> tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);

    var databaseResponse = genObjectNodeAttributeRecord(tombstoneAttributesFull);
    JsonApiWrapper responseExpected = genGenericRecordJsonResponse(handle, tombstoneAttributesFull,
        RECORD_TYPE_TOMBSTONE);

    given(handleRep.checkHandlesWritable(List.of(handle))).willReturn(List.of(handle));
    given(handleRep.resolveSingleRecord(handle)).willReturn(databaseResponse);

    // When
    var responseReceived = service.archiveRecord(archiveRequest, handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testeRecordBatch() throws Exception {
    // Given
    List<ObjectNode> updateRequest = genTombstoneRequestBatch();

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> singleRecord;
    for (byte[] handle : handlesList) {
      singleRecord = genTombstoneRecordFullAttributes(handle);
      aggrList.add(new ArrayList<>(singleRecord));
    }

    List<ObjectNode> databaseResponse = genObjectNodeRecordBatch(aggrList);

    var responseExpected = genTombstoneResponseBatch(handlesList);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handlesList);
    given(handleRep.resolveBatchRecord(anyList())).willReturn(databaseResponse);

    // When
    var responseReceived = service.archiveRecordBatch(updateRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  // TODO change a pid-type record (e.g. pid issuer

  private void initTime() {
    Clock clock = Clock.fixed(CREATED, ZoneOffset.UTC);
    instant = Instant.now(clock);
    mockedStatic = mockStatic(Instant.class);
    mockedStatic.when(Instant::now).thenReturn(instant);
    mockedStatic.when(() -> Instant.from(any())).thenReturn(instant);
  }

  private void initHandleList() {
    handlesList = new ArrayList<>();
    handlesList.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handlesList.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
  }


}
