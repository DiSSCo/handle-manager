package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;
import static eu.dissco.core.handlemanager.domain.PidRecords.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  private static final String INVALID_FIELD_ERROR = "Error: Attempting to add an invalid field into a pid record. Field: %s, Data: %s";
  private static final String FIELD_MISMATCH_ERROR = "Field mismatch: attempting to add a forbidden field in the record schema. Check ";
  private static final String PID_ROLLBACK_MESSAGE = "An error has occured posting the record. Reason: %s. Rolling back";
  private final DSLContext context;
  private final ObjectMapper mapper;

  // For Handle Name Generation
  public List<byte[]> checkHandlesExist(List<byte[]> handles) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  // Resolving handles
  public ObjectNode resolveSingleRecord(byte[] handle) throws PidResolutionException {
    var dbRecord = resolveHandleAttributes(handle);
    if (dbRecord.isEmpty()) {
      throw new PidResolutionException("Unable to resolve handle");
    }
    return jsonFormatSingleRecord(dbRecord);
  }

  public List<ObjectNode> resolveBatchRecord(List<byte[]> handles) throws PidResolutionException {
    var dbRecord = resolveHandleAttributes(handles);
    var handleMap = mapRecords(dbRecord);

    List<ObjectNode> rootNodeList = new ArrayList<>();
    Set<String> resolvedHandles = new HashSet<>();
    Set<String> allHandles = handleMap.keySet();

    for (Map.Entry<String, List<HandleAttribute>> handleRecord : handleMap.entrySet()) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord.getValue()));
      resolvedHandles.add(handleRecord.getKey());
    }

    allHandles.removeAll(resolvedHandles);
    if (!allHandles.isEmpty()) {
      throw new PidResolutionException(
          "PID RESOLUTION ERROR. Unable to resolve the following handles: " + allHandles);
    }
    return rootNodeList;
  }

  private ObjectNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode subNode;
    String data;
    String type;
    for (HandleAttribute row : dbRecord) {
      type = row.type();
      data = new String(row.data());
      if (FIELD_IS_PID_RECORD.contains(type)) {
        try {
          subNode = mapper.readValue(data, ObjectNode.class);
          rootNode.set(type, subNode);
        } catch (JsonProcessingException e) {
          // Not 100% sure if an exception should be thrown here. We don't want to make a poorly formatted record un-resolvable
          log.warn("Type \"{}\" is noncompliant to the PID kernel model. Invalid data: {}", type,
              data);
        }
      } else {
        rootNode.put(type, data);
      }
    }
    return rootNode;
  }

  public List<HandleAttribute> resolveHandleAttributes(byte[] handle) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);
  }

  private List<HandleAttribute> resolveHandleAttributes(List<byte[]> handles) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);
  }

  // Get List of Pids
  public List<String> getAllHandles(byte[] pidStatus, int pageNum, int pageSize) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.TYPE.eq(PID_STATUS.getBytes(StandardCharsets.UTF_8)))
        .and(HANDLES.DATA.eq(pidStatus))
        .limit(pageSize)
        .offset(pageNum)
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
        new String(row.get(HANDLES.TYPE)),
        row.get(HANDLES.DATA));
  }

  // Post
  public ObjectNode createRecord(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    ObjectNode postedRecord;
    try {
      postedRecord = resolveSingleRecord(handle);
    } catch (PidResolutionException e) {
      rollbackRecordCreation(handle);
      throw new PidCreationException(String.format(PID_ROLLBACK_MESSAGE, "2"));
    }
    return postedRecord;
  }

  public List<ObjectNode> createRecordBatchJson(List<byte[]> handles
      , Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    List<ObjectNode> postedRecords;
    try {
      postedRecords = resolveBatchRecord(handles);
    } catch (PidResolutionException e) {
      rollbackRecordCreation(handles);
      throw new PidCreationException(String.format(PID_ROLLBACK_MESSAGE, "1"));
    }
    return postedRecords;
  }

  public void postAttributesToDb(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
    var queryList = new ArrayList<Query>();

    for (var handleAttribute : handleAttributes) {
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handleAttribute.handle())
          .set(HANDLES.IDX, handleAttribute.index())
          .set(HANDLES.TYPE, handleAttribute.type().getBytes(StandardCharsets.UTF_8))
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TTL, 86400)
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .set(HANDLES.ADMIN_READ, true)
          .set(HANDLES.ADMIN_WRITE, true)
          .set(HANDLES.PUB_READ, true)
          .set(HANDLES.PUB_WRITE, false);
      queryList.add(query);
    }
    context.batch(queryList).execute();
  }

  private HashMap<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {

    HashMap<String, List<HandleAttribute>> handleMap = new HashMap<>();

    for (HandleAttribute row : flatList) {
      String handle = new String(row.handle());
      if (handleMap.containsKey(handle)) {
        List<HandleAttribute> tmpList = new ArrayList<>(handleMap.get(handle));
        tmpList.add(row);
        handleMap.replace(handle, tmpList);
      } else {
        handleMap.put(handle, List.of(row));
      }
    }
    return handleMap;
  }

  // Rollback
  private void rollbackRecordCreation(List<byte[]> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .execute();
  }

  private void rollbackRecordCreation(byte[] handle) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .execute();
  }

  // Update
  public ObjectNode updateRecord(Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException, PidResolutionException {
    byte[] handle = handleAttributes.get(0).handle();
    var query = prepareUpdateQuery(handle, recordTimestamp, handleAttributes, true);
    context.batch(query).execute();

    return resolveSingleRecord(handle);
  }

  public void updateRecordBatch(List<byte[]> handles, Instant recordTimestamp,
      List<List<HandleAttribute>> handleRecords)
      throws PidResolutionException {

    List<Query> queryList = new ArrayList<>();
    for (List<HandleAttribute> handleRecord : handleRecords) {
      queryList.addAll(
          prepareUpdateQuery(handleRecord.get(0).handle(), recordTimestamp, handleRecord, true));
    }
    context.batch(queryList).execute();
  }

  private ArrayList<Query> prepareUpdateQuery(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes, boolean versionIncrement) {
    var queryList = new ArrayList<Query>();
    for (var handleAttribute : handleAttributes) {
      var query = context.update(HANDLES)
          .set(HANDLES.DATA, handleAttribute.data())
          .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
          .where(HANDLES.HANDLE.eq(handle))
          .and(HANDLES.IDX.eq(handleAttribute.index()));
      queryList.add(query);
    }
    queryList.add(versionIncrement(handle, recordTimestamp, versionIncrement));
    return queryList;
  }

  private Query versionIncrement(byte[] handle, Instant recordTimestamp, boolean versionIncrement) {
    var currentVersion =
        Integer.parseInt(Objects.requireNonNull(context.select(HANDLES.DATA)
            .from(HANDLES)
            .where(HANDLES.HANDLE.eq(handle))
            .and(HANDLES.TYPE.eq(ISSUE_NUMBER.getBytes(StandardCharsets.UTF_8)))
            .fetchOne(dbRecord -> new String(dbRecord.value1()))));
    int version;
    if (versionIncrement) {
      version = currentVersion + 1;
    } else {
      version = currentVersion - 1;
    }

    return context.update(HANDLES)
        .set(HANDLES.DATA, String.valueOf(version).getBytes(StandardCharsets.UTF_8))
        .set(HANDLES.TIMESTAMP, recordTimestamp.getEpochSecond())
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.eq(ISSUE_NUMBER.getBytes(StandardCharsets.UTF_8)));
  }

}
