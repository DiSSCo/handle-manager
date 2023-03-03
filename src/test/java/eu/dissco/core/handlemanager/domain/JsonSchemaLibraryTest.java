package eu.dissco.core.handlemanager.domain;

import static eu.dissco.core.handlemanager.domain.PidRecords.LOC_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_REQ;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genCreateRecordRequest;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordRequestObject;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaLibrary;
import eu.dissco.core.handlemanager.domain.requests.validation.JsonSchemaStaticContextInitializer;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class JsonSchemaLibraryTest {

  @Autowired
  JsonSchemaStaticContextInitializer schemaInitializer;

  @Test
  void testPostRequestSchema() {
    // Given
    String missingAttribute = PID_ISSUER_REQ;
    String unknownAttribute = "badKey";

    var request = genCreateRecordRequest(genHandleRecordRequestObject(), RECORD_TYPE_HANDLE);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(missingAttribute);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).remove(LOC_REQ);
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).put(unknownAttribute, "badVal");
    ((ObjectNode) request.get(NODE_DATA).get(NODE_ATTRIBUTES)).put("badKey2", "badVal");
    // Then
    Exception e = assertThrows(InvalidRequestException.class, () -> {
      JsonSchemaLibrary.validatePostRequest(request);
    });

    assertThat(e.getMessage()).contains(missingAttribute);
  }


}
