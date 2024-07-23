package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PATH;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMapping;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingUpdated;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseFull;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseIdsOnly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.ANNOTATION);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewAnnotationRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

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
    given(fdoRecordService.prepareNewAnnotationRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

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
  void testCreateDigitalSpecimen() throws Exception {
    var request = givenPostRequest(givenDigitalSpecimen(),
        FdoType.DIGITAL_SPECIMEN);
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    var expected = givenWriteResponseIdsOnly(List.of(fdoRecord),
        FdoType.DIGITAL_SPECIMEN, HANDLE_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
  }


  @Test
  void testCreateDigitalSpecimenObjectExists() throws Exception {
    var request = givenPostRequest(givenDigitalSpecimen(),
        FdoType.DIGITAL_SPECIMEN);
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    given(mongoRepository.searchByPrimaryLocalId(any(), any())).willReturn(List.of(fdoRecord));

    // When / Then
    assertThrows(InvalidRequestException.class, () -> service.createRecords(List.of(request)));

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
    var expected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_SPECIMEN, HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDigitalSpecimenRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.updateRecords(updateRequest, true);

    // Then
    assertThat(result).isEqualTo(expected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateDoi() throws Exception {
    var request = givenPostRequest(givenDoiKernel(), FdoType.DOI);
    var fdoRecord = givenDoiFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.DOI);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDoiRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateDoiRecord() throws Exception {
    // Given
    var previousVersion = givenDoiFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDoiKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DOI, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DOI, null);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var expected = givenWriteResponseFull(updatedAttributeRecord);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDoiRecord(any(), any(), any(),
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
    given(fdoRecordService.prepareNewHandleRecord(any(), any(), any())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
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
  void testUpdateHandleRecordInternalDuplicates() throws Exception {
    // Given
    var attributes = new ArrayList<>(givenHandleFdoRecord(HANDLE).attributes());
    attributes.set(attributes.indexOf(new FdoAttribute(PID_STATUS, CREATED, PID_STATUS_TESTVAL)),
        new FdoAttribute(PID_STATUS, CREATED, PidStatus.TOMBSTONED.name()));
    var previousVersion = new FdoRecord(HANDLE_ALT, FdoType.HANDLE, attributes, null);
    var request = MAPPER.valueToTree(givenHandleKernelUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE, request);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));

    // When / Then
    assertThrows(InvalidRequestException.class, () -> service.updateRecords(updateRequest, true));
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
    given(fdoRecordService.prepareNewDataMappingRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

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
    given(fdoRecordService.prepareNewMasRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

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
  void testCreateDigitalMedia() throws Exception {
    var request = givenPostRequest(givenDigitalMedia(), FdoType.DIGITAL_MEDIA);
    var fdoRecord = givenDigitalMediaFdoRecord(HANDLE);
    var expected = givenWriteResponseIdsOnly(List.of(fdoRecord), FdoType.DIGITAL_MEDIA,
        HANDLE_DOMAIN);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalMediaRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateDigitalMedia() throws Exception {
    var previousVersion = givenDigitalMediaFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDigitalMediaUpdated());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_MEDIA, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_MEDIA,
        PRIMARY_MEDIA_ID_TESTVAL);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var responseExpected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_MEDIA, HANDLE_DOMAIN);
    given(mongoRepository.getHandleRecords(List.of(HANDLE))).willReturn(List.of(previousVersion));
    given(fdoRecordService.prepareUpdatedDigitalMediaRecord(any(), any(), any(),
        anyBoolean())).willReturn(updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.updateRecords(updateRequest, true);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(mongoRepository).should().updateHandleRecords(List.of(expectedDocument));
  }

  @Test
  void testCreateSourceSystem() throws Exception {
    var request = givenPostRequest(givenSourceSystem(), FdoType.SOURCE_SYSTEM);
    var fdoRecord = givenSourceSystemFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.SOURCE_SYSTEM);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewSourceSystemRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void testUpdateSourceSystem() throws Exception {
    // Given
    var previousVersion = givenMasFdoRecord(HANDLE);
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
  void testCreateOrganisation() throws Exception {
    var request = givenPostRequest(givenOrganisation(), FdoType.ORGANISATION);
    var fdoRecord = givenOrganisationFdoRecord(HANDLE);
    var expected = TestUtils.givenWriteResponseFull(List.of(HANDLE), FdoType.ORGANISATION);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewOrganisationRecord(any(), any(), any())).willReturn(fdoRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = service.createRecords(List.of(request));

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
    assertThrows(UnsupportedOperationException.class, () -> service.createRecords(requests));
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
  void testInternalDuplicates() {
    // Given
    var attributes = MAPPER.valueToTree(givenHandleKernel());
    var request = givenUpdateRequest(List.of(HANDLE, HANDLE, HANDLE_ALT), FdoType.ORGANISATION,
        attributes);

    // When
    var e = assertThrows(InvalidRequestException.class, () -> service.updateRecords(request, true));

    // Then
    assertThat(e.getMessage()).contains(HANDLE);
  }


}
