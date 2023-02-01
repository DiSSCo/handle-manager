package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.IN_COLLECTION_FACILITY_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRESERVED_OR_LIVING;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME_REQ;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PTR_HANDLE_RECORD;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordFullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Arrays;
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

@Slf4j
@ExtendWith(MockitoExtension.class)
class HandleServiceTest {

  @Mock
  private HandleRepository handleRep;

  @Mock
  private PidTypeService pidTypeService;

  @Mock
  private HandleGeneratorService hgService;

  private ObjectMapper mapper;

  private HandleService service;

  private List<byte[]> handles;

  private MockedStatic<Instant> mockedStatic;
  private final String SANDBOX_URI = "https://sandbox.dissco.tech/";


  @BeforeEach
  void setup() throws Exception {
    mapper = new ObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();

    service = new HandleService(handleRep, pidTypeService, hgService,
        documentBuilderFactory, mapper, transformerFactory);
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
    String path = SANDBOX_URI + HANDLE;
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle);

    var responseExpected = givenRecordResponseRead(List.of(handle), path, "PID");

    given(handleRep.resolveHandleAttributes(handle)).willReturn(recordAttributeList);

    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    String path = SANDBOX_URI;
    List<HandleAttribute> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handles) {
      repositoryResponse.addAll(genHandleRecordAttributes(handle));
    }

    given(handleRep.resolveHandleAttributes(anyList())).willReturn(repositoryResponse);

    var responseExpected = givenRecordResponseRead(handles, path, "PID");
    // When
    var responseReceived = service.resolveBatchRecord(handles, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_HANDLE);
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(handleRecord);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(Arrays.asList(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DOI);
    List<HandleAttribute> DoiRecord = genDoiRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(DoiRecord);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DS);
    List<HandleAttribute> DigitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(DigitalSpecimen);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DS_BOTANY);
    List<HandleAttribute> DigitalSpecimenBotany = genDigitalSpecimenBotanyAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(DigitalSpecimenBotany);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecordBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
      flatList.addAll(genHandleRecordAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_HANDLE);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecordBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI));
      flatList.addAll(genDoiRecordAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS));
      flatList.addAll(genDigitalSpecimenAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatch() throws Exception {
    // Given

    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(
          genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY));
      flatList.addAll(genDigitalSpecimenBotanyAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS_BOTANY);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);
    given(pidTypeService.resolveTypePid(any(String.class))).willReturn(PTR_HANDLE_RECORD);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordLocation() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var updateRequest = genUpdateRequestBatch(List.of(handle));
    var updatedAttributeRecord = genHandleRecordAttributesAltLoc(handle);
    var responseExpected = givenRecordResponseWriteAltLoc(List.of(handle));

    given(handleRep.checkHandlesWritable(anyList())).willReturn(List.of(handle));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(updatedAttributeRecord);

    // When
    var responseReceived = service.updateRecords(updateRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordLocationBatch() throws Exception {
    // Given

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    List<HandleAttribute> updatedAttributeRecord = new ArrayList<>();
    for (byte[] handle : handles) {
      updatedAttributeRecord.addAll(genHandleRecordAttributesAltLoc(handle));
    }

    var responseExpected = givenRecordResponseWriteAltLoc(handles);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(updatedAttributeRecord);

    // When
    var responseReceived = service.updateRecords(updateRequest);

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
      service.updateRecords(updateRequest);
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
      service.updateRecords(List.of(requestRoot));
    });
  }

  @Test
  void testUpdateRecordNonWritable() {
    // Given

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);
    given(handleRep.checkHandlesWritable(anyList())).willReturn(new ArrayList<>());

    // Then
    assertThrows(PidResolutionException.class, () -> {
      service.updateRecords(updateRequest);
    });
  }

  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var archiveRequest = genTombstoneRequestBatch(List.of(HANDLE));
    var tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle));

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(tombstoneAttributesFull);
    log.info(archiveRequest.toString());

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    List<String> handlesString = handles.stream().map(e -> new String(e, StandardCharsets.UTF_8)).toList();
    var archiveRequest = genTombstoneRequestBatch(handlesString);

    List<HandleAttribute> flatList = new ArrayList<>();
    for (byte[] handle : handles) {
      flatList.addAll(genTombstoneRecordFullAttributes(handle));
    }

    var responseExpected = givenRecordResponseWriteArchive(handles);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
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

  @Test
  void testInvalidInputExceptionCreateHandleRecord() {
    // Given
    String invalidType = "INVALID TYPE";
    String invalidMessage = "Invalid request. Reason: unrecognized type. Check: " + invalidType;

    HandleRecordRequest request = genHandleRecordRequestObject();
    ObjectNode requestNode = genCreateRecordRequest(request, invalidType);

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(List.of(requestNode));
    });

    // Then
    assertThat(exception.getMessage()).isEqualTo(invalidMessage);
  }

  @Test
  void testInvalidInputExceptionCreateHandleRecordBatch() {
    // Given
    String invalidType = "INVALID TYPE";
    String invalidMessage = "Invalid request. Reason: unrecognized type. Check: " + invalidType;

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genHandleRecordRequestObject(), invalidType));
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(requests);
    });

    // Then
    assertThat(exception.getMessage()).isEqualTo(invalidMessage);
  }

  @Test
  void testMissingFieldCreateHandleRecord() {

    HandleRecordRequest request = genHandleRecordRequestObject();
    ObjectNode requestObjectNode = genCreateRecordRequest(request, RECORD_TYPE_HANDLE);

    ((ObjectNode) requestObjectNode.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(PID_ISSUER_REQ);

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(List.of(requestObjectNode));
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

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(List.of(requestObjectNode));
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

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(List.of(requestObjectNode));
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

    given(hgService.genHandleList(1)).willReturn(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(List.of(requestObjectNode));
    });

    // Then
    assertThat(exception.getMessage()).contains(PRESERVED_OR_LIVING);
  }

  @Test
  void testMissingFieldCreateHandleRecordBatch() {

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
      service.createRecords(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(PID_ISSUER_REQ);
  }

  @Test
  void testMissingFieldDoiRecordBatch() {

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      ObjectNode request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
      ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(REFERENT_DOI_NAME_REQ);
      requests.add(request);
    }

    given(hgService.genHandleList(2)).willReturn(handles);

    // When
    Exception exception = assertThrows(InvalidRecordInput.class, () -> {
      service.createRecords(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(REFERENT_DOI_NAME_REQ);
  }

  @Test
  void testMissingFieldDigitalSpecimenRecordBatch() {

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
      service.createRecords(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(IN_COLLECTION_FACILITY_REQ);
  }
  @Test
  void testMissingFieldDigitalSpecimenBotanyRecordBatch() {

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
      service.createRecords(requests);
    });

    // Then
    assertThat(exception.getMessage()).contains(PRESERVED_OR_LIVING);
  }

  private void initTime() {
    Clock clock = Clock.fixed(CREATED, ZoneOffset.UTC);
    Instant instant = Instant.now(clock);
    mockedStatic = mockStatic(Instant.class);
    mockedStatic.when(Instant::now).thenReturn(instant);
    mockedStatic.when(() -> Instant.from(any())).thenReturn(instant);
  }

  private List<byte[]> initHandleList() {
    handles = new ArrayList<>();
    handles.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handles.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    return handles;
  }
}
