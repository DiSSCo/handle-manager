package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_URI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaObjectAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeAttributeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordFullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseReadSingle;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteGeneric;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSearchByPhysIdRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.component.FdoRecordBuilder;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandleServiceTest {

  private final String SANDBOX_URI = "https://sandbox.dissco.tech/";
  @Mock
  private HandleRepository handleRep;
  @Mock
  private FdoRecordBuilder fdoRecordBuilder;
  @Mock
  private HandleGeneratorService hgService;
  private HandleService service;
  private List<byte[]> handles;
  private MockedStatic<Instant> mockedStatic;

  @BeforeEach
  void setup() {
    service = new HandleService(handleRep, fdoRecordBuilder, hgService, MAPPER);
    initTime();
    initHandleList();
  }

  @AfterEach
  void destroy() {
    mockedStatic.close();
  }

  @Test
  void testResolveSingleRecord() throws Exception {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = SANDBOX_URI + HANDLE;
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle);

    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, "PID",
        genObjectNodeAttributeRecord(recordAttributeList));

    given(handleRep.resolveHandleAttributes(anyList())).willReturn(recordAttributeList);

    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveSingleRecordNotFound() {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = SANDBOX_URI + HANDLE;
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(new ArrayList<>());

    // When
    var exception = assertThrows(PidResolutionException.class, () -> {
      service.resolveSingleRecord(handle, path);
    });
    // Then
    assertThat(exception.getMessage()).contains(HANDLE);
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
  void testSearchByPhysicalSpecimenId() throws Exception {
    // Given
    var expectedAttributes = genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));
    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);

    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList()))
        .willReturn(expectedAttributes);

    // When
    var responseReceived = service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.CETAF,
        SPECIMEN_HOST_TESTVAL);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalSpecimenIdTwoResolution() throws Exception {
    // Given
    var request = givenSearchByPhysIdRequest();
    List<HandleAttribute> attributeList = new ArrayList<>();
    attributeList.addAll(genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8)));
    attributeList.addAll(genDigitalSpecimenAttributes(HANDLE_ALT.getBytes(StandardCharsets.UTF_8)));

    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);

    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList()))
        .willReturn(attributeList);

    // When
    Exception e = assertThrows(PidResolutionException.class, () -> {
      service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, PhysicalIdType.CETAF,
          SPECIMEN_HOST_TESTVAL);
    });

    // Then
    assertThat(e).hasMessage(
        "More than one handle record corresponds to the provided collection facility and physical identifier.");
  }

  @Test
  void testSearchByPhysicalSpecimenIdCombined() throws Exception {
    // Given
    var expectedAttributes = genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));
    var responseExpected = givenRecordResponseWriteGeneric(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), RECORD_TYPE_DS);

    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList()))
        .willReturn(expectedAttributes);

    // When
    var responseReceived = service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.COMBINED,
        SPECIMEN_HOST_TESTVAL);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_HANDLE);
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(handleRecord);
    given(fdoRecordBuilder.prepareHandleRecordAttributes(any(), any())).willReturn(handleRecord);

    // When
    var responseReceived = service.createRecords(Arrays.asList(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DOI);
    List<HandleAttribute> doiRecord = genDoiRecordAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(doiRecord);
    given(fdoRecordBuilder.prepareDoiRecordAttributes(any(), any())).willReturn(doiRecord);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DS);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(digitalSpecimen);
    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList())).willReturn(new ArrayList<>());
    given(fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
        digitalSpecimen);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenSpecimenExists() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList())).willReturn(digitalSpecimen);

    // When
    Exception e = assertThrows(PidCreationException.class, () -> {
      service.createRecords(List.of(request));
    });

    // Then
    assertThat(e).hasMessage(
        "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
            + List.of(new String(handle, StandardCharsets.UTF_8)));
  }

  @Test
  void testCreateDigitalSpecimenBotany() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DS_BOTANY);
    List<HandleAttribute> DigitalSpecimenBotany = genDigitalSpecimenBotanyAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(DigitalSpecimenBotany);
    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList())).willReturn(new ArrayList<>());

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBotanySpecimenExists() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    List<HandleAttribute> digitalSpecimenBotany = genDigitalSpecimenBotanyAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.searchByPhysicalIdentifierFullRecord(anyList())).willReturn(digitalSpecimenBotany);

    // When
    Exception e = assertThrows(PidCreationException.class, () -> {
      service.createRecords(List.of(request));
    });

    // Then
    assertThat(e).hasMessage(
        "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
            + List.of(new String(handle, StandardCharsets.UTF_8)));
  }

  @Test
  void testCreateMediaObjectRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(genMediaRequestObject(),
        RECORD_TYPE_MEDIA);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_MEDIA);
    List<HandleAttribute> mediaObject = genMediaObjectAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(mediaObject);

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
      requests.add(genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
      flatList.addAll(genHandleRecordAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_HANDLE);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

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
      requests.add(genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI));
      flatList.addAll(genDoiRecordAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

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
      var physId = new String(handle) + "a";
      requests.add(
          genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(physId), RECORD_TYPE_DS));
      flatList.addAll(genDigitalSpecimenAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS);
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
      var physId = new String(handle) + "a";
      requests.add(
          genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(physId), RECORD_TYPE_DS_BOTANY));
      flatList.addAll(genDigitalSpecimenBotanyAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS_BOTANY);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMediaObjectBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(genMediaRequestObject(), RECORD_TYPE_MEDIA));
      flatList.addAll(genMediaObjectAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_MEDIA);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

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
    var responseReceived = service.updateRecords(updateRequest, true);

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
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordInternalDuplicates() {

    // Given
    List<byte[]> handles = new ArrayList<>();
    handles.add(HANDLE.getBytes());
    handles.add(HANDLE.getBytes());

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    // Then
    assertThrows(InvalidRequestException.class, () -> {
      service.updateRecords(updateRequest, true);
    });
  }

  @Test
  void testUpdateRecordNonWritable() {
    // Given

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);
    given(handleRep.checkHandlesWritable(anyList())).willReturn(new ArrayList<>());

    // Then
    assertThrows(PidResolutionException.class, () -> {
      service.updateRecords(updateRequest, true);
    });
  }

  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var archiveRequest = genTombstoneRequestBatch(List.of(HANDLE));
    var originalRecord = genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));
    var tombstoneAttributesFull = genTombstoneRecordFullAttributes(handle);

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle));
    var tombstoneAttributes = genTombstoneRecordRequestAttributes(handle);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(tombstoneAttributesFull);
    given(fdoRecordBuilder.prepareTombstoneAttributes(any(), any())).willReturn(
        tombstoneAttributes);

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(handleRep).should()
        .archiveRecords(CREATED.getEpochSecond(), tombstoneAttributes, List.of(HANDLE));
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var handleAlt = HANDLE_ALT.getBytes(StandardCharsets.UTF_8);
    var archiveRequest = genTombstoneRequestBatch(List.of(HANDLE, HANDLE_ALT));

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle, handleAlt));
    var tombstoneAttributesResolved = new ArrayList<>(
        Stream.concat(genTombstoneRecordFullAttributes(HANDLE.getBytes(
                    StandardCharsets.UTF_8)).stream(),
                genTombstoneRecordFullAttributes(HANDLE_ALT.getBytes(StandardCharsets.UTF_8)).stream())
            .toList());

    var tombstoneAttributes = List.of(
        genTombstoneRecordRequestAttributes(handle),
        genTombstoneRecordRequestAttributes(handleAlt)
    );
    var tombstoneFlatlist = Stream.concat(tombstoneAttributes.get(0).stream(),
        tombstoneAttributes.get(1).stream()).toList();

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(tombstoneAttributesResolved);
    given(fdoRecordBuilder.prepareTombstoneAttributes(any(), any())).willReturn(
        tombstoneAttributes.get(0), tombstoneAttributes.get(1));

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(handleRep).should()
        .archiveRecords(CREATED.getEpochSecond(), tombstoneFlatlist, List.of(HANDLE, HANDLE_ALT));
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
    var responseExpectedSecond = service.getHandlesPaged(pageNum, pageSize, pidStatus);

    // Then
    assertThat(responseExpectedFirst).isEqualTo(handles);
    assertThat(responseExpectedSecond).isEqualTo(handles);
  }

  @Test
  void testUpsertDigitalSpecimens() throws Exception {
    // Given
    var existingHandle = handles.get(0);
    var newHandle = handles.get(1);
    List<byte[]> physicalIds = List.of(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, "alt").stream()
        .map(id -> id.getBytes(
            StandardCharsets.UTF_8)).toList();

    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS),
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(
            new String(physicalIds.get(1), StandardCharsets.UTF_8)), RECORD_TYPE_DS));
    var existingRecord = genDigitalSpecimenAttributes(existingHandle);
    var newRecord = genDigitalSpecimenAttributes(newHandle);
    var existingRecordWithStatus = new ArrayList<>(existingRecord);
    existingRecordWithStatus.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS),
        existingHandle, PID_STATUS, PID_STATUS_TESTVAL.getBytes(
        StandardCharsets.UTF_8)));
    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(existingRecordWithStatus, new String(existingHandle, StandardCharsets.UTF_8)),
            upsertedResponse(newRecord, new String(newHandle)))
    );

    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(
        List.of(new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID), existingHandle,
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE, physicalIds.get(0))));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(
        Stream.concat(existingRecord.stream(), newRecord.stream()).toList());
    given(fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
        newRecord);
    given(fdoRecordBuilder.prepareUpdateAttributes(any(), any())).willReturn(
        existingRecord);
    given(hgService.genHandleList(anyInt())).willReturn(new ArrayList<>(List.of(newHandle)));

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), newRecord, List.of(existingRecordWithStatus));
  }

  @Test
  void testInternalDuplicateSpecimenIds() throws Exception {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS),
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS)
    );
    var expectedMsg = "Bad Request. Some PhysicalSpecimenObjectIds are duplicated in request body";
    given(hgService.genHandleList(anyInt())).willReturn(handles);


    // When
    var response = assertThrows(InvalidRequestException.class, () -> service.createRecords(requests));

    // Then
    assertThat(response).hasMessage(expectedMsg);
  }

  @Test
  void testUpsertDigitalSpecimensOnlyUpdate() throws Exception {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
    var existingRecord = genDigitalSpecimenAttributes(handles.get(0));
    var existingRecordWithStatus = new ArrayList<>(existingRecord);
    existingRecordWithStatus.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS),
        handles.get(0), PID_STATUS, PID_STATUS_TESTVAL.getBytes(
        StandardCharsets.UTF_8)));

    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(existingRecordWithStatus, HANDLE))
    );

    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(
        List.of(new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID), handles.get(0),
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(
            StandardCharsets.UTF_8))));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(existingRecord);
    given(fdoRecordBuilder.prepareUpdateAttributes(any(), any())).willReturn(
        existingRecord);
    given(hgService.genHandleList(0)).willReturn(new ArrayList<>());

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), new ArrayList<>(), List.of(existingRecordWithStatus));
  }

  @Test
  void testUpsertDigitalSpecimensOnlyCreate() throws Exception {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
    var newRecord = genDigitalSpecimenAttributes(handles.get(1));
    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(newRecord, HANDLE_ALT))
    );

    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(new ArrayList<>());
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(newRecord);
    given(fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(newRecord);
    given(hgService.genHandleList(anyInt())).willReturn(List.of(handles.get(0)));

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), newRecord, new ArrayList<>());
  }

  @Test
  void testRollbackHandleCreation(){
    // Given
    var handleList = List.of(HANDLE, HANDLE_ALT);

    // When
    service.rollbackHandles(handleList);

    // Then
    then(handleRep).should().rollbackHandles(handleList);
  }

  JsonApiDataLinks upsertedResponse(List<HandleAttribute> handleRecord, String handle)
      throws Exception {
    JsonNode recordAttributes = genObjectNodeAttributeRecord(handleRecord);
    var pidLink = new JsonApiLinks(HANDLE_URI + handle);
    return new JsonApiDataLinks(handle, ObjectType.DIGITAL_SPECIMEN.toString(),
        recordAttributes, pidLink);
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
