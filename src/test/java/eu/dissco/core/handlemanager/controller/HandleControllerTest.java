package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_TOMBSTONE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiData;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;

@ExtendWith(MockitoExtension.class)
@Slf4j
class HandleControllerTest {

  private final int REQUEST_LEN = 3;
  @Mock
  private HandleService service;
  private HandleController controller;

  ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  @BeforeEach
  void setup() {
    controller = new HandleController(service, mapper);
  }

  @Test
  void testGetAllHandles() throws Exception {
    // Given
    int pageSize = 10;
    int pageNum = 1;
    String handle = HANDLE;
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
  void testGetAllHandlesByPidStatus() throws Exception {
    // Given
    int pageSize = 10;
    int pageNum = 1;
    String pidStatus = "TEST";
    String handle = HANDLE;
    List<String> expectedHandles = new ArrayList<>();
    for (int i = 0; i < pageSize; i++) {
      expectedHandles.add(handle);
    }
    given(service.getHandlesPaged(pageNum, pageSize, pidStatus)).willReturn(expectedHandles);

    // When
    ResponseEntity<List<String>> response = controller.getAllHandlesByPidStatus(pageNum, pageSize,
        pidStatus);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(expectedHandles).isEqualTo(response.getBody());
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    JsonApiWrapper responseExpected = genHandleRecordJsonResponse(handle);
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
    for (String hdlStr : HANDLE_LIST_STR) {
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
    }
    var responseExpected = genHandleRecordJsonResponseBatch(handles);
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

  // Single Handle Record Creation


  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest request = genHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_HANDLE);
    JsonApiWrapper responseExpected = genHandleRecordJsonResponse(handle);
    given(service.createHandleRecordJson(request)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    DoiRecordRequest request = genDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DOI);
    JsonApiWrapper responseExpected = genDoiRecordJsonResponse(handle);
    given(service.createDoiRecordJson(request)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    DigitalSpecimenRequest request = genDigitalSpecimenRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DS);
    JsonApiWrapper responseExpected = genDigitalSpecimenJsonResponse(handle);
    given(service.createDigitalSpecimenJson(request)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotany() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    DigitalSpecimenBotanyRequest request = genDigitalSpecimenBotanyRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DS_BOTANY);
    JsonApiWrapper responseExpected = genDigitalSpecimenBotanyJsonResponse(handle);
    given(service.createDigitalSpecimenBotanyJson(request)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }


  // Response Object

  @Test
  void testCreateHandleRecordJsonBatch()
      throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleRecordRequest> request = genHandleRecordRequestBatch(handles);
    List<JsonApiWrapper> responseExpected = genHandleRecordJsonResponseBatch(handles);

    given(service.createHandleRecordBatchJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<JsonApiWrapper>> responseReceived = controller.createHandleRecordJsonBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordJsonBatch()
      throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<DoiRecordRequest> request = genDoiRecordRequestBatch(handles);
    List<JsonApiWrapper> responseExpected = genDoiRecordJsonResponseBatch(handles);

    given(service.createDoiRecordBatchJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<JsonApiWrapper>> responseReceived = controller.createDoiRecordJsonBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenJsonBatch()
      throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<DigitalSpecimenRequest> request = genDigitalSpecimenRequestBatch(handles);
    List<JsonApiWrapper> responseExpected = genDigitalSpecimenJsonResponseBatch(handles);

    given(service.createDigitalSpecimenBatchJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<JsonApiWrapper>> responseReceived = controller.createDigitalSpecimenJsonBatch(
        request);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyJsonBatch()
      throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<DigitalSpecimenBotanyRequest> request = genDigitalSpecimenBotanyRequestBatch(handles);
    List<JsonApiWrapper> responseExpected = genDigitalSpecimenBotanyJsonResponseBatch(handles);

    given(service.createDigitalSpecimenBotanyBatchJson(request)).willReturn(responseExpected);

    // When
    ResponseEntity<List<JsonApiWrapper>> responseReceived = controller.createDigitalSpecimenBotanyJsonBatch(
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
  void testUpdateRecord() throws Exception {
    // Given

    byte[] handle = HANDLE.getBytes();
    var updateAttributes = genUpdateRequestAltLoc();
    JsonApiData updateRequest = new JsonApiData(HANDLE, RECORD_TYPE_HANDLE, updateAttributes);
    ObjectNode updateRequestNode = mapper.createObjectNode();
    updateRequestNode.set("data", mapper.valueToTree(updateRequest));
    log.info(updateRequestNode.toString());

    JsonApiWrapper responseExpected = genHandleRecordJsonResponseAltLoc(handle);
    given(service.updateRecord(updateAttributes, handle, RECORD_TYPE_HANDLE)).willReturn(
        responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.updateRecord(updateRequestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<ObjectNode> updateRequestList = new ArrayList<>();
    List<JsonApiWrapper> responseExpected = new ArrayList<>();

    for (byte[] handle : handles) {
      var updateAttributes = genUpdateRequestAltLoc();
      JsonApiData updateRequest = new JsonApiData(new String(handle), RECORD_TYPE_HANDLE,
          updateAttributes);
      ObjectNode updateRequestNode = mapper.createObjectNode();
      updateRequestNode.set("data", mapper.valueToTree(updateRequest));
      updateRequestList.add(updateRequestNode.deepCopy());

      responseExpected.add(genHandleRecordJsonResponseAltLoc(handle));
    }

    given(service.updateRecordBatch(updateRequestList)).willReturn(responseExpected);

    // When
    var responseReceived = controller.updateRecords(updateRequestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes();
    var archiveAttributes = genTombstoneRequest();
    JsonApiData archiveRequest = new JsonApiData(HANDLE, RECORD_TYPE_TOMBSTONE, archiveAttributes);

    ObjectNode archiveRequestNode = mapper.createObjectNode();
    archiveRequestNode.set("data", mapper.valueToTree(archiveRequest));
    log.info(archiveRequestNode.toString());

    List<HandleAttribute> tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);
    JsonApiWrapper responseExpected = genGenericRecordJsonResponse(handle, tombstoneAttributesFull,
        RECORD_TYPE_TOMBSTONE);
    given(service.archiveRecord(archiveRequestNode.get("data"), handle)).willReturn(
        responseExpected);

    // When
    ResponseEntity<JsonApiWrapper> responseReceived = controller.archiveRecord(archiveRequestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<ObjectNode> updateRequestList = new ArrayList<>();
    List<JsonApiWrapper> responseExpected = new ArrayList<>();

    for (byte[] handle : handles) {
      var updateAttributes = genTombstoneRequest();
      JsonApiData updateRequest = new JsonApiData(new String(handle), RECORD_TYPE_TOMBSTONE,
          updateAttributes);
      ObjectNode updateRequestNode = mapper.createObjectNode();
      updateRequestNode.set("data", mapper.valueToTree(updateRequest));
      updateRequestList.add(updateRequestNode.deepCopy());
      responseExpected.add(genHandleRecordJsonResponseAltLoc(handle));
    }

    given(service.archiveRecordBatch(updateRequestList)).willReturn(responseExpected);

    // When
    var responseReceived = controller.archiveRecords(updateRequestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  private <T extends HandleRecordRequest> ObjectNode genCreateRecordRequest(T request, String recordType){
    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode dataNode = mapper.createObjectNode();
    ObjectNode attributeNode = mapper.valueToTree(request);

    if (attributeNode.has("referent")){
      attributeNode.remove("referent");
    }

    dataNode.put("type", recordType);
    dataNode.set("attributes", attributeNode);
    rootNode.set("data", dataNode);

    return rootNode;
  }


}
