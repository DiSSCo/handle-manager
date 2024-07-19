package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.testUtils.TestUtils.API_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LINKED_DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LINKED_DO_PID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ORCHESTRATION_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TOMBSTONE_TEXT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UPDATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalMediaAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObjectNoHash;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasRecordRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObjectUpdate;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.DigitalMediaRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.DcTermsType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.web.PidResolver;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FdoRecordServiceTest {

  private FdoRecordService fdoRecordService;
  @Mock
  private PidResolver pidResolver;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  Environment environment;

  @BeforeEach
  void init() throws PidResolutionException {
    fdoRecordService = new FdoRecordService(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver,
        MAPPER, applicationProperties);
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(applicationProperties.getPrefix()).willReturn(PREFIX);
    given(applicationProperties.getApiUrl()).willReturn(API_URL);
    given(applicationProperties.getOrchestrationUrl()).willReturn(ORCHESTRATION_URL);
    given(applicationProperties.getUiUrl()).willReturn(UI_URL);
    given(environment.matchesProfiles(Profiles.DOI)).willReturn(false);
  }

  @Test
  void testPrepareNewHandleRecord() throws Exception {
    // Given
    var request = givenHandleRecordRequestObject();
    var expected = givenHandleFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewHandleRecord(request, HANDLE, FdoType.HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedHandleRecord() throws Exception {
    // Given
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.HANDLE, null);
    var request = givenHandleRecordRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedHandleRecord(request, FdoType.HANDLE, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedHandleRecordNoIncrement() throws Exception {
    // Given
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var expectedAttributes = new ArrayList<>(
        givenUpdatedFdoRecord(FdoType.HANDLE, null).attributes());
    expectedAttributes.set(expectedAttributes.indexOf(
            new FdoAttribute(FdoProfile.PID_RECORD_ISSUE_NUMBER, UPDATED, "2")),
        new FdoAttribute(FdoProfile.PID_RECORD_ISSUE_NUMBER, CREATED, "1"));
    var expected = new FdoRecord(HANDLE, FdoType.HANDLE, expectedAttributes, null);

    var request = givenHandleRecordRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedHandleRecord(request, FdoType.HANDLE, UPDATED,
        previousVersion, false);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewDoiRecord() throws Exception {
    // Given
    var request = givenDoiRecordRequestObject();
    var expected = givenDoiFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewDoiRecord(request, HANDLE, FdoType.DOI, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedDoiRecord() throws Exception {
    // Given
    var previousVersion = givenDoiFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DOI, null);
    var request = givenDoiRecordRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedDoiRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewMediaRecordMin() throws Exception {
    var request = givenDigitalMediaRequestObject();
    var expected = givenDigitalMediaFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewDigitalMediaRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewMediaRecordFull() throws Exception {
    var request = new DigitalMediaRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL,
        MEDIA_HOST_NAME_TESTVAL, MediaFormat.TEXT, Boolean.TRUE, LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, "a", PRIMARY_MEDIA_ID_TESTVAL,
        PrimarySpecimenObjectIdType.RESOLVABLE, "b", DcTermsType.IMAGE, "jpeg", "c", "license",
        "license", "c", "d", null, "e");
    var expected = new FdoRecord(HANDLE, FdoType.DIGITAL_MEDIA,
        genDigitalMediaAttributes(HANDLE, request, CREATED), PRIMARY_MEDIA_ID_TESTVAL);

    // When
    var result = fdoRecordService.prepareNewDigitalMediaRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedMediaRecord() throws Exception {
    // Given
    var previousVersion = givenDigitalMediaFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DIGITAL_MEDIA, PRIMARY_MEDIA_ID_TESTVAL);
    var request = givenDigitalMediaRequestObjectUpdate();
    // When
    var result = fdoRecordService.prepareUpdatedDigitalMediaRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewDigitalSpecimenRecordMin() throws Exception {
    var request = givenDigitalSpecimenRequestObjectNullOptionals();
    var expected = givenDigitalSpecimenFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewDigitalSpecimenRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewSpecimenRecordFull() throws Exception {
    var request = givenDigitalSpecimenRequestObject();
    var expected = new FdoRecord(HANDLE, FdoType.DIGITAL_SPECIMEN,
        genDigitalSpecimenAttributes(HANDLE, request, CREATED),
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);

    // When
    var result = fdoRecordService.prepareNewDigitalSpecimenRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedSpecimenRecord() throws Exception {
    // Given
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var request = givenDigitalSpecimenRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedDigitalSpecimenRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewAnnotationRecordMin() throws Exception {
    // Given
    var request = givenAnnotationRequestObjectNoHash();
    var expected = givenAnnotationFdoRecord(HANDLE, false);

    // When
    var result = fdoRecordService.prepareNewAnnotationRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewAnnotationRecordFull() throws Exception {
    // Given
    var request = givenAnnotationRequestObject();
    var expected = givenAnnotationFdoRecord(HANDLE, true);

    // When
    var result = fdoRecordService.prepareNewAnnotationRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedAnnotationRecord() throws Exception {
    // Given
    var previousVersion = givenAnnotationFdoRecord(HANDLE, false);
    var expected = givenUpdatedFdoRecord(FdoType.ANNOTATION, null);
    var request = givenAnnotationRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedAnnotationRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewMasRecord() throws Exception {
    // Given
    var request = givenMasRecordRequestObject();
    var expected = givenMasFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewMasRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedMasRecord() throws Exception {
    // Given
    var previousVersion = givenMasFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.MAS, null);
    var request = givenMasRecordRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedMasRecord(request, UPDATED, previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewDataMappingRecord() throws Exception {
    // Given
    var request = givenDataMappingRequestObject();
    var expected = givenDataMappingFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewDataMappingRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedDataMappingRecord() throws Exception {
    // Given
    var previousVersion = givenDataMappingFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DATA_MAPPING, null);
    var request = givenDataMappingRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedDataMappingRecord(request, UPDATED, previousVersion,
        true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewSourceSystemRecord() throws Exception {
    // Given
    var request = givenSourceSystemRequestObject();
    var expected = givenSourceSystemFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewSourceSystemRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedSourceSystemRecord() throws Exception {
    // Given
    var previousVersion = givenSourceSystemFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.SOURCE_SYSTEM, null);
    var request = givenSourceSystemRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedSourceSystemRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareNewOrganisationRecord() throws Exception {
    // Given
    var request = givenOrganisationRequestObject();
    var expected = givenOrganisationFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewOrganisationRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isEqualTo(expected.primaryLocalId());
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareUpdatedOrganisationRecord() throws Exception {
    // Given
    var previousVersion = givenOrganisationFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.ORGANISATION, null);
    var request = givenOrganisationRequestObjectUpdate();

    // When
    var result = fdoRecordService.prepareUpdatedOrganisationRecord(request, UPDATED,
        previousVersion, true);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareTombstoneRecordNoRelatedIds() throws Exception {
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var request = new TombstoneRecordRequest(TOMBSTONE_TEXT_TESTVAL, null);
    var expected = new FdoRecord(HANDLE, FdoType.HANDLE, genTombstoneAttributes(request),
        null);

    // When
    var result = fdoRecordService.prepareTombstoneRecord(request, UPDATED, previousVersion);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareTombstoneRecordEmptyRelatedIds() throws Exception {
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var request = new TombstoneRecordRequest(TOMBSTONE_TEXT_TESTVAL, Collections.emptyList());
    var expected = new FdoRecord(HANDLE, FdoType.HANDLE, genTombstoneAttributes(request),
        null);

    // When
    var result = fdoRecordService.prepareTombstoneRecord(request, UPDATED, previousVersion);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testPrepareTombstoneRecordFull() throws Exception {
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var request = givenTombstoneRecordRequestObject();
    var expected = givenTombstoneFdoRecord();

    // When
    var result = fdoRecordService.prepareTombstoneRecord(request, UPDATED, previousVersion);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

}
