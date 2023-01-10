package eu.dissco.core.handlemanager.repository;


import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.RECORD_TYPE_HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.*;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genObjectNodeAttributeRecord;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchNullPointerException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.database.jooq.tables.Handles;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.InvalidRecordInput;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Query;
import org.jooq.Record4;
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
  void testCreateRecord()  {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributesToPost = genHandleRecordAttributes(handle);

    // When
    handleRep.postAttributesToDb(CREATED, attributesToPost);
    var postedRecordContext = context.selectFrom(HANDLES).fetch();
    var postedRecordAttributes = handleRep.resolveHandleAttributes(handle);

    // Then
    assertThat(attributesToPost).isEqualTo(postedRecordAttributes);
    assertThat(postedRecordContext).hasSize(attributesToPost.size());
  }


  @Test
  void testHandlesExistTrue() {
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(
        new HandleAttribute(1, handles.get(0), PID_STATUS,
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS,
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = handleRep.getHandlesExist(handles);

    // Then
    assertThat(collisions).hasSize(handles.size());
    assert(byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesExistFalse() {
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    // When
    List<byte[]> collisions = handleRep.getHandlesExist(handles);

    // Then
    assertThat(collisions).isEmpty();
    assertFalse(byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesWritableTrue() {
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(
        new HandleAttribute(1, handles.get(0), PID_STATUS,
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS,
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = handleRep.checkHandlesWritable(handles);

    // Then
    assertThat(collisions).hasSize(handles.size());
    assert (byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesWritableFalse() {
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(
        new HandleAttribute(1, handles.get(0), PID_STATUS,
            "ARCHIVED".getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS,
            "ARCHIVED".getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = handleRep.checkHandlesWritable(handles);

    // Then
    assertThat(collisions).isEmpty();
    assertFalse(byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testResolveSingleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> responseExpected = genHandleRecordAttributes(handle);
    postAttributes(responseExpected);

    // When
    var responseReceived = handleRep.resolveHandleAttributes(handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      responseExpected.addAll(genHandleRecordAttributes(handle));
    }

    postAttributes(responseExpected);

    // When
    var responseReceived = handleRep.resolveHandleAttributes(handles);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testGetAllHandlesPaging() {
    // Given
    int pageNum = 0;
    int pageSize = 5;

    List<String> handles = genListofHandlesString(pageSize);
    List<HandleAttribute> rows = new ArrayList<>();

    for (String handle : handles) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS,
          PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    }
    postAttributes(rows);

    // When
    List<String> responseReceived = handleRep.getAllHandles(pageNum, pageSize);

    // Then
    Collections.sort(responseReceived);
    Collections.sort(handles);
    assertThat(responseReceived).isEqualTo(handles);
  }

  @Test
  void testGetAllHandlesPagingByPidStatus() {
    // Given
    int pageNum = 0;
    int pageSize = 5;
    byte[] pidStatusTarget = "ARCHIVED".getBytes(StandardCharsets.UTF_8);

    List<String> handles = genListofHandlesString(pageSize + 2);
    List<String> responseExpected = handles.subList(0, pageSize);
    List<String> extraHandles = handles.subList(pageSize, handles.size());
    List<HandleAttribute> rows = new ArrayList<>();

    for (String handle : responseExpected) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS,
          pidStatusTarget));
    }
    for (String handle : extraHandles) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS,
          PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    }
    postAttributes(rows);

    // When
    List<String> responseReceived = handleRep.getAllHandles(pidStatusTarget, pageNum, pageSize);

    // Then
    Collections.sort(responseReceived);
    Collections.sort(responseExpected);
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> originalRecord = genHandleRecordAttributes(handle);
    List<HandleAttribute> recordUpdate = genUpdateRecordAttributesAltLoc(handle);
    var responseExpected = incrementVersion(genHandleRecordAttributesAltLoc(handle));
    postAttributes(originalRecord);

    // When
    handleRep.updateRecord(CREATED, recordUpdate);
    var responseReceived = context
        .select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE, Handles.HANDLES.TYPE, Handles.HANDLES.DATA)
        .from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.eq(handle))
        .and(Handles.HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    for (var elem : responseExpected){
      log.info(elem.toString());
    }

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testUpdateRecordBatch() throws ParserConfigurationException, TransformerException {

    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> updateAttributes = new ArrayList<>();
    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      postAttributes(genHandleRecordAttributes(handle));
      updateAttributes.add(genUpdateRecordAttributesAltLoc(handle));
      responseExpected.addAll(incrementVersion(genHandleRecordAttributesAltLoc(handle)));
    }

    // When
    handleRep.updateRecordBatch(CREATED, updateAttributes);
    var responseReceived = context
        .select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE, Handles.HANDLES.TYPE, Handles.HANDLES.DATA)
        .from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.in(handles))
        .and(Handles.HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws ParserConfigurationException, TransformerException {

    // Given
    List<byte[]> handles = List.of(
        HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<HandleAttribute> archiveAttributes = new ArrayList<>();
    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      postAttributes(genHandleRecordAttributes(handle));
      archiveAttributes.addAll(genTombstoneRecordRequestAttributes(handle));
      responseExpected.addAll(incrementVersion(genTombstoneRecordFullAttributes(handle)));
    }

    // When
    handleRep.archiveRecords(CREATED, archiveAttributes, handles);
    var responseReceived = context
        .select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE, Handles.HANDLES.TYPE, Handles.HANDLES.DATA)
        .from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.in(handles))
        .and(Handles.HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> originalRecord = genHandleRecordAttributes(handle);
    List<HandleAttribute> recordArchive = genTombstoneRecordRequestAttributes(handle);
    var responseExpected = incrementVersion(genTombstoneRecordFullAttributes(handle));
    postAttributes(originalRecord);

    // When
    handleRep.archiveRecord(CREATED, recordArchive);
    var responseReceived = context
        .select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE, Handles.HANDLES.TYPE, Handles.HANDLES.DATA)
        .from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.eq(handle))
        .and(Handles.HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }



  private void postAttributes(List<HandleAttribute> rows) {
    List<Query> queryList = new ArrayList<>();
    for (var handleAttribute : rows) {
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
    context.batch(queryList).execute();
  }

  private boolean byteArrListsAreEqual(List<byte[]> a, List<byte[]> b) {
    if (a.size() != b.size()) {
      return false;
    }

    List<String> aStr = new ArrayList<>();
    List<String> bStr = new ArrayList<>();

    for (int i = 0; i < a.size(); i++) {
      aStr.add(new String(a.get(i)));
      bStr.add(new String(b.get(i)));
    }

    Collections.sort(aStr);
    Collections.sort(bStr);

    return aStr.equals(bStr);
  }

  private List<String> genListofHandlesString(int numberOfHandles) {
    Random random = new Random();
    int length = 3;
    char[] buffer = new char[length];
    char[] symbols = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890".toCharArray();
    Set<String> handles = new HashSet<>();

    while (handles.size() < numberOfHandles) {
      for (int j = 0; j < length; j++) {
        buffer[j] = symbols[random.nextInt(symbols.length)];
      }
      handles.add(new String(buffer));
    }
    return new ArrayList<>(handles);
  }

  private List<HandleAttribute> incrementVersion(List<HandleAttribute> handleAttributes) {
    for (int i = 0; i < handleAttributes.size(); i++) {
      if (handleAttributes.get(i).type().equals(ISSUE_NUMBER)) {
        var removedRecord = handleAttributes.remove(i);
        byte[] issueNum = String.valueOf(Integer.parseInt(new String(removedRecord.data())) + 1)
            .getBytes(StandardCharsets.UTF_8);
        handleAttributes.add(i,
            new HandleAttribute(removedRecord.index(), removedRecord.handle(), removedRecord.type(),
                issueNum));
      }
    }
    return handleAttributes;
  }

  private HandleAttribute mapToAttribute(Record4<Integer, byte[], byte[], byte[]> row) {
    return new HandleAttribute(
        row.get(Handles.HANDLES.IDX),
        row.get(Handles.HANDLES.HANDLE),
        new String(row.get(Handles.HANDLES.TYPE)),
        row.get(Handles.HANDLES.DATA));
  }

}
