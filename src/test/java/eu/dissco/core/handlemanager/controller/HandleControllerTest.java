package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.VALID_PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class HandleControllerTest {

  @Mock
  private HandleService service;
  private HandleController controller;

  private final String SANDBOX_URI = "https://sandbox.dissco.tech/";

  public ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  @BeforeEach
  void setup() {
    controller = new HandleController(service);
  }

  @Test
  void testGetAllHandlesByPidStatus() throws Exception {
    // Given
    int pageSize = 10;
    int pageNum = 1;
    String pidStatus = "TEST";
    List<String> expectedHandles = Collections.nCopies(pageSize, HANDLE);

    given(service.getHandlesPaged(pageNum, pageSize, pidStatus)).willReturn(expectedHandles);

    // When
    ResponseEntity<List<String>> response = controller.getAllHandlesByPidStatus(pageNum, pageSize,
        pidStatus);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(expectedHandles).isEqualTo(response.getBody());
  }

  @Test
  void testGetAllHandlesByPidStatusInvalid() {
    int pageSize = 10;
    int pageNum = 1;
    String pidStatus = "BAD";

    var exception = assertThrowsExactly(InvalidRecordInput.class,
        () -> controller.getAllHandlesByPidStatus(pageNum, pageSize, pidStatus));

    // Then
    assertThat(exception).hasMessage(
        "Invalid Input. Pid Status not recognized. Available Pid Statuses: " + VALID_PID_STATUS);
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    String prefix = "20.5000.1025";
    String suffix = "QRS-321-ABC";
    String path = SANDBOX_URI + prefix + "/" + suffix;
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(prefix + "/" + suffix);

    JsonApiWrapperRead responseExpected = givenRecordResponseRead(List.of(handle), path,
        RECORD_TYPE_HANDLE);
    given(service.resolveSingleRecord(handle, path)).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolvePid(prefix, suffix, r);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandle() throws Exception {
    // Given
    List<byte[]> handles = new ArrayList<>();
    List<JsonNode> requestRoot = new ArrayList<>();
    String path = SANDBOX_URI + "view";
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("view");

    for (String hdlStr : HANDLE_LIST_STR) {
      handles.add(hdlStr.getBytes(StandardCharsets.UTF_8));
      ObjectNode requestData = mapper.createObjectNode();
      ObjectNode requestId = mapper.createObjectNode();

      requestId.put(NODE_ID, hdlStr);
      requestData.set(NODE_DATA, requestId);
      requestRoot.add(requestData);
    }

    var responseExpected = givenRecordResponseRead(handles, path, RECORD_TYPE_HANDLE);
    given(service.resolveBatchRecord(anyList(), eq(path))).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolvePids(requestRoot, r);

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
        () -> controller.getAllHandlesByPidStatus(1, 10, "ALL"));

    // Then
    assertThat(exception).hasMessage("Unable to resolve pids");
  }

  // Single Handle Record Creation
  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = genHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_HANDLE);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_HANDLE);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

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
    HandleRecordRequest requestObject = genDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_DOI);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_DOI);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = genDigitalSpecimenRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_DS);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_DS);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = genDigitalSpecimenBotanyRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_DS_BOTANY);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_DS_BOTANY);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();

    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_HANDLE);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();

    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS));
    });
    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();

    handles.forEach(handle -> {
      requests.add(
          genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS_BOTANY));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS_BOTANY);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests);

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

  private JsonNode givenJsonNode(String id, String type, JsonNode attributes) {
    ObjectNode node = mapper.createObjectNode();
    node.put("id", id);
    node.put("type", type);
    node.set("attributes", attributes);
    return node;
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes();
    String prefix = HANDLE.split("/")[0];
    String suffix = HANDLE.split("/")[1];
    var updateAttributes = genUpdateRequestAltLoc();
    ObjectNode updateRequestNode = mapper.createObjectNode();
    updateRequestNode.set("data", givenJsonNode(HANDLE, RECORD_TYPE_HANDLE, updateAttributes));

    var responseExpected = givenRecordResponseWriteAltLoc(List.of(handle));
    given(service.updateRecords(List.of(updateRequestNode))).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.updateRecord(prefix, suffix, updateRequestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordBadRequest() {

    // Given
    String prefix = HANDLE.split("/")[0];
    String suffix = HANDLE.split("/")[1];
    var updateAttributes = genUpdateRequestAltLoc();
    ObjectNode updateRequestNode = mapper.createObjectNode();
    updateRequestNode.set("data", givenJsonNode(HANDLE_ALT, RECORD_TYPE_HANDLE, updateAttributes));

    // Then
    assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecord(prefix, suffix, updateRequestNode);
    });
  }

  @Test
  void testUpdateRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> updateRequestList = new ArrayList<>();
    var responseExpected = givenRecordResponseWriteAltLoc(handles);

    for (byte[] handle : handles) {
      var updateAttributes = genUpdateRequestAltLoc();
      ObjectNode updateRequestNode = mapper.createObjectNode();
      updateRequestNode.set("data", givenJsonNode(HANDLE, RECORD_TYPE_HANDLE, updateAttributes));
      updateRequestList.add(updateRequestNode.deepCopy());
    }

    given(service.updateRecords(updateRequestList)).willReturn(responseExpected);

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
    String prefix = HANDLE.split("/")[0];
    String suffix = HANDLE.split("/")[1];

    ObjectNode archiveRootNode = mapper.createObjectNode();
    archiveRootNode.set("data", givenJsonNode(HANDLE, RECORD_TYPE_HANDLE,
        mapper.valueToTree(genTombstoneRecordRequestObject())));
    ObjectNode archiveRequestNode = (ObjectNode) archiveRootNode.get(NODE_DATA)
        .get(NODE_ATTRIBUTES);

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle));
    given(service.archiveRecordBatch(List.of(archiveRootNode))).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.archiveRecord(prefix, suffix, archiveRootNode);

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

    List<JsonNode> archiveRequestList = new ArrayList<>();

    for (byte[] handle : handles) {
      ObjectNode archiveRootNode = mapper.createObjectNode();
      archiveRootNode.set("data", givenJsonNode(HANDLE, RECORD_TYPE_HANDLE,
          mapper.valueToTree(genTombstoneRecordRequestObject())));
      archiveRequestList.add(archiveRootNode.deepCopy());
    }
    var responseExpected = givenRecordResponseWriteArchive(handles);

    given(service.archiveRecordBatch(archiveRequestList)).willReturn(responseExpected);

    // When
    var responseReceived = controller.archiveRecords(archiveRequestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBadRequest() {
    // Given
    String prefix = HANDLE.split("/")[0];
    String suffix = HANDLE.split("/")[1];
    var archiveAttributes = genTombstoneRequest();
    ObjectNode archiveRequestNode = mapper.createObjectNode();
    archiveRequestNode.set("data",
        givenJsonNode(HANDLE_ALT, RECORD_TYPE_HANDLE, archiveAttributes));

    // Then
    assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecord(prefix, suffix, archiveRequestNode);
    });
  }

  @Test
  void testCheckRequestNodesPresent() {
    String message = "INVALID INPUT. Missing node \" %s \"";
    String prefix = HANDLE.split("/")[0];
    String suffix = HANDLE.split("/")[1];

    var noData = excludeValue("data");
    var noType = excludeValue("type");
    var noId = excludeValue("id");
    var noAttributes = excludeValue("attributes");

    // Then
    Exception exData = assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecord(prefix, suffix, noData);
    });
    assertThat(exData).hasMessage(String.format(message, "data"));

    Exception exType = assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecord(prefix, suffix, noType);
    });
    assertThat(exType).hasMessage(String.format(message, "type"));

    Exception exId = assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecords(List.of(noId));
    });
    assertThat(exId).hasMessage(String.format(message, "id"));

    Exception exAttribute = assertThrows(InvalidRecordInput.class, () -> {
      controller.updateRecord(prefix, suffix, noAttributes);
    });
    assertThat(exAttribute).hasMessage(String.format(message, "attributes"));
  }

  private JsonNode excludeValue(String val) {

    ObjectNode baseNode = mapper.createObjectNode();
    baseNode.put("type", RECORD_TYPE_HANDLE);
    baseNode.put("id", HANDLE);
    baseNode.set("attributes", genUpdateRequestAltLoc());
    if (val.equals("data")) {
      return baseNode;
    }

    baseNode.remove(val);
    ObjectNode baseNodeRoot = mapper.createObjectNode();
    baseNodeRoot.set("data", baseNode);
    return baseNodeRoot;
  }

  @Test
  void testPiDResolutionException() throws Exception {
    // Given
    DoiRecordRequest request = genDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DOI);
    String message = "123";
    given(service.createRecords(List.of(requestNode))).willThrow(
        new PidResolutionException(message));

    // Then
    Exception exception = assertThrows(PidResolutionException.class, () -> {
      controller.createRecord(requestNode);
    });
    assertThat(exception.getMessage()).isEqualTo(message);
  }
}
