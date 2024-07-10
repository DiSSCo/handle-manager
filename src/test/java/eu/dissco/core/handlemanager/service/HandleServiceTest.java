package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_LIST_STR;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObjectNoHash;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseNullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseRead;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseReadSingle;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWrite;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteArchive;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteSmallResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidMongoRepository;
import eu.dissco.core.handlemanager.repository.PidRepository;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = Profiles.HANDLE)
class HandleServiceTest {

  @Mock
  private PidRepository pidRepository;
  @Mock
  private FdoRecordService fdoRecordService;
  @Mock
  private PidNameGeneratorService pidNameGeneratorService;
  @Mock
  private ProfileProperties profileProperties;
  @Mock
  PidMongoRepository mongoRepository;
  private PidService service;
  private List<byte[]> handles;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    initTime();
    initHandleList();
    service = new HandleService(pidRepository, fdoRecordService, pidNameGeneratorService, MAPPER,
        profileProperties, mongoRepository);
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

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }


  @Test
  void testResolveSingleRecord() throws Exception {
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = UI_URL + HANDLE;
    List<HandleAttribute> recordAttributeList = genHandleRecordAttributes(handle,
        FdoType.HANDLE);

    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, FdoType.HANDLE,
        genObjectNodeAttributeRecord(recordAttributeList));

    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(recordAttributeList);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testRemoveHsAdmin() throws Exception {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = UI_URL + HANDLE;
    var adminHandle = new HandleAttribute(HS_ADMIN.index(), handle, HS_ADMIN.get(),
        "\\\\x0FFF000000153330303A302E4E412F32302E353030302E31303235000000C8".getBytes(
            StandardCharsets.UTF_8));
    var recordAttributeList = genHandleRecordAttributes(handle, FdoType.HANDLE);
    recordAttributeList.add(adminHandle);

    var responseExpected = givenRecordResponseReadSingle(HANDLE, path, FdoType.HANDLE,
        genObjectNodeAttributeRecord(recordAttributeList));

    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(recordAttributeList);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.resolveSingleRecord(handle, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveSingleRecordNotFound() {

    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    String path = UI_URL + HANDLE;
    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(new ArrayList<>());

    // When
    var exception = assertThrowsExactly(PidResolutionException.class,
        () -> service.resolveSingleRecord(handle, path));
    // Then
    assertThat(exception.getMessage()).contains(HANDLE);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    String path = UI_URL;
    List<HandleAttribute> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handles) {
      repositoryResponse.addAll(genHandleRecordAttributes(handle, FdoType.HANDLE));
    }
    var responseExpected = givenRecordResponseRead(handles, path, FdoType.HANDLE);

    given(pidRepository.resolveHandleAttributes(anyList())).willReturn(repositoryResponse);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.resolveBatchRecord(handles, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchDigitalSpecimenRecord() throws Exception {
    // Given
    String path = UI_URL;
    List<HandleAttribute> repositoryResponse = new ArrayList<>();
    for (byte[] handle : handles) {
      repositoryResponse.addAll(genDigitalSpecimenAttributes(handle));
    }
    var responseExpected = givenRecordResponseRead(handles, path,
        FdoType.DIGITAL_SPECIMEN);

    given(pidRepository.resolveHandleAttributes(anyList())).willReturn(repositoryResponse);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.resolveBatchRecord(handles, path);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }


  @Test
  void testSearchByPhysicalSpecimenId() throws Exception {
    // Given
    var expectedAttributes = genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));
    var responseExpected = givenRecordResponseWrite(
        List.of(HANDLE.getBytes(StandardCharsets.UTF_8)), FdoType.DIGITAL_SPECIMEN);

    given(pidRepository.searchByNormalisedPhysicalIdentifierFullRecord(anyList())).willReturn(
        expectedAttributes);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testSearchByPhysicalSpecimenIdTwoResolution() throws Exception {
    // Given
    List<HandleAttribute> attributeList = new ArrayList<>();
    attributeList.addAll(genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8)));
    attributeList.addAll(genDigitalSpecimenAttributes(HANDLE_ALT.getBytes(StandardCharsets.UTF_8)));

    given(pidRepository.searchByNormalisedPhysicalIdentifierFullRecord(anyList())).willReturn(
        attributeList);

    // When
    Exception e = assertThrowsExactly(PidResolutionException.class,
        () -> service.searchByPhysicalSpecimenId(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));

    // Then
    assertThat(e).hasMessage(
        "More than one handle record corresponds to the provided collection facility and physical identifier.");
  }

  @Test
  void testCreateHandleRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    var responseExpected = givenRecordResponseWrite(List.of(handle), FdoType.HANDLE);
    List<HandleAttribute> handleRecord = genHandleRecordAttributes(handle, FdoType.HANDLE);

    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareHandleRecordAttributes(any(), any(),
        eq(FdoType.HANDLE))).willReturn(handleRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDoiRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI);
    var responseExpected = givenRecordResponseWrite(List.of(handle), FdoType.DOI);
    List<HandleAttribute> doiRecord = genDoiRecordAttributes(handle, FdoType.DOI);

    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDoiRecordAttributes(any(), any(), eq(FdoType.DOI))).willReturn(
        doiRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

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
        FdoType.DIGITAL_SPECIMEN);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);
    var digitalSpecimenSublist = digitalSpecimen.stream()
        .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())).toList();

    var responseExpected = givenRecordResponseWriteSmallResponse(digitalSpecimenSublist,
        List.of(handle), FdoType.DIGITAL_SPECIMEN);

    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
        digitalSpecimen);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    then(pidRepository).should().postAttributesToDb(CREATED.getEpochSecond(), digitalSpecimen);
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateDigitalSpecimenSpecimenAlreadyExists() {
    // Given
    byte[] handle = handles.get(0);
    var digitalSpecimen = givenDigitalSpecimenRequestObjectNullOptionals();
    var request = List.of((JsonNode) genCreateRecordRequest(digitalSpecimen,
        FdoType.DIGITAL_SPECIMEN));
    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(pidRepository.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(List.of(
        new HandleAttribute(FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID, handle,
            digitalSpecimen.getNormalisedPrimarySpecimenObjectId())));

    // When Then
    assertThrowsExactly(InvalidRequestException.class, () -> service.createRecords(request));
  }

  @Test
  void testCreateMediaObjectRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenMediaRequestObject(), FdoType.MEDIA_OBJECT);
    var handleRecord = genMediaObjectAttributes(handle);
    var handleRecordSublist = handleRecord.stream().filter(
        row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
            .equals(LINKED_DO_PID.get())).toList();

    var responseExpected = givenRecordResponseWriteSmallResponse(handleRecordSublist,
        List.of(handle), FdoType.MEDIA_OBJECT);

    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareMediaObjectAttributes(any(), any())).willReturn(
        handleRecordSublist);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMasRecord() throws Exception {
    // Given
    byte[] handle = handles.get(0);
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.MAS);
    var responseExpected = givenRecordResponseWrite(List.of(handle), FdoType.MAS);
    List<HandleAttribute> handleRecord = genMasAttributes(handle);

    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareMasRecordAttributes(any(), any())).willReturn(handleRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When

    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateHandleRecordBatch() throws Exception {
    // Given

    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.HANDLE);

    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareHandleRecordAttributes(any(), any(), any())).willReturn(
            genHandleRecordAttributes(handles.get(0), FdoType.HANDLE))
        .willReturn(genHandleRecordAttributes(handles.get(1), FdoType.HANDLE));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

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
      requests.add(genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI));
      flatList.addAll(genDoiRecordAttributes(handle, FdoType.DOI));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.DOI);

    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareDoiRecordAttributes(any(), any(), any())).willReturn(flatList);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

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
      requests.add(genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(physId),
          FdoType.DIGITAL_SPECIMEN));
    }
    var sublist = Stream.concat(genDigitalSpecimenAttributes(handles.get(0)).stream()
            .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())),
        genDigitalSpecimenAttributes(handles.get(1)).stream()
            .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get()))).toList();

    var responseExpected = givenRecordResponseWriteSmallResponse(sublist, handles,
        FdoType.DIGITAL_SPECIMEN);

    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
            genDigitalSpecimenAttributes(handles.get(0)))
        .willReturn(genDigitalSpecimenAttributes(handles.get(1)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateAnnotationsBatch() throws Exception {
    // Given

    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenAnnotationRequestObject(), FdoType.ANNOTATION));
    }

    var responseExpected = givenAnnotationResponseWrite(handles);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareAnnotationAttributes(any(), any())).willReturn(
            genAnnotationAttributes(handles.get(0), true))
        .willReturn(genAnnotationAttributes(handles.get(1), true));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateAnnotationsBatchNoHash() throws Exception {
    // Given

    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(
          genCreateRecordRequest(givenAnnotationRequestObjectNoHash(), FdoType.ANNOTATION));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.ANNOTATION);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareAnnotationAttributes(any(), any())).willReturn(
            genAnnotationAttributes(handles.get(0), false))
        .willReturn(genAnnotationAttributes(handles.get(1), false));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMappingBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenMappingRequestObject(), FdoType.MAPPING));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.MAPPING);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareMappingAttributes(any(), any())).willReturn(
        genMappingAttributes(handles.get(0))).willReturn(genMappingAttributes(handles.get(1)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateSourceSystemBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(
          genCreateRecordRequest(givenSourceSystemRequestObject(), FdoType.SOURCE_SYSTEM));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.SOURCE_SYSTEM);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareSourceSystemAttributes(any(), any())).willReturn(
            genSourceSystemAttributes(handles.get(0)))
        .willReturn(genSourceSystemAttributes(handles.get(1)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

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
          genCreateRecordRequest(givenOrganisationRequestObject(), FdoType.ORGANISATION));
      flatList.addAll(genOrganisationAttributes(handle));
    }

    var responseExpected = givenRecordResponseWrite(handles, FdoType.ORGANISATION);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareOrganisationAttributes(any(), any())).willReturn(flatList);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testCreateMediaObjectBatch() throws Exception {
    // Given
    List<JsonNode> requests = new ArrayList<>();
    for (int i = 0; i < handles.size(); i++) {
      requests.add(genCreateRecordRequest(givenMediaRequestObject(), FdoType.MEDIA_OBJECT));
    }
    var sublist = Stream.concat(genMediaObjectAttributes(handles.get(0)).stream().filter(
        row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
            .equals(LINKED_DO_PID.get())), genMediaObjectAttributes(handles.get(1)).stream().filter(
        row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
            .equals(LINKED_DO_PID.get()))).toList();

    var responseExpected = givenRecordResponseWriteSmallResponse(sublist, handles,
        FdoType.MEDIA_OBJECT);
    given(pidNameGeneratorService.genHandleList(handles.size())).willReturn(handles);
    given(fdoRecordService.prepareMediaObjectAttributes(any(), any())).willReturn(
            genMediaObjectAttributes(handles.get(0)))
        .willReturn(genMediaObjectAttributes(handles.get(1)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

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
    var responseExpected = givenRecordResponseNullAttributes(List.of(handle));

    given(pidRepository.checkHandlesWritable(anyList())).willReturn(List.of(handle));
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any())).willReturn(
        updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(pidRepository).should()
        .updateRecordBatch(CREATED.getEpochSecond(), List.of(updatedAttributeRecord), true);
  }

  @Test
  void testUpdateRecordLocationBatch() throws Exception {
    // Given

    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);

    var responseExpected = givenRecordResponseNullAttributes(handles);
    var updatedAttributes = List.of(genUpdateRecordAttributesAltLoc(handles.get(0)),
        genUpdateRecordAttributesAltLoc(handles.get(1)));

    given(pidRepository.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any())).willReturn(
            genUpdateRecordAttributesAltLoc(handles.get(0)))
        .willReturn(genUpdateRecordAttributesAltLoc(handles.get(1)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    then(pidRepository).should()
        .updateRecordBatch(CREATED.getEpochSecond(), updatedAttributes, true);
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecordInternalDuplicates() throws Exception {
    // Given
    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any())).willReturn(
        genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8)));

    // Then
    assertThrowsExactly(InvalidRequestException.class, () -> {
      service.updateRecords(updateRequest, true);
    });
  }

  @Test
  void testUpdateRecordNonWritable() throws Exception {
    // Given
    List<JsonNode> updateRequest = genUpdateRequestBatch(handles);
    given(pidRepository.checkHandlesWritable(anyList())).willReturn(new ArrayList<>());
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any()))
        .willReturn(genDigitalSpecimenAttributes(HANDLE.getBytes(StandardCharsets.UTF_8)))
        .willReturn(genDigitalSpecimenAttributes(HANDLE_ALT.getBytes(StandardCharsets.UTF_8)));

    // Then
    assertThrowsExactly(PidResolutionException.class, () -> {
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

    given(pidRepository.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareTombstoneAttributes(any(), any())).willReturn(
        tombstoneAttributes);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(pidRepository).should()
        .archiveRecords(CREATED.getEpochSecond(), tombstoneAttributes, List.of(HANDLE));
  }

  @Test
  void testArchiveRecordBatch() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var handleAlt = HANDLE_ALT.getBytes(StandardCharsets.UTF_8);
    var archiveRequest = genTombstoneRequestBatch(List.of(HANDLE, HANDLE_ALT));

    var responseExpected = givenRecordResponseWriteArchive(List.of(handle, handleAlt));

    var tombstoneAttributes = List.of(genTombstoneRecordRequestAttributes(handle),
        genTombstoneRecordRequestAttributes(handleAlt));
    var tombstoneFlatlist = Stream.concat(tombstoneAttributes.get(0).stream(),
        tombstoneAttributes.get(1).stream()).toList();

    given(pidRepository.checkHandlesWritable(anyList())).willReturn(handles);
    given(fdoRecordService.prepareTombstoneAttributes(any(), any())).willReturn(
        tombstoneAttributes.get(0), tombstoneAttributes.get(1));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.archiveRecordBatch(archiveRequest);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(pidRepository).should()
        .archiveRecords(CREATED.getEpochSecond(), tombstoneFlatlist, List.of(HANDLE, HANDLE_ALT));
  }

  @Test
  void testGetHandlesPaged() {
    // Given
    int pageNum = 0;
    int pageSize = 2;
    byte[] pidStatus = PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8);

    given(pidRepository.getAllHandles(pageNum, pageSize)).willReturn(HANDLE_LIST_STR);
    given(pidRepository.getAllHandles(pidStatus, pageNum, pageSize)).willReturn(HANDLE_LIST_STR);

    // When
    var responseExpectedFirst = service.getHandlesPaged(pageNum, pageSize);
    var responseExpectedSecond = service.getHandlesPaged(pageNum, pageSize, pidStatus);

    // Then
    assertThat(responseExpectedFirst).isEqualTo(HANDLE_LIST_STR);
    assertThat(responseExpectedSecond).isEqualTo(HANDLE_LIST_STR);
  }

  @Test
  void testRollbackHandleCreation() {
    // Given
    var handleList = List.of(HANDLE, HANDLE_ALT);

    // When
    service.rollbackHandles(handleList);

    // Then
    then(pidRepository).should().rollbackHandles(handleList);
  }

  @Test
  void testRollbackHandlesFromPhysId() {
    // Given
    given(pidRepository.searchByNormalisedPhysicalIdentifier(anyList())).willReturn(
        List.of(new HandleAttribute(NORMALISED_SPECIMEN_OBJECT_ID,
            HANDLE.getBytes(StandardCharsets.UTF_8), PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL)));

    // When
    service.rollbackHandlesFromPhysId(
        List.of(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));

    // Then
    then(pidRepository).should().rollbackHandles(List.of(HANDLE));
  }

}
