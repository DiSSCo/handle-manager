package eu.dissco.core.handlemanager.repository;


import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.database.jooq.tables.Handles;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jooq.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle);
    ObjectNode responseExpected = genObjectNodeRecord(attributes);

    // When
    ObjectNode responseReceived = handleRep.createRecord(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDoiJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributes = genDoiRecordAttributes(handle);
    ObjectNode responseExpected = genObjectNodeRecord(attributes);

    // When
    ObjectNode responseReceived = handleRep.createRecord(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributes = genDigitalSpecimenAttributes(handle);
    ObjectNode responseExpected = genObjectNodeRecord(attributes);

    // When
    ObjectNode responseReceived = handleRep.createRecord(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateDigitalSpecimenBotanyJson() throws PidCreationException, JsonProcessingException {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributes = genDigitalSpecimenBotanyAttributes(handle);
    ObjectNode responseExpected = genObjectNodeRecord(attributes);

    // When
    ObjectNode responseReceived = handleRep.createRecord(handle, CREATED, attributes);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(attributes.size());
  }

  @Test
  void testCreateHandleRecordBatchJson() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handles){
      singleRecord = genHandleRecordAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> responseExpected = genObjectNodeRecordBatch(aggrList);

    // When
    List<ObjectNode> responseReceived = handleRep.createRecordBatchJson(handles, CREATED, flatList);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(flatList.size());
  }

  @Test
  void testCreateDoiRecordBatchJson() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handles){
      singleRecord = genDoiRecordAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> responseExpected = genObjectNodeRecordBatch(aggrList);

    // When
    List<ObjectNode> responseReceived = handleRep.createRecordBatchJson(handles, CREATED, flatList);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(flatList.size());
  }

  @Test
  void testCreateDigitalSpecimenBatchJson() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handles){
      singleRecord = genDigitalSpecimenAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> responseExpected = genObjectNodeRecordBatch(aggrList);

    // When
    List<ObjectNode> responseReceived = handleRep.createRecordBatchJson(handles, CREATED, flatList);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(flatList.size());
  }

  @Test
  void testCreateDigitalSpecimenBotanyBatchJson() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> flatList = new ArrayList<>();
    List<HandleAttribute> singleRecord;

    for (byte[] handle : handles){
      singleRecord = genDigitalSpecimenBotanyAttributes(handle);
      flatList.addAll(singleRecord);
      aggrList.add(new ArrayList<>(singleRecord));
    }
    List<ObjectNode> responseExpected = genObjectNodeRecordBatch(aggrList);

    // When
    List<ObjectNode> responseReceived = handleRep.createRecordBatchJson(handles, CREATED, flatList);
    var postedRecord = context.selectFrom(HANDLES).fetch();

    // Then
    assertThat(responseExpected).isEqualTo(responseReceived);
    assertThat(postedRecord).hasSize(flatList.size());
  }

  // TODO - How to post to context?
  void testCheckDuplicateHandlesNoCollision(){
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8), HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(
        new HandleAttribute(1, handles.get(0), PID_STATUS, PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS, PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    postAttributes(rows);

    // When
    List<byte[]> collisions = handleRep.checkHandlesExist(handles);

    // Then
    assertThat(collisions).isEmpty();

  }

  private void postAttributes(List<HandleAttribute> rows){
    List<Query> queryList = new ArrayList<>();
    for (var handleAttribute: rows){
      var query = context.insertInto(Handles.HANDLES)
          .set(Handles.HANDLES.HANDLE, handleAttribute.handle())
          .set(Handles.HANDLES.IDX, handleAttribute.index())
          .set(Handles.HANDLES.TYPE, handleAttribute.type().getBytes(StandardCharsets.UTF_8))
          .set(Handles.HANDLES.DATA, handleAttribute.data())
          .set(Handles.HANDLES.TTL, 86400)
          .set(Handles.HANDLES.TIMESTAMP, CREATED.getEpochSecond())
          .set(Handles.HANDLES.ADMIN_READ, true)
          .set(Handles.HANDLES.ADMIN_WRITE, true)
          .set(Handles.HANDLES.PUB_READ, true)
          .set(Handles.HANDLES.PUB_WRITE, false);
      queryList.add(query);
    }
    //context.batch(queryList).execute();
  }
}
