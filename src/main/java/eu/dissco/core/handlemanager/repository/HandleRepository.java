package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import java.nio.charset.StandardCharsets;
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
import org.jooq.SelectConditionStep;
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
        .and(HANDLES.TYPE.eq(PID_STATUS.get().getBytes(StandardCharsets.UTF_8)))
        .and(HANDLES.DATA.notEqual("ARCHIVED".getBytes()))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  public List<HandleAttribute> resolveHandleAttributes(byte[] handle) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.get().getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> resolveHandleAttributes(List<byte[]> handles) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.get().getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> searchByNormalisedPhysicalIdentifier(
      List<byte[]> normalisedPhysicalIdentifiers) {

    return searchByNormalisedPhysicalIdentifierQuery(normalisedPhysicalIdentifiers)
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> getPrimarySpecimenObjectId(List<byte[]> handles) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.eq(PRIMARY_SPECIMEN_OBJECT_ID.get().getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  public List<HandleAttribute> searchByNormalisedPhysicalIdentifierFullRecord(
      List<byte[]> normalisedPhysicalIdentifiers) {
    var normalisedPhysicalIdentifierTable = searchByNormalisedPhysicalIdentifierQuery(
        normalisedPhysicalIdentifiers)
        .asTable("normalisedPhysicalIdentifierTable");

    return context.select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .join(normalisedPhysicalIdentifierTable)
        .on(HANDLES.HANDLE.eq(normalisedPhysicalIdentifierTable.field(HANDLES.HANDLE)))
        .where(HANDLES.TYPE.notEqual(HS_ADMIN.get().getBytes(StandardCharsets.UTF_8)))
        .fetch(this::mapToAttribute);
  }

  private SelectConditionStep<Record4<Integer, byte[], byte[], byte[]>>
  searchByNormalisedPhysicalIdentifierQuery(List<byte[]> normalisedPhysicalIdentifiers) {
    return context.select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE,
            HANDLES.DATA)
        .from(HANDLES)
        .where(
            HANDLES.TYPE.eq(NORMALISED_SPECIMEN_OBJECT_ID.get().getBytes(StandardCharsets.UTF_8)))
        .and((HANDLES.DATA).in(normalisedPhysicalIdentifiers));
  }

  // Get List of Pids
  public List<String> getAllHandles(byte[] pidStatus, int pageNum, int pageSize) {
    int offset = getOffset(pageNum, pageSize);

    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.TYPE.eq(PID_STATUS.get().getBytes(StandardCharsets.UTF_8)))
        .and(HANDLES.DATA.eq(pidStatus))
        .limit(pageSize)
        .offset(offset)
        .fetch()
        .getValues(HANDLES.HANDLE, String.class);
  }

  public List<String> getAllHandles(int pageNum, int pageSize) {
    int offset = getOffset(pageNum, pageSize);
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .limit(pageSize)
        .offset(offset)
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
  public void postAttributesToDb(long recordTimestamp, List<HandleAttribute> handleAttributes) {
    var queryList = prepareBatchPostQuery(recordTimestamp, handleAttributes);
    context.batch(queryList).execute();
  }

  private List<Query> prepareBatchPostQuery(long recordTimestamp,
      List<HandleAttribute> handleAttributes) {
    var queryList = new ArrayList<Query>();

    for (var handleAttribute : handleAttributes) {
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handleAttribute.getHandle())
          .set(HANDLES.IDX, handleAttribute.getIndex())
          .set(HANDLES.TYPE, handleAttribute.getType().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.getData())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp)
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false);
      queryList.add(query);
    }
    return queryList;
  }

  // Archive
  public void archiveRecords(long recordTimestamp, List<HandleAttribute> handleAttributes,
      List<String> handles) {
    updateRecord(recordTimestamp, handleAttributes, true);
    deleteNonTombstoneAttributes(handles);
  }

  private void deleteNonTombstoneAttributes(List<String> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.IDX.notBetween(1).and(39))
        .and(HANDLES.IDX.notBetween(100).and(101))
        .execute();
  }

  public void postAndUpdateHandles(long recordTimestamp, List<HandleAttribute> createAttributes,
      List<List<HandleAttribute>> updateAttributes) {
    var queryList = prepareBatchUpdateQuery(recordTimestamp, updateAttributes, true);
    queryList.addAll(prepareBatchPostQuery(recordTimestamp, createAttributes));
    context.batch(queryList).execute();
  }

  // Update
  public void updateRecord(long recordTimestamp, List<HandleAttribute> handleAttributes,
      boolean incrementVersion) {
    var query = prepareUpdateQuery(recordTimestamp, handleAttributes, incrementVersion);
    context.batch(query).execute();
  }

  public void updateRecordBatch(long recordTimestamp,
      List<List<HandleAttribute>> handleRecords, boolean incrementVersion) {
    var queryList = prepareBatchUpdateQuery(recordTimestamp, handleRecords, incrementVersion);
    context.batch(queryList).execute();
  }

  private List<Query> prepareBatchUpdateQuery(long recordTimestamp,
      List<List<HandleAttribute>> handleRecords,
      boolean incrementVersion) {
    List<Query> queryList = new ArrayList<>();
    for (List<HandleAttribute> handleRecord : handleRecords) {
      queryList.addAll(
          prepareUpdateQuery(recordTimestamp, handleRecord, incrementVersion));
    }
    return queryList;
  }

  private ArrayList<Query> prepareUpdateQuery(long recordTimestamp,
      List<HandleAttribute> handleAttributes, boolean incrementVersion) {
    var queryList = new ArrayList<Query>();
    Set<byte[]> updatedHandles = new HashSet<>();
    for (var handleAttribute : handleAttributes) {
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handleAttribute.getHandle())
          .set(HANDLES.IDX, handleAttribute.getIndex())
          .set(HANDLES.TYPE, handleAttribute.getType().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.getData())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp)
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false)
          .onDuplicateKeyUpdate()
          .set(HANDLES.HANDLE, handleAttribute.getHandle())
          .set(HANDLES.IDX, handleAttribute.getIndex())
          .set(HANDLES.TYPE, handleAttribute.getType().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.getData())
          .set(HANDLES.TTL, TTL)
          .set(HANDLES.TIMESTAMP, recordTimestamp)
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false);
      queryList.add(query);
      if (updatedHandles.add(handleAttribute.getHandle())) {
        queryList.add(
            versionIncrement(handleAttribute.getHandle(), recordTimestamp, incrementVersion));
      }
    }
    return queryList;
  }

  private Query versionIncrement(byte[] handle, long recordTimestamp, boolean incrementVersion) {
    var currentVersion =
        Integer.parseInt(Objects.requireNonNull(context.select(HANDLES.DATA)
            .from(HANDLES)
            .where(HANDLES.HANDLE.eq(handle))
            .and(HANDLES.TYPE.eq(PID_RECORD_ISSUE_NUMBER.get().getBytes(StandardCharsets.UTF_8)))
            .fetchOne(dbRecord -> new String(dbRecord.value1(), StandardCharsets.UTF_8))));
    int version = incrementVersion ? currentVersion + 1 : currentVersion - 1;

    return context.update(HANDLES)
        .set(HANDLES.DATA, String.valueOf(version).getBytes(StandardCharsets.UTF_8))
        .set(HANDLES.TIMESTAMP, recordTimestamp)
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.eq(PID_RECORD_ISSUE_NUMBER.get().getBytes(StandardCharsets.UTF_8)));
  }

  public void rollbackHandles(List<String> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .execute();
  }

  private int getOffset(int pageNum, int pageSize) {
    int offset = 0;
    if (pageNum > 1) {
      offset = offset + (pageSize * (pageNum - 1));
    }
    return offset;
  }

}
