package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_TEXT;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.jsonapi.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.LOC_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_TESTVAL_OTHER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenAnnotationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDataMappingRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDigitalSpecimenRequestObjectNullOptionals;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenMasRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenOrganisationRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSourceSystemRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenTombstoneRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.domain.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
  void testPostDigitalMediaRequest() throws Exception {
    // Given
    var request = genCreateRecordRequest(givenDigitalMediaRequestObject(), FdoType.DIGITAL_MEDIA);

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
    var request = genCreateRecordRequest(givenDataMappingRequestObject(), FdoType.DATA_MAPPING);

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
  void testTombstoneRequest() {
    // Given
    var request = givenTombstoneRequest().get(0);

    // Then
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
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(ENUM_MSG).contains(targetEnum).contains(UNKNOWN_VAL);
  }

  @Test
  void testBadPostRequest() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);

    // Then
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_TYPE);
  }

  @Test
  void testBadPostHandleRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), FdoType.HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDoiRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), FdoType.DOI);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrowsExactly(InvalidRequestException.class,
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
    Exception e = assertThrowsExactly(InvalidRequestException.class,
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
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalMediaRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_MEDIA);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalMediaRequestMissingProperty() {
    // Given
    String missingAttribute = MEDIA_HOST.get();
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        FdoType.DIGITAL_MEDIA);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePostRequest(request));

    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadArchiveRequest() {
    // Given
    var request = givenTombstoneRequest().get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_ID);

    // When
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePutRequest(request));

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_ID);
  }

  @Test
  void testBadArchiveRequestMissingProperty() {
    // Given
    var request = givenTombstoneRequest().get(0);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(TOMBSTONED_TEXT.get());

    // When
    Exception e = assertThrowsExactly(InvalidRequestException.class,
        () -> schemaValidator.validatePutRequest(request));

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(TOMBSTONED_TEXT.get());
  }

}
