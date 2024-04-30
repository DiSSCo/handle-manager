package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ORGANISATION_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_DOI_NAME_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.FdoType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsonSchemaValidatorTest {

  JsonSchemaValidator schemaValidator;
  private static final String UNRECOGNIZED_MSG = "Unrecognized attributes: ";
  private static final String MISSING_MSG = "Missing attributes: ";
  private static final String ENUM_MSG = "Enum errors: ";
  private static final String UNKNOWN_ATTRIBUTE = "badKey";
  private static final String UNKNOWN_VAL = "badVal";

  @BeforeEach
  void setup() {
    schemaValidator = new JsonSchemaValidator();
  }

  @Test
  void testPostHandleRequest() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }


  @Test
  void testPostHandleRequestNoLoc() {
    // Given
    var requestAttributes = new HandleRecordRequest(
        "",
        "",
        "",
        StructuralType.DIGITAL,
        null
    );
    var request = genCreateRecordRequest(requestAttributes, FdoType.HANDLE);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostDoiRequest() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostDigitalSpecimenRequest() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostMediaObjectRequest() throws Exception {
    // Given
    var request = genCreateRecordRequest(givenMediaRequestObject(), FdoType.MEDIA_OBJECT);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostAnnotationRequest() {
    // Given
    var request = genCreateRecordRequest(givenAnnotationRequestObject(), FdoType.ANNOTATION);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostOrganisationRequest() {
    // Given
    var request = genCreateRecordRequest(givenOrganisationRequestObject(),
        FdoType.ORGANISATION);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostOrganisationNullTypeRequest() {
    // Given
    var orgRequestObject = new OrganisationRequest(
        "FDO_PROFILE_TESTVAL",
        "ISSUED_FOR_AGENT_TESTVAL",
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        "REFERENT_NAME_TESTVAL",
        "PRIMARY_REFERENT_TYPE_TESTVAL",
        SPECIMEN_HOST_TESTVAL,
        null
    );
    var request = genCreateRecordRequest(orgRequestObject, FdoType.ORGANISATION);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostMappingRequest() {
    // Given
    var request = genCreateRecordRequest(givenMappingRequestObject(), FdoType.MAPPING);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostSourceSystemRequest() {
    // Given
    var request = genCreateRecordRequest(givenSourceSystemRequestObject(),
        FdoType.SOURCE_SYSTEM);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostMasRequest() {
    // Given
    var request = genCreateRecordRequest(givenMasRecordRequestObject(), FdoType.MAS);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testHandlePatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.HANDLE, PID_ISSUER.get(),
        PID_ISSUER_TESTVAL_OTHER);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testDoiPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.DOI, REFERENT_NAME.get(),
        REFERENT_DOI_NAME_TESTVAL);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testDigitalSpecimenPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.DIGITAL_SPECIMEN, SPECIMEN_HOST.get(),
        SPECIMEN_HOST_TESTVAL);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testMediaObjectPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.MEDIA_OBJECT, MEDIA_HOST.get(), MEDIA_HOST_TESTVAL);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testAnnotationPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.ANNOTATION, TARGET_TYPE.get(),
        "Annotation");

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testOrganisationPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.ORGANISATION, ORGANISATION_ID.get(), "new");

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testMappingPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.MAPPING, SOURCE_DATA_STANDARD.get(), "new");

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testSourceSystemPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.SOURCE_SYSTEM, SOURCE_SYSTEM_NAME.get(), "new");

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  @Test
  void testMasPatchRequest() {
    // Given
    var request = givenUpdateRequest(FdoType.MAS, MAS_NAME.get(), "new");

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePatchRequest(request));
  }

  private ObjectNode givenUpdateRequest(FdoType type, String key, String val) {
    ObjectNode request = MAPPER.createObjectNode();
    ObjectNode data = MAPPER.createObjectNode();
    var attributes = (ObjectNode) genUpdateRequestAltLoc();
    attributes.put(key, val);

    data.put(NODE_TYPE, type.getDigitalObjectType());
    data.put(NODE_ID, HANDLE);
    data.set(NODE_ATTRIBUTES, attributes);
    request.set("data", data);
    return request;
  }

  @Test
  void testTombstoneRequest() {
    var data = MAPPER.createObjectNode();
    data.put(NODE_ID, HANDLE);
    var attributes = genTombstoneRequest();
    data.set(NODE_ATTRIBUTES, attributes);
    var request = MAPPER.createObjectNode();
    request.set(NODE_DATA, data);

    assertDoesNotThrow(() -> schemaValidator.validatePutRequest(request));
  }

  @ParameterizedTest
  @ValueSource(strings = {"livingOrPreserved", "primarySpecimenObjectIdType"})
  void testBadEnumValueRequest(String targetEnum) {
    // Given
    ObjectNode request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);
    ((ObjectNode) request.get("data").get("attributes")).remove(targetEnum);
    ((ObjectNode) request.get("data").get("attributes")).put(targetEnum, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(ENUM_MSG).contains(targetEnum).contains(UNKNOWN_VAL);
  }

  @Test
  void testBadPostRequest() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_TYPE);
  }

  @Test
  void testBadPostHandleRequestMissingRequiredProperty() {
    // Given
    String missingAttribute = FDO_PROFILE.get();
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostHandleRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDoiRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalSpecimenRequestMissingProperty() {
    // Given
    String missingAttribute = SPECIMEN_HOST.get();
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostDigitalSpecimenRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_SPECIMEN);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostMediaObjectRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.MEDIA_OBJECT);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostMediaObjectRequestMissingProperty() {
    // Given
    String missingAttribute = MEDIA_HOST.get();
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.MEDIA_OBJECT);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPatchRequest() {
    // Given
    ObjectNode request = MAPPER.createObjectNode();
    request.set("data", MAPPER.createObjectNode()
        .put(NODE_TYPE, "bad type")
        .put(NODE_ID, HANDLE)
        .set(NODE_ATTRIBUTES, genUpdateRequestAltLoc()));

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePatchRequest(request));
    assertThat(e.getMessage()).contains(ENUM_MSG).contains(NODE_TYPE);
  }

  @ParameterizedTest
  @MethodSource("provideFdoTypes")
  void testBadPatchRequestUnknownProperty(FdoType recordType) {
    // Given
    var request = givenUpdateRequest(recordType, UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePatchRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  private static Stream<Arguments> provideFdoTypes() {
    return Stream.of(
        Arguments.of(FdoType.HANDLE),
        Arguments.of(FdoType.DOI),
        Arguments.of(FdoType.DIGITAL_SPECIMEN),
        Arguments.of(FdoType.MEDIA_OBJECT)
    );

  }

  @Test
  void testBadArchiveRequest() {
    // Given
    var request = genTombstoneRequestBatch(List.of(HANDLE)).get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_ID);

    // When
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePutRequest(request));

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_ID);
  }

  @Test
  void testBadArchiveRequestMissingProperty() {
    // Given
    var request = genTombstoneRequestBatch(List.of(HANDLE)).get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(TOMBSTONE_TEXT.get());

    // When
    Exception e = assertThrows(InvalidRequestException.class,
        () -> schemaValidator.validatePutRequest(request));

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(TOMBSTONE_TEXT.get());
  }

}
