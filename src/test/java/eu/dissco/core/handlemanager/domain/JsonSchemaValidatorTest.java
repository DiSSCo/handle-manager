package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.FdoProfile.ANNOTATION_TOPIC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ORGANISATION_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
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
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ANNOTATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAPPING;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MAS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_ORGANISATION;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_SOURCE_SYSTEM;
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
import eu.dissco.core.handlemanager.domain.requests.vocabulary.StructuralType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.List;
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
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE);

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
        "",
        StructuralType.DIGITAL,
        null
    );
    var request = genCreateRecordRequest(requestAttributes, RECORD_TYPE_HANDLE);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostDoiRequest() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI);

    // Then
    assertDoesNotThrow(() -> schemaValidator.validatePostRequest(request));
  }

  @Test
  void testPostDigitalSpecimenRequest() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostMediaObjectRequest() throws Exception {
    // Given
    var request = genCreateRecordRequest(givenMediaRequestObject(), RECORD_TYPE_MEDIA);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostAnnotationRequest() {
    // Given
    var request = genCreateRecordRequest(givenAnnotationRequestObject(), RECORD_TYPE_ANNOTATION);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostOrganisationRequest() {
    // Given
    var request = genCreateRecordRequest(givenOrganisationRequestObject(),
        RECORD_TYPE_ORGANISATION);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostOrganisationNullTypeRequest() {
    // Given
    var orgRequestObject = new OrganisationRequest(
        "FDO_PROFILE_TESTVAL",
        "ISSUED_FOR_AGENT_TESTVAL",
        "DIGITAL_OBJECT_TYPE_TESTVAL",
        PID_ISSUER_TESTVAL_OTHER,
        LOC_TESTVAL,
        "REFERENT_NAME_TESTVAL",
        "PRIMARY_REFERENT_TYPE_TESTVAL",
        SPECIMEN_HOST_TESTVAL,
        null
    );
    var request = genCreateRecordRequest(orgRequestObject, RECORD_TYPE_ORGANISATION);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostMappingRequest() {
    // Given
    var request = genCreateRecordRequest(givenMappingRequestObject(), RECORD_TYPE_MAPPING);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostSourceSystemRequest() {
    // Given
    var request = genCreateRecordRequest(givenSourceSystemRequestObject(),
        RECORD_TYPE_SOURCE_SYSTEM);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostMasRequest() {
    // Given
    var request = genCreateRecordRequest(givenMasRecordRequestObject(), RECORD_TYPE_MAS);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testHandlePatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_HANDLE, PID_ISSUER.get(),
        PID_ISSUER_TESTVAL_OTHER);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testDoiPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_DOI, REFERENT_NAME.get(),
        REFERENT_DOI_NAME_TESTVAL);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testDigitalSpecimenPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_DS, SPECIMEN_HOST.get(), SPECIMEN_HOST_TESTVAL);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testMediaObjectPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_MEDIA, MEDIA_HOST.get(), MEDIA_HOST_TESTVAL);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testAnnotationPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_ANNOTATION, ANNOTATION_TOPIC.get(), "new");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testOrganisationPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_ORGANISATION, ORGANISATION_ID.get(), "new");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testMappingPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_MAPPING, SOURCE_DATA_STANDARD.get(), "new");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testSourceSystemPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_SOURCE_SYSTEM, SOURCE_SYSTEM_NAME.get(), "new");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testMasPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_MAS, MAS_NAME.get(), "new");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  private ObjectNode givenUpdateRequest(String type, String key, String val) {
    ObjectNode request = MAPPER.createObjectNode();
    ObjectNode data = MAPPER.createObjectNode();
    var attributes = (ObjectNode) genUpdateRequestAltLoc();
    attributes.put(key, val);

    data.put(NODE_TYPE, type);
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

    assertDoesNotThrow(() -> {
      schemaValidator.validatePutRequest(request);
    });
  }

  @Test
  void testBadTypeRequest() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), UNKNOWN_ATTRIBUTE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(ENUM_MSG).contains(NODE_TYPE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"livingOrPreserved", "primarySpecimenObjectIdType"})
  void testBadEnumValueRequest(String targetEnum) {
    // Given
    ObjectNode request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);
    ((ObjectNode) request.get("data").get("attributes")).remove(targetEnum);
    ((ObjectNode) request.get("data").get("attributes")).put(targetEnum, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });

    assertThat(e.getMessage()).contains(ENUM_MSG).contains(targetEnum);
  }

  @Test
  void testBadPostRequest() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_TYPE);
  }

  @Test
  void testBadPostHandleRequestMissingRequiredProperty() {
    // Given
    String missingAttribute = FDO_PROFILE.get();
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostHandleRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDoiRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDoiRecordRequestObject(), RECORD_TYPE_DOI);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalSpecimenRequestMissingProperty() {
    // Given
    String missingAttribute = SPECIMEN_HOST.get();
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostDigitalSpecimenRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_DS);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostMediaObjectRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_MEDIA);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostMediaObjectRequestMissingProperty() {
    // Given
    String missingAttribute = MEDIA_HOST.get();
    var request = genCreateRecordRequest(givenDigitalSpecimenRequestObjectNullOptionals(),
        RECORD_TYPE_MEDIA);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPatchRequest() {
    // Given
    var badType = "badType";
    var request = givenUpdateRequest("badType", UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePatchRequest(request);
    });
    assertThat(e.getMessage()).contains(ENUM_MSG).contains(NODE_TYPE);
  }

  @ParameterizedTest
  @ValueSource(strings = {RECORD_TYPE_HANDLE, RECORD_TYPE_DOI, RECORD_TYPE_DS, RECORD_TYPE_MEDIA})
  void testBadPatchRequestUnknownProperty(String recordType) {
    // Given
    var request = givenUpdateRequest(recordType, UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePatchRequest(request);
    });

    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadArchiveRequest() {
    // Given
    var request = genTombstoneRequestBatch(List.of(HANDLE)).get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_ID);

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePutRequest(request);
    });

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
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePutRequest(request);
    });

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(TOMBSTONE_TEXT.get());
  }

}
