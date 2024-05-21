package eu.dissco.core.handlemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.fdo.*;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.MediaFormat;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.media.PrimaryMediaObjectType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.*;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import eu.dissco.core.handlemanager.web.PidResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.*;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FdoRecordServiceTest {

    private static final Set<String> HANDLE_FIELDS = Set.of(FDO_PROFILE.get(),
            FDO_RECORD_LICENSE.get(), DIGITAL_OBJECT_TYPE.get(), DIGITAL_OBJECT_NAME.get(), PID.get(),
            PID_ISSUER.get(), PID_ISSUER_NAME.get(), ISSUED_FOR_AGENT.get(), ISSUED_FOR_AGENT_NAME.get(),
            PID_RECORD_ISSUE_DATE.get(), PID_RECORD_ISSUE_NUMBER.get(), STRUCTURAL_TYPE.get(),
            PID_STATUS.get(), HS_ADMIN.get(), LOC.get());

    private static final Set<String> DOI_FIELDS = Set.of(REFERENT_TYPE.get(), REFERENT_DOI_NAME.get(),
            REFERENT_NAME.get(), PRIMARY_REFERENT_TYPE.get());

    private static final Set<String> DS_FIELDS_MANDATORY = Set.of(SPECIMEN_HOST.get(),
            SPECIMEN_HOST_NAME.get(), PRIMARY_SPECIMEN_OBJECT_ID.get(),
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE.get(), NORMALISED_SPECIMEN_OBJECT_ID.get());

    private static final Set<String> DS_FIELDS_OPTIONAL = Set.of(
            PRIMARY_SPECIMEN_OBJECT_ID_NAME.get(), OTHER_SPECIMEN_IDS.get(), TOPIC_ORIGIN.get(),
            TOPIC_DOMAIN.get(), TOPIC_DISCIPLINE.get(), TOPIC_CATEGORY.get(), LIVING_OR_PRESERVED.get(),
            BASE_TYPE_OF_SPECIMEN.get(), INFORMATION_ARTEFACT_TYPE.get(), MATERIAL_SAMPLE_TYPE.get(),
            MATERIAL_OR_DIGITAL_ENTITY.get(), MARKED_AS_TYPE.get(), WAS_DERIVED_FROM_ENTITY.get(),
            CATALOG_IDENTIFIER.get());

    private static final Set<String> MEDIA_FIELDS_MANDATORY = Set.of(MEDIA_HOST.get(),
            MEDIA_HOST_NAME.get(), IS_DERIVED_FROM_SPECIMEN.get(), LINKED_DO_PID.get(),
            LINKED_DO_TYPE.get(), PRIMARY_MEDIA_ID.get(), RIGHTSHOLDER_PID.get(),
            RIGHTSHOLDER_NAME.get());

    private static final Set<String> MEDIA_FIELDS_OPTIONAL = Set.of(MEDIA_FORMAT.get(),
            LINKED_ATTRIBUTE.get(), PRIMARY_MO_ID_TYPE.get(), PRIMARY_MO_ID_NAME.get(),
            PRIMARY_MO_TYPE.get(), MEDIA_MIME_TYPE.get(), DERIVED_FROM_ENTITY.get(), LICENSE_NAME.get(),
            LICENSE_URL.get(), RIGHTSHOLDER_PID_TYPE.get(), DC_TERMS_CONFORMS.get());

    private static final Set<String> ANNOTATION_FIELDS_MANDATORY = Set.of(TARGET_PID.get(),
            TARGET_TYPE.get(),
            MOTIVATION.get());
    private static final Set<String> ANNOTATION_FIELDS_OPTIONAL = Set.of(ANNOTATION_HASH.get());

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
    private static final int HANDLE_QTY = HANDLE_FIELDS.size();
    private static final int DOI_QTY = HANDLE_QTY + DOI_FIELDS.size();
    private static final int MEDIA_QTY = DOI_QTY + MEDIA_FIELDS_MANDATORY.size();
    private static final int MEDIA_OPTIONAL_QTY = MEDIA_QTY + MEDIA_FIELDS_OPTIONAL.size();
    private static final int DS_MANDATORY_QTY = DOI_QTY + DS_FIELDS_MANDATORY.size();
    private static final int DS_OPTIONAL_QTY = DS_MANDATORY_QTY + DS_FIELDS_OPTIONAL.size();
    private static final int ANNOTATION_MANDATORY_QTY =
            HANDLE_QTY + ANNOTATION_FIELDS_MANDATORY.size();
    private static final int ANNOTATION_OPTIONAL_QTY =
            ANNOTATION_MANDATORY_QTY + ANNOTATION_FIELDS_OPTIONAL.size();
    private static final String ROR_API = "https://api.ror.org/organizations/";

    @BeforeEach
    void init() {
        fdoRecordService = new FdoRecordService(TRANSFORMER_FACTORY, DOC_BUILDER_FACTORY, pidResolver,
                MAPPER, appProperties, profileProperties);
        given(appProperties.getApiUrl()).willReturn(API_URL);
        given(appProperties.getOrchestrationUrl()).willReturn(ORCHESTRATION_URL);
        given(appProperties.getUiUrl()).willReturn(UI_URL);
        given(environment.matchesProfiles(Profiles.DOI)).willReturn(false);
    }


    @Test
    void testPrepareHandleRecordAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenHandleRecordRequestObject();

        // When
        var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE);

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
        var result = fdoRecordService.prepareDoiRecordAttributes(request, handle, FdoType.HANDLE);

        // Then
        assertThat(result).hasSize(DOI_QTY);
        assertThat(
                hasCorrectLocations(result, request.getLocations(), FdoType.HANDLE, false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareDoiRecordAttributesDoiProfile() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenDoiRecordRequestObject();
        given(environment.matchesProfiles(Profiles.DOI)).willReturn(true);

        // When
        var result = fdoRecordService.prepareDoiRecordAttributes(request, handle, FdoType.DOI);

        // Then
        assertThat(result).hasSize(DOI_QTY);
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.DOI, true)).isTrue();
    }

    @Test
    void testPrepareMediaObjectAttributesMandatory() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenMediaRequestObject();

        // When
        var result = fdoRecordService.prepareMediaObjectAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(MEDIA_QTY);
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.MEDIA_OBJECT,
                false)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
        assertThat(hasCorrectElements(result, MEDIA_FIELDS_MANDATORY)).isTrue();
    }

    @Test
    void testPrepareMediaObjectAttributesOptional() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new MediaObjectRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL, null, MediaFormat.TEXT, Boolean.TRUE,
                LINKED_DO_PID_TESTVAL, LINKED_DIGITAL_OBJECT_TYPE_TESTVAL, "a", HANDLE,
                PrimarySpecimenObjectIdType.RESOLVABLE, "b", PrimaryMediaObjectType.IMAGE, "jpeg", "c",
                "license",
                "license", "c", "d", PrimarySpecimenObjectIdType.LOCAL, "e");

        // When
        var result = fdoRecordService.prepareMediaObjectAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(MEDIA_OPTIONAL_QTY);
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.MEDIA_OBJECT,
                false)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
        assertThat(hasCorrectElements(result, MEDIA_FIELDS_MANDATORY)).isTrue();
    }

    @Test
    void testPrepareMediaObjectFullAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new MediaObjectRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, MEDIA_HOST_TESTVAL, MEDIA_HOST_NAME_TESTVAL,
                MediaFormat.TEXT, Boolean.TRUE, LINKED_DO_PID_TESTVAL, LINKED_DIGITAL_OBJECT_TYPE_TESTVAL,
                "a", "b", PrimarySpecimenObjectIdType.GLOBAL, "d", PrimaryMediaObjectType.IMAGE, "e", "f",
                LICENSE_NAME_TESTVAL, "g", "h", "i", PrimarySpecimenObjectIdType.LOCAL, "j");

        // When
        var result = fdoRecordService.prepareMediaObjectAttributes(request, handle
        );

        // Then

        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.MEDIA_OBJECT,
                false)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
        assertThat(hasCorrectElements(result, MEDIA_FIELDS_OPTIONAL)).isTrue();
        assertThat(result).hasSize(MEDIA_OPTIONAL_QTY);
    }

    @Test
    void testPrepareMediaObjectAttributesNamesDontResolve() throws Exception {
        // Given
        var request = givenMediaRequestObject();
        var mediaHostRor = MEDIA_HOST_TESTVAL.replace(ROR_DOMAIN, ROR_API);
        var placeholder = "placeholder";
        given(pidResolver.getObjectName(not(eq(mediaHostRor)))).willReturn(placeholder);
        given(pidResolver.getObjectName(mediaHostRor)).willThrow(PidResolutionException.class);

        // Then
        assertThrows(PidResolutionException.class,
                () -> fdoRecordService.prepareMediaObjectAttributes(request, handle
                ));
    }

    @Test
    void testPrepareDigitalSpecimenRecordMandatoryAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenDigitalSpecimenRequestObjectNullOptionals();

        // When
        var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(DS_MANDATORY_QTY);
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.DIGITAL_SPECIMEN,
                false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareDigitalSpecimenRecordMandatoryAttributesQNumber() throws Exception {
        // Given
        String qid = "https://www.wikidata.org/wiki/Q12345";
        String qidUrl = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/Q12345";
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        given(pidResolver.resolveQid(any())).willReturn("placeholder");
        var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, qid, null, "PhysicalId", null, null,
                NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        // When
        fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
        );

        // Then
        then(pidResolver).should().resolveQid(qidUrl);
    }

    @Test
    void testPrepareDigitalSpecimenRecordMandatoryAttributesBadSpecimenHost() throws Exception {
        // Given
        String specimenId = "12345";
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, specimenId, null, "PhysicalId", null, null,
                NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        // When
        assertThrows(PidResolutionException.class,
                () -> fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
                ));
    }

    @Test
    void testPrepareDigitalSpecimenRecordOptionalAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenDigitalSpecimenRequestObjectOptionalsInit();

        // When
        var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(DS_OPTIONAL_QTY);
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.DIGITAL_SPECIMEN,
                false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DOI_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, DS_FIELDS_MANDATORY)).isTrue();
        assertThat(hasCorrectElements(result, DS_FIELDS_OPTIONAL)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareAnnotationAttributesOptional() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
                genHandleRecordAttributes(handle, FdoType.ANNOTATION));
        var request = givenAnnotationRequestObject();

        // When
        var result = fdoRecordService.prepareAnnotationAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(ANNOTATION_OPTIONAL_QTY);
        assertThat(
                hasCorrectLocations(result, request.getLocations(), FdoType.ANNOTATION, false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, ANNOTATION_FIELDS_MANDATORY)).isTrue();
        assertThat(hasCorrectElements(result, ANNOTATION_FIELDS_OPTIONAL)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareAnnotationAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        given(pidRepository.resolveHandleAttributes(any(byte[].class))).willReturn(
                genHandleRecordAttributes(handle, FdoType.ANNOTATION));
        var request = new AnnotationRequest(
                ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER,
                LOC_TESTVAL,
                TARGET_DOI_TESTVAL,
                TARGET_TYPE_TESTVAL,
                MOTIVATION_TESTVAL,
                null
        );

        // When
        var result = fdoRecordService.prepareAnnotationAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(ANNOTATION_MANDATORY_QTY);
        assertThat(
                hasCorrectLocations(result, request.getLocations(), FdoType.ANNOTATION, false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, ANNOTATION_FIELDS_MANDATORY)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareMasRecordAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenMasRecordRequestObject();

        // When
        var result = fdoRecordService.prepareMasRecordAttributes(request, handle);

        // Then
        assertThat(result).hasSize(HANDLE_QTY + 1);
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.MAS, false)).isTrue();
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectElements(result, Set.of(MAS_NAME.get()))).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareMappingAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenMappingRequestObject();

        // When
        var result = fdoRecordService.prepareMappingAttributes(request, handle);

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
        var result = fdoRecordService.prepareSourceSystemAttributes(request, handle
        );

        // Then
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasCorrectLocations(result, request.getLocations(), FdoType.SOURCE_SYSTEM,
                false)).isTrue();
        assertThat(result).hasSize(HANDLE_QTY + 1);
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPrepareOrganisationAttributes() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = givenOrganisationRequestObject();

        // When
        var result = fdoRecordService.prepareOrganisationAttributes(request, handle
        );

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
        var request = new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL,
                ISSUED_FOR_AGENT_TESTVAL, STRUCTURAL_TYPE_TESTVAL, null);

        // When
        var result = fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE);

        // Then
        assertThat(result).hasSize(HANDLE_QTY);
        assertThat(hasCorrectElements(result, HANDLE_FIELDS)).isTrue();
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testPidIssuerBad() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new HandleRecordRequest(ISSUED_FOR_AGENT_TESTVAL,
                "abc", STRUCTURAL_TYPE_TESTVAL, null);

        // Then
        var e = assertThrows(InvalidRequestException.class,
                () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE));
        assertThat(e.getMessage()).contains(ROR_DOMAIN).contains(HANDLE_DOMAIN);
    }

    @Test
    void testBadRor() throws Exception {
        // Given
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new HandleRecordRequest("abc",
                ISSUED_FOR_AGENT_TESTVAL, STRUCTURAL_TYPE_TESTVAL, null);

        var e = assertThrows(InvalidRequestException.class,
                () -> fdoRecordService.prepareHandleRecordAttributes(request, handle, FdoType.HANDLE));
        assertThat(e.getMessage()).contains(ROR_DOMAIN);
    }

    @Test
    void testSpecimenHostResolvable() throws Exception {
        given(pidResolver.getObjectName(any())).willReturn("placeholder");
        var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL, null,
                PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null,
                NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        // When
        var result = fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
        );

        // Then
        assertThat(result).hasSize(DS_MANDATORY_QTY);
        assertThat(hasNoDuplicateElements(result)).isTrue();
    }

    @Test
    void testSpecimenHostNotResolvable() throws Exception {
        var request = new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL, null,
                PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null,
                NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null);

        var specimenHostRorApi = request.getSpecimenHost().replace(ROR_DOMAIN, ROR_API);
        given(pidResolver.getObjectName(specimenHostRorApi)).willThrow(PidResolutionException.class);
        given(pidResolver.getObjectName(not(eq(specimenHostRorApi)))).willReturn("placeholder");

        // Then
        assertThrows(PidResolutionException.class,
                () -> fdoRecordService.prepareDigitalSpecimenRecordAttributes(request, handle
                ));
    }

    @Test
    void testUpdateSpecimenHostResolveName() throws Exception {
        // Given
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
        return new DigitalSpecimenRequest(ISSUED_FOR_AGENT_TESTVAL,
                PID_ISSUER_TESTVAL_OTHER, LOC_TESTVAL, REFERENT_NAME_TESTVAL,
                PRIMARY_REFERENT_TYPE_TESTVAL, SPECIMEN_HOST_TESTVAL, SPECIMEN_HOST_NAME_TESTVAL,
                PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, PrimarySpecimenObjectIdType.LOCAL, "b",
                NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL, null,
                List.of(new OtherSpecimenId("Id", "local identifier")),
                TopicOrigin.NATURAL, TopicDomain.LIFE, TopicDiscipline.ZOO, TopicCategory.AMPHIBIANS,
                LivingOrPreserved.LIVING, BaseTypeOfSpecimen.INFO, InformationArtefactType.MOVING_IMG,
                MaterialSampleType.ORG_PART, MaterialOrDigitalEntity.DIGITAL, false, HANDLE_ALT,
                HANDLE_ALT);
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
            if (row.getType().equals(expectedAttribute)) {
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
                                        FdoType type, boolean isDoiProfileTest) throws Exception {
        var expectedLocations = new String(setLocations(userLocations, HANDLE, type, isDoiProfileTest));
        for (var row : fdoRecord) {
            if (row.getType().equals(LOC.get())) {
                return (new String(row.getData(), StandardCharsets.UTF_8)).equals(expectedLocations);
            }
        }
        throw new IllegalStateException("No locations in fdo record");
    }

    private boolean hasNoDuplicateElements(List<HandleAttribute> fdoRecord) {
        return fdoRecord.size() == (new HashSet<>(fdoRecord).size());
    }


}
