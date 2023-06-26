package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ANNOTATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAPPING;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ORGANISATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genAnnotationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMappingAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaObjectAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genOrganisationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genSourceSystemAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeAttributeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordFullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseReadSingle;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteGeneric;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSearchByPhysIdRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.component.FdoRecordBuilder;
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
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    service = new HandleService(handleRep, fdoRecordBuilder, hgService, MAPPER);
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
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle, ObjectType.HANDLE);

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

    given(handleRep.searchByPhysicalIdentifier(anyList()))
        .willReturn(expectedAttributes);

    // When
    var responseReceived = service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, PhysicalIdType.CETAF,
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

    given(handleRep.searchByPhysicalIdentifier(anyList()))
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

    given(handleRep.searchByPhysicalIdentifier(anyList()))
        .willReturn(expectedAttributes);

    // When
    var responseReceived = service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, PhysicalIdType.COMBINED,
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
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(handleRecord);
    given(fdoRecordBuilder.prepareHandleRecordAttributes(any(), any(), eq(ObjectType.HANDLE))).willReturn(handleRecord);

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
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(doiRecord);
    given(fdoRecordBuilder.prepareDoiRecordAttributes(any(), any(), eq(ObjectType.DOI))).willReturn(doiRecord);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS);
    var responseExpected = givenRecordResponseWrite(List.of(handle), RECORD_TYPE_DS);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(digitalSpecimen);
    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(new ArrayList<>());
    given(fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(any(), any(), eq(ObjectType.DIGITAL_SPECIMEN))).willReturn(digitalSpecimen);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenSpecimenExists() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(digitalSpecimen);

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
    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(new ArrayList<>());

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
    given(handleRep.searchByPhysicalIdentifier(anyList())).willReturn(digitalSpecimenBotany);

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
    List<HandleAttribute> mediaObject = genMediaObjectAttributes(handle);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(mediaObject);

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
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle, ObjectType.MAS);

    given(hgService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(handleRecord);
    given(fdoRecordBuilder.prepareMasRecordAttributes(any(), any(), eq(ObjectType.MAS))).willReturn(handleRecord);

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
      flatList.addAll(genHandleRecordAttributes(handle, ObjectType.HANDLE));
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
      flatList.addAll(genDoiRecordAttributes(handle, ObjectType.DOI));
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
      requests.add(genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(), RECORD_TYPE_DS));
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
  void testCreateAnnotationsBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenAnnotationRequestObject(), RECORD_TYPE_ANNOTATION));
      flatList.addAll(genAnnotationAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_ANNOTATION);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMappingBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenMappingRequestObject(), RECORD_TYPE_MAPPING));
      flatList.addAll(genMappingAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_MAPPING);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateSourceSystemBatch() throws Exception {
    // Given
    List<HandleAttribute> flatList = new ArrayList<>();

    List<JsonNode> requests = new ArrayList<>();
    for (byte[] handle : handles) {
      requests.add(genCreateRecordRequest(givenSourceSystemRequestObject(), RECORD_TYPE_SOURCE_SYSTEM));
      flatList.addAll(genSourceSystemAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_SOURCE_SYSTEM);
    given(hgService.genHandleList(handles.size())).willReturn(handles);
    given(handleRep.resolveHandleAttributes(anyList())).willReturn(flatList);

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
      requests.add(genCreateRecordRequest(givenOrganisationRequestObject(), RECORD_TYPE_ORGANISATION));
      flatList.addAll(genOrganisationAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, RECORD_TYPE_ORGANISATION);
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
    var updatedAttributeRecord = genHandleRecordAttributesAltLoc(handle, ObjectType.HANDLE);
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
      updatedAttributeRecord.addAll(genHandleRecordAttributesAltLoc(handle, ObjectType.HANDLE));
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
    assertThrows(InvalidRequestException.class, () -> {
      service.updateRecords(updateRequest);
    });
  }

  @Test
  void testUpdateRecordNonWritable() throws Exception {
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

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    List<String> handlesString = handles.stream().map(e -> new String(e, StandardCharsets.UTF_8))
        .toList();
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
    var responseExpectedSecond = service.getHandlesPaged(pageNum, pageSize, pidStatus);

    // Then
    assertThat(responseExpectedFirst).isEqualTo(handles);
    assertThat(responseExpectedSecond).isEqualTo(handles);
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

  private List<byte[]> initHandleList() {
    handles = new ArrayList<>();
    handles.add(HANDLE.getBytes(StandardCharsets.UTF_8));
    handles.add(HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    return handles;
  }
}
