package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PATH;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMapping;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimen;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernel;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleKernelUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMas;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMongoDocument;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenPostRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenReadResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystem;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdateRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenVirtualCollection;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenVirtualCollectionFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenVirtualCollectionUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseFull;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseIdsOnly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;

import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.PidStatus;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
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
@ActiveProfiles(profiles = Profiles.HANDLE)
class HandleServiceTest {

  @Mock
  private FdoRecordService fdoRecordService;
  @Mock
  private PidNameGeneratorService pidNameGeneratorService;
  @Mock
  private ProfileProperties profileProperties;
  @Mock
  MongoRepository mongoRepository;
  private PidService service;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    initTime();
    service = new HandleService(fdoRecordService, pidNameGeneratorService, MAPPER,
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

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }

  @Test
  void testCreateAnnotationNoHash() throws Exception {
    var request = givenPostRequest(givenAnnotation(false), FdoType.ANNOTATION);
    var fdoRecord = givenAnnotationFdoRecord(HANDLE, false);
    var expected = TestUtils.givenWriteResponseIdsOnly(List.of(fdoRecord), FdoType.ANNOTATION,
        HANDLE_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(
        fdoRecordService.prepareNewAnnotationRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testCreateAnnotationIncludeHash() throws Exception {
    var request = givenPostRequest(givenAnnotation(true), FdoType.ANNOTATION);
    var fdoRecord = givenAnnotationFdoRecord(HANDLE, true);
    var expected = givenWriteResponseIdsOnly(List.of(fdoRecord), FdoType.ANNOTATION,
        HANDLE_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(
        fdoRecordService.prepareNewAnnotationRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }


  @Test
  void testUpdateAnnotation() throws Exception {
    // Given
    var previousVersion = givenAnnotationFdoRecord(HANDLE, false);
    var request = MAPPER.valueToTree(givenAnnotationUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.ANNOTATION, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.ANNOTATION, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.ANNOTATION, HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedAnnotationRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateHandle() throws Exception {
    var request = givenPostRequest(givenHandleKernel(), FdoType.HANDLE);
    var fdoRecord = givenHandleFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.HANDLE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewHandleRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testActivateHandle() throws Exception {
    // Given
    var draftAttributes = genHandleRecordAttributes(HANDLE, CREATED, FdoType.HANDLE);
    draftAttributes.replace(PID_STATUS,
        new FdoAttribute(PID_STATUS, CREATED, PidStatus.DRAFT));
    var draftRecord = new FdoRecord(HANDLE, FdoType.HANDLE, draftAttributes, null,
        draftAttributes.values());
    var expectedAttributes = givenHandleFdoRecord(HANDLE).attributes();
    expectedAttributes.replace(PID_RECORD_ISSUE_NUMBER,
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, CREATED, 2));
    var activeRecord = new FdoRecord(HANDLE, FdoType.HANDLE, expectedAttributes, null,
        expectedAttributes.values());
    var expected = givenMongoDocument(activeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(draftRecord));
    given(fdoRecordService.activatePidRecord(any(), eq(CREATED)))
        .willReturn(activeRecord);

    // When
    service.activateRecords(List.of(HANDLE));

    // Then
    then(mongoRepository).should().updateHandleRecords(List.of(expected));
  }

  @Test
  void testUpdateHandleRecord() throws Exception {
    // Given
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenHandleKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.HANDLE, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedHandleRecord(any(), any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testUpdateHandleRecordNotWritableInternalDuplicates() throws Exception {
    // Given
    var request = MAPPER.valueToTree(givenHandleKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE, request);

    // When / Then
    assertThrows(InvalidRequestException.class, () -> service.updateRecords(updateRequest, true));
  }

  @Test
  void testUpdateHandleRecordNotFound() throws Exception {
    // Given
    var request = MAPPER.valueToTree(givenHandleKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE, request);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(Collections.emptyList());

    // When
    assertThrows(InvalidRequestException.class,
        () -> service.updateRecords(updateRequest, true));
  }

  @Test
  void testCreateDataMapping() throws Exception {
    var request = givenPostRequest(givenDataMapping(), FdoType.DATA_MAPPING);
    var fdoRecord = givenDataMappingFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.DATA_MAPPING);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(
        fdoRecordService.prepareNewDataMappingRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateDataMapping() throws Exception {
    // Given
    var previousVersion = givenDataMappingFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDataMappingUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DATA_MAPPING, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DATA_MAPPING, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDataMappingRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateMas() throws Exception {
    var request = givenPostRequest(givenMas(), FdoType.MAS);
    var fdoRecord = givenMasFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.MAS);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewMasRecord(any(), any(), any(), anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateMas() throws Exception {
    // Given
    var previousVersion = givenMasFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenMasUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.MAS, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.MAS, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedMasRecord(any(), any(), any(), anyBoolean())).willReturn(
        updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateSourceSystem() throws Exception {
    var request = givenPostRequest(givenSourceSystem(), FdoType.SOURCE_SYSTEM);
    var fdoRecord = givenSourceSystemFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.SOURCE_SYSTEM);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewSourceSystemRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateSourceSystem() throws Exception {
    // Given
    var previousVersion = givenSourceSystemFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenSourceSystemUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.SOURCE_SYSTEM, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.SOURCE_SYSTEM, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedSourceSystemRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateVirtualCollection() throws Exception {
    var request = givenPostRequest(givenVirtualCollection(), FdoType.VIRTUAL_COLLECTION);
    var fdoRecord = givenVirtualCollectionFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.VIRTUAL_COLLECTION);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewVirtualCollection(any(), any(), any(),
        anyBoolean())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateVirtualCollection() throws Exception {
    // Given
    var previousVersion = givenVirtualCollectionFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenVirtualCollectionUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.VIRTUAL_COLLECTION, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.VIRTUAL_COLLECTION, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedVirtualCollection(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @ParameterizedTest
  @MethodSource("equalArgs")
  void testUpdateRecordIsEqual(FdoRecord previousVersion, FdoType fdoType, Object request)
      throws Exception {
    var updateRequest = givenUpdateRequest(List.of(HANDLE), fdoType, MAPPER.valueToTree(request));
    var expected = givenWriteResponseFull(previousVersion);
    fdoRecordServiceReturnsPreviousVersion(previousVersion);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(Collections.emptyList());
  }

  private void fdoRecordServiceReturnsPreviousVersion(FdoRecord previousVersion) throws Exception {
    lenient().when(fdoRecordService.prepareUpdatedHandleRecord(any(), any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
    lenient().when(fdoRecordService.prepareUpdatedDataMappingRecord(any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
    lenient().when(fdoRecordService.prepareUpdatedMasRecord(any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
    lenient().when(fdoRecordService.prepareUpdatedOrganisationRecord(any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
    lenient().when(fdoRecordService.prepareUpdatedSourceSystemRecord(any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
    lenient().when(fdoRecordService.prepareUpdatedVirtualCollection(any(), any(), any(),
        anyBoolean())).thenReturn(previousVersion);
  }

  private static Stream<Arguments> equalArgs() throws Exception {
    return Stream.of(
        Arguments.of(givenHandleFdoRecord(HANDLE), FdoType.HANDLE, givenHandleKernel()),
        Arguments.of(givenDataMappingFdoRecord(HANDLE), FdoType.DATA_MAPPING, givenDataMapping()),
        Arguments.of(givenMasFdoRecord(HANDLE), FdoType.MAS, givenMas()),
        Arguments.of(givenOrganisationFdoRecord(HANDLE), FdoType.ORGANISATION, givenOrganisation()),
        Arguments.of(givenSourceSystemFdoRecord(HANDLE), FdoType.SOURCE_SYSTEM,
            givenSourceSystem()),
        Arguments.of(givenVirtualCollectionFdoRecord(HANDLE), FdoType.VIRTUAL_COLLECTION,
            givenVirtualCollection())
    );
  }

  @Test
  void testCreateOrganisation() throws Exception {
    var request = givenPostRequest(givenOrganisation(), FdoType.ORGANISATION);
    var fdoRecord = givenOrganisationFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.ORGANISATION);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewOrganisationRecord(any(), any(), any(),
        anyBoolean())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request), false);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateOrganisation() throws Exception {
    // Given
    var previousVersion = givenOrganisationFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenOrganisationUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.ORGANISATION, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.ORGANISATION, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedOrganisationRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
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
  }

  @Test
  void testResolveSingleRecord() throws Exception {
    // Given
    var expected = givenReadResponse(List.of(HANDLE), PATH, FdoType.HANDLE, HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(
        List.of(givenHandleFdoRecord(HANDLE)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.resolveSingleRecord(HANDLE, PATH);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    var expected = givenReadResponse(List.of(HANDLE), PATH, FdoType.HANDLE, HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(
        List.of(givenHandleFdoRecord(HANDLE)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.resolveBatchRecord(List.of(HANDLE), PATH);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testResolveBatchRecordNotFound() throws Exception {
    // Given
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(Collections.emptyList());

    // When / Then
    assertThrows(PidResolutionException.class,
        () -> service.resolveBatchRecord(List.of(HANDLE), ""));
  }

  @Test
  void testSearchByPhysicalSpecimenId() throws Exception {
    // Given
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.DIGITAL_SPECIMEN);
    given(mongoRepository.searchByPrimaryLocalId(any(), any())).willReturn(
        List.of(givenDigitalSpecimenFdoRecord(HANDLE)));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.searchByPhysicalSpecimenId(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testSearchByPhysicalSpecimenIdDuplicates() throws Exception {
    // Given
    given(mongoRepository.searchByPrimaryLocalId(any(), any())).willReturn(
        List.of(givenDigitalSpecimenFdoRecord(HANDLE),
            (givenDigitalSpecimenFdoRecord(HANDLE_ALT))));

    // When / Then
    assertThrows(PidResolutionException.class,
        () -> service.searchByPhysicalSpecimenId(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));
  }

  @Test
  void testSearchByPhysicalSpecimenIdNotFound() throws Exception {
    // Given
    given(mongoRepository.searchByPrimaryLocalId(any(), any())).willReturn(Collections.emptyList());

    // When / Then
    assertThrows(PidResolutionException.class,
        () -> service.searchByPhysicalSpecimenId(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));
  }

  @Test
  void testDifferentTypes() {
    // Given
    var requests = List.of(
        givenPostRequest(givenHandleKernel(), FdoType.HANDLE),
        givenPostRequest(givenDoiKernel(), FdoType.DOI)
    );

    // When / Then
    assertThrows(UnsupportedOperationException.class, () -> service.createRecords(requests, false));
  }

  @Test
  void testRollbackHandles() {
    // Given

    // When
    service.rollbackHandles(List.of(HANDLE));

    // Then
    then(mongoRepository).should().rollbackHandles(List.of(HANDLE));
  }

  @Test
  void testRollbackHandlesFromPhysId() {
    // When
    service.rollbackHandlesFromPhysId(List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));

    // Then
    then(mongoRepository).should().rollbackHandlesFromLocalId(NORMALISED_SPECIMEN_OBJECT_ID.get(),
        List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL));
  }

  @Test
  void testCreateInvalidType() {
    // Given
    var request = List.of(givenPostRequest(givenDigitalSpecimen(),
        FdoType.DIGITAL_SPECIMEN));

    // When / Then
    assertThrowsExactly(UnsupportedOperationException.class, () -> service.createRecords(request,
        false));
  }

  @Test
  void testUpdateInvalidType() {
    // Given
    var request = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_MEDIA,
        MAPPER.valueToTree(givenDigitalMediaUpdated()));

    // When / Then
    assertThrowsExactly(UnsupportedOperationException.class,
        () -> service.updateRecords(request, true));
  }

}
