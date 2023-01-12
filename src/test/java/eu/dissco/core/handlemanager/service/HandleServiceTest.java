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

  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    var responseExpected = genHandleRecordJsonResponse(handle);
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(handleRecord);

    // When
    var responseReceived = service.createRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
    var responseExpected = genDoiRecordJsonResponse(handle, RECORD_TYPE_DOI);
    List<HandleAttribute> DoiRecord = genDoiRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(DoiRecord);

    // When
    var responseReceived = service.createRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
    var responseExpected = genDigitalSpecimenJsonResponse(handle, RECORD_TYPE_DS);
    List<HandleAttribute> DigitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(DigitalSpecimen);

    // When
    var responseReceived = service.createRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {
    // Given
    byte[] handle = handlesList.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    var responseExpected = genDigitalSpecimenBotanyJsonResponse(handle, RECORD_TYPE_DS_BOTANY);
    List<HandleAttribute> DigitalSpecimenBotany = genDigitalSpecimenBotanyAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(DigitalSpecimenBotany);

    // When
    var responseReceived = service.createRecord(request);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  @Test
  void testCreateHandleRecordBatch() throws Exception {
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
  void testCreateDoiRecordBatch() throws Exception {
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
  void testCreateDigitalSpecimenBatch() throws Exception {
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS));
      flatList.addAll(genDigitalSpecimenAttributes(handle));
    }

    log.info(requests.toString());

    List<JsonApiWrapper> responseExpected = genDigitalSpecimenJsonResponseBatch(handles, "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatch() throws Exception {
    // Given
    List<byte[]> handles = initHandleList();

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(
          genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY));
      flatList.addAll(genDigitalSpecimenBotanyAttributes(handle));
    }

    List<JsonApiWrapper> responseExpected = genDigitalSpecimenBotanyJsonResponseBatch(handles,
        "PID");

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecordBatch(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  /*
  @Test
  void testCreateHandleRecord1() throws Exception {

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
  } */

  @Test
  void testUpdateRecordLocationBatch() throws Exception {
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
  void testUpdateRecordInternalDuplicates() throws Exception {

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
    assertThrows(InvalidRecordInput.class, () -> {
      service.updateRecord(requestRoot, HANDLE.getBytes(StandardCharsets.UTF_8),
          RECORD_TYPE_HANDLE);
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
  void testUpdateRecordLocation() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);

    JsonNode updateRequest = genUpdateRequestAltLoc();

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
  void testGetHandlesPaged() {
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

  // Exceptions

  /*
    @Test
  void testPidInternalServiceError() throws Exception{
    // Given
    HandleRecordRequest requestObject = genHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_HANDLE);
    given(service.createHandleRecordJson(requestObject)).willThrow(PidServiceInternalError.class);

    // Then
    assertThrows(PidServiceInternalError.class, () -> {
      controller.createRecord(requestNode);
    });
  }

*/
  @Test
  void testInvalidInputExceptionCreateHandleRecord() throws Exception {
    // Given
    String invalidType = "INVALID TYPE";
    String invalidMessage = "Invalid request. Reason: unrecognized type. Check: " + invalidType;

    HandleRecordRequest request = genHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, invalidType);

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecord(requestNode);
    });

    // Then
    assertThat(exception.getMessage()).isEqualTo(invalidMessage);
  }

  @Test
  void testInvalidInputExceptionCreateHandleRecordBatch() {
    // Given
    String invalidType = "INVALID TYPE";
    String invalidMessage = "Invalid request. Reason: unrecognized type. Check: " + invalidType;

    List<byte[]> handles = initHandleList();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genHandleRecordRequestObject(), invalidType));
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecordBatch(requests);
    });

    // Then
    assertThat(exception.getMessage()).isEqualTo(invalidMessage);
  }


  @Test
  void testMissingFieldCreateHandleRecord() {

    HandleRecordRequest request = genHandleRecordRequestObject();
    ObjectNode requestObjectNode = genCreateRecordRequest(request, RECORD_TYPE_HANDLE);

    ((ObjectNode) requestObjectNode.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(PID_ISSUER_REQ);
    log.info(requestObjectNode.toString());

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecord(requestObjectNode);
    });

    // Then
    assertThat(exception.getMessage()).contains(PID_ISSUER_REQ);
  }

  @Test
  void testMissingFieldCreateDoiRecord() {

    HandleRecordRequest request = genDoiRecordRequestObject();
    ObjectNode requestObjectNode = genCreateRecordRequest(request, RECORD_TYPE_DOI);

    ((ObjectNode) requestObjectNode.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(
        REFERENT_DOI_NAME_REQ);
    log.info(requestObjectNode.toString());

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecord(requestObjectNode);
    });

    // Then
    assertThat(exception.getMessage()).contains(REFERENT_DOI_NAME_REQ);
  }

  @Test
  void testMissingFieldCreateDigitalSpecimen() {

    HandleRecordRequest request = genDigitalSpecimenBotanyRequestObject();
    ObjectNode requestObjectNode = genCreateRecordRequest(request, RECORD_TYPE_DS);

    ((ObjectNode) requestObjectNode.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(
        IN_COLLECTION_FACILITY_REQ);
    log.info(requestObjectNode.toString());

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecord(requestObjectNode);
    });

    // Then
    assertThat(exception.getMessage()).contains(IN_COLLECTION_FACILITY_REQ);
  }

  @Test
  void testMissingFieldCreateDigitalSpecimenBotany() {

    HandleRecordRequest request = genDigitalSpecimenBotanyRequestObject();
    ObjectNode requestObjectNode = genCreateRecordRequest(request, RECORD_TYPE_DS_BOTANY);

    ((ObjectNode) requestObjectNode.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(
        PRESERVED_OR_LIVING);
    log.info(requestObjectNode.toString());

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecord(requestObjectNode);
    });

    // Then
    assertThat(exception.getMessage()).contains(PRESERVED_OR_LIVING);
  }

  @Test
  void testMissingFieldCreateHandleRecordBatch() {

    List<byte[]> handles = initHandleList();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      ObjectNode request = genCreateRecordRequest(genHandleRecordRequestObject(),
          RECORD_TYPE_HANDLE);
      ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(PID_ISSUER_REQ);

      requests.add(request);
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecordBatch(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(PID_ISSUER_REQ);
  }

  @Test
  void testMissingFieldDoiRecordBatch() {

    List<byte[]> handles = initHandleList();
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      ObjectNode request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
      ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(REFERENT_DOI_NAME_REQ);
      requests.add(request);
    }
    log.info(requests.toString());

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecordBatch(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(REFERENT_DOI_NAME_REQ);
  }

  @Test
  void testMissingFieldDigitalSpecimenRecordBatch() {

    List<byte[]> handles = initHandleList();
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      ObjectNode request = genCreateRecordRequest(genDigitalSpecimenRequestObject(),
          RECORD_TYPE_DS);
      ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(IN_COLLECTION_FACILITY_REQ);

      requests.add(request);
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecordBatch(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(IN_COLLECTION_FACILITY_REQ);
  }

  @Test
  void testMissingFieldDigitalSpecimenBotanyRecordBatch() {

    List<byte[]> handles = initHandleList();
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      ObjectNode request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
          RECORD_TYPE_DS_BOTANY);
      ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(PRESERVED_OR_LIVING);

      requests.add(request);
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecordBatch(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(PRESERVED_OR_LIVING);
  }



  /*

    @Test
  void testUnrecognizedPropertyException(){
    // Given
    DoiRecordRequest request = genDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DOI);
    ObjectNode dataNode = (ObjectNode) requestNode.get(NODE_DATA);
    dataNode.remove(NODE_TYPE);
    dataNode.put(NODE_TYPE, RECORD_TYPE_HANDLE);
    requestNode.replace(NODE_DATA, dataNode);

    // Then
    assertThrows(UnrecognizedPropertyException.class, () -> {
      controller.createRecord(requestNode);
    });
  }

   */


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
