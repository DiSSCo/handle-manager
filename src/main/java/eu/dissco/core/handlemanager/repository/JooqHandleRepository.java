package eu.dissco.core.handlemanager.repository;

import eu.dissco.core.handlemanager.domain.pidrecords.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.*;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.jooqrepository.tables.records.HandlesRecord;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import static eu.dissco.core.handlemanager.jooqrepository.tables.Handles.HANDLES;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqHandleRepository {
  private static final int TTL = 86400;
  private static final String ERROR = "Error: Attempting to add an invalid field into a pid record. Field: %s, Data: %s";
  private final DSLContext context;

  private final Map<String, TableField<HandlesRecord, ? extends Serializable>> attributeMapping = Map.of(
      "handle", HANDLES.HANDLE,
      "idx", HANDLES.IDX,
      "type", HANDLES.TYPE,
      "data", HANDLES.DATA
  );

  public HandleRecordResponse createHandle(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    HandleRecordResponse response = new HandleRecordResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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

  public DoiRecordResponse createDoi(byte[] handle, Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DoiRecordResponse response = new DoiRecordResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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

  public DigitalSpecimenResponse createDigitalSpecimen(byte[] handle, Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DigitalSpecimenResponse response = new DigitalSpecimenResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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

  public DigitalSpecimenBotanyResponse createDigitalSpecimenBotany(byte[] handle, Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    DigitalSpecimenBotanyResponse response = new DigitalSpecimenBotanyResponse();
    for (var handleAttribute : handleAttributes) {
      try {
        response.setAttribute(handleAttribute.type(), new String(handleAttribute.data()));
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(String.format(ERROR, handleAttribute.type(), handleAttribute.type()));
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
