package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_RECORD_FIELDS_BYTES;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record4;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HandleRepository {

  private static final int TTL = 86400;
  private final DSLContext context;

  // For Handle Name Generation
  public List<byte[]> getHandlesExist(List<byte[]> handles) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  public List<byte[]> checkHandlesWritable(List<byte[]> handles) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.eq(PID_STATUS.getBytes(StandardCharsets.UTF_8)))
        .and(HANDLES.DATA.notEqual("ARCHIVED".getBytes()))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  public List<HandleAttribute> resolveHandleAttributes(byte[] handle) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> resolveHandleAttributes(List<byte[]> handles) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> resolveHandleAttributesByPhysicalIdentifier(byte[] physicalIdentifier, byte[] hostInstitution){
    var hostInstitutionTable = context.select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.TYPE.eq(SPECIMEN_HOST.getBytes(StandardCharsets.UTF_8)))
        .and((HANDLES.DATA).eq(hostInstitution))
        .asTable("hostInstitutionTable");

    var physicalIdentifierTable = context.select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.TYPE.eq(PHYSICAL_IDENTIFIER.getBytes(StandardCharsets.UTF_8)))
        .and((HANDLES.DATA).eq(physicalIdentifier))
        .asTable("physicalIdentifierTable");

    return context.select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .join(hostInstitutionTable)
        .on(HANDLES.HANDLE.eq(hostInstitutionTable.field(HANDLES.HANDLE)))
        .join(physicalIdentifierTable)
        .on(HANDLES.HANDLE.eq(physicalIdentifierTable.field(HANDLES.HANDLE)))
        .fetch(this::mapToAttribute);
  }

  // Get List of Pids
  public List<String> getAllHandles(byte[] pidStatus, int pageNum, int pageSize) {
    int offset = 0;
    if (pageNum > 1) {
      offset = offset + (pageSize * (pageNum - 1));
    }

    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.TYPE.eq(PID_STATUS.getBytes(StandardCharsets.UTF_8)))
        .and(HANDLES.DATA.eq(pidStatus))
        .limit(pageSize)
        .offset(offset)
        .fetch()
        .getValues(HANDLES.HANDLE, String.class);
  }

  public List<String> getAllHandles(int pageNum, int pageSize) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .limit(pageSize)
        .offset(pageNum)
        .fetch()
        .getValues(HANDLES.HANDLE, String.class);
  }

  private HandleAttribute mapToAttribute(Record4<Integer, byte[], byte[], byte[]> row) {
    return new HandleAttribute(
        row.get(HANDLES.IDX),
        row.get(HANDLES.HANDLE),
        new String(row.get(HANDLES.TYPE), StandardCharsets.UTF_8),
        row.get(HANDLES.DATA));
  }

  // Post

  public void postAttributesToDb(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
    var queryList = new ArrayList<Query>();

    for (var handleAttribute : handleAttributes) {
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handleAttribute.handle())
          .set(HANDLES.IDX, handleAttribute.index())
          .set(HANDLES.TYPE, handleAttribute.type().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false);
      queryList.add(query);
    }
    context.batch(queryList).execute();
  }

  private void mergeAttributesToDb(Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) {
    var queryList = new ArrayList<Query>();
    Set<byte[]> updatedHandles = new HashSet<>();
    for (var handleAttribute : handleAttributes) {
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handleAttribute.handle())
          .set(HANDLES.IDX, handleAttribute.index())
          .set(HANDLES.TYPE, handleAttribute.type().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false)
          .onDuplicateKeyUpdate()
          .set(HANDLES.HANDLE, handleAttribute.handle())
          .set(HANDLES.IDX, handleAttribute.index())
          .set(HANDLES.TYPE, handleAttribute.type().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false);
      queryList.add(query);
      if (updatedHandles.add(handleAttribute.handle())) {
        queryList.add(versionIncrement(handleAttribute.handle(), recordTimestamp));
      }
    }
    context.batch(queryList).execute();
  }

  // Archive
  public void archiveRecord(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
    mergeAttributesToDb(recordTimestamp, handleAttributes);
    removeNonTombstoneFields(List.of(handleAttributes.get(0).handle()));
  }

  public void archiveRecords(Instant recordTimestamp, List<HandleAttribute> handleAttributes,
      List<byte[]> handles) {
    mergeAttributesToDb(recordTimestamp, handleAttributes);
    removeNonTombstoneFields(handles);
  }

  // Update
  public void updateRecord(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
    byte[] handle = handleAttributes.get(0).handle();
    var query = prepareUpdateQuery(handle, recordTimestamp, handleAttributes);
    context.batch(query).execute();
  }

  public void updateRecordBatch(Instant recordTimestamp,
      List<List<HandleAttribute>> handleRecords) {

    List<Query> queryList = new ArrayList<>();
    for (List<HandleAttribute> handleRecord : handleRecords) {
      queryList.addAll(
          prepareUpdateQuery(handleRecord.get(0).handle(), recordTimestamp, handleRecord));
    }
    context.batch(queryList).execute();
  }

  private ArrayList<Query> prepareUpdateQuery(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) {
    var queryList = new ArrayList<Query>();
    for (var handleAttribute : handleAttributes) {
      var query = context.update(HANDLES)
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .where(HANDLES.HANDLE.eq(handle))
          .and(HANDLES.IDX.eq(handleAttribute.index()));
      queryList.add(query);
    }
    queryList.add(versionIncrement(handle, recordTimestamp));
    return queryList;
  }

  private Query versionIncrement(byte[] handle, Instant recordTimestamp) {
    var currentVersion =
        Integer.parseInt(Objects.requireNonNull(context.select(HANDLES.DATA)
            .from(HANDLES)
            .where(HANDLES.HANDLE.eq(handle))
            .and(HANDLES.TYPE.eq(ISSUE_NUMBER.getBytes(StandardCharsets.UTF_8)))
            .fetchOne(dbRecord -> new String(dbRecord.value1(), StandardCharsets.UTF_8))));
    int version = currentVersion + 1;

    return context.update(HANDLES)
        .set(HANDLES.DATA, String.valueOf(version).getBytes(StandardCharsets.UTF_8))
        .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.eq(ISSUE_NUMBER.getBytes(StandardCharsets.UTF_8)));
  }

  private void removeNonTombstoneFields(List<byte[]> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.notIn(TOMBSTONE_RECORD_FIELDS_BYTES))
        .execute();
  }

}
