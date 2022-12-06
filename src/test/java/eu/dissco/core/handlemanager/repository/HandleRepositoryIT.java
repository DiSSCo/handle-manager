package eu.dissco.core.handlemanager.repository;


import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateRecordObjectNode;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestJsonHandleRecordResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapper;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class HandleRepositoryIT extends BaseRepositoryIT {
  private HandleRepository handleRep;
  private ObjectMapper mapper;

  @BeforeEach
  void setup() {
    mapper = new ObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    handleRep = new HandleRepository(context, mapper);
  }

  @AfterEach
  void destroy() {
    context.truncate(HANDLES).execute();
  }

  @Test
  void testCreateHandleJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestHandleAttributes(handle);
    ObjectNode responseExpected =generateRecordObjectNode(attributes);

    // When
    ObjectNode responseReceived = handleRep.createHandleRecordJson(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDoiJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDoiAttributes(handle);
    ObjectNode responseExpected =generateRecordObjectNode(attributes);

    // When
    ObjectNode responseReceived = handleRep.createDoiRecordJson(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDigitalSpecimenAttributes(handle);
    ObjectNode responseExpected =generateRecordObjectNode(attributes);

    // When
    ObjectNode responseReceived = handleRep.createDigitalSpecimenJson(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenBotanyJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDigitalSpecimenBotanyAttributes(handle);
    ObjectNode responseExpected =generateRecordObjectNode(attributes);

    // When
    ObjectNode responseReceived = handleRep.createDigitalSpecimenBotanyJson(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }



  @Test
  void testCreateHandleBatch() throws PidCreationException {
    // Given    
    List<byte[]> handleList = new ArrayList<>();
    handleList.add(HANDLE.getBytes());
    handleList.add(HANDLE_ALT.getBytes());

    List<HandleAttribute> attributes = generateBatchHandleAttributeList(handleList);
    List<HandleRecordResponse> responseExpected = generateBatchHandleResponse(handleList);

    // When
    List<HandleRecordResponse> responseReceived = handleRep.createHandleRecordBatch(handleList,
        CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDoiBatch() throws PidCreationException {
    // Given
    List<byte[]> handleList = new ArrayList<>();
    handleList.add(HANDLE.getBytes());
    handleList.add(HANDLE_ALT.getBytes());

    List<HandleAttribute> attributes = generateBatchDoiAttributeList(handleList);
    List<DoiRecordResponse> responseExpected = generateBatchDoiResponse(handleList);

    // When
    List<DoiRecordResponse> responseReceived = handleRep.createDoiRecordBatch(handleList, CREATED,
        attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenBatch() throws PidCreationException {
    // Given
    List<byte[]> handleList = new ArrayList<>();
    handleList.add(HANDLE.getBytes());
    handleList.add(HANDLE_ALT.getBytes());

    List<HandleAttribute> attributes = generateBatchDigitalSpecimenAttributeList(handleList);
    List<DigitalSpecimenResponse> responseExpected = generateBatchDigitalSpecimenResponse(
        handleList);

    // When
    List<DigitalSpecimenResponse> responseReceived = handleRep.createDigitalSpecimenBatch(
        handleList, CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatch() throws PidCreationException {
    // Given
    List<byte[]> handleList = new ArrayList<>();
    handleList.add(HANDLE.getBytes());
    handleList.add(HANDLE_ALT.getBytes());

    List<HandleAttribute> attributes = generateBatchDigitalSpecimenBotanyAttributeList(handleList);
    List<DigitalSpecimenBotanyResponse> responseExpected = generateBatchDigitalSpecimenBotanyResponse(
        handleList);

    // When
    List<DigitalSpecimenBotanyResponse> responseReceived = handleRep.createDigitalSpecimenBotanyBatch(
        handleList, CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  private List<HandleRecordResponse> generateBatchHandleResponse(List<byte[]> handleList) {
    List<HandleRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handleList) {
      responseList.add(generateTestHandleResponse(h));
    }
    return responseList;
  }

  private List<DoiRecordResponse> generateBatchDoiResponse(List<byte[]> handleList) {
    List<DoiRecordResponse> responseList = new ArrayList<>();
    for (byte[] h : handleList) {
      responseList.add(generateTestDoiResponse(h));
    }
    return responseList;
  }

  private List<DigitalSpecimenResponse> generateBatchDigitalSpecimenResponse(
      List<byte[]> handleList) {
    List<DigitalSpecimenResponse> responseList = new ArrayList<>();
    for (byte[] h : handleList) {
      responseList.add(generateTestDigitalSpecimenResponse(h));
    }
    return responseList;
  }

  private List<DigitalSpecimenBotanyResponse> generateBatchDigitalSpecimenBotanyResponse(
      List<byte[]> handleList) {
    List<DigitalSpecimenBotanyResponse> responseList = new ArrayList<>();
    for (byte[] h : handleList) {
      responseList.add(generateTestDigitalSpecimenBotanyResponse(h));
    }
    return responseList;
  }

  private List<HandleAttribute> generateBatchHandleAttributeList(List<byte[]> handleList) {
    List<HandleAttribute> attributes = new ArrayList<>();
    for (byte[] h : handleList) {
      attributes.addAll(generateTestHandleAttributes(h));
    }
    return attributes;
  }

  private List<HandleAttribute> generateBatchDoiAttributeList(List<byte[]> handleList) {
    List<HandleAttribute> attributes = new ArrayList<>();
    for (byte[] h : handleList) {
      attributes.addAll(generateTestDoiAttributes(h));
    }
    return attributes;
  }

  private List<HandleAttribute> generateBatchDigitalSpecimenAttributeList(List<byte[]> handleList) {
    List<HandleAttribute> attributes = new ArrayList<>();
    for (byte[] h : handleList) {
      attributes.addAll(generateTestDigitalSpecimenAttributes(h));
    }
    return attributes;
  }

  private List<HandleAttribute> generateBatchDigitalSpecimenBotanyAttributeList(
      List<byte[]> handleList) {
    List<HandleAttribute> attributes = new ArrayList<>();
    for (byte[] h : handleList) {
      attributes.addAll(generateTestDigitalSpecimenBotanyAttributes(h));
    }
    return attributes;
  }


}
