package eu.dissco.core.handlemanager.service;

import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_SUBTYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OR_PHYSICAL;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IS_PID_RECORD;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.IN_COLLECTION_FACILITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_HASH;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_KERNEL_METADATA_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRESERVED_OR_LIVING;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.utils.AdminHandleGenerator.genAdminHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.ObjectType;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidCreationException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleService {

  private static final String INVALID_TYPE_ERROR = "Invalid request. Reason: unrecognized type. Check: ";
  private final HandleRepository handleRep;
  private final PidTypeService pidTypeService;
  private final HandleGeneratorService hf;
  private final DocumentBuilderFactory dbf;
  private final ObjectMapper mapper;
  private final TransformerFactory tf;

  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS",
      Locale.ENGLISH).withZone(ZoneId.of("UTC"));

  // Resolve Record
  public JsonApiWrapperRead resolveSingleRecord(byte[] handle, String path)
      throws PidResolutionException {
    var recordAttributeList = resolveAndFormatRecords(List.of(handle)).get(0);
    var dataNode = wrapData(recordAttributeList, "PID");
    JsonApiLinks links = new JsonApiLinks(path);
    return new JsonApiWrapperRead(links, List.of(dataNode));
  }

  public JsonApiWrapperRead resolveBatchRecord(List<byte[]> handles, String path)
      throws PidResolutionException {
    List<JsonApiDataLinks> dataList = new ArrayList<>();

    var recordAttributeList = resolveAndFormatRecords(handles);
    for (JsonNode recordAttributes : recordAttributeList) {
      dataList.add(wrapData(recordAttributes, "PID"));
    }
    return new JsonApiWrapperRead(new JsonApiLinks(path), dataList);
  }


  private List<JsonNode> resolveAndFormatRecords(List<byte[]> handles)
      throws PidResolutionException {
    var dbRecord = handleRep.resolveHandleAttributes(handles);
    var handleMap = mapRecords(dbRecord);
    Set<String> resolvedHandles = new HashSet<>();

    List<JsonNode> rootNodeList = new ArrayList<>();

    for (var handleRecord : handleMap.entrySet()) {
      rootNodeList.add(jsonFormatSingleRecord(handleRecord.getValue()));
      resolvedHandles.add(new String(handleRecord.getValue().get(0).handle(), StandardCharsets.UTF_8));
    }

    if (handles.size() > resolvedHandles.size()) {
      Set<String> unresolvedHandles = new HashSet<>();
      handles.stream()
          .filter(h -> !resolvedHandles.contains(new String(h, StandardCharsets.UTF_8))).toList()
              .forEach(h -> unresolvedHandles.add(new String(h, StandardCharsets.UTF_8)));

      throw new PidResolutionException(
          "Unable to resolve the following handles: " + unresolvedHandles);
    }
    return rootNodeList;
  }

  // Other Getters

  // Getters

  public List<String> getHandlesPaged(int pageNum, int pageSize, String pidStatus) {
    return handleRep.getAllHandles(pidStatus.getBytes(StandardCharsets.UTF_8), pageNum, pageSize);
  }

  public List<String> getHandlesPaged(int pageNum, int pageSize) {
    return handleRep.getAllHandles(pageNum, pageSize);
  }

  // Response Formatting

  private String getPidName(String pidLink) {
    return pidLink.substring(pidLink.length() - 24);
  }

  private HashMap<String, List<HandleAttribute>> mapRecords(List<HandleAttribute> flatList) {

    HashMap<String, List<HandleAttribute>> handleMap = new HashMap<>();

    for (HandleAttribute row : flatList) {
      String handle = new String(row.handle(), StandardCharsets.UTF_8);
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

  private JsonApiDataLinks wrapData(JsonNode recordAttributes, String recordType) {
    String pidLink = recordAttributes.get(PID).asText();
    String pidName = getPidName(pidLink);
    var handleLink = new JsonApiLinks(pidLink);
    return new JsonApiDataLinks(pidName, recordType, recordAttributes, handleLink);
  }

  private JsonNode jsonFormatSingleRecord(List<HandleAttribute> dbRecord) {
    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode subNode;
    for (HandleAttribute row : dbRecord) {
      String type = row.type();
      String data = new String(row.data(), StandardCharsets.UTF_8);
      if (FIELD_IS_PID_RECORD.contains(type)) {
        try {
          subNode = mapper.readValue(data, ObjectNode.class);
          rootNode.set(type, subNode);
        } catch (JsonProcessingException e) {
          log.warn("Type \"{}\" is noncompliant to the PID kernel model. Invalid data: {}", type,
              data);
        }
      } else {
        rootNode.put(type, data);
      }
    }
    return rootNode;
  }

  // Search by Physical Specimen Identifier
  public JsonApiWrapperWrite searchByPhysicalSpecimenId(String physicalId, PhysicalIdType physicalIdType, String specimenHostPid)
      throws PidResolutionException, InvalidRequestException {

    var physicalIdentifier = setPhysicalId(physicalId, physicalIdType, specimenHostPid);
    var returnedRows = handleRep.searchByPhysicalIdentifier(List.of(physicalIdentifier));
    var handleNames = listHandleNamesReturnedFromQuery(returnedRows);
    if (handleNames.size() > 1) {
      throw new PidResolutionException(
          "More than one handle record corresponds to the provided collection facility and physical identifier.");
    }
    List<JsonApiDataLinks> dataNode = new ArrayList<>();

    var jsonFormattedRecord = jsonFormatSingleRecord(returnedRows);
    dataNode.add(wrapData(jsonFormattedRecord, "PID"));
    return new JsonApiWrapperWrite(dataNode);
  }

  private byte[] setPhysicalId(String physicalIdentifer, PhysicalIdType physicalIdType, String specimenHostPid)
      throws InvalidRequestException {
    if (physicalIdType.equals(PhysicalIdType.COMBINED)){
      if (specimenHostPid==null){
        throw new InvalidRequestException("Missing specimen host ID.");
      }
      var hostIdArr = specimenHostPid.split("/");
      return (physicalIdentifer + ":" + hostIdArr[hostIdArr.length - 1]).getBytes(StandardCharsets.UTF_8);
    }
    return physicalIdentifer.getBytes(StandardCharsets.UTF_8);
  }

  private <T extends DigitalSpecimenRequest> void verifyNoRegisteredSpecimens(List<T> requests)
      throws PidCreationException {
    List<byte[]> physicalIds = new ArrayList<>();
    for (var request : requests) {
      physicalIds.add(setUniquePhysicalIdentifierId(request));
    }
    var registeredSpecimens = handleRep.searchByPhysicalIdentifier(physicalIds);
    if (!registeredSpecimens.isEmpty()) {
      var registeredHandles = listHandleNamesReturnedFromQuery(registeredSpecimens);
      throw new PidCreationException(
          "Unable to create PID records. Some requested records are already registered. Verify the following digital specimens:"
              + registeredHandles);
    }
  }

  private Set<String> listHandleNamesReturnedFromQuery(List<HandleAttribute> rows) {
    Set<String> handles = new HashSet<>();
    rows.forEach(row -> handles.add((new String(row.handle(), StandardCharsets.UTF_8))));
    return handles;
  }

  // Pid Record Creation
  public <T extends DigitalSpecimenRequest> JsonApiWrapperWrite createRecords(
      List<JsonNode> requests)
      throws PidResolutionException, PidServiceInternalError, InvalidRequestException, PidCreationException {

    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = hf.genHandleList(requests.size());
    List<byte[]> handlesPost = new ArrayList<>(handles);
    List<T> digitalSpecimenList = new ArrayList<>();

    List<HandleAttribute> handleAttributes = new ArrayList<>();
    Map<String, String> recordTypes = new HashMap<>();

    for (var request : requests) {
      ObjectNode dataNode = (ObjectNode) request.get(NODE_DATA);
      ObjectType type = ObjectType.fromString(dataNode.get(NODE_TYPE).asText());
      recordTypes.put(new String(handles.get(0), StandardCharsets.UTF_8), type.getType());
      try {
        switch (type) {
          case HANDLE -> {
            HandleRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                HandleRecordRequest.class);
            handleAttributes.addAll(
                prepareHandleRecordAttributes(requestObject, handles.remove(0)));
          }
          case DOI -> {
            DoiRecordRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DoiRecordRequest.class);
            handleAttributes.addAll(prepareDoiRecordAttributes(requestObject, handles.remove(0)));
          }
          case DIGITAL_SPECIMEN -> {
            DigitalSpecimenRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                DigitalSpecimenRequest.class);
            handleAttributes.addAll(
                prepareDigitalSpecimenRecordAttributes(requestObject, handles.remove(0)));
            digitalSpecimenList.add((T) requestObject);
          }
          case DIGITAL_SPECIMEN_BOTANY -> {
            DigitalSpecimenBotanyRequest requestObject = mapper.treeToValue(
                dataNode.get(NODE_ATTRIBUTES), DigitalSpecimenBotanyRequest.class);
            handleAttributes.addAll(
                prepareDigitalSpecimenBotanyRecordAttributes(requestObject, handles.remove(0)));
            digitalSpecimenList.add((T) requestObject);
          }
          case MEDIA_OBJECT -> {
            MediaObjectRequest requestObject = mapper.treeToValue(dataNode.get(NODE_ATTRIBUTES),
                MediaObjectRequest.class);
            handleAttributes.addAll(prepareMediaObjectAttributes(requestObject, handles.remove(0)));
          }
          default -> throw new InvalidRequestException(INVALID_TYPE_ERROR + type);
        }
      } catch (JsonProcessingException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing a record in request. More information: "
                + e.getMessage());
      }
    }
    verifyNoRegisteredSpecimens(digitalSpecimenList);
    handleRep.postAttributesToDb(recordTimestamp, handleAttributes);

    var postedRecordAttributes = resolveAndFormatRecords(handlesPost);
    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (JsonNode recordAttributes : postedRecordAttributes) {
      dataList.add(
          wrapData(recordAttributes, getRecordTypeFromTypeList(recordAttributes, recordTypes)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  // Update Records
  public JsonApiWrapperWrite updateRecords(List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException, PidServiceInternalError {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<List<HandleAttribute>> attributesToUpdate = new ArrayList<>();
    Map<String, String> recordTypes = new HashMap<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      String recordType = data.get(NODE_TYPE).asText();
      recordTypes.put(new String(handle, StandardCharsets.UTF_8), recordType);

      JsonNode validatedAttributes = setLocationFromJson(requestAttributes);
      var attributes = prepareUpdateAttributes(handle, validatedAttributes);
      attributesToUpdate.add(attributes);
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.updateRecordBatch(recordTimestamp, attributesToUpdate);
    var updatedRecords = resolveAndFormatRecords(handles);

    List<JsonApiDataLinks> dataList = new ArrayList<>();
    for (JsonNode updatedRecord : updatedRecords) {
      dataList.add(wrapData(updatedRecord, getRecordTypeFromTypeList(updatedRecord, recordTypes)));
    }
    return new JsonApiWrapperWrite(dataList);
  }

  private String getRecordTypeFromTypeList(JsonNode recordAttributes,
      Map<String, String> recordTypes) {
    String pid = getPidName(recordAttributes.get(PID).asText());
    return recordTypes.get(pid);
  }

  private JsonNode setLocationFromJson(JsonNode request)
      throws InvalidRequestException, PidServiceInternalError {
    var keys = getKeys(request);
    if (!keys.contains(LOC_REQ)) {
      return request;
    }
    ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
    });
    JsonNode locNode = request.get(LOC_REQ);
    ObjectNode requestObjectNode = request.deepCopy();
    if (locNode.isArray()) {
      try {
        List<String> locList = reader.readValue(locNode);
        String[] locArr = locList.toArray(new String[0]);
        requestObjectNode.put(LOC, new String(setLocations(locArr), StandardCharsets.UTF_8));
        requestObjectNode.remove(LOC_REQ);
      } catch (IOException e) {
        throw new InvalidRequestException(
            "An error has occurred parsing \"locations\" array. " + e.getMessage());
      } catch (PidServiceInternalError e) {
        throw e;
      }
    }
    return requestObjectNode;
  }

  private List<String> getKeys(JsonNode request) {
    List<String> keys = new ArrayList<>();
    Iterator<String> fieldItr = request.fieldNames();
    fieldItr.forEachRemaining(keys::add);
    return keys;
  }

  private void checkHandlesWritable(List<byte[]> handles) throws PidResolutionException {
    Set<byte[]> handlesToUpdate = new HashSet<>(handles);

    Set<byte[]> handlesExist = new HashSet<>(handleRep.checkHandlesWritable(handles));
    if (handlesExist.size() < handles.size()) {
      handlesToUpdate.removeAll(handlesExist);
      Set<String> handlesDontExist = new HashSet<>();
      for (byte[] handle : handlesToUpdate) {
        handlesDontExist.add(new String(handle, StandardCharsets.UTF_8));
      }
      throw new PidResolutionException(
          "INVALID INPUT. One or more handles in request do not exist or are archived. Verify the following handle(s): "
              + handlesDontExist);
    }
  }

  private void checkInternalDuplicates(List<byte[]> handles) throws InvalidRequestException {
    Set<String> handlesToUpdateStr = new HashSet<>();
    for (byte[] handle : handles) {
      handlesToUpdateStr.add(new String(handle, StandardCharsets.UTF_8));
    }

    if (handlesToUpdateStr.size() < handles.size()) {
      Set<String> duplicateHandles = findDuplicates(handles, handlesToUpdateStr);
      throw new InvalidRequestException(
          "INVALID INPUT. Attempting to update the same record multiple times in one request. "
              + "The following handles are duplicated in the request: " + duplicateHandles);
    }
  }

  private Set<String> findDuplicates(List<byte[]> handles, Set<String> handlesToUpdate) {
    Set<String> duplicateHandles = new HashSet<>();
    for (byte[] handle : handles) {
      if (!handlesToUpdate.add(new String(handle, StandardCharsets.UTF_8))) {
        duplicateHandles.add(new String(handle, StandardCharsets.UTF_8));
      }
    }
    return duplicateHandles;
  }

  private List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode request)
      throws PidResolutionException {
    Map<String, String> updateRecord = mapper.convertValue(request,
        new TypeReference<Map<String, String>>() {
        });
    List<HandleAttribute> attributesToUpdate = new ArrayList<>();

    for (var requestField : updateRecord.entrySet()) {
      String type = requestField.getKey().replace("Pid", "");
      byte[] data = requestField.getValue().getBytes(StandardCharsets.UTF_8);
      byte[] pidData;

      // Resolve data if it's a pid
      if (FIELD_IS_PID_RECORD.contains(type)) {
        pidData = (pidTypeService.resolveTypePid(
            new String(data, StandardCharsets.UTF_8))).getBytes(StandardCharsets.UTF_8);
        data = pidData;
      }
      attributesToUpdate.add(new HandleAttribute(FIELD_IDX.get(type), handle, type, data));
    }
    return attributesToUpdate;
  }

  // Archive
  public JsonApiWrapperWrite archiveRecordBatch(List<JsonNode> requests)
      throws InvalidRequestException, PidResolutionException {
    var recordTimestamp = Instant.now().getEpochSecond();
    List<byte[]> handles = new ArrayList<>();
    List<HandleAttribute> archiveAttributes = new ArrayList<>();

    for (JsonNode root : requests) {
      JsonNode data = root.get(NODE_DATA);
      JsonNode requestAttributes = data.get(NODE_ATTRIBUTES);
      byte[] handle = data.get(NODE_ID).asText().getBytes(StandardCharsets.UTF_8);
      handles.add(handle);
      archiveAttributes.addAll(prepareUpdateAttributes(handle, requestAttributes));
      archiveAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
          "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    }
    checkInternalDuplicates(handles);
    checkHandlesWritable(handles);

    handleRep.archiveRecords(recordTimestamp, archiveAttributes, handles);
    var archivedRecords = resolveAndFormatRecords(handles);

    List<JsonApiDataLinks> dataList = new ArrayList<>();

    for (JsonNode updatedRecord : archivedRecords) {
      String pidLink = updatedRecord.get(PID).asText();
      String pidName = getPidName(pidLink);
      dataList.add(new JsonApiDataLinks(pidName, ObjectType.TOMBSTONE.getType(), updatedRecord,
          new JsonApiLinks(pidLink)));
    }
    return new JsonApiWrapperWrite(dataList);

  }

  // Prepare Attribute lists

  private List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle) throws PidResolutionException, PidServiceInternalError {
    List<HandleAttribute> handleRecord = new ArrayList<>();

    // 100: Admin Handle
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

    // 1: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 2: PidIssuer
    String pidIssuer = pidTypeService.resolveTypePid(request.getPidIssuerPid());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER,
        pidIssuer.getBytes(StandardCharsets.UTF_8)));

    // 3: Digital Object Type
    String digitalObjectType = pidTypeService.resolveTypePid(request.getDigitalObjectTypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            digitalObjectType.getBytes(StandardCharsets.UTF_8)));

    // 4: Digital Object Subtype
    String digitalObjectSubtype = pidTypeService.resolveTypePid(
        request.getDigitalObjectSubtypePid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_SUBTYPE), handle, DIGITAL_OBJECT_SUBTYPE,
            digitalObjectSubtype.getBytes(StandardCharsets.UTF_8)));

    // 5: 10320/loc
    byte[] loc = setLocations(request.getLocations());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 6: Issue Date
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUE_DATE), handle, ISSUE_DATE,
        getDate().getBytes(StandardCharsets.UTF_8)));

    // 7: Issue number
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUE_NUMBER), handle, ISSUE_NUMBER,
        "1".getBytes(StandardCharsets.UTF_8)));

    // 8: PidStatus
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "TEST".getBytes(StandardCharsets.UTF_8)));

    // 9, 10: tombstone text, tombstone pids -> Skip

    // 11: PidKernelMetadataLicense:
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(PID_KERNEL_METADATA_LICENSE), handle,
        PID_KERNEL_METADATA_LICENSE, pidKernelMetadataLicense));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidResolutionException, PidServiceInternalError {
    var handleRecord = prepareHandleRecordAttributes(request, handle);

    // 12: Referent DOI Name
    String referentDoiName = pidTypeService.resolveTypePid(request.getReferentDoiNamePid());

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME,
            referentDoiName.getBytes(StandardCharsets.UTF_8)));

    // 13: Referent -> NOTE: Referent is blank currently until we have a model
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        request.getReferent().getBytes(StandardCharsets.UTF_8)));
    return handleRecord;
  }

  private List<HandleAttribute> prepareMediaObjectAttributes(MediaObjectRequest request,
      byte[] handle)
      throws PidResolutionException, PidServiceInternalError {
    var handleRecord = prepareDoiRecordAttributes(request, handle);

    // 14 Media Hash
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_HASH), handle, MEDIA_HASH,
        request.getMediaHash().getBytes(StandardCharsets.UTF_8)));

    // 15 Subject Specimen Host
    String specimenHost = pidTypeService.resolveTypePid(request.getSubjectSpecimenHostPid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(SUBJECT_SPECIMEN_HOST), handle, SUBJECT_SPECIMEN_HOST,
            specimenHost.getBytes(StandardCharsets.UTF_8)));

    // 16 Media Url
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_URL), handle, MEDIA_URL,
        request.getMediaUrl().getBytes(StandardCharsets.UTF_8)));

    // 17 : Subject Physical Identifier
    // Encoding here is UTF-8
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(SUBJECT_PHYSICAL_IDENTIFIER), handle,
        SUBJECT_PHYSICAL_IDENTIFIER, setUniquePhysicalIdentifierId(request)));
    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidResolutionException, PidServiceInternalError {
    var handleRecord = prepareDoiRecordAttributes(request, handle);

    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OR_PHYSICAL), handle, DIGITAL_OR_PHYSICAL,
            request.getDigitalOrPhysical().getBytes()));

    // 15: specimenHost
    String specimenHost = pidTypeService.resolveTypePid(request.getSpecimenHostPid());
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST,
        specimenHost.getBytes(StandardCharsets.UTF_8)));

    // 16: In collectionFacility
    String inCollectionFacility = pidTypeService.resolveTypePid(
        request.getInCollectionFacilityPid());
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(IN_COLLECTION_FACILITY), handle, IN_COLLECTION_FACILITY,
            inCollectionFacility.getBytes(StandardCharsets.UTF_8)));

    // 17 : Institutional Identifier
    // Encoding here is UTF-8
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PHYSICAL_IDENTIFIER), handle, PHYSICAL_IDENTIFIER,
            setUniquePhysicalIdentifierId(request)));

    return handleRecord;
  }

  private List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request, byte[] handle)
      throws PidResolutionException, PidServiceInternalError {
    List<HandleAttribute> handleRecord = prepareDigitalSpecimenRecordAttributes(request, handle);

    // 17: ObjectType
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle, OBJECT_TYPE,
        request.getObjectType().getBytes(StandardCharsets.UTF_8)));

    // 18: preservedOrLiving
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRESERVED_OR_LIVING), handle, PRESERVED_OR_LIVING,
            setUniquePhysicalIdentifierId(request)));

    return handleRecord;
  }

  private <T extends DigitalSpecimenRequest> byte[] setUniquePhysicalIdentifierId(T request) {
    var physicalIdentifier = request.getPhysicalIdentifier();
    if (physicalIdentifier.physicalIdType() == PhysicalIdType.CETAF) {
      return physicalIdentifier.physicalId().getBytes(StandardCharsets.UTF_8);
    }
    return concatIds(physicalIdentifier, request.getSpecimenHostPid());
  }

  private byte[] setUniquePhysicalIdentifierId(MediaObjectRequest request) {
    var physicalIdentifier = request.getSubjectPhysicalIdentifier();
    if (physicalIdentifier.physicalIdType() == PhysicalIdType.CETAF) {
      return physicalIdentifier.physicalId().getBytes(StandardCharsets.UTF_8);
    }
    return concatIds(physicalIdentifier, request.getSubjectSpecimenHostPid());
  }

  private byte[] concatIds(PhysicalIdentifier physicalIdentifier, String specimenHostPid) {
    var hostIdArr = specimenHostPid.split("/");
    var hostId = hostIdArr[hostIdArr.length - 1];
    return (physicalIdentifier.physicalId() + ":" + hostId).getBytes(StandardCharsets.UTF_8);
  }

  private String getDate() {
    return dt.format(Instant.now());
  }

  public byte[] setLocations(String[] objectLocations) throws PidServiceInternalError {

    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new PidServiceInternalError(e.getMessage(), e);
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQ);
    doc.appendChild(locations);
    for (int i = 0; i < objectLocations.length; i++) {

      var locs = doc.createElement("location");
      locs.setAttribute(NODE_ID, String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      locs.setAttribute("weight", "0");
      locations.appendChild(locs);
    }
    try {
      return documentToString(doc).getBytes(StandardCharsets.UTF_8);
    } catch (TransformerException e) {
      throw new PidServiceInternalError("An internal error has occurred parsing location data", e);
    }
  }

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

}

