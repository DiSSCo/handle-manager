package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.domain.FdoProfile.ACCESS_RESTRICTED;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ANNOTATION_TOPIC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_OBJECT_IS_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_OBJECT_URL;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REPLACE_OR_APPEND;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SUBJECT_DIGITAL_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.WAS_DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.API_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ORCHESTRATION_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SOURCE_SYSTEM_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordRequestAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.setLocations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OtherSpecimenId;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.InformationArtefactType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialOrDigitalEntity;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicCategory;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicDomain;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.TopicOrigin;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import eu.dissco.core.handlemanager.service.FdoRecordService;
import eu.dissco.core.handlemanager.web.PidResolver;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FdoRecordServiceTest {

  private static final Set<String> HANDLE_FIELDS = Set.of(FDO_PROFILE.get(),
      FDO_RECORD_LICENSE.get(),
      DIGITAL_OBJECT_TYPE.get(), DIGITAL_OBJECT_NAME.get(), PID.get(), PID_ISSUER.get(),
      PID_ISSUER_NAME.get(), ISSUED_FOR_AGENT.get(),
      ISSUED_FOR_AGENT_NAME.get(), PID_RECORD_ISSUE_DATE.get(), PID_RECORD_ISSUE_NUMBER.get(),
      STRUCTURAL_TYPE.get(),
      PID_STATUS.get(), HS_ADMIN.get(), LOC.get());

  private static final Set<String> DOI_FIELDS = Set.of(REFERENT_TYPE.get(), REFERENT_DOI_NAME.get(),
      REFERENT_NAME.get(), PRIMARY_REFERENT_TYPE.get());
  private static final Set<String> DS_FIELDS_MANDATORY = Set.of(SPECIMEN_HOST.get(),
      SPECIMEN_HOST_NAME.get(),
      PRIMARY_SPECIMEN_OBJECT_ID.get(), PRIMARY_SPECIMEN_OBJECT_ID_TYPE.get(),
      NORMALISED_SPECIMEN_OBJECT_ID.get());

  private static final Set<String> DS_FIELDS_OPTIONAL = Set.of(
      PRIMARY_SPECIMEN_OBJECT_ID_NAME.get(),
      OTHER_SPECIMEN_IDS.get(), TOPIC_ORIGIN.get(), TOPIC_DISCIPLINE.get(), TOPIC_CATEGORY.get(),
      LIVING_OR_PRESERVED.get(),
      BASE_TYPE_OF_SPECIMEN.get(), INFORMATION_ARTEFACT_TYPE.get(),
      MATERIAL_SAMPLE_TYPE.get(), MARKED_AS_TYPE.get(), WAS_DERIVED_FROM_ENTITY.get());

  private static final Set<String> ANNOTATION_FIELDS = Set.of(SUBJECT_DIGITAL_OBJECT_ID.get(),
      ANNOTATION_TOPIC.get(), REPLACE_OR_APPEND.get(), ACCESS_RESTRICTED.get(),
      LINKED_OBJECT_URL.get(), LINKED_OBJECT_IS_PID.get());

  private final byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
  private FdoRecordService fdoRecordService;
  @Mock
  private PidResolver pidResolver;
  @Mock
  private HandleRepository handleRepository;
  @Mock
  private ApplicationProperties appProperties;
  private static final int HANDLE_QTY = 15;
  private static final int DOI_QTY = 19;
  private static final int MEDIA_QTY = 24;
  private static final int DS_MANDATORY_QTY = 24;
  private static final int DS_OPTIONAL_QTY = 37;
  private static final int ANNOTATION_QTY = 21;

  @BeforeEach
  void init() {
    fdoRecordService = new FdoRecordService(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver,
        MAPPER, handleRepository, appProperties);
    given(appProperties.getApiUrl()).willReturn(API_URL);
    given(appProperties.getOrchestrationUrl()).willReturn(ORCHESTRATION_URL);
    given(appProperties.getUiUrl()).willReturn(UI_URL);
  }

  @Test
  void testPrepareHandleRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenHandleRecordRequestObject();

    // When
    var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(HANDLE_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareDoiRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDoiRecordRequestObject();

    // When
    var result = fdoRecordService.prepareDoiRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(DOI_QTY);
    assertThat(hasCorrectLocations(result, request.getLocations(), ObjectType.HANDLE)).isTrue();
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareMediaObjectAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenMediaRequestObject();

    // When
    var result = fdoRecordService.prepareMediaObjectAttributes(request, handle,
        ObjectType.MEDIA_OBJECT);

    // Then
    assertThat(result).hasSize(MEDIA_QTY);
    assertThat(
        hasCorrectLocations(result, request.getLocations(), ObjectType.MEDIA_OBJECT)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDigitalSpecimenRequestObjectNullOptionals();

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY);
    assertThat(
        hasCorrectLocations(result, request.getLocations(), ObjectType.DIGITAL_SPECIMEN)).isTrue();
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributesQNumber() throws Exception {
    // Given
    String qid = "Q12345";
    String qidUrl = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/" + qid;
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(pidResolver.resolveQid(any())).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        qid,
        null,
        "PhysicalId",
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
    );

    // When
    fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    then(pidResolver).should().resolveQid(qidUrl);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributesBadSpecimenHost() throws Exception {
    // Given
    String specimenId = "12345";
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        specimenId,
        null,
        "PhysicalId",
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL
    );

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY - 1);
  }

  @Test
  void testPrepareDigitalSpecimenRecordOptionalAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDigitalSpecimenRequestObjectOptionalsInit();

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_OPTIONAL_QTY);
    assertThat(
        hasCorrectLocations(result, request.getLocations(), ObjectType.DIGITAL_SPECIMEN)).isTrue();
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_OPTIONAL)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareAnnotationAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(handleRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
        genHandleRecordAttributes(handle, ObjectType.ANNOTATION));
    var request = givenAnnotationRequestObject();

    // When
    var result = fdoRecordService.prepareAnnotationAttributes(request, handle,
        ObjectType.ANNOTATION);

    // Then
    assertThat(result).hasSize(ANNOTATION_QTY);
    assertThat(hasCorrectLocations(result, request.getLocations(), ObjectType.ANNOTATION)).isTrue();
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, ANNOTATION_FIELDS)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareMasRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenMasRecordRequestObject();

    // When
    var result = fdoRecordService.prepareMasRecordAttributes(request, handle, ObjectType.MAS);

    // Then
    assertThat(result).hasSize(HANDLE_QTY + 1);
    assertThat(hasCorrectLocations(result, request.getLocations(), ObjectType.MAS)).isTrue();
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, Set.of(MAS_NAME.get()))).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareAnnotationAttributesPidResolution() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(handleRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
        new ArrayList<>());
    var request = givenAnnotationRequestObject();

    // Then
    assertThrows(PidResolutionException.class,
        () -> fdoRecordService.prepareAnnotationAttributes(request, handle, ObjectType.ANNOTATION));
  }

  @Test
  void testPrepareMappingAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenMappingRequestObject();

    // When
    var result = fdoRecordService.prepareMappingAttributes(request, handle, ObjectType.MAPPING);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(result).hasSize(HANDLE_QTY + 1);
    assertThat(hasNoDuplicateElements(result)).isTrue();

  }


  @Test
  void testPrepareSourceSystemAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenSourceSystemRequestObject();

    // When
    var result = fdoRecordService.prepareSourceSystemAttributes(request, handle,
        ObjectType.SOURCE_SYSTEM);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(
        hasCorrectLocations(result, request.getLocations(), ObjectType.SOURCE_SYSTEM)).isTrue();
    assertThat(result).hasSize(HANDLE_QTY + 1);
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPrepareOrganisationAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenOrganisationRequestObject();

    // When
    var result = fdoRecordService.prepareOrganisationAttributes(request, handle,
        ObjectType.ORGANISATION);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(result).hasSize(DOI_QTY + 3);
    assertThat(hasNoDuplicateElements(result)).isTrue();

  }

  @Test
  void testPidIssuerIsRor() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        STRUCTURAL_TYPE_TESTVAL,
        null
    );

    // When
    var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(HANDLE_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testPidIssuerBad() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        "abc",
        STRUCTURAL_TYPE_TESTVAL,
        null
    );

    // Then
    var e = assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
    assertThat(e.getMessage()).contains(ROR_DOMAIN).contains(HANDLE_DOMAIN);
  }

  @Test
  void testBadRor() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        "abc",
        DIGITAL_OBJECT_TYPE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        STRUCTURAL_TYPE_TESTVAL,
        null
    );

    var e = assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
    assertThat(e.getMessage()).contains(ROR_DOMAIN);
  }

  @Test
  void testSpecimenHostResolvable() throws Exception {
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL);

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY);
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testSpecimenHostNotResolvable() throws Exception {
    var rorApi = "https://api.ror.org/organizations/0x123";
    given(pidResolver.getObjectName(rorApi)).willThrow(new PidResolutionException(""));
    given(pidResolver.getObjectName(not(eq(rorApi)))).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        SOURCE_SYSTEM_TESTVAL);

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY - 1);
    assertThat(hasNoDuplicateElements(result)).isTrue();
  }

  @Test
  void testBadHandle() {
    // Given
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        "abc",
        ISSUED_FOR_AGENT_TESTVAL,
        STRUCTURAL_TYPE_TESTVAL,
        null
    );

    var e = assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
    assertThat(e.getMessage()).contains(HANDLE_DOMAIN);
  }

  @Test
  void testUpdateSpecimenHostResolveName() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = generalUpdateRequest(List.of(SPECIMEN_HOST.get()), SPECIMEN_HOST_TESTVAL);
    var apiLocation = "https://api.ror.org/organizations/0x123";
    given(pidResolver.getObjectName(apiLocation)).willReturn(SPECIMEN_HOST_NAME_TESTVAL);
    ArrayList<HandleAttribute> expected = new ArrayList<>();
    expected.add(
        new HandleAttribute(SPECIMEN_HOST.index(), handle, SPECIMEN_HOST.get(),
            SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    expected.add(
        new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle, SPECIMEN_HOST_NAME.get(),
            SPECIMEN_HOST_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(), request,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(hasNoDuplicateElements(response)).isTrue();
  }

  @Test
  void testUpdateSpecimenHostNameInRequest() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = generalUpdateRequest(List.of(SPECIMEN_HOST.get(), SPECIMEN_HOST_NAME.get()),
        SPECIMEN_HOST_TESTVAL);
    ArrayList<HandleAttribute> expected = new ArrayList<>();
    expected.add(
        new HandleAttribute(SPECIMEN_HOST.index(), handle, SPECIMEN_HOST.get(),
            SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    expected.add(
        new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle, SPECIMEN_HOST_NAME.get(),
            SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(), request,
        ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(response).isEqualTo(expected);
    verifyNoInteractions(pidResolver);
    assertThat(hasNoDuplicateElements(response)).isTrue();
  }

  @Test
  void testUpdateAttributesAltLoc() throws Exception {
    // Given
    var updateRequest = genUpdateRequestAltLoc();
    var expected = genUpdateRecordAttributesAltLoc(HANDLE.getBytes(StandardCharsets.UTF_8));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(StandardCharsets.UTF_8),
        updateRequest, ObjectType.HANDLE);

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(hasNoDuplicateElements(response)).isTrue();
  }

  @Test
  void testUpdateAttributesStructuralType() throws Exception {
    // Given
    var updateRequest = MAPPER.createObjectNode();
    updateRequest.put(STRUCTURAL_TYPE.get(), STRUCTURAL_TYPE_TESTVAL.toString());
    var expected = List.of(
        new HandleAttribute(STRUCTURAL_TYPE.index(), HANDLE.getBytes(StandardCharsets.UTF_8),
            STRUCTURAL_TYPE.get(), STRUCTURAL_TYPE_TESTVAL.toString().getBytes(
            StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(StandardCharsets.UTF_8),
        updateRequest, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(hasNoDuplicateElements(response)).isTrue();
  }

  @Test
  void testTombstoneAttributes() throws Exception {
    // Given
    var expected = genTombstoneRecordRequestAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));

    // When
    var response = fdoRecordService.prepareTombstoneAttributes(HANDLE.getBytes(),
        genTombstoneRequest());

    // Then
    assertThat(response).isEqualTo(expected);
    assertThat(hasNoDuplicateElements(response)).isTrue();
  }

  private DigitalSpecimenRequest givenDigitalSpecimenRequestObjectOptionalsInit()
      throws InvalidRequestException {
    return new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.LOCAL, "b", null,
        List.of(new OtherSpecimenId("Id", "local identifier", "id for institute")),
        TopicOrigin.NATURAL, TopicDomain.LIFE,
        TopicDiscipline.ZOO, TopicCategory.AMPHIBIANS, LivingOrPreserved.LIVING,
        BaseTypeOfSpecimen.INFO, InformationArtefactType.MOVING_IMG,
        MaterialSampleType.ORG_PART, MaterialOrDigitalEntity.DIGITAL, false, "p",
        SOURCE_SYSTEM_TESTVAL);
  }

  private boolean hasCorrectElements(List<HandleAttribute> fdoRecord,
      Set<String> expectedAttributes) {
    for (var attribute : expectedAttributes) {
      if (!elementIsPresent(fdoRecord, attribute)) {
        return false;
      }
    }
    return true;
  }

  private boolean elementIsPresent(List<HandleAttribute> fdoRecord, String expectedAttribute) {
    for (var row : fdoRecord) {
      if (row.type().equals(expectedAttribute)) {
        return true;
      }
    }
    return false;
  }

  private JsonNode generalUpdateRequest(List<String> attributesToUpdate, String placeholder) {
    var requestAttributes = MAPPER.createObjectNode();
    for (var attribute : attributesToUpdate) {
      requestAttributes.put(attribute, placeholder);
    }
    return requestAttributes;
  }

  private boolean hasCorrectLocations(List<HandleAttribute> fdoRecord, String[] userLocations,
      ObjectType type) throws Exception {
    var expectedLocations = new String(setLocations(userLocations, HANDLE, type));
    for (var row : fdoRecord) {
      if (row.type().equals(LOC.get())) {
        return (new String(row.data(), StandardCharsets.UTF_8)).equals(expectedLocations);
      }
    }
    throw new IllegalStateException("No locations in fdo record");
  }

  private boolean hasNoDuplicateElements(List<HandleAttribute> fdoRecord) {
    return fdoRecord.size() == (new HashSet<>(fdoRecord).size());
  }


}
