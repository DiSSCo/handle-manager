package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SUFFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenReadResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdateRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.JsonNode;
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
import eu.dissco.core.handlemanager.testUtils.TestUtils;
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
  @Mock
  private ApplicationProperties applicationProperties;

  @BeforeEach
  void setup() {
    controller = new PidController(service, schemaValidator, applicationProperties);
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    String path = SANDBOX_URI + PREFIX + "/" + SUFFIX;
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(PREFIX + "/" + SUFFIX);
    var responseExpected = givenReadResponse(List.of(HANDLE), path, FdoType.HANDLE,
        HANDLE_DOMAIN);

    given(applicationProperties.getUiUrl()).willReturn(SANDBOX_URI);
    given(service.resolveSingleRecord(HANDLE, path)).willReturn(responseExpected);
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

    var responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE), FdoType.DIGITAL_SPECIMEN);
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
    var responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE), FdoType.DIGITAL_SPECIMEN);
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

    var responseExpected = givenReadResponse(handleString, path, FdoType.HANDLE,
        HANDLE_DOMAIN);
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
    HandleRecordRequest requestObject = givenHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.HANDLE);
    JsonApiWrapperWrite responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE),
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
    HandleRecordRequest requestObject = givenDoiRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.DOI);
    JsonApiWrapperWrite responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE),
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
    DigitalSpecimenRequest requestObject = givenDigitalSpecimenRequestObjectNullOptionals();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.DIGITAL_SPECIMEN);
    JsonApiWrapperWrite responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE),
        FdoType.DIGITAL_SPECIMEN);

    given(service.createRecords(List.of(requestNode))).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalMediaRecord() throws Exception {
    // Given
    HandleRecordRequest requestObject = givenDigitalMediaRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(requestObject, FdoType.DIGITAL_MEDIA);
    JsonApiWrapperWrite responseExpected = TestUtils.givenWriteResponseFull(
        List.of(HANDLE),
        FdoType.DIGITAL_MEDIA);

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
    var handles = List.of(HANDLE, HANDLE_ALT);

    List<JsonNode> requests = new ArrayList<>();

    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE)));

    var responseExpected = TestUtils.givenWriteResponseFull(handles, FdoType.HANDLE);
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
    var handles = List.of(HANDLE, HANDLE_ALT);

    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(
        handle -> requests.add(genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI)));

    var responseExpected = TestUtils.givenWriteResponseFull(handles, FdoType.DOI);
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle ->
        requests.add(
            genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
                FdoType.DIGITAL_SPECIMEN))
    );
    var responseExpected = TestUtils.givenWriteResponseFull(handles,
        FdoType.DIGITAL_SPECIMEN);
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenDigitalMediaRequestObject(), FdoType.DIGITAL_MEDIA));
    }
    var responseExpected = TestUtils.givenWriteResponseFull(handles, FdoType.DOI);
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenSourceSystemRequestObject(), FdoType.SOURCE_SYSTEM)));

    var responseExpected = TestUtils.givenWriteResponseFull(handles,
        FdoType.SOURCE_SYSTEM);
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenAnnotationRequestObject(), FdoType.ANNOTATION)));
    var responseExpected = TestUtils.givenWriteResponseFull(handles,
        FdoType.ANNOTATION);
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> requests = new ArrayList<>();
    handles.forEach(handle -> requests.add(
        genCreateRecordRequest(givenDataMappingRequestObject(), FdoType.DATA_MAPPING)));

    var responseExpected = TestUtils.givenWriteResponseFull(handles,
        FdoType.DATA_MAPPING);
    given(service.createRecords(requests)).willReturn(responseExpected);

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    var request = givenUpdateRequest();

    // When
    var result = controller.updateRecord(PREFIX, SUFFIX, request.get(0),
        authentication);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    then(service).should().updateRecords(request, true);
  }

  @Test
  void testUpdateRecordBadRequest() {
    // Given
    var request = MAPPER.createObjectNode()
        .set(NODE_DATA, MAPPER.createObjectNode()
            .put(NODE_TYPE, FdoType.HANDLE.getDigitalObjectType())
            .put(NODE_ID, HANDLE_ALT)
            .set(NODE_ATTRIBUTES, MAPPER.valueToTree(givenHandleRecordRequestObjectUpdate())));

    // Then
    assertThrowsExactly(InvalidRequestException.class,
        () -> controller.updateRecord(PREFIX, SUFFIX, request, authentication));
  }

  @Test
  void testUpdateRecordBatch() throws Exception {
    // Given
    var request = givenUpdateRequest();

    // When
    var responseReceived = controller.updateRecords(request, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    then(service).should().updateRecords(request, true);
  }

  @Test
  void testRollbackUpdate() throws Exception {
    // Given
    var request = givenUpdateRequest();

    // When
    var responseReceived = controller.rollbackHandleUpdate(request, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    then(service).should().updateRecords(request, false);
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
    var responseExpected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.TOMBSTONE);
    var archiveRequest = givenArchiveRequest();
    given(service.tombstoneRecords(List.of(archiveRequest))).willReturn(
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
    var handles = List.of(HANDLE, HANDLE_ALT);
    List<JsonNode> archiveRequestList = new ArrayList<>();
    handles.forEach(h -> archiveRequestList.add(givenArchiveRequest()));
    var responseExpected = TestUtils.givenWriteResponseFull(handles, FdoType.TOMBSTONE);
    given(service.tombstoneRecords(archiveRequestList)).willReturn(responseExpected);

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
    archiveRequestData.set(NODE_ATTRIBUTES,
        MAPPER.valueToTree(givenTombstoneRecordRequestObject()));
    archiveRequest.set(NODE_DATA, archiveRequestData);
    return archiveRequest;
  }

  @Test
  void testPidResolutionException() throws Exception {
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
