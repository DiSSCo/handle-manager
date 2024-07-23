package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SUFFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMedia;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimen;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernelUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenPostRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenReadResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdateRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.component.SchemaValidatorComponent;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.requests.RollbackRequest;
import eu.dissco.core.handlemanager.domain.responses.JsonApiWrapperWrite;
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
  private ApplicationProperties applicationProperties;
  @Mock
  private SchemaValidatorComponent validatorComponent;

  private PidController controller;

  @BeforeEach
  void setup() {
    controller = new PidController(service, applicationProperties, validatorComponent);
  }

  @Test
  void testResolveSingleHandle() throws Exception {
    // Given
    String path = UI_URL + "/" + PREFIX + "/" + SUFFIX;
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI(PREFIX + "/" + SUFFIX);
    var responseExpected = givenReadResponse(List.of(HANDLE), path, FdoType.HANDLE, HANDLE_DOMAIN);

    given(applicationProperties.getUiUrl()).willReturn(UI_URL);
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
    assertThrowsExactly(PidResolutionException.class,
        () -> controller.resolvePid(SUFFIX, SUFFIX, request));
  }

  @Test
  void testSearchByPhysicalId() throws Exception {
    // Given

    var responseExpected = TestUtils.givenWriteResponseFull(List.of(HANDLE),
        FdoType.DIGITAL_SPECIMEN);
    given(service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL)).willReturn(
        responseExpected);

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
    var responseExpected = TestUtils.givenWriteResponseFull(List.of(HANDLE),
        FdoType.DIGITAL_SPECIMEN);
    given(service.searchByPhysicalSpecimenId(physicalId)).willReturn(responseExpected);

    // When
    var responseReceived = controller.searchByPrimarySpecimenObjectId(physicalId);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchHandle() throws Exception {
    // Given
    String path = UI_URL + "/view";
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("view");

    List<String> handleString = List.of(HANDLE, HANDLE_ALT);

    var responseExpected = givenReadResponse(handleString, path, FdoType.HANDLE, HANDLE_DOMAIN);
    given(applicationProperties.getUiUrl()).willReturn(UI_URL);
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
    var requestObject = givenHandleKernel();
    var requestNode = givenPostRequest(requestObject, FdoType.HANDLE);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    var requestObject = givenDoiKernel();
    var requestNode = givenPostRequest(requestObject, FdoType.DOI);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {
    // Given
    var requestObject = givenDigitalSpecimen();
    var requestNode = givenPostRequest(requestObject, FdoType.DIGITAL_SPECIMEN);
    JsonApiWrapperWrite responseExpected = TestUtils.givenWriteResponseFull(List.of(HANDLE),
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
    var requestObject = givenDigitalMedia();
    var requestNode = givenPostRequest(requestObject, FdoType.DIGITAL_MEDIA);

    // When
    var responseReceived = controller.createRecord(requestNode, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void testCreateHandleRecordBatch() throws Exception {
    // Given
    var requests = List.of(givenPostRequest(givenHandleKernel(), FdoType.HANDLE));

    var responseExpected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.HANDLE);
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
    var requests = List.of(givenPostRequest(givenDoiKernel(), FdoType.DOI));

    // When
    var responseReceived = controller.createRecords(requests, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    var request = givenUpdateRequest();

    // When
    var result = controller.updateRecord(PREFIX, SUFFIX, request.get(0), authentication);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    then(service).should().updateRecords(request, true);
  }

  @Test
  void testUpdateRecordBadRequest() {
    // Given
    var request = givenUpdateRequest(List.of(HANDLE_ALT), FdoType.HANDLE,
        MAPPER.valueToTree(givenHandleKernelUpdated())).get(0);

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
  void testRollbackUpdated() throws Exception {
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
    var archiveRequest = givenTombstoneRequest();
    given(service.tombstoneRecords(List.of(archiveRequest))).willReturn(responseExpected);

    // When
    var responseReceived = controller.archiveRecord(PREFIX, SUFFIX, archiveRequest, authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseReceived.getBody()).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBadHandle() {
    // Given
    var archiveRequest = givenTombstoneRequest();

    // When
    assertThrowsExactly(InvalidRequestException.class,
        () -> controller.archiveRecord(PREFIX, "123", archiveRequest, authentication));
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given

    // When
    var responseReceived = controller.archiveRecords(List.of(givenTombstoneRequest()),
        authentication);

    // Then
    assertThat(responseReceived.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testPidResolutionException() throws Exception {
    // Given
    var request = givenDoiKernel();
    var requestNode = givenPostRequest(request, FdoType.DOI);
    String message = "123";
    given(service.createRecords(List.of(requestNode))).willThrow(
        new PidResolutionException(message));

    // Then
    Exception exception = assertThrowsExactly(PidResolutionException.class,
        () -> controller.createRecord(requestNode, authentication));
    assertThat(exception.getMessage()).isEqualTo(message);
  }
}
