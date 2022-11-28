package eu.dissco.core.handlemanager.repository;

import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.*;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.database.jooq.tables.records.HandlesRecord;
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

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqHandleRepository {

  private static final int TTL = 86400;
  private static final String ERROR = "Error: Attempting to add an invalid field into a pid record. Field: %s, Data: %s";
  private final DSLContext context;

  private final Map<String, TableField<HandlesRecord, ? extends Serializable>> attributeMapping = Map.of(
      "index", HANDLES.IDX,
      "handle", HANDLES.HANDLE,
      "type", HANDLES.TYPE,
      "data", HANDLES.DATA
  );

  // Resolve Pid
  public List<HandleAttribute> resolveHandle(byte[] handle){
    List<Record4<Integer, byte[], byte[], byte[]>> dbRecord = context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .fetch();

    return mapToAttribute(dbRecord);
  }

  private List<HandleAttribute> mapToAttribute(List<Record4<Integer, byte[], byte[], byte[]>> dbRecord){
    List<HandleAttribute> attributes = new ArrayList<>();
    for (Record4<Integer, byte[], byte[], byte[]> row: dbRecord) {
      attributes.add(new HandleAttribute(
          row.get(HANDLES.IDX),
          row.get(HANDLES.HANDLE),
          new String(row.get(HANDLES.TYPE)),
          row.get(HANDLES.DATA)
      ));
    }
    return attributes;
  }

  // Handle Batch Creation
  public List<HandleRecordResponse> createHandleRecordBatch(List<byte[]> handles, Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException{
    postBatchRecord(recordTimestamp, handleAttributes);
    // Todo catch pidCreationException, rollback any handles created
    return mapPostedRecordToHandleRecordResponse(handles);
  }

  private List<HandleRecordResponse> mapPostedRecordToHandleRecordResponse(List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    byte[] handle = posted.keySet().iterator().next();
    List<Record3<byte[], byte[], byte[]>> aggregatedRecord = new ArrayList<>();
    List<HandleRecordResponse> responses = new ArrayList<>();

    int endOfList = posted.size() - 1;
    int i = 0;

    // Create a handle record response for each group of handles
    for (Map.Entry<byte[], Result<Record3<byte[], byte[], byte[]>>> entry : posted.entrySet()) {
      if (Arrays.equals(handle, entry.getKey()) && i < endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
      } else if (i == endOfList) {
        aggregatedRecord.add(entry.getValue().get(0));
        responses.add(buildHandleRecordResponse(aggregatedRecord));
      } else {
        responses.add(buildHandleRecordResponse(aggregatedRecord));
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

  private HandleRecordResponse buildHandleRecordResponse(
      List<Record3<byte[], byte[], byte[]>> records)
      throws PidCreationException {
    HandleRecordResponse response = new HandleRecordResponse();

    String type;
    String data;
    for (Record3 r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            "Field mismatch: attempting to add a forbidden field in the record schema. Check "
                + type);
      }
    }
    return response;
  }
  
  // Doi Batch Creation
  public List<DoiRecordResponse> createDoiRecordBatch(List<byte[]> handles, Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException{
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
    for (Record3 r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            "Field mismatch: attempting to add a forbidden field in the record schema. Check "
                + type);
      }
    }
    return response;
  }

  // Batch Digital Specimen Creation
  public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(List<byte[]> handles, Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException{
    postBatchRecord(recordTimestamp, handleAttributes);
    return mapPostedRecordToDigitalSpecimenResponse(handles);
  }

  private List<DigitalSpecimenResponse> mapPostedRecordToDigitalSpecimenResponse(List<byte[]> handles)
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
    for (Record3 r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            "Field mismatch: attempting to add a forbidden field in the record schema. Check "
                + type);
      }
    }
    return response;
  }
  
  // DigitalSpecimenBotany Batch Creation

  public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(List<byte[]> handles, Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException{
    postBatchRecord(recordTimestamp, handleAttributes);
    return mapPostedRecordToDigitalSpecimenBotanyResponse(handles);
  }

  private List<DigitalSpecimenBotanyResponse> mapPostedRecordToDigitalSpecimenBotanyResponse(List<byte[]> handles)
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
    for (Record3 r : records) {
      type = new String((byte[]) r.getValue(1));
      data = new String((byte[]) r.getValue(2));

      try {
        response.setAttribute(type, data);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            "Field mismatch: attempting to add a forbidden field in the record schema. Check "
                + type);
      }
    }
    return response;
  }

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
            String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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
            String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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
            String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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
            String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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
