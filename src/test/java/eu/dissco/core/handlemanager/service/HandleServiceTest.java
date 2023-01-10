package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.*;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
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
  void testResolveSingleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle);

    var responseExpected = genHandleRecordJsonResponse(handle, "PID");

    given(handleRep.resolveHandleAttributes(handle)).willReturn(recordAttributeList);

    // When
    var responseReceived = service.resolveSingleRecord(handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    List<HandleAttribute> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handlesList) {
      repositoryResponse.addAll(genHandleRecordAttributes(handle));
    }

    given(handleRep.resolveHandleAttributes(anyList())).willReturn(repositoryResponse);

    var responseExpected = genHandleRecordJsonResponseBatch(handlesList, "PID");
    // When
    var responseReceived = service.resolveBatchRecord(handlesList);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  /*@Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    var request = genHandleRecordRequestObject();
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genHandleRecordJsonResponse(handle);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handle);

    // When
    JsonApiWrapper responseReceived = service.createHandleRecordJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  /*

  @Test
  void testCreateDoiRecordJson() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    DoiRecordRequest request = genDoiRecordRequestObject();
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genDoiRecordJsonResponse(handle, RECORD_TYPE_DOI);

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
    byte[] handle = HANDLE.getBytes();
    DigitalSpecimenRequest request = genDigitalSpecimenRequestObject();
    List<HandleAttribute> handleRecord = genDigitalSpecimenAttributes(handle);
    var databaseResponse = genObjectNodeAttributeRecord(handleRecord);
    var responseExpected = genDigitalSpecimenJsonResponse(handle, RECORD_TYPE_DS);

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
    var responseExpected = genDigitalSpecimenBotanyJsonResponse(handle, RECORD_TYPE_DS_BOTANY);

    given(handleRep.createRecord(handle, instant, handleRecord)).willReturn(databaseResponse);
    given(hgService.genHandleList(1)).willReturn(handlesList);

    // When
    JsonApiWrapper responseReceived = service.createDigitalSpecimenBotanyJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  } */

  @Test
  void testCreateHandleRecordBatch() throws Exception{
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
      flatList.addAll(genHandleRecordAttributes(handle));
    }

    List<JsonApiWrapper> responseExpected = genHandleRecordJsonResponseBatch(handles, "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordBatch() throws Exception{
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI));
      flatList.addAll(genDoiRecordAttributes(handle));
    }

    List<JsonApiWrapper> responseExpected = genDoiRecordJsonResponseBatch(handles, "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  @Test
  void testCreateDigitalSpecimenBatch() throws Exception{
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS));
      flatList.addAll(genDigitalSpecimenAttributes(handle));
    }

    List<JsonApiWrapper> responseExpected = genDigitalSpecimenJsonResponseBatch(handles, "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatch() throws Exception{
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY));
      flatList.addAll(genDigitalSpecimenBotanyAttributes(handle));
    }

    List<JsonApiWrapper> responseExpected = genDigitalSpecimenBotanyJsonResponseBatch(handles, "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecord() throws Exception {

    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    var request = genHandleRecordRequestObject();
    var responseAttributes = genHandleRecordAttributes(handle);
    var responseExpected = genHandleRecordJsonResponse(handle);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(responseAttributes);

    // When
    var responseReceived = service.createHandleRecordJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {

    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    var request = genDoiRecordRequestObject();
    var responseAttributes = genDoiRecordAttributes(handle);
    var responseExpected = genDoiRecordJsonResponse(handle, RECORD_TYPE_DOI);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(responseAttributes);

    // When
    var responseReceived = service.createDoiRecordJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {

    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    var request = genDigitalSpecimenRequestObject();
    var responseAttributes = genDigitalSpecimenAttributes(handle);
    var responseExpected = genDigitalSpecimenJsonResponse(handle, RECORD_TYPE_DS);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(responseAttributes);

    // When
    var responseReceived = service.createDigitalSpecimenJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotany() throws Exception {

    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    var request = genDigitalSpecimenBotanyRequestObject();
    var responseAttributes = genDigitalSpecimenBotanyAttributes(handle);
    var responseExpected = genDigitalSpecimenBotanyJsonResponse(handle, RECORD_TYPE_DS_BOTANY);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(responseAttributes);

    // When
    var responseReceived = service.createDigitalSpecimenBotanyJson(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordBatchLoc() throws Exception {
    // Given
    List<byte[]> handles = initHandleList();
    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    List<HandleAttribute> updatedAttributeRecord = new ArrayList<>();
    for (byte[] handle : handles) {
      updatedAttributeRecord.addAll(genHandleRecordAttributesAltLoc(handle));
    }

    var responseExpected = genUpdateAltLocResponseBatch(handles);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(updatedAttributeRecord);

    // When
    var responseReceived = service.updateRecordBatch(updateRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }



  @Test
  void testUpdateRecordInternalDuplicates() throws Exception{

    // Given
    List<byte[]> handles = new ArrayList<>();
    handles.add(HANDLE.getBytes());
    handles.add(HANDLE.getBytes());

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    // Then
    assertThrows(InvalidRecordInput.class, () -> {
      service.updateRecordBatch(updateRequest);
    });
  }


  @Test
  void testUpdateRecordInvalidField() throws Exception {

    // Given
    ObjectNode requestRoot = mapper.createObjectNode();
    ObjectNode requestData = mapper.createObjectNode();
    ObjectNode requestAttributes = mapper.createObjectNode();

    requestAttributes.put("invalidField", "invalidValue");
    requestData.put(NODE_TYPE, RECORD_TYPE_HANDLE);
    requestData.put(NODE_ID, HANDLE);
    requestData.set(NODE_ATTRIBUTES, requestAttributes);

    requestRoot.set(NODE_DATA, requestData);

    // Then
    assertThrows(InvalidRecordInput.class, () ->{
      service.updateRecord(requestRoot, HANDLE.getBytes(StandardCharsets.UTF_8), RECORD_TYPE_HANDLE);
    });

  }

  @Test
  void testUpdateRecordNonWritable() {
    // Given
    List<byte[]> handles = initHandleList();
    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);
    given(handleRep.checkHandlesWritable(anyList())).willReturn(new ArrayList<>());

    // Then
    assertThrows(PidResolutionException.class, () -> {
      service.updateRecordBatch(updateRequest);
    });
  }



  @Test
  void testUpdateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    JsonNode updateRequest = genUpdateRequestAltLoc();
    log.info(updateRequest.toString());

    List<HandleAttribute> updatedAttributeRecord = genHandleRecordAttributesAltLoc(handle);
    JsonApiWrapper responseExpected = genHandleRecordJsonResponseAltLoc(handle);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(updatedAttributeRecord);

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
    List<HandleAttribute> tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);

    var databaseResponse = genObjectNodeAttributeRecord(tombstoneAttributesFull);
    JsonApiWrapper responseExpected = genGenericRecordJsonResponse(handle, tombstoneAttributesFull,
        RECORD_TYPE_TOMBSTONE);

    given(handleRep.checkHandlesWritable(List.of(handle))).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(tombstoneAttributesFull);

    // When
    var responseReceived = service.archiveRecord(archiveRequest, handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    List<JsonNode> archiveRequest = genTombstoneRequestBatch();

    List<HandleAttribute> flatList = new ArrayList<>();
    for (byte[] handle : handlesList) {
      flatList.addAll(genTombstoneRecordFullAttributes(handle));
    }

    var responseExpected = genTombstoneResponseBatch(handlesList);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handlesList);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testGetHandlesPaged(){
    // Given
    int pageNum = 0;
    int pageSize = 2;
    byte[] pidStatus = PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8);
    List<String> handles = HANDLE_LIST_STR;

    given(handleRep.getAllHandles(pageNum, pageSize)).willReturn(handles);
    given(handleRep.getAllHandles(pidStatus, pageNum, pageSize)).willReturn(handles);

    // When
    var responseExpectedFirst = service.getHandlesPaged(pageNum, pageSize);
    var responseExpectedSecond = service.getHandlesPaged(pageNum, pageSize, new String(pidStatus));

    // Then
    assertThat(responseExpectedFirst).isEqualTo(handles);
    assertThat(responseExpectedSecond).isEqualTo(handles);
  }

  private void initTime() {
    Clock clock = Clock.fixed(CREATED, ZoneOffset.UTC);
    instant = Instant.now(clock);
    mockedStatic = mockStatic(Instant.class);
    mockedStatic.when(Instant::now).thenReturn(instant);
    mockedStatic.when(() -> Instant.from(any())).thenReturn(instant);
  }

  private List<byte[]> initHandleList() {
    handlesList = new ArrayList<>();
    handlesList.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handlesList.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    return handlesList;
  }


}
