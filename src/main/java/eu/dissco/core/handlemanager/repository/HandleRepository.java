package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;

import eu.dissco.core.handlemanager.database.jooq.tables.records.HandlesRecord;
import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HandleRepository {

  private static final int TTL = 86400;
  private static final String INVALID_FIELD_ERROR = "Error: Attempting to add an invalid field into a pid record. Field: %s, Data: %s";
  private static final String FIELD_MISMATCH_ERROR = "Field mismatch: attempting to add a forbidden field in the record schema. Check ";
  private final DSLContext context;

  private final Map<String, TableField<HandlesRecord, ? extends Serializable>> attributeMapping = Map.of(
      "index", HANDLES.IDX,
      "handle", HANDLES.HANDLE,
      "type", HANDLES.TYPE,
      "data", HANDLES.DATA
  );

  public List<byte[]> checkDuplicateHandles(List<byte[]> handles) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  // Resolve Pid
  public List<HandleAttribute> resolveHandle(byte[] handle) {
    List<Record4<Integer, byte[], byte[], byte[]>> dbRecord = context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .fetch();

    return mapToAttribute(dbRecord);
  }

  private List<HandleAttribute> mapToAttribute(
      List<Record4<Integer, byte[], byte[], byte[]>> dbRecord) {
    List<HandleAttribute> attributes = new ArrayList<>();
    for (Record4<Integer, byte[], byte[], byte[]> row : dbRecord) {
      attributes.add(new HandleAttribute(
          row.get(HANDLES.IDX),
          row.get(HANDLES.HANDLE),
          new String(row.get(HANDLES.TYPE)),
          row.get(HANDLES.DATA)
      ));
    }
    return attributes;
  }

  // Get List of Pids
  public List<String> getAllHandles(byte[] pidStatus, int pageNum, int pageSize) {
    return context
        .selectDistinct(HANDLES.HANDLE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.DATA.eq(pidStatus)) // Hmm...
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
  private void rollbackHandleCreation(List<byte[]> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .execute();
  }

  private void rollbackHandleCreation(List<byte[]> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .execute();
  }

  // Record Creation
  // Batch Record Posting  - Same for Handle, DOI, DigitalSpecimen, DigitalSpecimenBotany
  private void postBatchRecord(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
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

  // Handle Batch Creation
  public List<HandleRecordResponse> createHandleRecordBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    postBatchRecord(recordTimestamp, handleAttributes);
    try {
      return mapPostedRecordToHandleRecordResponse(handles);
    } catch (PidCreationException e) {
      rollbackHandleCreation(handles); // If an error has occured, delete any handles we've posted
      throw new PidCreationException(e.getMessage());
    }
  }

  private List<HandleRecordResponse> mapPostedRecordToHandleRecordResponse(List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    byte[] handle = posted.keySet().iterator().next(); // First handle retrieved
    List<Record3<byte[], byte[], byte[]>> aggregatedRecord = new ArrayList<>();
    List<HandleRecordResponse> responses = new ArrayList<>();

    int endOfList = posted.size() - 1;
    int i = 0;

    // Our database result is a list of all rows posted (functionally decoupled from their handle id)
    // In this loop, we create a HandleRecordResponse for each group of handles
    // Where each "group" of handles is a handle record
    for (Map.Entry<byte[], Result<Record3<byte[], byte[], byte[]>>> entry : posted.entrySet()) {
      if (Arrays.equals(handle, entry.getKey()) && i < endOfList) {
        // While previous handle = current handle, add this row to the handle record
        aggregatedRecord.add(entry.getValue().get(0));
      } else if (i == endOfList) {
        // Special case for end of list
        aggregatedRecord.add(entry.getValue().get(0));
        responses.add(buildHandleRecordResponse(aggregatedRecord));
      } else {
        // If we've found all rows under a given handle, create a response from those rows
        responses.add(buildHandleRecordResponse(aggregatedRecord));
        if (i < endOfList - 1) {
          // if this is not the last record in the list, we clear the list for the next handle record
          aggregatedRecord.clear();
        }
        aggregatedRecord.add(entry.getValue().get(0));
        handle = entry.getKey();
      }
      i++;
    }
    return responses;
  }

  // Given a list of database rows (which can be considered
  private HandleRecordResponse buildHandleRecordResponse(
      List<Record3<byte[], byte[], byte[]>> records)
      throws PidCreationException {
    HandleRecordResponse response = new HandleRecordResponse();
    String type;
    String data;

    for (Record3<byte[], byte[], byte[]> r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  // Doi Batch Creation
  public List<DoiRecordResponse> createDoiRecordBatch(List<byte[]> handles, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postBatchRecord(recordTimestamp, handleAttributes);
    return mapPostedRecordToDoiResponse(handles);
  }

  private List<DoiRecordResponse> mapPostedRecordToDoiResponse(List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    byte[] handle = posted.keySet().iterator().next();
    List<Record3<byte[], byte[], byte[]>> aggregatedRecord = new ArrayList<>();
    List<DoiRecordResponse> responses = new ArrayList<>();

    int endOfList = posted.size() - 1;
    int i = 0;

    // Create a handle record response for each group of handles
    for (Map.Entry<byte[], Result<Record3<byte[], byte[], byte[]>>> entry : posted.entrySet()) {
      if (Arrays.equals(handle, entry.getKey()) && i < endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
      } else if (i == endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
        responses.add(buildDoiRecordResponse(aggregatedRecord));
      } else {
        responses.add(buildDoiRecordResponse(aggregatedRecord));
        if (i < endOfList - 1) {
          aggregatedRecord.clear();
        }
        aggregatedRecord.add(entry.getValue().get(0));
        handle = entry.getKey();
      }
      i++;
    }
    return responses;
  }

  private DoiRecordResponse buildDoiRecordResponse(
      List<Record3<byte[], byte[], byte[]>> records)
      throws PidCreationException {
    DoiRecordResponse response = new DoiRecordResponse();

    String type;
    String data;
    for (Record3<byte[], byte[], byte[]> r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  // Batch Digital Specimen Creation
  public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postBatchRecord(recordTimestamp, handleAttributes);
    return mapPostedRecordToDigitalSpecimenResponse(handles);
  }

  private List<DigitalSpecimenResponse> mapPostedRecordToDigitalSpecimenResponse(
      List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    byte[] handle = posted.keySet().iterator().next();
    List<Record3<byte[], byte[], byte[]>> aggregatedRecord = new ArrayList<>();
    List<DigitalSpecimenResponse> responses = new ArrayList<>();

    int endOfList = posted.size() - 1;
    int i = 0;

    // Create a handle record response for each group of handles
    for (Map.Entry<byte[], Result<Record3<byte[], byte[], byte[]>>> entry : posted.entrySet()) {
      if (Arrays.equals(handle, entry.getKey()) && i < endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
      } else if (i == endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
        responses.add(buildDigitalSpecimenResponse(aggregatedRecord));
      } else {
        responses.add(buildDigitalSpecimenResponse(aggregatedRecord));
        if (i < endOfList - 1) {
          aggregatedRecord.clear();
        }
        aggregatedRecord.add(entry.getValue().get(0));
        handle = entry.getKey();
      }
      i++;
    }
    return responses;
  }

  private DigitalSpecimenResponse buildDigitalSpecimenResponse(
      List<Record3<byte[], byte[], byte[]>> records)
      throws PidCreationException {
    DigitalSpecimenResponse response = new DigitalSpecimenResponse();

    String type;
    String data;
    for (Record3<byte[], byte[], byte[]> r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  // DigitalSpecimenBotany Batch Creation

  public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postBatchRecord(recordTimestamp, handleAttributes);
    return mapPostedRecordToDigitalSpecimenBotanyResponse(handles);
  }

  private List<DigitalSpecimenBotanyResponse> mapPostedRecordToDigitalSpecimenBotanyResponse(
      List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    byte[] handle = posted.keySet().iterator().next();
    List<Record3<byte[], byte[], byte[]>> aggregatedRecord = new ArrayList<>();
    List<DigitalSpecimenBotanyResponse> responses = new ArrayList<>();

    int endOfList = posted.size() - 1;
    int i = 0;

    // Create a handle record response for each group of handles
    for (Map.Entry<byte[], Result<Record3<byte[], byte[], byte[]>>> entry : posted.entrySet()) {
      if (Arrays.equals(handle, entry.getKey()) && i < endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
      } else if (i == endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
        responses.add(buildDigitalSpecimenBotanyResponse(aggregatedRecord));
      } else {
        responses.add(buildDigitalSpecimenBotanyResponse(aggregatedRecord));
        if (i < endOfList - 1) {
          aggregatedRecord.clear();
        }
        aggregatedRecord.add(entry.getValue().get(0));
        handle = entry.getKey();
      }
      i++;
    }
    return responses;
  }

  private DigitalSpecimenBotanyResponse buildDigitalSpecimenBotanyResponse(
      List<Record3<byte[], byte[], byte[]>> records)
      throws PidCreationException {
    DigitalSpecimenBotanyResponse response = new DigitalSpecimenBotanyResponse();

    String type;
    String data;
    for (Record3<byte[], byte[], byte[]> r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  // Create Individual Records
  public HandleRecordResponse createHandle(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    HandleRecordResponse response = new HandleRecordResponse();

    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            String.format(INVALID_FIELD_ERROR, handleAttribute.type(), handleAttribute.type()));
      }
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handle)
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
    return response;
  }

  public DoiRecordResponse createDoi(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DoiRecordResponse response = new DoiRecordResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            String.format(INVALID_FIELD_ERROR, handleAttribute.type(), handleAttribute.type()));
      }
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handle)
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
    return response;
  }

  public DigitalSpecimenResponse createDigitalSpecimen(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DigitalSpecimenResponse response = new DigitalSpecimenResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            String.format(INVALID_FIELD_ERROR, handleAttribute.type(), handleAttribute.type()));
      }
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handle)
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
    return response;
  }

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotany(byte[] handle,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DigitalSpecimenBotanyResponse response = new DigitalSpecimenBotanyResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            String.format(INVALID_FIELD_ERROR, handleAttribute.type(), handleAttribute.type()));
      }
      var query = context.insertInto(HANDLES)
          .set(HANDLES.HANDLE, handle)
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
    return response;
  }

}
