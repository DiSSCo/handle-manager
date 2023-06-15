package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.domain.PidRecords.ACCESS_RESTRICTED;
import static eu.dissco.core.handlemanager.domain.PidRecords.ANNOTATION_TOPIC;
import static eu.dissco.core.handlemanager.domain.PidRecords.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.LINKED_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.REPLACE_OR_APPEND;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_DIGITAL_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.WAS_DERIVED_FROM;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.FDO_PROFILE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
class FdoRecordBuilderTest {

  private static final Set<String> HANDLE_FIELDS = Set.of(FDO_PROFILE, FDO_RECORD_LICENSE,
      DIGITAL_OBJECT_TYPE, DIGITAL_OBJECT_NAME, PID, PID_ISSUER, PID_ISSUER_NAME, ISSUED_FOR_AGENT,
      ISSUED_FOR_AGENT_NAME, PID_RECORD_ISSUE_DATE, PID_RECORD_ISSUE_NUMBER, STRUCTURAL_TYPE,
      PID_STATUS, HS_ADMIN, LOC);

  private static final Set<String> DOI_FIELDS = Set.of(REFERENT_TYPE, REFERENT_DOI_NAME,
      REFERENT_NAME, PRIMARY_REFERENT_TYPE, REFERENT);
  private static final Set<String> DS_FIELDS_MANDATORY = Set.of(SPECIMEN_HOST, SPECIMEN_HOST_NAME,
      PRIMARY_SPECIMEN_OBJECT_ID, PRIMARY_SPECIMEN_OBJECT_ID_TYPE, MATERIAL_OR_DIGITAL_ENTITY);

  private static final Set<String> DS_FIELDS_OPTIONAL = Set.of(PRIMARY_SPECIMEN_OBJECT_ID_NAME,
      OTHER_SPECIMEN_IDS, TOPIC_ORIGIN, TOPIC_DISCIPLINE, OBJECT_TYPE, LIVING_OR_PRESERVED,
      BASE_TYPE_OF_SPECIMEN, INFORMATION_ARTEFACT_TYPE,
      MATERIAL_SAMPLE_TYPE, MARKED_AS_TYPE, WAS_DERIVED_FROM);

  private static final Set<String> ANNOTATION_FIELDS = Set.of(SUBJECT_DIGITAL_OBJECT_ID,
      ANNOTATION_TOPIC, REPLACE_OR_APPEND, ACCESS_RESTRICTED, LINKED_URL);

  private final byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
  private FdoRecordBuilder fdoRecordBuilder;
  @Mock
  private PidResolverComponent pidResolver;
  @Mock
  private HandleRepository handleRepository;
  @Mock
  private ApplicationProperties appProperties;
  private static final int HANDLE_QTY = 15;
  private static final int DOI_QTY = 20;
  private static final int MEDIA_QTY = 25;
  private static final int DS_MANDATORY_QTY = 25;
  private static final int DS_OPTIONAL_QTY = 37;
  private static final int BOTANY_QTY = 25;
  private static final int ANNOTATION_QTY = 20;

  @BeforeEach
  void init() {
    fdoRecordBuilder = new FdoRecordBuilder(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver,
        MAPPER, handleRepository, appProperties);
  }

  @Test
  void testPrepareHandleRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenHandleRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(HANDLE_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
  }

  @Test
  void testPrepareDoiRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDoiRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDoiRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(DOI_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
  }

  @Test
  void testPrepareMediaObjectAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenMediaRequestObject();

    // When
    var result = fdoRecordBuilder.prepareMediaObjectAttributes(request, handle, ObjectType.MEDIA_OBJECT);

