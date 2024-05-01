package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.API_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.DOC_BUILDER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ISSUED_FOR_AGENT_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LICENSE_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LINKED_DIGITAL_OBJECT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LINKED_DO_PID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MOTIVATION_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ORCHESTRATION_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_REFERENT_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.ROR_DOMAIN;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.STRUCTURAL_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TARGET_DOI_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TARGET_TYPE_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.TRANSFORMER_FACTORY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.UI_URL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genAnnotationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMappingAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMasAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaObjectAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genOrganisationAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genSourceSystemAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTettrisRequestAttributes;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTettrisServiceRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTettrisServiceRequestObjectFull;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.setLocations;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.DctermsType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.BaseTypeOfSpecimen;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.InformationArtefactType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialOrDigitalEntity;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.MaterialSampleType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.OtherSpecimenId;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.PrimarySpecimenObjectIdType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicCategory;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicDiscipline;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicDomain;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.TopicOrigin;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import eu.dissco.core.handlemanager.web.PidResolver;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FdoRecordServiceTest {

  private static final HandleAttribute ADMIN_HANDLE;

  static {
    ADMIN_HANDLE = new HandleAttribute(100, HANDLE.getBytes(StandardCharsets.UTF_8), HS_ADMIN.get(),
        "\\\\x0FFF000000153330303A302E4E412F32302E353030302E31303235000000C8".getBytes(
            StandardCharsets.UTF_8));
  }

  private final byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
  private FdoRecordService fdoRecordService;
  @Mock
  private PidResolver pidResolver;
  @Mock
  private PidRepository pidRepository;
  @Mock
  private ApplicationProperties appProperties;
  @Mock
  Environment environment;
  @Mock
  ProfileProperties profileProperties;
  private MockedStatic<Instant> mockedStatic;
  private MockedStatic<Clock> mockedClock;
  private static final String ROR_API = "https://api.ror.org/organizations/";

  @BeforeEach
  void init() {
    fdoRecordService = new FdoRecordService(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver,
        MAPPER, appProperties, profileProperties);
    initTime();
    given(appProperties.getApiUrl()).willReturn(API_URL);
    given(appProperties.getOrchestrationUrl()).willReturn(ORCHESTRATION_URL);
    given(appProperties.getUiUrl()).willReturn(UI_URL);
    given(environment.matchesProfiles(Profiles.DOI)).willReturn(false);
  }

  @AfterEach
  void destroy() {
    mockedStatic.close();
    mockedClock.close();
  }

  @Test
  void testPrepareHandleRecordAttributes() throws Exception {
    // Given
    var request = givenHandleRecordRequestObject();
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var expected = genHandleRecordAttributes(handle, FdoType.HANDLE);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE);

    // Then
    assertThat(result).hasSameSizeAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareDoiRecordAttributes() throws Exception {
    // Given
    var request = givenDoiRecordRequestObject();
    var expected = genDoiRecordAttributes(handle, FdoType.DOI);
    expected.add(ADMIN_HANDLE);
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);

    // When
    var result = fdoRecordService.prepareDoiRecordAttributes(request, handle, FdoType.DOI);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareDoiRecordAttributesDoiProfile() throws Exception {
    // Given
    var request = givenDoiRecordRequestObject();
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var expected = genDoiRecordAttributes(handle, FdoType.DOI);
    expected.add(ADMIN_HANDLE);
    var replaceThis = new HandleAttribute(LOC, handle, new String(
        setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8),
            FdoType.DOI, false), StandardCharsets.UTF_8));
    var withThis = new HandleAttribute(LOC, handle, new String(
        setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8),
            FdoType.DOI, true), StandardCharsets.UTF_8));
    expected.remove(replaceThis);
    expected.add(withThis);

    // When
    var result = fdoRecordService.prepareDoiRecordAttributes(request, handle, FdoType.DOI);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareMediaObjectAttributesMandatory() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenMediaRequestObject();
    var expected = genMediaObjectAttributes(handle);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareMediaObjectAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareMediaObjectAttributesOptional() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL).willReturn(MEDIA_HOST_NAME_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = new MediaObjectRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL, null,
        MediaFormat.TEXT, Boolean.TRUE, LINKED_DO_PID_TESTVAL, LINKED_DIGITAL_OBJECT_TYPE_TESTVAL,
        "a", HANDLE, PrimarySpecimenObjectIdType.RESOLVABLE, "b", DctermsType.IMAGE, "jpeg", "c",
        "license", "license", "c", "d", null, "e");
    var expected = genMediaObjectAttributes(handle, request);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareMediaObjectAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareMediaObjectFullAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL).willReturn(MEDIA_HOST_NAME_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = new MediaObjectRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL,
        MEDIA_HOST_NAME_TESTVAL, MediaFormat.TEXT, Boolean.TRUE, LINKED_DO_PID_TESTVAL,
        LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, "a", "b", PrimarySpecimenObjectIdType.GLOBAL, "d",
        DctermsType.IMAGE, "e", "f", LICENSE_NAME_TESTVAL, "g", "h", "i",
        PrimarySpecimenObjectIdType.LOCAL, "j");
    var expected = genMediaObjectAttributes(handle, request);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareMediaObjectAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL).willReturn(SPECIMEN_HOST_NAME_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenDigitalSpecimenRequestObjectNullOptionals();
    var expected = genDigitalSpecimenAttributes(handle, request);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributesQNumber() throws Exception {
    // Given
    String qid = "https://www.wikidata.org/wiki/Q12345";
    String qidUrl = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/Q12345";
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    given(pidResolver.resolveQid(any())).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, qid, null, "PhysicalId",
        null, null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null);

    // When
    fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    then(pidResolver).should().resolveQid(qidUrl);
  }

  @Test
  void testPrepareDigitalSpecimenRecordMandatoryAttributesBadSpecimenHost() throws Exception {
    // Given
    String specimenId = "12345";
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, specimenId, null,
        "PhysicalId", null, null, NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null);

    // When
    assertThrows(PidResolutionException.class,
        () -> fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle));
  }

  @Test
  void testPrepareDigitalSpecimenRecordOptionalAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL).willReturn(SPECIMEN_HOST_NAME_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenDigitalSpecimenRequestObjectOptionalsInit();
    var expected = genDigitalSpecimenAttributes(handle, request);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareAnnotationAttributesOptional() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
        genHandleRecordAttributes(handle, FdoType.ANNOTATION));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenAnnotationRequestObject();
    var expected = genAnnotationAttributes(handle, true);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareAnnotationAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareAnnotationAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
        genHandleRecordAttributes(handle, FdoType.ANNOTATION));
    given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
        genHandleRecordAttributes(handle, FdoType.ANNOTATION));
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = new AnnotationRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, TARGET_DOI_TESTVAL, TARGET_TYPE_TESTVAL, MOTIVATION_TESTVAL, null);
    var expected = genAnnotationAttributes(handle, false);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareAnnotationAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareMasRecordAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenMasRecordRequestObject();
    var expected = genMasAttributes(handle);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareMasRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareMappingAttributes() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenMappingRequestObject();
    var expected = genMappingAttributes(handle);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareMappingAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }


  @Test
  void testPrepareSourceSystemAttributes() throws Exception {
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenSourceSystemRequestObject();
    var expected = genSourceSystemAttributes(handle);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareSourceSystemAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareTettrisServiceMin() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var expected = genHandleRecordAttributes(handle, FdoType.TETTRIS_SERVICE);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareTettrisServiceAttributes(givenTettrisServiceRequest(),
        handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareTettrisServiceFull() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var expected = genTettrisRequestAttributes(handle);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareTettrisServiceAttributes(
        givenTettrisServiceRequestObjectFull(), handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPrepareOrganisationAttributes() throws Exception {
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL).willReturn(SPECIMEN_HOST_NAME_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = givenOrganisationRequestObject();
    var expected = genOrganisationAttributes(handle, request);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareOrganisationAttributes(request, handle);

    // Then
    assertThat(result).hasSameElementsAs(expected).hasSameSizeAs(expected);

  }

  @Test
  void testPidIssuerIsRor() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var request = new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL, ISSUED_FOR_AGENT_TESTVAL,
        STRUCTURAL_TYPE_TESTVAL, null);
    var expected = genHandleRecordAttributes(handle, FdoType.HANDLE);
    expected.add(ADMIN_HANDLE);

    // When
    var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE);

    // Then
    assertThat(result).hasSameSizeAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testPidIssuerBad() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL, "abc", STRUCTURAL_TYPE_TESTVAL,
        null);

    // Then
    var e = assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE));
    assertThat(e.getMessage()).contains(ROR_DOMAIN).contains(HANDLE_DOMAIN);
  }

  @Test
  void testBadRor() throws Exception {
    // Given
    given(pidResolver.getObjectName(any())).willReturn("placeholder");
    var request = new HandleRecordRequest("abc", ISSUED_FOR_AGENT_TESTVAL, STRUCTURAL_TYPE_TESTVAL,
        null);

    var e = assertThrows(InvalidRequestException.class,
        () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE));
    assertThat(e.getMessage()).contains(ROR_DOMAIN);
  }

  @Test
  void testSpecimenHostResolvable() throws Exception {
    given(pidResolver.getObjectName(any())).willReturn(PID_ISSUER_TESTVAL_OTHER)
        .willReturn(ISSUED_FOR_AGENT_TESTVAL);
    given(profileProperties.getDomain()).willReturn(HANDLE_DOMAIN);
    var expected = genDigitalSpecimenAttributes(handle);
    expected.add(ADMIN_HANDLE);

    var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL,
        null, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null);

    // When
    var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle);

    // Then
    assertThat(result).hasSameSizeAs(expected).hasSameSizeAs(expected);
  }

  @Test
  void testSpecimenHostNotResolvable() throws Exception {
    var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL,
        null, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null,
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null);

    var specimenHostRorApi = request.getSpecimenHost().replace(ROR_DOMAIN, ROR_API);
    given(pidResolver.getObjectName(specimenHostRorApi)).willThrow(PidResolutionException.class);
    given(pidResolver.getObjectName(not(eq(specimenHostRorApi)))).willReturn("placeholder");

    // Then
    assertThrows(PidResolutionException.class,
        () -> fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle));
  }

  @Test
  void testUpdateSpecimenHostResolveName() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var request = generalUpdateRequest(List.of(SPECIMEN_HOST.get()), SPECIMEN_HOST_TESTVAL);
    var apiLocation = "https://api.ror.org/organizations/0x123";
    given(pidResolver.getObjectName(apiLocation)).willReturn(SPECIMEN_HOST_NAME_TESTVAL);
    ArrayList<HandleAttribute> expected = new ArrayList<>();
    expected.add(new HandleAttribute(SPECIMEN_HOST.index(), handle, SPECIMEN_HOST.get(),
        SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    expected.add(new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle, SPECIMEN_HOST_NAME.get(),
        SPECIMEN_HOST_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(), request,
        FdoType.DIGITAL_SPECIMEN);

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
    expected.add(new HandleAttribute(SPECIMEN_HOST.index(), handle, SPECIMEN_HOST.get(),
        SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    expected.add(new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle, SPECIMEN_HOST_NAME.get(),
        SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(), request,
        FdoType.DIGITAL_SPECIMEN);

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
        updateRequest, FdoType.HANDLE);

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
            STRUCTURAL_TYPE.get(),
            STRUCTURAL_TYPE_TESTVAL.toString().getBytes(StandardCharsets.UTF_8)));

    // When
    var response = fdoRecordService.prepareUpdateAttributes(HANDLE.getBytes(StandardCharsets.UTF_8),
        updateRequest, FdoType.DIGITAL_SPECIMEN);

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
    return new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL, PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL, REFERENT_NAME_TESTVAL, PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL,
        SPECIMEN_HOST_NAME_TESTVAL, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
        PrimarySpecimenObjectIdType.LOCAL, "b", NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null,
        List.of(new OtherSpecimenId("Id", "local identifier")), TopicOrigin.NATURAL,
        TopicDomain.LIFE, TopicDiscipline.ZOO, TopicCategory.AMPHIBIANS, LivingOrPreserved.LIVING,
        BaseTypeOfSpecimen.INFO, InformationArtefactType.MOVING_IMG, MaterialSampleType.ORG_PART,
        MaterialOrDigitalEntity.DIGITAL, false, HANDLE_ALT, HANDLE_ALT);
  }

  private JsonNode generalUpdateRequest(List<String> attributesToUpdate, String placeholder) {
    var requestAttributes = MAPPER.createObjectNode();
    for (var attribute : attributesToUpdate) {
      requestAttributes.put(attribute, placeholder);
    }
    return requestAttributes;
  }

  private boolean hasNoDuplicateElements(List<HandleAttribute> fdoRecord) {
    return fdoRecord.size() == (new HashSet<>(fdoRecord).size());
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

}
