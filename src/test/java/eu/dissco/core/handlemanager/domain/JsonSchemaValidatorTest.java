package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OR_PHYSICAL;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRESERVED_OR_LIVING;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MEDIA_URL_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_ISSUER_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.REFERENT_DOI_NAME_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_PID;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genMediaRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRequestAltLoc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaValidator;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
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
  void setup(){
    schemaValidator = new JsonSchemaValidator();
  }

  @Test
  void testPostHandleRequest() {
    // Given
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostDoiRequest() {
    // Given
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostDigitalSpecimenRequest() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostDigitalSpecimenBotanyRequest() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPostMediaObjectRequest() {
    // Given
    var request = genCreateRecordRequest(genMediaRequestObject(), RECORD_TYPE_MEDIA);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePostRequest(request);
    });
  }

  @Test
  void testPatchHandleRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_HANDLE, PID_ISSUER_REQ, PID_ISSUER_PID);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testDoiPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_DOI, REFERENT_DOI_NAME_REQ, REFERENT_DOI_NAME_PID);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testDigitalSpecimenPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_DS, SPECIMEN_HOST_REQ, SPECIMEN_HOST_PID);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testDigitalSpecimenBotanyPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_DS_BOTANY, PRESERVED_OR_LIVING, "living");

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  @Test
  void testMediaObjectPatchRequest() {
    // Given
    var request = givenUpdateRequest(RECORD_TYPE_MEDIA, MEDIA_URL, MEDIA_URL_TESTVAL);

    // Then
    assertDoesNotThrow(() -> {
      schemaValidator.validatePatchRequest(request);
    });
  }

  private ObjectNode givenUpdateRequest(String type, String key, String val){
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
  void testTombstoneRequest(){
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
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), UNKNOWN_ATTRIBUTE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(ENUM_MSG).contains(NODE_TYPE);
  }

  @ParameterizedTest
  @ValueSource(strings = {PRESERVED_OR_LIVING, DIGITAL_OR_PHYSICAL})
  void testBadEnumValueRequest(String targetEnum){
    // Given
    ObjectNode request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY);
    ((ObjectNode) request.get("data").get("attributes")).remove(targetEnum);
    ((ObjectNode) request.get("data").get("attributes")).put(targetEnum, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });

    assertThat(e.getMessage()).contains(ENUM_MSG).contains(targetEnum);
  }

  @Test
  void testBadEnumPhysicalIdType(){
    // Given
    String targetEnum = "physicalIdType";
    ObjectNode request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(), RECORD_TYPE_DS_BOTANY);
    ((ObjectNode) request.get("data").get("attributes").get(PHYSICAL_IDENTIFIER)).remove(targetEnum);
    ((ObjectNode) request.get("data").get("attributes").get(PHYSICAL_IDENTIFIER)).put(targetEnum, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });

    assertThat(e.getMessage()).contains(ENUM_MSG).contains(targetEnum);
  }

  @Test
  void testBadPostRequest() {
    // Given
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(NODE_TYPE);
  }

  @Test
  void testBadPostHandleRequestMissingProperty() {
    // Given
    String missingAttribute = PID_ISSUER_REQ;
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
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
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDoiRequestMissingProperty() {
    // Given
    String missingAttribute = REFERENT_DOI_NAME_REQ;
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostDoiRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
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
    String missingAttribute = SPECIMEN_HOST_REQ;
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
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
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
       schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(UNRECOGNIZED_MSG).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalSpecimenBotanyRequestMissingProperty() {
    // Given
    String missingAttribute = OBJECT_TYPE;
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
       schemaValidator.validatePostRequest(request);
    });
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(missingAttribute);
  }

  @Test
  void testBadPostDigitalSpecimenBotanyRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
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
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_MEDIA);
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
    String missingAttribute = MEDIA_URL;
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
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
  @ValueSource(strings = {RECORD_TYPE_HANDLE, RECORD_TYPE_DOI, RECORD_TYPE_DS,
      RECORD_TYPE_DS_BOTANY, RECORD_TYPE_MEDIA})
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
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(TOMBSTONE_TEXT);

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
     schemaValidator.validatePutRequest(request);
    });

    // Then
    assertThat(e.getMessage()).contains(MISSING_MSG).contains(TOMBSTONE_TEXT);
  }

}
