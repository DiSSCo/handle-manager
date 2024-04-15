package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.Tables.HANDLES;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.CREATED;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.HANDLE_ALT;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.PID_STATUS_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.SPECIMEN_HOST_TESTVAL;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDigitalSpecimenAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genDoiRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genHandleRecordAttributesAltLoc;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genTombstoneRecordFullAttributes;
import static eu.dissco.core.handlemanager.testUtils.TestUtils.genUpdateRecordAttributesAltLoc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.then;

import eu.dissco.core.handlemanager.database.jooq.tables.Handles;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import org.jooq.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PidRepositoryIT extends BaseRepositoryIT {

  private PidRepository pidRepository;
  @Mock
  BatchInserter batchInserter;

  @BeforeEach
  void setup() {
    pidRepository = new PidRepository(context, batchInserter);
  }

  @AfterEach
  void destroy() {
    context.truncate(HANDLES).execute();
  }

  @Test
  void testCreateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> attributesToPost = genHandleRecordAttributes(handle, ObjectType.HANDLE);

    // When
    pidRepository.postAttributesToDb(CREATED.getEpochSecond(), attributesToPost);

    // Then
    then(batchInserter).should().batchCopy(CREATED.getEpochSecond(), attributesToPost);
  }

  @Test
  void testHandlesExistTrue() {
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(new HandleAttribute(1, handles.get(0), PID_STATUS.get(),
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS.get(),
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = pidRepository.getHandlesExist(handles);

    // Then
    assertThat(collisions).hasSize(handles.size());
    assert (byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesExistFalse() {
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    // When
    List<byte[]> collisions = pidRepository.getHandlesExist(handles);

    // Then
    assertThat(collisions).isEmpty();
    assertFalse(byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesWritableTrue() {
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(new HandleAttribute(1, handles.get(0), PID_STATUS.get(),
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS.get(),
            PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = pidRepository.checkHandlesWritable(handles);

    // Then
    assertThat(collisions).hasSize(handles.size());
    assert (byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testHandlesWritableFalse() {
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<HandleAttribute> rows = List.of(new HandleAttribute(1, handles.get(0), PID_STATUS.get(),
            "ARCHIVED".getBytes(StandardCharsets.UTF_8)),
        new HandleAttribute(1, handles.get(1), PID_STATUS.get(),
            "ARCHIVED".getBytes(StandardCharsets.UTF_8)));

    postAttributes(rows);

    // When
    List<byte[]> collisions = pidRepository.checkHandlesWritable(handles);

    // Then
    assertThat(collisions).isEmpty();
    assertFalse(byteArrListsAreEqual(handles, collisions));
  }

  @Test
  void testResolveSingleRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> responseExpected = genHandleRecordAttributes(handle, ObjectType.HANDLE);
    postAttributes(responseExpected);

    // When
    var responseReceived = pidRepository.resolveHandleAttributes(handle);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testGetPrimarySpecimenObjectIds() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var handleAlt = HANDLE_ALT.getBytes(StandardCharsets.UTF_8);
    var postedAttributes = Stream.concat(
        genHandleRecordAttributes(handle, ObjectType.DIGITAL_SPECIMEN).stream(),
        genHandleRecordAttributes(handleAlt, ObjectType.DIGITAL_SPECIMEN).stream()).toList();
    postAttributes(postedAttributes);
    List<HandleAttribute> expected = new ArrayList<>();
    for (var row : postedAttributes) {
      if (row.getType().equals(PRIMARY_SPECIMEN_OBJECT_ID.get())) {
        expected.add(row);
      }
    }
    // When
    var result = pidRepository.getPrimarySpecimenObjectId(List.of(handle, handleAlt));

    // Then
    assertThat(result).hasSameElementsAs(expected);
  }

  @Test
  void testResolveBatchRecord() throws Exception {
    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      responseExpected.addAll(genHandleRecordAttributes(handle, ObjectType.HANDLE));
    }

    postAttributes(responseExpected);

    // When
    var responseReceived = pidRepository.resolveHandleAttributes(handles);

    // Then
    assertThat(responseReceived).isEqualTo(responseExpected);
  }

  @Test
  void testGetAllHandlesPaging() {
    // Given
    int pageNum = 1;
    int pageSize = 5;

    List<String> handles = genListofHandlesString(pageSize);
    List<HandleAttribute> rows = new ArrayList<>();

    for (String handle : handles) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS.get(),
          PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    }
    postAttributes(rows);

    // When
    List<String> responseReceived = pidRepository.getAllHandles(pageNum, pageSize);

    // Then
    assertThat(responseReceived).hasSameElementsAs(handles);
  }

  @Test
  void testGetAllHandlesLastPage() {
    // Given
    int pageNum = 2;
    int pageSize = 5;

    List<String> handles = genListofHandlesString(pageSize + 1);
    List<HandleAttribute> rows = new ArrayList<>();

    for (String handle : handles) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS.get(),
          PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    }
    postAttributes(rows);

    // When
    List<String> responseReceived = pidRepository.getAllHandles(pageNum, pageSize);

    // Then
    assertThat(responseReceived).hasSize(1);
  }

  @Test
  void testGetAllHandlesPagingByPidStatus() {
    // Given
    int pageNum = 1;
    int pageSize = 5;
    byte[] pidStatusTarget = "ARCHIVED".getBytes(StandardCharsets.UTF_8);

    List<String> handles = genListofHandlesString(pageSize + 2);
    List<String> responseExpected = handles.subList(0, pageSize);
    List<String> extraHandles = handles.subList(pageSize, handles.size());
    List<HandleAttribute> rows = new ArrayList<>();

    for (String handle : responseExpected) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS.get(),
          pidStatusTarget));
    }
    for (String handle : extraHandles) {
      rows.add(new HandleAttribute(1, handle.getBytes(StandardCharsets.UTF_8), PID_STATUS.get(),
          PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    }
    postAttributes(rows);

    // When
    List<String> responseReceived = pidRepository.getAllHandles(pidStatusTarget, pageNum, pageSize);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testSearchByPhysicalIdentifierFullRecord() {
    // Given
    var targetPhysicalIdentifier = NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(
        StandardCharsets.UTF_8);
    List<HandleAttribute> responseExpected = new ArrayList<>();
    responseExpected.add(new HandleAttribute(1, HANDLE.getBytes(StandardCharsets.UTF_8),
        NORMALISED_SPECIMEN_OBJECT_ID.get(), targetPhysicalIdentifier));
    responseExpected.add(
        new HandleAttribute(2, HANDLE.getBytes(StandardCharsets.UTF_8), SPECIMEN_HOST.get(),
            SPECIMEN_HOST_TESTVAL.getBytes(
                StandardCharsets.UTF_8)));

    List<HandleAttribute> nonTargetAttributes = new ArrayList<>();
    nonTargetAttributes.add(new HandleAttribute(1, HANDLE_ALT.getBytes(StandardCharsets.UTF_8),
        NORMALISED_SPECIMEN_OBJECT_ID.get(), "A".getBytes(
        StandardCharsets.UTF_8)));

    postAttributes(responseExpected);
    postAttributes(nonTargetAttributes);

    // When
    var responseReceived = pidRepository.searchByNormalisedPhysicalIdentifierFullRecord(
        List.of(targetPhysicalIdentifier));

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testSearchByPhysicalSpecimenId() throws Exception {
    //Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var dbRecord = List.of(new HandleAttribute(NORMALISED_SPECIMEN_OBJECT_ID.index(), handle,
        NORMALISED_SPECIMEN_OBJECT_ID.get(),
        NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    postAttributes(genDoiRecordAttributes(handle, ObjectType.DOI));
    postAttributes(dbRecord);

    // When
    var response = pidRepository.searchByNormalisedPhysicalIdentifier(
        List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // Then
    assertThat(response).isEqualTo(dbRecord);
  }

  @Test
  void testSearchByPhysicalSpecimenIdIsArchived() {
    //Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var record = List.of(new HandleAttribute(NORMALISED_SPECIMEN_OBJECT_ID, handle,
            NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL),
        new HandleAttribute(PID_STATUS, handle, "ARCHIVED"));

    postAttributes(record);

    // When
    var response = pidRepository.searchByNormalisedPhysicalIdentifier(
        List.of(NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // Then
    assertThat(response).isEmpty();
  }

  @Test
  void testUpdateRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> originalRecord = genHandleRecordAttributes(handle);
    List<HandleAttribute> recordUpdate = genUpdateRecordAttributesAltLoc(handle);
    var responseExpected = incrementVersion(genHandleRecordAttributesAltLoc(handle), true);
    postAttributes(originalRecord);

    // When
    pidRepository.updateRecord(CREATED.getEpochSecond(), recordUpdate, true);
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.eq(handle)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testUpdateRecordBatch() throws Exception {

    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> updateAttributes = new ArrayList<>();
    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      postAttributes(genHandleRecordAttributes(handle));
      updateAttributes.add(genUpdateRecordAttributesAltLoc(handle));
      responseExpected.addAll(incrementVersion(genHandleRecordAttributesAltLoc(handle), true));
    }

    // When
    pidRepository.updateRecordBatch(CREATED.getEpochSecond(), updateAttributes, true);
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.in(handles)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testUpdateRecordBatchNoIncrement() throws Exception {

    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));

    List<List<HandleAttribute>> updateAttributes = new ArrayList<>();
    List<HandleAttribute> responseExpected = new ArrayList<>();
    for (byte[] handle : handles) {
      postAttributes(genHandleRecordAttributes(handle));
      updateAttributes.add(genUpdateRecordAttributesAltLoc(handle));
      responseExpected.addAll(incrementVersion(genHandleRecordAttributesAltLoc(handle), false));
    }

    // When
    pidRepository.updateRecordBatch(CREATED.getEpochSecond(), updateAttributes, false);
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.in(handles)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testArchiveRecordBatch() throws Exception {

    // Given
    List<byte[]> handles = List.of(HANDLE.getBytes(StandardCharsets.UTF_8),
        HANDLE_ALT.getBytes(StandardCharsets.UTF_8));
    List<String> handlesStr = List.of(HANDLE, HANDLE_ALT);

    List<HandleAttribute> tombstoneAttributes = new ArrayList<>();
    for (var handle : handles) {
      postAttributes(genDigitalSpecimenAttributes(handle));
      tombstoneAttributes.addAll(incrementVersion(genTombstoneRecordFullAttributes(handle), true));
    }

    // When
    pidRepository.archiveRecords(CREATED.getEpochSecond(), tombstoneAttributes, handlesStr);
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.in(handles)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(tombstoneAttributes);
  }

  @Test
  void testArchiveRecord() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> originalRecord = genDigitalSpecimenAttributes(handle);
    var tombstoneAttributes = incrementVersion(genTombstoneRecordFullAttributes(handle), true);

    postAttributes(originalRecord);

    // When
    pidRepository.archiveRecords(CREATED.getEpochSecond(), tombstoneAttributes, List.of(HANDLE));
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.eq(handle)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(tombstoneAttributes);
  }

  @Test
  void testRollbackHandleCreation() throws Exception {
    // Given
    var expected = genHandleRecordAttributes(HANDLE.getBytes(StandardCharsets.UTF_8));
    postAttributes(expected);
    postAttributes(genHandleRecordAttributes(HANDLE_ALT.getBytes(StandardCharsets.UTF_8)));

    // When
    pidRepository.rollbackHandles(List.of(HANDLE_ALT));
    var response = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
        Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(HANDLES).fetch(this::mapToAttribute);

    // Then
    assertThat(response).hasSameElementsAs(expected);
  }

  @Test
  void testRollbackHandleUpdate() throws Exception {
    // Given
    byte[] handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    List<HandleAttribute> originalRecord = genHandleRecordAttributes(handle);
    List<HandleAttribute> recordUpdate = genUpdateRecordAttributesAltLoc(handle);
    var responseExpected = incrementVersion(genHandleRecordAttributesAltLoc(handle), false);
    postAttributes(originalRecord);

    // When
    pidRepository.updateRecord(CREATED.getEpochSecond(), recordUpdate, false);
    var responseReceived = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
            Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(Handles.HANDLES)
        .where(Handles.HANDLES.HANDLE.eq(handle)).and(Handles.HANDLES.TYPE.notEqual(
            HS_ADMIN.get().getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);

    // Then
    assertThat(responseReceived).hasSameElementsAs(responseExpected);
  }

  @Test
  void testPostAndUpdateHandles() throws Exception {
    // Given
    var handle = HANDLE.getBytes(StandardCharsets.UTF_8);
    var handleAlt = HANDLE_ALT.getBytes(StandardCharsets.UTF_8);
    var existingRecord = genHandleRecordAttributes(handle, ObjectType.HANDLE);
    postAttributes(existingRecord);
    var updatedRecord = new ArrayList<>(existingRecord);
    updatedRecord.add(
        new HandleAttribute(MATERIAL_SAMPLE_TYPE.index(), handle, MATERIAL_SAMPLE_TYPE.get(),
            "digital".getBytes(StandardCharsets.UTF_8)));
    updatedRecord.add(
        new HandleAttribute(PID_RECORD_ISSUE_NUMBER.index(), handle, PID_RECORD_ISSUE_NUMBER.get(),
            String.valueOf(2).getBytes(
                StandardCharsets.UTF_8)));
    updatedRecord.remove(
        new HandleAttribute(PID_RECORD_ISSUE_NUMBER.index(), handle, PID_RECORD_ISSUE_NUMBER.get(),
            String.valueOf(1).getBytes(
                StandardCharsets.UTF_8)));

    var newRecord = genHandleRecordAttributes(handleAlt, ObjectType.HANDLE);
    var expected = Stream.concat(updatedRecord.stream(), newRecord.stream()).toList();

    // When
    pidRepository.postAndUpdateHandles(CREATED.getEpochSecond(), newRecord, List.of(updatedRecord));
    var response = context.select(Handles.HANDLES.IDX, Handles.HANDLES.HANDLE,
        Handles.HANDLES.TYPE, Handles.HANDLES.DATA).from(HANDLES).fetch(this::mapToAttribute);

    // Then
    assertThat(response).hasSameElementsAs(expected);
  }

  private void postAttributes(List<HandleAttribute> rows) {
    List<Query> queryList = new ArrayList<>();
    for (var handleAttribute : rows) {
      var query = context.insertInto(Handles.HANDLES)
          .set(Handles.HANDLES.HANDLE, handleAttribute.getHandle())
          .set(Handles.HANDLES.IDX, handleAttribute.getIndex())
          .set(Handles.HANDLES.TYPE, handleAttribute.getType().getBytes(StandardCharsets.UTF_8))
          .set(Handles.HANDLES.DATA, handleAttribute.getData()).set(Handles.HANDLES.TTL, 86400)
          .set(Handles.HANDLES.TIMESTAMP, CREATED.getEpochSecond())
          .set(Handles.HANDLES.ADMIN_READ, true).set(Handles.HANDLES.ADMIN_WRITE, true)
          .set(Handles.HANDLES.PUB_READ, true).set(Handles.HANDLES.PUB_WRITE, false);
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

  private List<HandleAttribute> incrementVersion(List<HandleAttribute> handleAttributes,
      boolean increaseVersionNum) {
    for (int i = 0; i < handleAttributes.size(); i++) {
      if (handleAttributes.get(i).getType().equals(PID_RECORD_ISSUE_NUMBER.get())) {
        var removedRecord = handleAttributes.remove(i);
        var currentVersion = Integer.parseInt(new String(removedRecord.getData()));
        var newVersionNum = increaseVersionNum ? currentVersion + 1 : currentVersion;
        byte[] issueNum = String.valueOf(newVersionNum).getBytes(StandardCharsets.UTF_8);
        handleAttributes.add(i,
            new HandleAttribute(removedRecord.getIndex(), removedRecord.getHandle(),
                removedRecord.getType(),
                issueNum));
      }
    }
    return handleAttributes;
  }

}
