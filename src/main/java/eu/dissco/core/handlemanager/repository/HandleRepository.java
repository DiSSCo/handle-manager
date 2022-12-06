package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.database.jooq.tables.Handles.HANDLES;
import static eu.dissco.core.handlemanager.domain.PidRecords.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.database.jooq.tables.records.HandlesRecord;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenBotanyResponse;
import eu.dissco.core.handlemanager.domain.responses.DigitalSpecimenResponse;
import eu.dissco.core.handlemanager.domain.responses.DoiRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.HandleRecordResponse;
import eu.dissco.core.handlemanager.domain.responses.TombstoneRecordResponse;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

  private static final String TOO_MANY_FIELDS_ERROR = "Inappropriate record has been created. Reason: Too many fields created for type of record. Expected type = %s. Superfluous fields = %s";
  private static final String INSUFFICIENT_FIELDS_ERROR = "Inappropriate record has been created. Reason: Missing fields for type this of record. Expected type = %s. Missing fields = %s";
  private final DSLContext context;
  private final ObjectMapper mapper;

  private final Map<String, TableField<HandlesRecord, ? extends Serializable>> attributeMapping = Map.of(
      "index", HANDLES.IDX,
      "handle", HANDLES.HANDLE,
      "type", HANDLES.TYPE,
      "data", HANDLES.DATA
  );

  // For Handle Name Generation
  public List<byte[]> checkDuplicateHandles(List<byte[]> handles) {
    return context
        .selectDistinct(HANDLES.HANDLE)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetch()
        .getValues(HANDLES.HANDLE, byte[].class);
  }

  // Resolving handles
  public ObjectNode resolveSingleRecord(byte[] handle) throws PidResolutionException {
    var dbRecord = fetchRecordFromDb(handle);
    if (dbRecord.isEmpty()) {
      throw new PidResolutionException("Unable to resolve handle");
    }
    return jsonFormatSingleRecord(dbRecord);
  }

  public List<ObjectNode> resolveBatchRecord(List<byte[]> handles) {
    var dbRecord = fetchRecordFromDb(handles);
    var aggrRecord = aggregateRecords(dbRecord);
    List<ObjectNode> rootNodeList = new ArrayList<>();

    for (List<HandleAttribute> handleRecord : aggrRecord) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord));
    }
    return rootNodeList;
  }

  private HandleAttribute mapToAttribute(Record4<Integer, byte[], byte[], byte[]> row) {
    return new HandleAttribute(
        row.get(HANDLES.IDX),
        row.get(HANDLES.HANDLE),
        new String(row.get(HANDLES.TYPE)),
        row.get(HANDLES.DATA));
  }

  private List<HandleAttribute> fetchRecordFromDb(byte[] handle) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);
  }

  private List<HandleAttribute> fetchRecordFromDb(List<byte[]> handles) {
    return context
        .select(HANDLES.IDX, HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .and(HANDLES.TYPE.notEqual(HS_ADMIN.getBytes(StandardCharsets.UTF_8))) // Omit HS_ADMIN
        .fetch(this::mapToAttribute);
  }

  private List<List<HandleAttribute>> aggregateRecords(List<HandleAttribute> flatList) {

    byte[] handle = flatList.get(0).handle(); // First handle retrieved
    List<List<HandleAttribute>> aggrList = new ArrayList<>();
    List<HandleAttribute> singleRecord = new ArrayList<>();

    int endOfList = flatList.size() - 1;
    int i = 0;

    // Our database result is a list of all rows posted (functionally decoupled from their handle id)
    // In this loop, we create a HandleRecordResponse for each group of handles
    // Where each "group" of handles is a handle record

    for (HandleAttribute row : flatList) {
      if (Arrays.equals(handle, row.handle()) && i < endOfList) {
        // While previous handle = current handle, add this row to the handle record
        singleRecord.add(row);
      } else if (i == endOfList) {
        // Special case for end of list
        singleRecord.add(row);
        aggrList.add(singleRecord);
      } else {
        // If we've found all rows under a given handle, create a response from those rows
        aggrList.add(singleRecord);
        if (i < endOfList - 1) {
          // if this is not the last record in the fetch, we clear the list for the next handle record
          singleRecord.clear();
        }
        singleRecord.add(row);
        handle = row.handle();
      }
      i++;
    }
    return aggrList;
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

  // Resolve Pid
  public List<HandleAttribute> resolveHandleAttributes(byte[] handle) {
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

  // TODO: When we resolve a record, will we always know its type (i.e. handle, DOI, digitalSpecimen)? I'm inclined to say no.
  // Will we have to make that decision based on what fields are present in the resolved record?
  // Should this be determined by digitalObjectType?

  private List<HandleRecordResponse> resolveHandleRecordBatch(List<byte[]> handles)
      throws PidCreationException {

    // Fetch all posted handles
    Map<byte[], Result<Record3<byte[], byte[], byte[]>>> posted = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .fetchGroups(HANDLES.HANDLE);

    /* The result of this fetch is a "map" (wherein keys are not unique) of handle, result(handle, type, data)
    So it would look like:
      20.10/123,   (20.10/123, pidStatus, ACTIVE)
      20.10/123,   (20.10/123, issueDate, 2022-10-10)
      20.10/ABC,   (20.10/ABC, pidStatus, DRAFT)
      20.10/ABC,   (20.10/ABC, issueDate, 2022-06-06)

    And so on.
    This next code block goes through the "map" sequentially and groups rows with the same handle into a list.
    The list gets turned into a HandleRecordResponse object, then we aggregate the next group of handle rows.

    It's this complicated because of how records are stored in the database. I'm open to other approaches.
     */

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
        response = setHandleRecordAttribute(type, data, response);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  public DoiRecordResponse resolveDoiRecord(byte[] handle) throws PidCreationException {
    Result<Record3<byte[], byte[], byte[]>> handleRecord = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .fetch();
    return buildDoiRecordResponse(handleRecord);
  }

  private List<DoiRecordResponse> resolveDoiRecordBatch(List<byte[]> handles)
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
        response = setDoiRecordAttribute(type, data, response);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  public DigitalSpecimenResponse resolveDigitalSpecimenRecord(byte[] handle)
      throws PidCreationException {
    Result<Record3<byte[], byte[], byte[]>> handleRecord = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .fetch();
    return buildDigitalSpecimenResponse(handleRecord);
  }

  private List<DigitalSpecimenResponse> resolveDigitalSpecimenBatch(
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
        response = setDigitalSpecimenRecordAttribute(type, data, response);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  public DigitalSpecimenBotanyResponse resolveDigitalSpecimenBotanyRecord(byte[] handle)
      throws PidCreationException {
    Result<Record3<byte[], byte[], byte[]>> handleRecord = context
        .select(HANDLES.HANDLE, HANDLES.TYPE, HANDLES.DATA)
        .from(HANDLES)
        .where(HANDLES.HANDLE.eq(handle))
        .fetch();
    return buildDigitalSpecimenBotanyResponse(handleRecord);
  }

  private List<DigitalSpecimenBotanyResponse> resolveDigitalSpecimenBotanyBatch(
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
        response = setDigitalSpecimenBotanyRecordAttribute(type, data, response);
      } catch (NoSuchFieldException e) {
        throw new PidCreationException(
            FIELD_MISMATCH_ERROR + type);
      }
    }
    return response;
  }

  // Get List of Pids
  public List<String> getAllHandles(byte[] pidStatus, int pageNum, int pageSize) {
    return context
        .selectDistinct(HANDLES.HANDLE, HANDLES.DATA)
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

  private void rollbackRecordCreation(List<byte[]> handles) {
    context.delete(HANDLES)
        .where(HANDLES.HANDLE.in(handles))
        .execute();
  }

  // Record Creation
  // Batch Record Posting  - Same for Handle, DOI, DigitalSpecimen, DigitalSpecimenBotany
  private void postAttributesToDb(Instant recordTimestamp, List<HandleAttribute> handleAttributes) {
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
  public ObjectNode createHandleRecordJson(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    ObjectNode postedRecord = createGenericRecord(handle, recordTimestamp, handleAttributes);
    checkPostedRecordFields(postedRecord, handle, "handle");
    return postedRecord;
  }

  public ObjectNode createDoiRecordJson(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    ObjectNode postedRecord = createGenericRecord(handle, recordTimestamp, handleAttributes);
    checkPostedRecordFields(postedRecord, handle, "doi");
    return postedRecord;
  }

  public ObjectNode createDigitalSpecimenJson(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    ObjectNode postedRecord = createGenericRecord(handle, recordTimestamp, handleAttributes);
    checkPostedRecordFields(postedRecord, handle, "digitalSpecimen");
    return postedRecord;
  }

  public ObjectNode createDigitalSpecimenBotanyJson(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    ObjectNode postedRecord = createGenericRecord(handle, recordTimestamp, handleAttributes);
    checkPostedRecordFields(postedRecord, handle, "digitalSpecimenBotany");
    return postedRecord;
  }
  
  private ObjectNode createGenericRecord(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    ObjectNode postedRecord;
    try {
      postedRecord = resolveSingleRecord(handle);
    } catch (PidResolutionException e) {
      rollbackRecordCreation(List.of(handle));
      throw new PidCreationException("An error has occured posting the record. Rolling back");
    }
    return postedRecord;
  }

  private void checkPostedRecordFields(ObjectNode handleRecord, byte[] handle, String recordType)
      throws PidCreationException {
    Set<String> observedFields = new HashSet<>();
    var fieldIterator = handleRecord.fieldNames();
    fieldIterator.forEachRemaining(observedFields::add);
    Set<String> expectedFields;
    
    switch(recordType){
      case "handle" -> expectedFields = HANDLE_RECORD;
      case "doi" -> expectedFields = DOI_RECORD;
      case "digitalSpecimen" -> expectedFields = DIGITAL_SPECIMEN;
      case "digitalSpecimenBotany" -> expectedFields = DIGITAL_SPECIMEN_BOTANY;
      default -> expectedFields = new HashSet<>();
    }

    Set<String> extraFields = observedFields.stream().filter(e -> !expectedFields.contains(e)).collect(
        Collectors.toSet());

    Set<String> missingFields = expectedFields.stream().filter(e -> !observedFields.contains(e)).collect(
        Collectors.toSet());

    if (!extraFields.isEmpty()) {
      rollbackRecordCreation(List.of(handle));
      log.info("Expected fields:" + expectedFields);
      log.info("Observed fields: " + observedFields);
      throw new PidCreationException(String.format(TOO_MANY_FIELDS_ERROR, recordType, extraFields));
    }
    if (!missingFields.isEmpty()) {
      rollbackRecordCreation(List.of(handle));
      log.info("Expected fields:" + expectedFields);
      log.info("Observed fields: " + observedFields);
      throw new PidCreationException(String.format(INSUFFICIENT_FIELDS_ERROR, recordType, missingFields));
    }
  }

  public List<HandleRecordResponse> createHandleRecordBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes) throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    try {
      return resolveHandleRecordBatch(handles);
    } catch (PidCreationException e) {
      rollbackRecordCreation(handles); // If an error has occured, delete any handles we've posted
      throw new PidCreationException(e.getMessage());
    }
  }

  // Given an attribute (type) and value (data), set the Response object's attribute to that value
  private <T extends HandleRecordResponse> T setHandleRecordAttribute(String type, String data,
      T response)
      throws NoSuchFieldException {
    switch (type) {
      case PID -> response.setPid(data);
      case PID_ISSUER -> response.setPidIssuer(data);
      case DIGITAL_OBJECT_TYPE -> response.setDigitalObjectType(data);
      case DIGITAL_OBJECT_SUBTYPE -> response.setDigitalObjectSubtype(data);
      case LOC -> response.setLocs(data);
      case ISSUE_DATE -> response.setIssueDate(data);
      case ISSUE_NUMBER -> response.setIssueNumber(data);
      case PID_STATUS -> response.setPidStatus(data);
      case PID_KERNEL_METADATA_LICENSE -> response.setPidKernelMetadataLicense(data);
      case HS_ADMIN -> response.setHsAdmin(data);
      default -> throw new NoSuchFieldException();
    }
    return response;
  }

  private <T extends DoiRecordResponse> T setDoiRecordAttribute(String type, String data,
      T response)
      throws NoSuchFieldException {
    switch (type) {
      case REFERENT_DOI_NAME -> response.setReferentDoiName(data);
      case REFERENT -> response.setReferent(data);
      default -> {
        return setHandleRecordAttribute(type, data, response);
      }
    }
    return response;
  }

  private <T extends DigitalSpecimenResponse> T setDigitalSpecimenRecordAttribute(String type,
      String data, T response)
      throws NoSuchFieldException {
    switch (type) {
      case DIGITAL_OR_PHYSICAL -> response.setDigitalOrPhysical(data);
      case SPECIMEN_HOST -> response.setSpecimenHost(data);
      case IN_COLLECTION_FACILITY -> response.setInCollectionFacility(data);
      default -> {
        return setDoiRecordAttribute(type, data, response);
      }
    }
    return response;
  }

  private DigitalSpecimenBotanyResponse setDigitalSpecimenBotanyRecordAttribute(String type,
      String data, DigitalSpecimenBotanyResponse response)
      throws NoSuchFieldException {
    switch (type) {
      case OBJECT_TYPE -> response.setObjectType(data);
      case PRESERVED_OR_LIVING -> response.setPreservedOrLiving(data);
      default -> {
        return setDigitalSpecimenRecordAttribute(type, data, response);
      }
    }
    return response;
  }

  // Doi Batch Creation
  public List<DoiRecordResponse> createDoiRecordBatch(List<byte[]> handles, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    return resolveDoiRecordBatch(handles);
  }


  // Batch Digital Specimen Creation
  public List<DigitalSpecimenResponse> createDigitalSpecimenBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    return resolveDigitalSpecimenBatch(handles);
  }

  // DigitalSpecimenBotany Batch Creation

  public List<DigitalSpecimenBotanyResponse> createDigitalSpecimenBotanyBatch(List<byte[]> handles,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes)
      throws PidCreationException {
    postAttributesToDb(recordTimestamp, handleAttributes);
    return resolveDigitalSpecimenBotanyBatch(handles);
  }

  // Create Individual Records
  public HandleRecordResponse createHandle(byte[] handle, Instant recordTimestamp,
      List<HandleAttribute> handleAttributes) throws PidCreationException {
    var queryList = new ArrayList<Query>();
    HandleRecordResponse response = new HandleRecordResponse();

    for (var handleAttribute : handleAttributes) {
      try {
        response = setHandleRecordAttribute(handleAttribute.type(),
            new String(handleAttribute.data()), response);
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
        response = setDoiRecordAttribute(handleAttribute.type(), new String(handleAttribute.data()),
            response);
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
        response = setDigitalSpecimenRecordAttribute(handleAttribute.type(),
            new String(handleAttribute.data()), response);
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
        response = setDigitalSpecimenBotanyRecordAttribute(handleAttribute.type(),
            new String(handleAttribute.data()), response);
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

  private void updateHandleAttributes(byte[] handle, Instant recordTimestamp,
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
    context.batch(queryList).execute();
  }

  private Query versionIncrement(byte[] handle, Instant recordTimestamp, boolean versionIncrement) {
    var currentVersion =
        Integer.parseInt(context.select(HANDLES.DATA)
            .from(HANDLES)
            .where(HANDLES.HANDLE.eq(handle))
            .and(HANDLES.TYPE.eq("issueNumber".getBytes(StandardCharsets.UTF_8)))
            .fetchOne(dbRecord -> new String(dbRecord.value1())));
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
        .and(HANDLES.TYPE.eq("issueNumber".getBytes(StandardCharsets.UTF_8)));
  }

  public TombstoneRecordResponse archiveRecord(byte[] handle,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes, boolean versionIncrement) {
    return null;

  }

  public DoiRecordResponse updateDoiRecord(byte[] handle,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes, boolean versionIncrement)
      throws PidCreationException {

    updateHandleAttributes(handle, recordTimestamp, handleAttributes, versionIncrement);
    return resolveDoiRecord(handle);
  }

  public DigitalSpecimenResponse updateDigitalSpecimen(byte[] handle,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes, boolean versionIncrement)
      throws PidCreationException {

    updateHandleAttributes(handle, recordTimestamp, handleAttributes, versionIncrement);
    return resolveDigitalSpecimenRecord(handle);
  }

  public DigitalSpecimenBotanyResponse updateDigitalSpecimenBotany(byte[] handle,
      Instant recordTimestamp, List<HandleAttribute> handleAttributes, boolean versionIncrement)
      throws PidCreationException {

    updateHandleAttributes(handle, recordTimestamp, handleAttributes, versionIncrement);
    return resolveDigitalSpecimenBotanyRecord(handle);
  }
}
