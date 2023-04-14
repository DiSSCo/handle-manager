package eu.dissco.core.handlemanager.component;

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
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.STRUCTURAL_TYPE;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
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
  private static final Set<String> TOMBSTONE_FIELDS = Set.of();

  private final byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
  private FdoRecordBuilder fdoRecordBuilder;
  @Mock
  private PidResolverComponent pidResolver;

  @BeforeEach
  void init() throws Exception {
    fdoRecordBuilder = new FdoRecordBuilder(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver, MAPPER);
  }

  @Test
  void testPrepareHandleRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenHandleRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareHandleRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(15);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
  }

  @Test
  void testPrepareDoiRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDoiRecordRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDoiRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(20);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
  }

  @Test
  void testPrepareMediaObjectAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = genMediaRequestObject();

    // When
    var result = fdoRecordBuilder.prepareMediaObjectAttributes(request, handle);

    // Then
    assertThat(result).hasSize(24);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = givenDigitalSpecimenRequestObjectNullOptionals();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(25);
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
    var result = fdoRecordBuilder.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(37);
    assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
    assertThat(hasCorrectElements(result, DS_FIELDS_OPTIONAL)).isTrue();
  }

  @Test
  void testPrepareDigitalSpecimenBotanyAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = genDigitalSpecimenBotanyRequestObject();

    // When
    var result = fdoRecordBuilder.prepareDigitalSpecimenBotanyRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(25);
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
    var result = fdoRecordBuilder.prepareHandleRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSize(15);
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
    var e = assertThrows(InvalidRequestException.class, () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle));
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

    var e = assertThrows(InvalidRequestException.class, () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle));
    assertThat(e.getMessage()).contains(ROR_DOMAIN);
  }

  @Test
  void testBadHandle(){
    // Given
    var request = new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        "abc",
        ISSUED_FOR_AGENT_TESTVAL,
        STRUCTURAL_TYPE_TESTVAL,
        null
    );

    var e = assertThrows(InvalidRequestException.class, () -> fdoRecordBuilder.prepareHandleRecordAttributes(request, handle));
    assertThat(e.getMessage()).contains(HANDLE_DOMAIN);
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
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PhysicalIdType.CETAF, "b", null, new String[]{"d"}, "e", "f", "g", "h",
        LivingOrPreserved.PRESERVED, "j", "k", "l", "m", "n", "o");
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