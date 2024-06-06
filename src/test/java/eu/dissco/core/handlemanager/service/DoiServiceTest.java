package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaObjectAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeAttributeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseNullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenRecordResponseWriteSmallResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.datacite.DataCiteEvent;
import eu.dissco.core.handlemanager.domain.datacite.EventType;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = Profiles.DOI)
class DoiServiceTest {

  @Mock
  private PidRepository pidRepository;
  @Mock
  private FdoRecordService fdoRecordService;
  @Mock
  private PidNameGeneratorService pidNameGeneratorService;
  @Mock
  private ProfileProperties profileProperties;
  @Mock
  private DataCiteService dataCiteService;
  private PidService service;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;

  @BeforeEach
  void setup() {
    initTime();
    service = new DoiService(pidRepository, fdoRecordService, pidNameGeneratorService, MAPPER,
        profileProperties, dataCiteService);
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
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);
    var digitalSpecimenSublist = digitalSpecimen.stream()
        .filter(row -> row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())).toList();

    var responseExpected = givenRecordResponseWriteSmallResponse(digitalSpecimenSublist,
        List.of(handle), FdoType.DIGITAL_SPECIMEN);
    var dataCiteEvent = new DataCiteEvent(genObjectNodeAttributeRecord(digitalSpecimen),
        EventType.CREATE);
    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
        digitalSpecimen);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.DIGITAL_SPECIMEN);
  }

  @Test
  void testCreateMediaObject() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = genCreateRecordRequest(givenMediaRequestObject(), FdoType.MEDIA_OBJECT);
    List<HandleAttribute> mediaObject = genMediaObjectAttributes(handle);
    var mediaSublist = mediaObject.stream().filter(
        row -> row.getType().equals(PRIMARY_MEDIA_ID.get()) || row.getType()
            .equals(LINKED_DO_PID.get())).toList();
    var responseExpected = givenRecordResponseWriteSmallResponse(mediaSublist, List.of(handle),
        FdoType.MEDIA_OBJECT);
    var dataCiteEvent = new DataCiteEvent(genObjectNodeAttributeRecord(mediaObject),
        EventType.CREATE);
    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareMediaObjectAttributes(any(), any())).willReturn(mediaObject);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var responseReceived = service.createRecords(List.of(request));

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
    then(dataCiteService).should().publishToDataCite(dataCiteEvent, FdoType.MEDIA_OBJECT);
  }

  @Test
  void testCreateDigitalSpecimenDataCiteFails() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = List.of(
        (JsonNode) genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
            FdoType.DIGITAL_SPECIMEN));
    List<HandleAttribute> digitalSpecimen = genDigitalSpecimenAttributes(handle);
    given(pidNameGeneratorService.genHandleList(1)).willReturn(new ArrayList<>(List.of(handle)));
    given(fdoRecordService.prepareDigitalSpecimenRecordAttributes(any(), any())).willReturn(
        digitalSpecimen);
    doThrow(JsonProcessingException.class).when(dataCiteService).publishToDataCite(any(), any());

    // When
    assertThrows(UnprocessableEntityException.class, () -> service.createRecords(request));

    // Then
    then(pidRepository).should().rollbackHandles(List.of(HANDLE));
  }

  @Test
  void testUpdateRecordLocation() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var updateRequest = genUpdateRequestBatch(List.of(handle), FdoType.DIGITAL_SPECIMEN);
    var updatedAttributeRecord = genUpdateRecordAttributesAltLoc(handle);
    var responseExpected = givenRecordResponseNullAttributes(List.of(handle),
        FdoType.DIGITAL_SPECIMEN);
    var expectedEvent = new DataCiteEvent(
        ((ObjectNode) genObjectNodeAttributeRecord(updatedAttributeRecord))
            .put("pid", HANDLE),
        EventType.UPDATE);
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
    then(dataCiteService).should().publishToDataCite(expectedEvent, FdoType.DIGITAL_SPECIMEN);
  }

  @Test
  void testUpdateInvalidType() {
    // Given
    var updateRequest = genUpdateRequestBatch(List.of(HANDLE.getBytes(StandardCharsets.UTF_8)),
        FdoType.HANDLE);

    // When Then
    assertThrowsExactly(InvalidRequestException.class,
        () -> service.updateRecords(updateRequest, true));
  }


  @Test
  void testUpdateRecordLocationDataCiteFails() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var updateRequest = genUpdateRequestBatch(List.of(handle), FdoType.DIGITAL_SPECIMEN);
    var updatedAttributeRecord = genUpdateRecordAttributesAltLoc(handle);

    given(pidRepository.checkHandlesWritable(anyList())).willReturn(List.of(handle));
    given(fdoRecordService.prepareUpdateAttributes(any(), any(), any())).willReturn(
        updatedAttributeRecord);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    doThrow(JsonProcessingException.class).when(dataCiteService).publishToDataCite(any(), any());

    // When
    assertThrows(UnprocessableEntityException.class,
        () -> service.updateRecords(updateRequest, true));

    // Then
    then(pidRepository).should()
        .updateRecordBatch(CREATED.getEpochSecond(), List.of(updatedAttributeRecord), true);
    then(pidRepository).shouldHaveNoMoreInteractions();
  }

}
