package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
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
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.requests.RollbackRequest;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.service.PidService;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = Profiles.HANDLE)
class PidControllerTest {

  @Mock
  private PidService service;

  @Mock
  private Authentication authentication;

  @Mock
  private JsonSchemaValidator schemaValidator;

  private PidController controller;

  private final String SANDBOX_URI = "https://sandbox.dissco.tech";

  public ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
  @Mock
  private ApplicationProperties applicationProperties;

  @BeforeEach
  void setup() {
    controller = new PidController(service, schemaValidator, applicationProperties);
  }

  @Test
  void testResolveSingleHandle() throws PidResolutionException {
    // Given
    String path = SANDBOX_URI + PREFIX + "/" + SUFFIX;
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(PREFIX + "/" + SUFFIX);
    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, FdoType.HANDLE, null);

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
    var request = new MockHttpServletRequest();
    assertThrowsExactly(PidResolutionException.class, () ->
        controller.resolvePid(SUFFIX, SUFFIX, request));
  }

  @Test
  void testSearchByPhysicalId() throws Exception {
    // Given

    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), FdoType.DIGITAL_SPECIMEN);
    given(
        service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL
        )).willReturn(responseExpected);

    // When
    var responseReceived = controller.searchByPrimarySpecimenObjectId(
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalIdCombined() throws Exception {
    // Given
    String physicalId = PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), FdoType.DIGITAL_SPECIMEN);
    given(
        service.searchByPhysicalSpecimenId(physicalId)).willReturn(
        responseExpected);

    // When
    var responseReceived = controller.searchByPrimarySpecimenObjectId(physicalId);

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

    var responseExpected = givenRecordResponseRead(handles, path, FdoType.HANDLE);
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
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> controller.resolvePids(handleString, r));

    // Then
    assertThat(e.getMessage()).contains(String.valueOf(maxHandles));
  }

  // Single Handle Record Creation
  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    HandleRecordRequest requestObject = givenHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.HANDLE);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        FdoType.HANDLE);

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
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.DOI);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        FdoType.DOI);

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
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.DIGITAL_SPECIMEN);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        FdoType.DIGITAL_SPECIMEN);

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
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.MEDIA_OBJECT);
    JsonApiWrapperWrite responseExpected = givenRecordResponseWrite(List.of(handle),
        FdoType.MEDIA_OBJECT);

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

    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE)));

    var responseExpected = givenRecordResponseWrite(handles, FdoType.HANDLE);
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
    handles.forEach(
        handle -> requests.add(genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI)));

    var responseExpected = givenRecordResponseWrite(handles, FdoType.DOI);
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

    handles.forEach(handle ->
        requests.add(
            genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
                FdoType.DIGITAL_SPECIMEN))
    );
    var responseExpected = givenRecordResponseWrite(handles, FdoType.DIGITAL_SPECIMEN);
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
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenMediaRequestObject(), FdoType.MEDIA_OBJECT));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.DOI);
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
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenSourceSystemRequestObject(), FdoType.SOURCE_SYSTEM)));

    var responseExpected = givenRecordResponseWrite(handles, FdoType.SOURCE_SYSTEM);
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
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenAnnotationRequestObject(), FdoType.ANNOTATION)));

    var responseExpected = givenRecordResponseWrite(handles, FdoType.ANNOTATION);
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
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenMappingRequestObject(), FdoType.MAPPING)));

    var responseExpected = givenRecordResponseWrite(handles, FdoType.MAPPING);
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
    updateRequestNode.set(NODE_DATA,
        givenJsonNode(HANDLE, FdoType.HANDLE.getDigitalObjectType(), updateAttributes));

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
    updateRequestNode.set("data",
        givenJsonNode(HANDLE_ALT, FdoType.HANDLE.getDigitalObjectType(), updateAttributes));

    // Then
    assertThrowsExactly(InvalidRequestException.class,
        () -> controller.updateRecord(PREFIX, SUFFIX, updateRequestNode, authentication));
  }

  @Test
  void testUpdateRecordBatch() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<JsonNode> updateRequestList = new ArrayList<>();
    var responseExpected = givenRecordResponseWriteAltLoc(handles);
    handles.forEach(h -> {
      var updateAttributes = genUpdateRequestAltLoc();
      ObjectNode updateRequestNode = mapper.createObjectNode();
      updateRequestNode.set("data",
          givenJsonNode(HANDLE, FdoType.HANDLE.getDigitalObjectType(), updateAttributes));
      updateRequestList.add(updateRequestNode.deepCopy());
    });
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
    handles.forEach(h -> {
      var updateAttributes = genUpdateRequestAltLoc();
      ObjectNode updateRequestNode = mapper.createObjectNode();
      updateRequestNode.set("data",
          givenJsonNode(HANDLE, FdoType.HANDLE.getDigitalObjectType(), updateAttributes));
      updateRequestList.add(updateRequestNode.deepCopy());
    });

    given(service.updateRecords(updateRequestList, false)).willReturn(responseExpected);

    // When
    var responseReceived = controller.rollbackHandleUpdate(updateRequestList, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testRollbackHandles() throws InvalidRequestException {
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
    assertThrowsExactly(InvalidRequestException.class,
        () -> controller.rollbackHandleCreation(request, authentication));
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
    assertThrowsExactly(InvalidRequestException.class,
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
        givenJsonNode(HANDLE_ALT, FdoType.HANDLE.getDigitalObjectType(), archiveAttributes));

    // Then
    assertThrowsExactly(InvalidRequestException.class,
        () -> controller.updateRecord(PREFIX, SUFFIX, archiveRequestNode, authentication));
  }

  @Test
  void testPiDResolutionException() throws Exception {
    // Given
    DoiRecordRequest request = givenDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, FdoType.DOI);
    String message = "123";
    given(service.createRecords(List.of(requestNode))).willThrow(
        new PidResolutionException(message));

    // Then
    Exception exception = assertThrowsExactly(PidResolutionException.class,
        () -> controller.createRecord(requestNode, authentication));
    assertThat(exception.getMessage()).isEqualTo(message);
  }
}
