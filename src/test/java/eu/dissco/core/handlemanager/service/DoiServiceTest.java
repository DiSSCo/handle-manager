package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOI_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMongoDocument;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdateRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenWriteResponseIdsOnly;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.jsonFormatFdoRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.MongoRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);
    var fdoRecord = givenDigitalSpecimenFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(fdoRecord),
        FdoType.DIGITAL_SPECIMEN, DOI_DOMAIN);
    var dataCiteEvent = new DataCiteEvent(jsonFormatFdoRecord(fdoRecord.attributes()),
        EventType.CREATE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any())).willReturn(
        fdoRecord);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.DIGITAL_SPECIMEN);
  }

  @Test
  void testCreateDigitalMedia() throws Exception {
    // Given
    var request = genCreateRecordRequest(givenDigitalMediaRequestObject(), FdoType.DIGITAL_MEDIA);
    var digitalMedia = givenDigitalMediaFdoRecord(HANDLE);
    var responseExpected = givenWriteResponseIdsOnly(List.of(digitalMedia),
        FdoType.DIGITAL_MEDIA, DOI_DOMAIN);
    var dataCiteEvent = new DataCiteEvent(jsonFormatFdoRecord(digitalMedia.attributes()),
        EventType.CREATE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalMediaRecord(any(), any(), any())).willReturn(
        digitalMedia);
    given(profileProperties.getDomain()).willReturn(DOI_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.DIGITAL_MEDIA);
  }

  @Test
  void testCreateDigitalSpecimenDataCiteFails() throws Exception {
    // Given
    var request = List.of(
        (JsonNode) genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
            FdoType.DIGITAL_SPECIMEN));
    var digitalSpecimen = givenDigitalSpecimenFdoRecord(HANDLE);
    given(pidNameGeneratorService.generateNewHandles(1)).willReturn(Set.of(HANDLE));
    given(fdoRecordService.prepareNewDigitalSpecimenRecord(any(), any(), any())).willReturn(
        digitalSpecimen);
    doThrow(JsonProcessingException.class).when(dataCiteService).publishToDataCite(any(), any());

    // When
    assertThrows(UnprocessableEntityException.class, () -> service.createRecords(request));

    // Then
    then(mongoRepository).should().rollbackHandles(List.of(HANDLE));
  }

  @Test
  void testUpdateDigitalSpecimen() throws Exception {
    // Given
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var request = MAPPER.valueToTree(givenDigitalSpecimenRequestObjectUpdate());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_SPECIMEN, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var responseExpected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_SPECIMEN, DOI_DOMAIN);
    var expectedEvent = new DataCiteEvent(
        (jsonFormatFdoRecord(updatedAttributeRecord.attributes())),
        EventType.UPDATE);
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
    var request = MAPPER.valueToTree(givenDigitalMediaRequestObjectUpdate());
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.DIGITAL_MEDIA, request);
    var updatedAttributeRecord = givenUpdatedFdoRecord(FdoType.DIGITAL_MEDIA,
        PRIMARY_MEDIA_ID_TESTVAL);
    var expectedDocument = givenMongoDocument(updatedAttributeRecord);
    var responseExpected = givenWriteResponseIdsOnly(List.of(updatedAttributeRecord),
        FdoType.DIGITAL_MEDIA, DOI_DOMAIN);
    var expectedEvent = new DataCiteEvent(
        (jsonFormatFdoRecord(updatedAttributeRecord.attributes())),
        EventType.UPDATE);
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
  void testUpdateInvalidType() throws Exception {
    // Given
    var updateRequest = givenUpdateRequest(List.of(HANDLE), FdoType.HANDLE,
        MAPPER.valueToTree(givenDigitalSpecimenRequestObjectUpdate()));

    // When Then
    assertThrowsExactly(InvalidRequestException.class,
        () -> service.updateRecords(updateRequest, true));
  }

  @Test
  void testUpdateRecordLocationDataCiteFails() throws Exception {
    // Given
    var requestAttributes = MAPPER.valueToTree(givenDigitalSpecimenRequestObjectUpdate());
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
    var requestJson =
        MAPPER.createObjectNode()
            .set("data", MAPPER.createObjectNode()
                .put("type", FdoType.HANDLE.getFdoProfile())
                .set("attributes", MAPPER.createObjectNode()));
    var request = List.of(requestJson);

    // When Then
    assertThrows(UnsupportedOperationException.class, () ->
        service.createRecords(request));

  }

}
