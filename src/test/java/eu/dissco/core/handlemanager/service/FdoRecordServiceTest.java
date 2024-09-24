package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.API_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ORCHESTRATION_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_MEDIA_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_IDENTIFIER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TOMBSTONE_TEXT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UPDATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genAnnotationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.getField;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisation;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystem;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemUpdated;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenUpdatedFdoRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.setLocations;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.component.PidResolver;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.PidStatus;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.OtherspecimenIds;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    given(pidResolver.getObjectName(any())).willReturn(SPECIMEN_HOST_NAME_TESTVAL)
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
    var request = givenHandleKernel();
    var expected = givenHandleFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewHandleRecord(request, HANDLE, CREATED);

    // Then
    assertThat(result.attributes()).hasSameElementsAs(expected.attributes());
    assertThat(result.primaryLocalId()).isNull();
    assertThat(result.fdoType()).isEqualTo(expected.fdoType());
    assertThat(result.handle()).isEqualTo(expected.handle());
  }

  @Test
  void testGetObjectNameRor() throws Exception {
    // Given
    var request = new HandleRequestAttributes()
        .withIssuedForAgent(ROR_DOMAIN + ROR_IDENTIFIER);

    // When
    fdoRecordService.prepareNewHandleRecord(request, HANDLE, CREATED);

    // Then
    then(pidResolver).should().getObjectName("https://api.ror.org/organizations/" + ROR_IDENTIFIER);
  }

  @Test
  void testGetObjectNameHandle() throws Exception {
    // Given
    var id = "https://hdl.handle.net/" + HANDLE;
    var request = new HandleRequestAttributes()
        .withIssuedForAgent(id);

    // When
    fdoRecordService.prepareNewHandleRecord(request, HANDLE, CREATED);

    // Then
    then(pidResolver).should().getObjectName(id);
  }

  @Test
  void testGetObjectNameQid() throws Exception {
    // Given
    var id = "https://www.wikidata.org/wiki/123";
    var request = new HandleRequestAttributes()
        .withIssuedForAgent(id);
    var expected = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/123";

    // When
    fdoRecordService.prepareNewHandleRecord(request, HANDLE, CREATED);

    // Then
    then(pidResolver).should().resolveQid(expected);
  }

  @Test
  void testPrepareUpdatedHandleRecord() throws Exception {
    // Given
    var previousVersion = givenHandleFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.HANDLE, null);
    var request = givenHandleKernelUpdated();

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
  void testPrepareUpdatedHandleRecordReviveTombstone() throws Exception {
    // Given
    var previousVersion = givenTombstoneFdoRecord();
    var expectedAttributes = new ArrayList<>(
        givenUpdatedFdoRecord(FdoType.HANDLE, null).attributes());
    expectedAttributes.set(
        expectedAttributes.indexOf(getField(expectedAttributes, PID_RECORD_ISSUE_NUMBER)),
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, UPDATED, "3"));
    expectedAttributes.set(
        expectedAttributes.indexOf(getField(expectedAttributes, PID_STATUS)),
        new FdoAttribute(PID_STATUS, UPDATED, PidStatus.ACTIVE));
    var expected = new FdoRecord(HANDLE, FdoType.HANDLE, expectedAttributes, null);
    var request = givenHandleKernelUpdated();

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
    var request = givenHandleKernelUpdated();

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
    var request = givenDoiKernel();
    var expected = givenDoiFdoRecord(HANDLE);

    // When
    var result = fdoRecordService.prepareNewDoiRecord(request, HANDLE, CREATED);

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
    var request = givenDoiKernelUpdated();

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
    var request = givenDigitalMedia();
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
  void testPrepareNewMediaRecordInvalidRightsholder() {
    var request = givenDigitalMedia()
        .withRightsholderPid(null);

    // When
    assertThrows(InvalidRequestException.class, () ->
        fdoRecordService.prepareNewDigitalMediaRecord(request, HANDLE, CREATED));
  }

  @Test
  void testPrepareUpdatedMediaRecord() throws Exception {
    // Given
    var previousVersion = givenDigitalMediaFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DIGITAL_MEDIA, PRIMARY_MEDIA_ID_TESTVAL);
    var request = givenDigitalMediaUpdated();

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
  void testPrepareNewDigitalSpecimenRecord() throws Exception {
    // Given
    var request = givenDigitalSpecimen();
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
  void testPrepareNewDigitalSpecimenRecordOtherSpecimenIds() throws Exception {
    // Given
    var otherSpecimenId = new OtherspecimenIds(HANDLE_ALT, "Handle");
    var request = givenDigitalSpecimen()
        .withOtherSpecimenIds(List.of(otherSpecimenId));
    var attributes = new ArrayList<>(genDigitalSpecimenAttributes(HANDLE, CREATED));
    attributes.set(attributes.indexOf(new FdoAttribute(OTHER_SPECIMEN_IDS, CREATED, null)),
        new FdoAttribute(OTHER_SPECIMEN_IDS, CREATED,
            MAPPER.valueToTree(List.of(otherSpecimenId))));
    var expected = new FdoRecord(HANDLE, FdoType.DIGITAL_SPECIMEN, attributes,
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
  void testPrepareNewDigitalSpecimenRecordMissingIdAndAbsence() {
    // Given
    var request = givenDigitalSpecimen()
        .withPrimarySpecimenObjectId(null);

    // When / Then
    assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareNewDigitalSpecimenRecord(request, HANDLE, CREATED));
  }

  @Test
  void testPrepareUpdatedSpecimenRecord() throws Exception {
    // Given
    var previousVersion = givenDigitalSpecimenFdoRecord(HANDLE);
    var expected = givenUpdatedFdoRecord(FdoType.DIGITAL_SPECIMEN,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
    var request = givenDigitalSpecimenUpdated();

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
    var request = givenAnnotation(false);
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
  void testPrepareNewAnnotationRecordMinWithLoc() throws Exception {
    // Given
    var request = givenAnnotation(false)
        .withLocations(List.of(LOC_TESTVAL));
    var attributes = new ArrayList<>(genAnnotationAttributes(HANDLE, false));
    attributes.set(
        attributes.indexOf(
            new FdoAttribute(LOC, CREATED, setLocations(HANDLE, FdoType.ANNOTATION))),
        new FdoAttribute(LOC, CREATED, "<locations>"
            + "<location href=\"https://sandbox.dissco.tech/api/v1/annotations/20.5000.1025/QRS-321-ABC\" id=\"0\" weight=\"1\"/>"
            + "<location href=\"" + LOC_TESTVAL + "\" id=\"1\" weight=\"0\"/>"
            + "</locations>")
    );
    var expected = new FdoRecord(HANDLE, FdoType.ANNOTATION, attributes, null);

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
    var request = givenAnnotation(true);
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
    var request = givenAnnotationUpdated();

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
    var request = givenMas();
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
    var request = givenMasUpdated();

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
    var request = givenDataMapping();
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
    var request = givenDataMappingUpdated();

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
    var request = givenSourceSystem();
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
    var request = givenSourceSystemUpdated();

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
    var request = givenOrganisation();
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
    var request = givenOrganisationUpdated();

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
    var request = new TombstoneRequestAttributes(TOMBSTONE_TEXT_TESTVAL, null);
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
    var request = new TombstoneRequestAttributes(TOMBSTONE_TEXT_TESTVAL, Collections.emptyList());
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