    // Then
    assertThat(result).hasSize(MEDIA_QTY);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDigitalSpecimenRequestObjectNullOptionals();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
  }

  @Test
  void testPrepareDigitalSpecimenRecordOptionalAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDigitalSpecimenRequestObjectOptionalsInit();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_OPTIONAL_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_OPTIONAL)).isTrue();
  }

  @Test
  void testPrepareAnnotationAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(handleRepository.resolveHandleAttributes(any(byte[].class))).willReturn(genHandleRecordAttributes(handle, ObjectType.ANNOTATION));
    var request = givenAnnotationRequestObject();

    // When
    var result = fdoRecordBuilder.prepareAnnotationAttributes(request, handle, ObjectType.ANNOTATION);

    // Then
    assertThat(result).hasSize(ANNOTATION_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, ANNOTATION_FIELDS)).isTrue();
  }

  @Test
  void testPrepareAnnotationAttributesPidResolution() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(handleRepository.resolveHandleAttributes(any(byte[].class))).willReturn(new ArrayList<>());
    var request = givenAnnotationRequestObject();

    // Then
    assertThrows(PidResolutionException.class, () -> fdoRecordBuilder.prepareAnnotationAttributes(request, handle, ObjectType.ANNOTATION));
  }

  @Test
  void testPrepareMappingAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenMappingRequestObject();

    // When
    var result = fdoRecordBuilder.prepareMappingAttributes(request, handle, ObjectType.MAPPING);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(result).hasSize(HANDLE_QTY+1);
  }


  @Test
  void testPrepareSourceSystemAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenSourceSystemRequestObject();

    // When
    var result = fdoRecordBuilder.prepareSourceSystemAttributes(request, handle, ObjectType.SOURCE_SYSTEM);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(result).hasSize(HANDLE_QTY+1);
  }

  @Test
  void testPrepareOrganisationAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenOrganisationRequestObject();

    // When
    var result = fdoRecordBuilder.prepareOrganisationAttributes(request, handle, ObjectType.ORGANISATION);

    // Then
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(result).hasSize(DOI_QTY+3);
  }

  @Test
  void testPrepareDigitalSpecimenBotanyAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = genDigitalSpecimenBotanyRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenBotanyRecordAttributes(request, handle, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(BOTANY_QTY);
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
    var result = fdoRecordBuilder.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE);

    // Then
    assertThat(result).hasSize(HANDLE_QTY);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
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
        () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
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
        () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
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
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY);
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
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        null,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(result).hasSize(DS_MANDATORY_QTY - 1);
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
        () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle, ObjectType.HANDLE));
    assertThat(e.getMessage()).contains(HANDLE_DOMAIN);
  }

  @Test
  void testUpdateAttributesAltLoc() throws Exception {
    // Given
    var updateRequest = genUpdateRequestAltLoc();
    var expected = genUpdateRecordAttributesAltLoc(HANDLE.getBytes(StandardCharsets.UTF_8), ObjectType.HANDLE);

    // When
    var response = fdoRecordBuilder.prepareUpdateAttributes(HANDLE.getBytes(StandardCharsets.UTF_8),
        updateRequest, ObjectType.HANDLE);

    // Then
    assertThat(response).isEqualTo(expected);
  }

  @Test
  void testUpdateAttributesStructuralType() throws Exception {
    // Given
    var updateRequest = MAPPER.createObjectNode();
    updateRequest.put(STRUCTURAL_TYPE, STRUCTURAL_TYPE_TESTVAL);
    var expected = List.of(
        new HandleAttribute(FIELD_IDX.get(STRUCTURAL_TYPE), HANDLE.getBytes(StandardCharsets.UTF_8),
            STRUCTURAL_TYPE, STRUCTURAL_TYPE_TESTVAL.getBytes(
            StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordBuilder.prepareUpdateAttributes(HANDLE.getBytes(StandardCharsets.UTF_8),
        updateRequest, ObjectType.DIGITAL_SPECIMEN);

    // Then
    assertThat(response).isEqualTo(expected);
  }


  private DigitalSpecimenRequest givenDigitalSpecimenRequestObjectOptionalsInit()
      throws InvalidRequestException {
    return new DigitalSpecimenRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.CETAF, "b", null, new String[]{"d"}, "e", "f", "g", "h",
        LivingOrPreserved.PRESERVED, "j", "k", "l", "m", false, "o");
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
}
