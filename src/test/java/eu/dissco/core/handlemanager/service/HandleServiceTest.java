package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_URI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ANNOTATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAPPING;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ORGANISATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_IDENTIFIER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genAnnotationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMappingAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMasAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaObjectAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeAttributeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genOrganisationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genSourceSystemAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseReadSingle;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteGeneric;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSearchByPhysIdRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static eu.dissco.core.handlemanager.utils.AdminHandleGenerator.genAdminHandle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
  private FdoRecordService fdoRecordService;
  @Mock
  private HandleGeneratorService hgService;
  private HandleService service;
  private List<byte[]> handles;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    service = new HandleService(handleRep, fdoRecordService, hgService, MAPPER);
    initTime();
    initHandleList();
  }

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }

  @Test
  void testResolveSingleRecord() throws Exception {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = SANDBOX_URI + HANDLE;
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle,
        ObjectType.HANDLE);

    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, "PID",
        genObjectNodeAttributeRecord(recordAttributeList));

    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(recordAttributeList);

    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testRemoveHsAdmin() throws Exception {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = SANDBOX_URI + HANDLE;
    var adminHandle = new HandleAttribute(HS_ADMIN.index(), handle, HS_ADMIN.get(),
        genAdminHandle());
    var recordAttributeList = genHandleRecordAttributes(handle,
        ObjectType.HANDLE);
    recordAttributeList.add(adminHandle);

    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, "PID",
        genObjectNodeAttributeRecord(recordAttributeList));

    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(recordAttributeList);
    
    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveSingleRecordNotFound() {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = SANDBOX_URI + HANDLE;
    given(handleRep.resolveHandleAttributes(any(byte[].class))).willReturn(new ArrayList<>());

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
      repositoryResponse.addAll(genHandleRecordAttributes(handle, ObjectType.HANDLE));
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

    given(handleRep.searchByNormalisedPhysicalIdentifierFullRecord(anyList()))
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

    given(handleRep.searchByNormalisedPhysicalIdentifierFullRecord(anyList()))
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

    given(handleRep.searchByNormalisedPhysicalIdentifierFullRecord(anyList()))
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
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle, ObjectType.HANDLE);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareHandleRecordAttributes(any(), any(),
        eq(ObjectType.HANDLE))).willReturn(handleRecord);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DOI);
    List<HandleAttribute> doiRecord = genDoiRecordAttributes(handle, ObjectType.HANDLE);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDoiRecordAttributes(any(), any(), eq(ObjectType.DOI))).willReturn(
        doiRecord);

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
    given(handleRep.searchByNormalisedPhysicalIdentifierFullRecord(anyList())).willReturn(
        new ArrayList<>());
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any(), any())).willReturn(
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
    given(handleRep.searchByNormalisedPhysicalIdentifierFullRecord(anyList())).willReturn(
        digitalSpecimen);

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
    var request = genCreateRecordRequest(givenMediaRequestObject(),
        RECORD_TYPE_MEDIA);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_MEDIA);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareMediaObjectAttributes(any(), any(), any())).willReturn(
        genMediaObjectAttributes(handle));

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMasRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_MAS);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_MAS);
    List<HandleAttribute> handleRecord = genMasAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareMasRecordAttributes(any(), any(), eq(ObjectType.MAS))).willReturn(
        handleRecord);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecordBatch() throws Exception {
    // Given

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_HANDLE);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareHandleRecordAttributes(any(), any(), any()))
        .willReturn(genHandleRecordAttributes(handles.get(0), ObjectType.HANDLE))
        .willReturn(genHandleRecordAttributes(handles.get(1), ObjectType.HANDLE));

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
      flatList.addAll(genDoiRecordAttributes(handle, ObjectType.DOI));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DOI);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareDoiRecordAttributes(any(), any(), any())).willReturn(flatList);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      var physId = new String(handle) + "a";
      requests.add(
          genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(physId),
              RECORD_TYPE_DS));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_DS);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any(), any()))
        .willReturn(genDigitalSpecimenAttributes(handles.get(0)))
        .willReturn(genDigitalSpecimenAttributes(handles.get(1)));

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateAnnotationsBatch() throws Exception {
    // Given

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenAnnotationRequestObject(), RECORD_TYPE_ANNOTATION));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_ANNOTATION);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareAnnotationAttributes(any(), any(), any()))
        .willReturn(genAnnotationAttributes(handles.get(0)))
        .willReturn(genAnnotationAttributes(handles.get(1)));

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMappingBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenMappingRequestObject(), RECORD_TYPE_MAPPING));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_MAPPING);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareMappingAttributes(any(), any(), any()))
        .willReturn(genMappingAttributes(handles.get(0)))
        .willReturn(genMappingAttributes(handles.get(1)));

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateSourceSystemBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(
          genCreateRecordRequest(givenSourceSystemRequestObject(), RECORD_TYPE_SOURCE_SYSTEM));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_SOURCE_SYSTEM);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareSourceSystemAttributes(any(), any(), any()))
        .willReturn(genSourceSystemAttributes(handles.get(0)))
        .willReturn(genSourceSystemAttributes(handles.get(1)));

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateOrganisationBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(
          genCreateRecordRequest(givenOrganisationRequestObject(), RECORD_TYPE_ORGANISATION));
      flatList.addAll(genOrganisationAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_ORGANISATION);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareOrganisationAttributes(any(), any(), any())).willReturn(flatList);

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
      requests.add(genCreateRecordRequest(givenMediaRequestObject(), RECORD_TYPE_MEDIA));
      flatList.addAll(genMediaObjectAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_MEDIA);

    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareMediaObjectAttributes(any(), any(), any())).willReturn(flatList);

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
    var updatedAttributeRecord = genUpdateRecordAttributesAltLoc(handle);
    var responseExpected = givenRecordResponseWriteAltLoc(List.of(handle));

    given(handleRep.checkHandlesWritable(anyList())).willReturn(List.of(handle));
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any()))
        .willReturn(updatedAttributeRecord);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordLocationBatch() throws Exception {
    // Given

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    var responseExpected = givenRecordResponseWriteAltLoc(handles);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any()))
        .willReturn(genUpdateRecordAttributesAltLoc(handles.get(0)))
        .willReturn(genUpdateRecordAttributesAltLoc(handles.get(1)));

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

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle));
    var tombstoneAttributes = genTombstoneRecordRequestAttributes(handle);

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareTombstoneAttributes(any(), any())).willReturn(
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

    var tombstoneAttributes = List.of(
        genTombstoneRecordRequestAttributes(handle),
        genTombstoneRecordRequestAttributes(handleAlt)
    );
    var tombstoneFlatlist = Stream.concat(tombstoneAttributes.get(0).stream(),
        tombstoneAttributes.get(1).stream()).toList();

    given(handleRep.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareTombstoneAttributes(any(), any())).willReturn(
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
    var existingPhysId = "Alt ID";

    var newRecordRequest = givenDigitalSpecimenRequestObjectNullOptionals(
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var existingRecordRequest = givenDigitalSpecimenRequestObjectNullOptionals(existingPhysId);
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(newRecordRequest, RECORD_TYPE_DS),
        genCreateRecordRequest(existingRecordRequest, RECORD_TYPE_DS));

    var existingRecordAttributes = genDigitalSpecimenAttributes(existingHandle,
        existingRecordRequest);
    var newRecordAttributes = genDigitalSpecimenAttributes(newHandle, newRecordRequest);
    var primarySpecimenObjectIdAttributes = getPrimarySpecimenObjectIds(
        Stream.concat(existingRecordAttributes.stream(), newRecordAttributes.stream()).toList());

    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(getPrimarySpecimenObjectIds(newRecordAttributes),
                new String(newHandle)),
            upsertedResponse(getPrimarySpecimenObjectIds(existingRecordAttributes),
                new String(existingHandle, StandardCharsets.UTF_8))));

    given(handleRep.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(
        List.of(new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID.index(), existingHandle,
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE.get(),
            (existingPhysId + ":" + ROR_IDENTIFIER).getBytes(StandardCharsets.UTF_8))));
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(eq(newRecordRequest), any(),
        any())).willReturn(newRecordAttributes);
    given(fdoRecordService.prepareUpdateAttributes(any(),
        eq(MAPPER.valueToTree(existingRecordRequest)), any())).willReturn(
        existingRecordAttributes);
    given(hgService.genHandleList(anyInt())).willReturn(new ArrayList<>(List.of(newHandle)));

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), newRecordAttributes,
            List.of(existingRecordAttributes));
  }

  @Test
  void testInternalDuplicateSpecimenIds() {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS),
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS)
    );
    var expectedMsg = "Bad Request. Some PhysicalSpecimenObjectIds are duplicated in request body";
    given(hgService.genHandleList(anyInt())).willReturn(handles);

    // When
    var response = assertThrows(InvalidRequestException.class,
        () -> service.createRecords(requests));

    // Then
    assertThat(response).hasMessage(expectedMsg);
  }

  @Test
  void testUpsertDigitalSpecimensOnlyUpdate() throws Exception {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
    var existingRecord = genDigitalSpecimenAttributes(handles.get(0));

    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(getPrimarySpecimenObjectIds(existingRecord), HANDLE))
    );

    given(handleRep.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(
        List.of(new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID.index(), handles.get(0),
            PRIMARY_SPECIMEN_OBJECT_ID.get(),
            (PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":" + ROR_IDENTIFIER).getBytes(
                StandardCharsets.UTF_8))));
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any())).willReturn(
        existingRecord);
    given(hgService.genHandleList(0)).willReturn(new ArrayList<>());

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), new ArrayList<>(),
            List.of(existingRecord));
  }

  @Test
  void testUpsertDigitalSpecimensOnlyCreate() throws Exception {
    // Given
    List<JsonNode> requests = List.of(
        genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
    var newRecord = genDigitalSpecimenAttributes(handles.get(1));
    var expected = new JsonApiWrapperWrite(
        List.of(upsertedResponse(getPrimarySpecimenObjectIds(newRecord), HANDLE_ALT))
    );

    given(handleRep.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(new ArrayList<>());
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any(), any())).willReturn(
        newRecord);
    given(hgService.genHandleList(anyInt())).willReturn(List.of(handles.get(0)));

    // When
    var response = service.upsertDigitalSpecimens(requests);

    // Then
    assertThat(response).isEqualTo(expected);
    then(handleRep).should()
        .postAndUpdateHandles(CREATED.getEpochSecond(), newRecord, new ArrayList<>());
  }

  @Test
  void testRollbackHandleCreation() {
    // Given
    var handleList = List.of(HANDLE, HANDLE_ALT);

    // When
    service.rollbackHandles(handleList);

    // Then
    then(handleRep).should().rollbackHandles(handleList);
  }

  @Test
  void testRollbackHandlesFromPhysId() {
    // Given
    given(handleRep.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(List.of(
        new HandleAttribute(1, HANDLE.getBytes(StandardCharsets.UTF_8),
            PRIMARY_SPECIMEN_OBJECT_ID.get(),
            PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8))));

    // When
    service.rollbackHandlesFromPhysId(List.of(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        "This phys id is not in the database"));

    // Then
    then(handleRep).should().rollbackHandles(List.of(HANDLE));
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
    mockedClock = mockStatic(Clock.class);
    mockedClock.when(Clock::systemUTC).thenReturn(clock);
  }

  private void initHandleList() {
    handles = new ArrayList<>();
    handles.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handles.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
  }

  private List<HandleAttribute> getPrimarySpecimenObjectIds(
      List<HandleAttribute> handleAttributes) {
    List<HandleAttribute> primaryIdList = new ArrayList<>();
    for (var row : handleAttributes) {
      if (row.type().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())) {
        primaryIdList.add(row);
      }
    }
    return primaryIdList;
  }
}
