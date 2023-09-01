package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ANNOTATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAPPING;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SOURCE_SYSTEM_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SUFFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseReadSingle;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteGeneric;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.RollbackRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PrimaryObjectIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.service.HandleService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class HandleControllerTest {

  @Mock
  private HandleService service;

  @Mock
  private Authentication authentication;

  @Mock
  private JsonSchemaValidator schemaValidator;

  private HandleController controller;

  private final String SANDBOX_URI = "https://sandbox.dissco.tech";

  public ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
  @Mock
  private ApplicationProperties applicationProperties;

  @BeforeEach
  void setup() {
    controller = new HandleController(service, schemaValidator, applicationProperties);
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    String path = SANDBOX_URI + PREFIX + "/" + SUFFIX;
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(PREFIX + "/" + SUFFIX);
    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, RECORD_TYPE_HANDLE, null);

    given(applicationProperties.getUiUrl()).willReturn(SANDBOX_URI);
    given(service.resolveSingleRecord(handle, path)).willReturn(responseExpected);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);

    // When
    var responseReceived = controller.resolvePid(PREFIX, SUFFIX, r);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolvePidBadPrefix() {
    assertThrows(PidResolutionException.class, () ->
        controller.resolvePid(SUFFIX, SUFFIX, new MockHttpServletRequest()));
  }

  @Test
  void testSearchByPhysicalId() throws Exception {
    // Given

    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);
    given(
        service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
            PrimaryObjectIdType.LOCAL,
            SOURCE_SYSTEM_TESTVAL)).willReturn(responseExpected);

    // When
    var responseReceived = controller.searchByPrimarySpecimenObjectId(
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimaryObjectIdType.LOCAL, SOURCE_SYSTEM_TESTVAL);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalIdCombined() throws Exception {
    // Given
    String physicalId = PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
    var physicalIdType = PrimaryObjectIdType.LOCAL;
    String specimenHostPid = SPECIMEN_HOST_TESTVAL;
    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);
    given(
        service.searchByPhysicalSpecimenId(physicalId, physicalIdType, specimenHostPid)).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.searchByPrimarySpecimenObjectId(physicalId, physicalIdType,
        specimenHostPid);

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
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    var responseExpected = givenRecordResponseRead(handles, path, RECORD_TYPE_HANDLE);
    given(applicationProperties.getUiUrl()).willReturn(SANDBOX_URI);
    given(applicationProperties.getMaxHandles()).willReturn(1000);
    given(service.resolveBatchRecord(anyList(), eq(path))).willReturn(responseExpected);

    // When
    var responseReceived = controller.resolvePids(handleString, r);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandleExceedsMax() {
    // Given
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("view");
    int maxHandles = 200;
    List<String> handleString = new ArrayList<>();
    for (int i = 0; i <= maxHandles; i++) {
      handleString.add(String.valueOf(i));
    }
    given(applicationProperties.getMaxHandles()).willReturn(maxHandles);

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.resolvePids(handleString, r);
    });

    // Then
    assertThat(e.getMessage()).contains(String.valueOf(maxHandles));
  }

  // Single Handle Record Creation
  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = givenHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_HANDLE);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_HANDLE);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = givenDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_DOI);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_DOI);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    DigitalSpecimenRequest requestObject = givenDigitalSpecimenRequestObjectNullOptionals();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_DS);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_DS);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMediaObjectRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = givenMediaRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, RECORD_TYPE_MEDIA);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        RECORD_TYPE_MEDIA);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

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
      requests.add(genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_HANDLE);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

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
      requests.add(genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

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
      requests.add(
          genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
    });
    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

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
    for (var handle : handles) {
      requests.add(genCreateRecordRequest(givenMediaRequestObject(), RECORD_TYPE_MEDIA));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateSourceSystemsBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> {
      requests.add(
          genCreateRecordRequest(givenSourceSystemRequestObject(), RECORD_TYPE_SOURCE_SYSTEM));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_SOURCE_SYSTEM);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateAnnotationsBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(givenAnnotationRequestObject(), RECORD_TYPE_ANNOTATION));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_ANNOTATION);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMappingBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> {
      requests.add(genCreateRecordRequest(givenMappingRequestObject(), RECORD_TYPE_MAPPING));
    });

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_MAPPING);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  private JsonNode givenJsonNode(String id, String type, JsonNode attributes) {
    ObjectNode node = mapper.createObjectNode();
    node.put(NODE_ID, id);
    node.put(NODE_TYPE, type);
    node.set(NODE_ATTRIBUTES, attributes);
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
    given(service.updateRecords(List.of(updateRequestNode), true)).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.updateRecord(PREFIX, SUFFIX, updateRequestNode,
        authentication);

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
      controller.updateRecord(PREFIX, SUFFIX, updateRequestNode, authentication);
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

    given(service.updateRecords(updateRequestList, true)).willReturn(responseExpected);

    // When
    var responseReceived = controller.updateRecords(updateRequestList, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testRollbackUpdate() throws Exception {
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

    given(service.updateRecords(updateRequestList, false)).willReturn(responseExpected);

    // When
    var responseReceived = controller.rollbackHandleUpdate(updateRequestList, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testRollbackHandles() throws Exception {
    // Given
    var dataNode1 = MAPPER.createObjectNode();
    dataNode1.put("id", HANDLE);
    var dataNode2 = MAPPER.createObjectNode();
    dataNode2.put("id", HANDLE_ALT);
    List<JsonNode> dataNode = List.of(dataNode1, dataNode2);
    var request = new RollbackRequest(dataNode);
    given(authentication.getName()).willReturn("name");

    // When
    controller.rollbackHandleCreation(request, authentication);

    // Then
    then(service).should().rollbackHandles(List.of(HANDLE, HANDLE_ALT));
  }

  @Test
  void testRollbackHandlesByPhysId() {
    // Given
    given(authentication.getName()).willReturn("name");
    var physIds = List.of("a", "b");

    // When
    controller.rollbackHandlePhysId(physIds, authentication);

    //Then
    then(service).should().rollbackHandlesFromPhysId(physIds);
  }

  @Test
  void testRollbackHandlesBadRequest() {
    // Given
    List<JsonNode> dataNode = List.of(MAPPER.createObjectNode());
    var request = new RollbackRequest(dataNode);

    // Then
    assertThrows(InvalidRequestException.class,
        () -> controller.rollbackHandleCreation(request, authentication));
  }


  @Test
  void testUpsert() throws Exception {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);

    // When
    var response = controller.upsertRecord(List.of(request), authentication);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testUpsertBadType() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_MAPPING);

    // Then
    assertThrows(InvalidRequestException.class, () -> controller.upsertRecord(List.of(request),
        authentication));
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
    var responseReceived = controller.archiveRecord(PREFIX, SUFFIX, archiveRequest, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBadHandle() {
    // Given
    var archiveRequest = givenArchiveRequest();

    // When
    assertThrows(InvalidRequestException.class,
        () -> controller.archiveRecord(PREFIX, "123", archiveRequest,
            authentication));
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
    var responseReceived = controller.archiveRecords(archiveRequestList, authentication);

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
      controller.updateRecord(PREFIX, SUFFIX, archiveRequestNode, authentication);
    });
  }

  @Test
  void testPiDResolutionException() throws Exception {
    // Given
    DoiRecordRequest request = givenDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, RECORD_TYPE_DOI);
    String message = "123";
    given(service.createRecords(List.of(requestNode))).willThrow(
        new PidResolutionException(message));

    // Then
    Exception exception = assertThrows(PidResolutionException.class, () -> {
      controller.createRecord(requestNode, authentication);
    });
    assertThat(exception.getMessage()).isEqualTo(message);
  }
}
