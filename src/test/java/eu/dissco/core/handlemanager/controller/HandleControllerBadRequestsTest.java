package eu.dissco.core.handlemanager.controller;

import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DOI;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_DS_BOTANY;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_MEDIA;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.MAPPER;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PREFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SUFFIX;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenBotanyRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRequestBatch;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.givenSearchByPhysIdRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaStaticContextInitializer;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.service.HandleService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class HandleControllerBadRequestsTest {

  private static final String UNKNOWN_ATTRIBUTE = "badKey";
  private static final String UNKNOWN_VAL = "badVal";
  @Autowired
  JsonSchemaStaticContextInitializer schemaInitializer;
  @Mock
  private HandleService service;
  private HandleController controller;

  @BeforeEach
  void setup() {
    controller = new HandleController(service, schemaInitializer);
  }

  @Test
  void testBadTypeRequest(){
    //When
    var badType = "bad";
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), badType);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(badType);
  }

  @Test
  void testBadPostRequest() {
    // Given
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(NODE_TYPE);
  }

  @Test
  void testBadPostHandleRequestMissingProperty() {
    // Given
    String missingAttribute = PID_ISSUER_REQ;
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(missingAttribute);
  }

  @Test
  void testBadPostHandleRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDoiRequestMissingProperty() {
    // Given
    String missingAttribute = REFERENT_DOI_NAME_REQ;
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(missingAttribute);
  }

  @Test
  void testBadPostDoiRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDoiRecordRequestObject(), RECORD_TYPE_DOI);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostDigitalSpecimenRequestMissingProperty() {
    // Given
    String missingAttribute = SPECIMEN_HOST_REQ;
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(missingAttribute);
  }

  @Test
  void testBadPostDigitalSpecimenRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_DS);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
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
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(missingAttribute);
  }

  @Test
  void testBadPostDigitalSpecimenBotanyRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenBotanyRequestObject(),
        RECORD_TYPE_DS_BOTANY);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadPostMediaObjectRequestUnknownProperty() {
    // Given
    var request = genCreateRecordRequest(genDigitalSpecimenRequestObject(), RECORD_TYPE_MEDIA);
    ((ObjectNode) request.get(NODE_DATA)).put(UNKNOWN_ATTRIBUTE, UNKNOWN_VAL);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
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
      controller.createRecord(request);
    });
    assertThat(e.getMessage()).contains(missingAttribute);
  }

  @Test
  void testBadPatchRequest() {
    // Given
    var request = givenPatchRequest(null, "");

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.updateRecord(PREFIX, SUFFIX, request);
    });
    assertThat(e.getMessage()).contains(NODE_TYPE);
  }

  @ParameterizedTest
  @ValueSource(strings = {RECORD_TYPE_HANDLE, RECORD_TYPE_DOI, RECORD_TYPE_DS,
      RECORD_TYPE_DS_BOTANY, RECORD_TYPE_MEDIA})
  void testBadPatchRequestUnknownProperty(String recordType) {
    // Given
    var request = givenPatchRequest(recordType, UNKNOWN_ATTRIBUTE);

    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.updateRecord(PREFIX, SUFFIX, request);
    });
    assertThat(e.getMessage()).contains(UNKNOWN_ATTRIBUTE);
  }

  @Test
  void testBadArchiveRequest() {
    // Given
    var request = genTombstoneRequestBatch(List.of(HANDLE)).get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_ID);

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.archiveRecord(PREFIX, SUFFIX, request);
    });

    // Then
    assertThat(e.getMessage()).contains(NODE_ID);
  }

  @Test
  void testBadArchiveRequestMissingProperty() {
    // Given
    var request = genTombstoneRequestBatch(List.of(HANDLE)).get(0);
    ((ObjectNode) request.get(NODE_DATA)).remove(NODE_TYPE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(TOMBSTONE_TEXT);

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.archiveRecord(PREFIX, SUFFIX, request);
    });

    // Then
    assertThat(e.getMessage()).contains(TOMBSTONE_TEXT);
  }

  @Test
  void testBadResolveRequestMissingProperty() {
    // Given
    var request = MAPPER.createObjectNode();
    request.put(NODE_DATA, "");
    MockHttpServletRequest r = new MockHttpServletRequest();
    r.setRequestURI("a");

    // When
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      controller.resolvePids(List.of(request), r);
    });

    // Then
    assertThat(e.getMessage()).contains(NODE_ID);
  }

  private ObjectNode givenPatchRequest(String type, String badKey) {
    ObjectNode attributeNode = MAPPER.createObjectNode();
    attributeNode.put(badKey, "val");

    ObjectNode dataNode = MAPPER.createObjectNode();
    dataNode.put(NODE_TYPE, type);
    dataNode.put(NODE_ID, HANDLE);
    dataNode.set(NODE_ATTRIBUTES, attributeNode);

    ObjectNode request = MAPPER.createObjectNode();
    request.set(NODE_DATA, dataNode);
    return request;
  }
}
