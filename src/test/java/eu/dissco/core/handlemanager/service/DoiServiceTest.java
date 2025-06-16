package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_ID_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalMediaAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMedia;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimen;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiKernelUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHasRelatedPid;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMongoDocument;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenPostRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdateRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseFull;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseIdsOnly;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.jsonFormatFdoRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import eu.dissco.core.handlemanager.testUtils.TestUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = Profiles.DOI)
class DoiServiceTest {

  @Mock
  private FdoRecordService fdoRecordService;
  @Mock
  private PidNameGeneratorService pidNameGeneratorService;
  @Mock
  private ProfileProperties profileProperties;
  @Mock
  private DataCiteService dataCiteService;
  @Mock
  private MongoRepository mongoRepository;
  @Mock
  private PidService service;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    initTime();
    service = new DoiService(fdoRecordService, pidNameGeneratorService, MAPPER, profileProperties,
        dataCiteService, mongoRepository);
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

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }

  @Test
  void testCreateDigitalSpecimen() throws Exception {
    // Given
    var request = givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN);
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecord), FdoType.DIGITAL_SPECIMEN,
        DOI_DOMAIN);
    var dataCiteEvent = new DataCiteEvent(jsonFormatFdoRecord(fdoRecord.values()),
        EventType.CREATE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request), false);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.DIGITAL_SPECIMEN);
  }

  @Test
  void testCreateDigitalSpecimenDraft() throws Exception {
    // Given
    var request = givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN);
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecord), FdoType.DIGITAL_SPECIMEN,
        DOI_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request), true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).shouldHaveNoInteractions();
  }

  @Test
  void testCreateDigitalMedia() throws Exception {
    // Given
    var request = givenPostRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA);
    var digitalMedia = givenDigitalMediaFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(digitalMedia), FdoType.DIGITAL_MEDIA,
        DOI_DOMAIN);
    var dataCiteEvent = new DataCiteEvent(jsonFormatFdoRecord(digitalMedia.values()),
        EventType.CREATE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        digitalMedia);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request), false);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.DIGITAL_MEDIA);
  }

  @Test
  void testCreateDigitalMediaDraft() throws Exception {
    // Given
    var request = givenPostRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA);
    var digitalMedia = givenDigitalMediaFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(digitalMedia), FdoType.DIGITAL_MEDIA,
        DOI_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        digitalMedia);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request), true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).shouldHaveNoInteractions();
  }

  @Test
  void testCreateDigitalSpecimenDataCiteFails() throws Exception {
    // Given
    var request = List.of(givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN));
    var digitalSpecimen = givenDigitalSpecimenFdoRecord(HANDLE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        digitalSpecimen);
    doThrow(JsonProcessingException.class).when(dataCiteService).publishToDataCite(any(), any());

    // When
    assertThrows(UnprocessableEntityException.class, () -> service.createRecords(request, false));

    // Then
    then(mongoRepository).should().rollbackHandles(List.of(HANDLE));
  }

  @Test
  void testUpsertDigitalSpecimen() throws Exception {
    // Given
    var requests = List.of(givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN),
        givenPostRequest(
            givenDigitalSpecimen().withNormalisedPrimarySpecimenObjectId(PRIMARY_SPECIMEN_ID_ALT),
            FdoType.DIGITAL_SPECIMEN));
    var fdoRecordNew = givenDigitalSpecimenFdoRecord(HANDLE);
    var attriubtes = genDigitalSpecimenAttributes(HANDLE_ALT, CREATED);
    var fdoRecordUpdate = new FdoRecord(HANDLE_ALT, FdoType.DIGITAL_SPECIMEN,
        attriubtes, PRIMARY_SPECIMEN_ID_ALT, attriubtes.values());
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecordUpdate, fdoRecordNew),
        FdoType.DIGITAL_SPECIMEN, DOI_DOMAIN);
    var dataCiteEventCreate = new DataCiteEvent(jsonFormatFdoRecord(fdoRecordNew.values()),
        EventType.CREATE);
    var dataCiteEventUpdate = new DataCiteEvent(jsonFormatFdoRecord(fdoRecordUpdate.values()),
        EventType.UPDATE);
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE_ALT);
    given(mongoRepository.searchByPrimaryLocalId(any(), anyList())).willReturn(
        List.of(previousVersion));
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecordNew);
    given(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(fdoRecordUpdate);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests, false);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEventCreate, FdoType.DIGITAL_SPECIMEN);
    then(dataCiteService).should().publishToDataCite(dataCiteEventUpdate, FdoType.DIGITAL_SPECIMEN);
    then(mongoRepository).should().postHandleRecords(any());
    then(mongoRepository).should().updateHandleRecords(any());
  }

  @Test
  void testUpsertDigitalSpecimenDuplicate() throws Exception {
    // Given
    var requests = List.of(givenPostRequest(givenDigitalSpecimen(), FdoType.DIGITAL_SPECIMEN),
        givenPostRequest(
            givenDigitalSpecimen().withNormalisedPrimarySpecimenObjectId(PRIMARY_SPECIMEN_ID_ALT),
            FdoType.DIGITAL_SPECIMEN));
    var fdoRecordNew = givenDigitalSpecimenFdoRecord(HANDLE);
    var attriubtes = genDigitalSpecimenAttributes(HANDLE_ALT, CREATED);
    var fdoRecordUpdate = new FdoRecord(HANDLE_ALT, FdoType.DIGITAL_SPECIMEN,
        attriubtes, PRIMARY_SPECIMEN_ID_ALT, attriubtes.values());
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecordUpdate, fdoRecordNew),
        FdoType.DIGITAL_SPECIMEN, DOI_DOMAIN);
    var dataCiteEventCreate = new DataCiteEvent(jsonFormatFdoRecord(fdoRecordNew.values()),
        EventType.CREATE);
    var dataCiteEventUpdate = new DataCiteEvent(jsonFormatFdoRecord(fdoRecordUpdate.values()),
        EventType.UPDATE);
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE_ALT);
    var tombstoneAttributes = genTombstoneAttributes(givenTombstoneRecordRequestObject());
    var tombstoneRecord = new FdoRecord("anotherHandle", FdoType.DIGITAL_SPECIMEN,
        tombstoneAttributes, "anotherPhysId", tombstoneAttributes.values());
    given(mongoRepository.searchByPrimaryLocalId(any(), anyList())).willReturn(
        List.of(previousVersion, tombstoneRecord));
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecordNew);
    given(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(fdoRecordUpdate);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests, false);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEventCreate, FdoType.DIGITAL_SPECIMEN);
    then(dataCiteService).should().publishToDataCite(dataCiteEventUpdate, FdoType.DIGITAL_SPECIMEN);
    then(mongoRepository).should().postHandleRecords(any());
    then(mongoRepository).should().updateHandleRecords(any());
  }


  @Test
  void testUpdateDigitalSpecimen() throws Exception {
    // Given
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDigitalSpecimenUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_SPECIMEN, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var responseExpected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_SPECIMEN, DOI_DOMAIN);
    var expectedEvent = new DataCiteEvent(
        (jsonFormatFdoRecord(updatedAttributeRecord.values())), EventType.UPDATE);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
    then(dataCiteService).should().publishToDataCite(expectedEvent, FdoType.DIGITAL_SPECIMEN);
  }

  @Test
  void testUpdateDigitalMedia() throws Exception {
    // Given
    var previousVersion = givenDigitalMediaFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDigitalMediaUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_MEDIA, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_MEDIA,
        PRIMARY_MEDIA_ID_TESTVAL);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var responseExpected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_MEDIA, DOI_DOMAIN);
    var expectedEvent = new DataCiteEvent(
        (jsonFormatFdoRecord(updatedAttributeRecord.values())), EventType.UPDATE);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
    then(dataCiteService).should().publishToDataCite(expectedEvent, FdoType.DIGITAL_MEDIA);
  }

  @Test
  void testUpsertDigitalMedia() throws Exception {
    // Given
    var altMedia = "https://123";
    var requests = List.of(givenPostRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA),
        givenPostRequest(givenDigitalMedia().withPrimaryMediaId(altMedia), DIGITAL_MEDIA));
    var prevAttributes = genDigitalMediaAttributes(HANDLE_ALT, CREATED);
    var updatedAttributes = genDigitalMediaAttributes(HANDLE_ALT, CREATED);
    var previousVersion = new FdoRecord(HANDLE_ALT, DIGITAL_MEDIA, prevAttributes, altMedia,
        prevAttributes.values());
    var fdoRecordUpdate = new FdoRecord(HANDLE_ALT, DIGITAL_MEDIA, updatedAttributes, altMedia,
        updatedAttributes.values());
    var fdoRecordNew = givenDigitalMediaFdoRecord(HANDLE);
    var dataCiteEventNew = new DataCiteEvent(jsonFormatFdoRecord(fdoRecordNew.values()),
        EventType.CREATE);
    var dataCiteEventUpdate = new DataCiteEvent((jsonFormatFdoRecord(fdoRecordUpdate.values())),
        EventType.UPDATE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(mongoRepository.searchByPrimaryLocalId(any(), any())).willReturn(
        List.of(previousVersion));
    given(fdoRecordService.prepareNewDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecordNew);
    given(fdoRecordService.prepareUpdatedDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(fdoRecordUpdate);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecordUpdate, fdoRecordNew),
        DIGITAL_MEDIA, DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(requests, false);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(mongoRepository).should().updateHandleRecords(any());
    then(mongoRepository).should().postHandleRecords(any());
    then(dataCiteService).should().publishToDataCite(dataCiteEventNew, FdoType.DIGITAL_MEDIA);
    then(dataCiteService).should().publishToDataCite(dataCiteEventUpdate, FdoType.DIGITAL_MEDIA);

  }

  @Test
  void testUpdateInvalidType() {
    // Given
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE,
        MAPPER.valueToTree(givenDigitalSpecimenUpdated()));

    // When Then
    assertThrowsExactly(UnsupportedOperationException.class,
        () -> service.updateRecords(updateRequest, true));
  }

  @Test
  void testUpdateDuplicateHandles() throws Exception {
    // Given
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDigitalSpecimenUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_SPECIMEN, request);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(
        List.of(previousVersion, previousVersion));

    // When / Then
    assertThrowsExactly(UnprocessableEntityException.class,
        () -> service.updateRecords(updateRequest, true));
  }

  @ParameterizedTest
  @MethodSource("equalArgs")
  void testUpdateRecordIsEqual(FdoRecord previousVersion, FdoType fdoType, Object request)
      throws Exception {
    var updateRequest = givenUpdateRequest(List.of(HANDLE), fdoType, MAPPER.valueToTree(request));
    var expected = givenWriteResponseIdsOnly(List.of(previousVersion), fdoType, DOI_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);
    fdoRecordServiceReturnsPreviousVersion(previousVersion);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(Collections.emptyList());
  }

  private void fdoRecordServiceReturnsPreviousVersion(FdoRecord previousVersion) throws Exception {
    lenient().when(fdoRecordService.prepareUpdatedDoiRecord(any(), any(), any(), anyBoolean()))
        .thenReturn(previousVersion);
    lenient().when(
            fdoRecordService.prepareUpdatedDigitalMediaRecord(any(), any(), any(), anyBoolean()))
        .thenReturn(previousVersion);
    lenient().when(
            fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), any(), anyBoolean()))
        .thenReturn(previousVersion);
  }

  private static Stream<Arguments> equalArgs() throws Exception {
    return Stream.of(Arguments.of(givenDoiFdoRecord(HANDLE), FdoType.DOI, givenDoiKernel()),
        Arguments.of(givenDigitalSpecimenFdoRecord(HANDLE), FdoType.DIGITAL_SPECIMEN,
            givenDigitalSpecimen()),
        Arguments.of(givenDigitalMediaFdoRecord(HANDLE), DIGITAL_MEDIA, givenDigitalMedia()));
  }

  @Test
  void testUpdateRecordLocationDataCiteFails() throws Exception {
    // Given
    var requestAttributes = MAPPER.valueToTree(givenDigitalSpecimenUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_SPECIMEN,
        requestAttributes);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    given(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), eq(previousVersion),
        eq(true))).willReturn(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    doThrow(JsonProcessingException.class).when(dataCiteService).publishToDataCite(any(), any());

    // When
    assertThrows(UnprocessableEntityException.class,
        () -> service.updateRecords(updateRequest, true));

    // Then
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
    then(mongoRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  void testCreateInvalidType() {
    // Given
    var request = List.of(givenPostRequest(givenHandleKernel(), FdoType.HANDLE));

    // When Then
    assertThrows(UnsupportedOperationException.class, () -> service.createRecords(request, false));

  }

  @Test
  void testTombstoneRecords() throws Exception {
    // Given
    var request = givenTombstoneRequest();
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var fdoRecord = givenTombstoneFdoRecord();
    var expectedDocument = givenMongoDocument(fdoRecord);
    var expected = givenWriteResponseFull(List.of(HANDLE), FdoType.TOMBSTONE);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareTombstoneRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.tombstoneRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
    then(dataCiteService).should().tombstoneDataCite(HANDLE, List.of(givenHasRelatedPid()));
  }

  @Test
  void testTombstoneRecordsDataCiteFails() throws Exception {
    // Given
    var request = givenTombstoneRequest();
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var fdoRecord = givenTombstoneFdoRecord();
    var expectedDocument = givenMongoDocument(fdoRecord);
    var expected = givenWriteResponseFull(List.of(HANDLE), FdoType.TOMBSTONE);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareTombstoneRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    doThrow(JsonProcessingException.class).when(dataCiteService)
        .tombstoneDataCite(anyString(), anyList());

    // When
    var result = service.tombstoneRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
    then(dataCiteService).should().tombstoneDataCite(HANDLE, List.of(givenHasRelatedPid()));
  }

  @Test
  void testCreateDoi() throws Exception {
    var request = givenPostRequest(givenDoiKernel(), FdoType.DOI);
    var fdoRecord = givenDoiFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.DOI);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDoiRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateExistingDoiRecordRecord() throws Exception {
    // Given
    var previousVersion = givenDoiFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDoiKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DOI, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DOI, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDoiRecord(any(), any(), any(), anyBoolean())).willReturn(
        updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

}
