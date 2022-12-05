package eu.dissco.core.handlemanager.repository;


import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenBotanyResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDigitalSpecimenResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestDoiResponse;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.generateTestHandleResponse;
import static org.assertj.core.api.Assertions.assertThat;

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

class HandleRepositoryIT extends BaseRepositoryIT {

  private HandleRepository handleRep;

  @BeforeEach
  void setup() {
    handleRep = new HandleRepository(context);
  }

  @AfterEach
  void destroy() {
    context.truncate(HANDLES).execute();
  }

  @Test
  void testCreateHandle() throws PidCreationException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestHandleAttributes(handle);
    HandleRecordResponse responseExpected = generateTestHandleResponse(handle);

    // When
    HandleRecordResponse responseReceived = handleRep.createHandle(handle, CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDoi() throws PidCreationException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDoiAttributes(handle);
    DoiRecordResponse responseExpected = generateTestDoiResponse(handle);

    // When
    DoiRecordResponse responseReceived = handleRep.createDoi(handle, CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimen() throws PidCreationException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDigitalSpecimenAttributes(handle);
    DigitalSpecimenResponse responseExpected = generateTestDigitalSpecimenResponse(handle);

    // When
    DigitalSpecimenResponse responseReceived = handleRep.createDigitalSpecimen(handle, CREATED,
        attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenBotany() throws PidCreationException {
    // Given
    byte[] handle = HANDLE.getBytes();
    List<HandleAttribute> attributes = generateTestDigitalSpecimenBotanyAttributes(handle);
    DigitalSpecimenBotanyResponse responseExpected = generateTestDigitalSpecimenBotanyResponse(
        handle);

    // When
    DigitalSpecimenBotanyResponse responseReceived = handleRep.createDigitalSpecimenBotany(handle,
        CREATED, attributes);

    // Then
    var postedRecord = context.selectFrom(HANDLES).fetch();
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
