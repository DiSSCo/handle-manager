package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.domain.PidRecords.VALID_PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PHYSICAL_IDENTIFIER_LOCAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SUFFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteGeneric;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSearchByPhysIdRequest;
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
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaStaticContextInitializer;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class HandleControllerTest {

  @Mock
  private HandleService service;
  @Autowired
  JsonSchemaStaticContextInitializer schemaInitializer;

  private HandleController controller;

  private final String SANDBOX_URI = "https://sandbox.dissco.tech/";

  public ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  @BeforeEach
  void setup() {
    controller = new HandleController(service, schemaInitializer);
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

    var exception = assertThrowsExactly(InvalidRequestException.class,
        () -> controller.getAllHandlesByPidStatus(pageNum, pageSize, pidStatus));

    // Then
    assertThat(exception).hasMessage(
        "Invalid Input. Pid Status not recognized. Available Pid Statuses: " + VALID_PID_STATUS);
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    String path = SANDBOX_URI + PREFIX + "/" + SUFFIX;
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(PREFIX + "/" + SUFFIX);

    JsonApiWrapperRead responseExpected = givenRecordResponseRead(List.of(handle), path,
        RECORD_TYPE_HANDLE);
    given(service.resolveSingleRecord(handle, path)).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolvePid(PREFIX, SUFFIX, r);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalId() throws Exception {
    // Given

    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);
    given(service.searchByPhysicalSpecimenId(PHYSICAL_IDENTIFIER_LOCAL, PhysicalIdType.CETAF,
        SPECIMEN_HOST_PID)).willReturn(responseExpected);

    // When
    var responseReceived = controller.searchByPhysicalSpecimenId(PHYSICAL_IDENTIFIER_LOCAL, PhysicalIdType.CETAF,
        SPECIMEN_HOST_PID);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalIdCombined() throws Exception {
    // Given
    String physicalId = PHYSICAL_IDENTIFIER_LOCAL;
    var physicalIdType = PhysicalIdType.COMBINED;
    String specimenHostPid = SPECIMEN_HOST_PID;
    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);
    given(service.searchByPhysicalSpecimenId(physicalId, physicalIdType, specimenHostPid)).willReturn(responseExpected);

    // When
    var responseReceived = controller.searchByPhysicalSpecimenId(physicalId, physicalIdType, specimenHostPid);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandle() throws Exception {
    // Given
    String path = SANDBOX_URI + "view";
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("view");

    List<String> handleString = List.of(HANDLE, HANDLE_ALT);
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8), HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    var responseExpected = givenRecordResponseRead(handles, path, RECORD_TYPE_HANDLE);
    given(service.resolveBatchRecord(anyList(), eq(path))).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolvePids(handleString, r);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandleExceedsMax() throws Exception {
    // Given
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("view");
    int maxHandles = 200;
    List<String> handleString = new ArrayList<>();
    for (int i = 0; i<= maxHandles; i++){
      handleString.add(String.valueOf(i));
    }

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.resolvePids(handleString, r);
    });

    // Then
    assertThat(e.getMessage()).contains(String.valueOf(maxHandles));
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
    DigitalSpecimenRequest requestObject = genDigitalSpecimenRequestObject();
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
  void testCreateMediaObjectRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = genMediaRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_MEDIA);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_MEDIA);

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
          genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY));
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
  void testCreateMediaRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(genMediaRequestObject(), RECORD_TYPE_MEDIA));
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
    var updateAttributes = genUpdateRequestAltLoc();
    ObjectNode updateRequestNode = mapper.createObjectNode();
    updateRequestNode.set(NODE_DATA, givenJsonNode(HANDLE, RECORD_TYPE_HANDLE, updateAttributes));

    var responseExpected = givenRecordResponseWriteAltLoc(List.of(handle));
    given(service.updateRecords(List.of(updateRequestNode))).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.updateRecord(PREFIX, SUFFIX, updateRequestNode);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordBadRequest() {

    // Given
    var updateAttributes = genUpdateRequestAltLoc();
    ObjectNode updateRequestNode = mapper.createObjectNode();
    updateRequestNode.set("data", givenJsonNode(HANDLE_ALT, RECORD_TYPE_HANDLE, updateAttributes));

    // Then
    assertThrows(InvalidRequestException.class, () -> {
      controller.updateRecord(PREFIX, SUFFIX, updateRequestNode);
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

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle));
    var archiveRequest = givenArchiveRequest();
    given(service.archiveRecordBatch(List.of(archiveRequest))).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.archiveRecord(PREFIX, SUFFIX, archiveRequest);

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
    handles.forEach(h -> archiveRequestList.add(givenArchiveRequest()));
    var responseExpected = givenRecordResponseWriteArchive(handles);
    given(service.archiveRecordBatch(archiveRequestList)).willReturn(responseExpected);

    // When
    var responseReceived = controller.archiveRecords(archiveRequestList);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  private JsonNode givenArchiveRequest() {
    ObjectNode archiveRequest = MAPPER.createObjectNode();
    ObjectNode archiveRequestData = MAPPER.createObjectNode();
    archiveRequestData.put(NODE_ID, HANDLE);
    archiveRequestData.set(NODE_ATTRIBUTES, MAPPER.valueToTree(genTombstoneRecordRequestObject()));
    archiveRequest.set(NODE_DATA, archiveRequestData);
    return archiveRequest;
  }


  @Test
  void testArchiveRecordBadRequest() {
    // Given
    var archiveAttributes = genTombstoneRequest();
    ObjectNode archiveRequestNode = mapper.createObjectNode();
    archiveRequestNode.set("data",
        givenJsonNode(HANDLE_ALT, RECORD_TYPE_HANDLE, archiveAttributes));

    // Then
    assertThrows(InvalidRequestException.class, () -> {
      controller.updateRecord(PREFIX, SUFFIX, archiveRequestNode);
    });
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
