package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.configuration.AppConfig.DATE_STRING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.CATALOG_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_FORMAT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_SUBJECT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DCTERMS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DC_TERMS_CONFORMS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.IS_DERIVED_FROM_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_URL;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_ATTRIBUTE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MOTIVATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ORGANISATION_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MO_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MO_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTSHOLDER_PID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_OBJECT_ID_ABSENCE_REASON;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAPPING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MEDIA_OBJECT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ORGANISATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.SOURCE_SYSTEM;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttributeJson;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestRuntimeException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.web.PidResolver;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.bson.Document;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class FdoRecordService {

  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolver pidResolver;
  private final ObjectMapper mapper;
  private final ApplicationProperties applicationProperties;
  private final ProfileProperties profileProperties;
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String DOI_DOMAIN = "https://doi.org/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
  private static final String WIKIDATA_DOMAIN = "https://www.wikidata.org/wiki/";
  private static final String WIKIDATA_API = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/";
  private static final String PROXY_ERROR = "Invalid attribute: %s must contain proxy: %s";
  private static final String PID_KERNEL_METADATA_LICENSE = "https://creativecommons.org/publicdomain/zero/1.0/";
  private static final byte[] ADMIN_HEX = "\\\\x0FFF000000153330303A302E4E412F32302E353030302E31303235000000C8".getBytes(
      StandardCharsets.UTF_8);
  private static final String LOC_REQUEST = "locations";
  public static final Map<String, String> RESOLVABLE_KEYS;

  static {
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put(DIGITAL_OBJECT_TYPE.get(), DIGITAL_OBJECT_NAME.get());
    hashMap.put(PID_ISSUER.get(), PID_ISSUER_NAME.get());
    hashMap.put(ISSUED_FOR_AGENT.get(), ISSUED_FOR_AGENT_NAME.get());
    hashMap.put(SPECIMEN_HOST.get(), SPECIMEN_HOST_NAME.get());
    RESOLVABLE_KEYS = Collections.unmodifiableMap(hashMap);
  }

  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern(DATE_STRING)
      .withZone(ZoneId.of("UTC"));

  public HandleAttribute genHsAdmin(byte[] handle) {
    return new HandleAttribute(HS_ADMIN.index(), handle, HS_ADMIN.get(), ADMIN_HEX);
  }

  private Document toMongoDbDocument(List<JsonNode> fdoRecord, String handle)
      throws JsonProcessingException {
    var arrayNode = mapper.createArrayNode();
    for (var attribute : fdoRecord) {
      arrayNode.add(attribute);
    }
    var vals = mapper.createObjectNode()
        .set("values", arrayNode);
    return Document.parse(mapper.writeValueAsString(vals))
        .append("_id", handle);
  }

  /* Handle Record Creation */

  public Document prepareNewHandleDocument(HandleRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareHandleAttributesGenerated(handle, HANDLE, timestamp);
    prepareNewHandleJsonNodeRecord(request, handle, fdoType, timestamp);
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedHandleDocument(HandleRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedHandleJsonNodeRecord(request, handle, fdoType, timestamp,
        issueNumber);
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareNewHandleJsonNodeRecord(HandleRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var handleAttributes = prepareHandleAttributesFromRequest(request, handle, fdoType, timestamp);
    handleAttributes.addAll(prepareHandleAttributesGenerated(handle, fdoType, timestamp));
    return handleAttributes;
  }

  private List<JsonNode> prepareUpdatedHandleJsonNodeRecord(HandleRecordRequest request,
      String handle, FdoType fdoType, Instant timestamp, int issueNumber)
      throws InvalidRequestException {
    var handleAttributes = prepareHandleAttributesFromRequest(request, handle, fdoType, timestamp);
    handleAttributes.add(mapper.valueToTree(
        new HandleAttributeJson(PID_RECORD_ISSUE_NUMBER, timestamp, String.valueOf(issueNumber))));
    return handleAttributes;
  }

  // These attributes may change on an update
  private ArrayList<JsonNode> prepareHandleAttributesFromRequest(HandleRecordRequest request,
      String handle,
      FdoType fdoType, Instant timestamp)
      throws InvalidRequestException {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 101: 10320/Loc
    if (!fdoType.equals(ORGANISATION)) {
      handleAttributeList.add(new HandleAttributeJson(LOC, timestamp,
          setLocations(request.getLocations(), handle, fdoType)));
    }
    // 1: FDO Profile
    handleAttributeList.add(
        new HandleAttributeJson(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 3: Digital Object Type
    handleAttributeList.add(
        new HandleAttributeJson(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 4: Digital ObjectName
    handleAttributeList.add(
        new HandleAttributeJson(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectType()));
    // 6: PID Issuer
    handleAttributeList.add(new HandleAttributeJson(PID_ISSUER, timestamp, request.getPidIssuer()));
    // 7: PID Issuer Name
    handleAttributeList.add(new HandleAttributeJson(PID_ISSUER_NAME, timestamp,
        getObjectName(request.getPidIssuer(), null)));
    // 8: Issued For Agent
    handleAttributeList.add(
        new HandleAttributeJson(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new HandleAttributeJson(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new HandleAttributeJson(STRUCTURAL_TYPE, timestamp,
            request.getStructuralType().toString()));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  // These attributes do not change on an update (except issue number, which is dealt with separately)
  private List<JsonNode> prepareHandleAttributesGenerated(String handle, FdoType fdoType,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 2: FDO Record License
    handleAttributeList.add(
        new HandleAttributeJson(FDO_RECORD_LICENSE, timestamp, PID_KERNEL_METADATA_LICENSE));
    // 5: PID
    handleAttributeList.add(new HandleAttributeJson(PID, timestamp, fdoType.getDomain() + handle));
    // 10: PID Record Issue Date
    handleAttributeList.add(
        new HandleAttributeJson(PID_RECORD_ISSUE_DATE, timestamp, getDate(timestamp)));
    // 11: Pid Record Issue Number
    handleAttributeList.add(
        new HandleAttributeJson(PID_RECORD_ISSUE_NUMBER, timestamp,
            "1")); // This gets replaced on an update
    // 13: Pid Status
    handleAttributeList.add(new HandleAttributeJson(PID_STATUS, timestamp, "TEST"));
    // 100 HS Admin
    handleAttributeList.add(new HandleAttributeJson(timestamp, applicationProperties.getPrefix()));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* DOI Record Creation */
  public Document prepareNewDoiDocument(DoiRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewDoiJsonNodeRecord(request, handle, fdoType, timestamp);
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedDoiDocument(DoiRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp, int issueNum)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedDoiJsonNodeRecord(request, handle, fdoType, timestamp, issueNum);
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareNewDoiJsonNodeRecord(DoiRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var fdoProfile = prepareNewHandleJsonNodeRecord(request, handle, fdoType, timestamp);
    fdoProfile.addAll(prepareDoiAttributesFromRequest(request, handle, timestamp));
    return fdoProfile;
  }

  private List<JsonNode> prepareUpdatedDoiJsonNodeRecord(DoiRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp, int issueNumber) throws InvalidRequestException {
    var fdoProfile = prepareUpdatedHandleJsonNodeRecord(request, handle, fdoType, timestamp,
        issueNumber);
    fdoProfile.addAll(prepareDoiAttributesFromRequest(request, handle, timestamp));
    return fdoProfile;
  }

  private List<JsonNode> prepareDoiAttributesFromRequest(DoiRecordRequest request, String handle,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();

    // 40: Referent Type
    handleAttributeList.add(
        new HandleAttributeJson(REFERENT_TYPE, timestamp, request.getReferentType()));
    // 41: Referent DOI Name
    handleAttributeList.add(new HandleAttributeJson(REFERENT_DOI_NAME, timestamp, handle));
    // 42: Referent Name
    handleAttributeList.add(
        new HandleAttributeJson(REFERENT_NAME, timestamp, request.getReferentName()));
    // 43: Primary Referent Type
    handleAttributeList.add(new HandleAttributeJson(PRIMARY_REFERENT_TYPE, timestamp,
        request.getPrimaryReferentType()));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* Annotation Record Creation */
  public Document prepareNewAnnotationDocument(AnnotationRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewHandleJsonNodeRecord(request, handle, FdoType.ANNOTATION, timestamp);
    fdoRecord.addAll(prepareAnnotationAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedAnnotationDocument(AnnotationRequest request, String handle,
      Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedHandleJsonNodeRecord(request, handle, FdoType.ANNOTATION,
        timestamp, issueNumber);
    fdoRecord.addAll(prepareAnnotationAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareAnnotationAttributesFromRequest(AnnotationRequest request,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 500 Target PID
    handleAttributeList.add(new HandleAttributeJson(TARGET_PID, timestamp, request.getTargetPid()));
    // 501 Target Type
    handleAttributeList.add(
        new HandleAttributeJson(TARGET_TYPE, timestamp, request.getTargetType()));
    // 502 Motivation
    handleAttributeList.add(new HandleAttributeJson(MOTIVATION, timestamp,
        request.getMotivation().toString()));
    // 503 Annotation Hash
    handleAttributeList.add(new HandleAttributeJson(PRIMARY_REFERENT_TYPE, timestamp,
        request.getMotivation().toString()));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* Data Mapping Record Creation */
  public Document prepareNewDataMappingDocument(MappingRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewHandleJsonNodeRecord(request, handle, MAPPING, timestamp);
    fdoRecord.addAll(prepareDataMappingAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedDataMappingDocument(MappingRequest request, String handle,
      Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedHandleJsonNodeRecord(request, handle, MAPPING, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareDataMappingAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareDataMappingAttributesFromRequest(MappingRequest request,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    fdoProfile.add(mapper.valueToTree(
        new HandleAttributeJson(SOURCE_DATA_STANDARD, timestamp, request.getSourceDataStandard())));
    return fdoProfile;
  }

  /* Digital Specimen Record Creation */
  public Document prepareNewDigitalSpecimenRecord(DigitalSpecimenRequest request,
      String handle, Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewDoiJsonNodeRecord(request, handle, DIGITAL_SPECIMEN, timestamp);
    fdoRecord.addAll(prepareDigitalSpecimenRecordAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle)
        .append(NORMALISED_SPECIMEN_OBJECT_ID.get(),
            request.getNormalisedPrimarySpecimenObjectId());
  }

  public List<JsonNode> prepareUpdatedDigitalSpecimenRecord(DigitalSpecimenRequest request,
      String handle, Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedDoiJsonNodeRecord(request, handle, DIGITAL_SPECIMEN, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareDigitalSpecimenRecordAttributesFromRequest(request, timestamp));
    return fdoRecord;
  }

  private List<JsonNode> prepareDigitalSpecimenRecordAttributesFromRequest(
      DigitalSpecimenRequest request, Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 200 Specimen Host
    handleAttributeList.add(
        new HandleAttributeJson(SPECIMEN_HOST, timestamp, request.getSpecimenHost()));
    // 201 Specimen Host Name
    handleAttributeList.add(new HandleAttributeJson(SPECIMEN_HOST_NAME, timestamp,
        getObjectName(request.getSpecimenHost(), request.getSpecimenHostName())));
    // 202 Primary Specimen Object ID
    handleAttributeList.add(new HandleAttributeJson(PRIMARY_SPECIMEN_OBJECT_ID, timestamp,
        getObjectName(request.getSpecimenHost(), request.getPrimarySpecimenObjectId())));
    // 203 Primary Specimen Object ID Type
    handleAttributeList.add(new HandleAttributeJson(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, timestamp,
        getObjectName(request.getSpecimenHost(),
            request.getPrimarySpecimenObjectIdType().toString())));
    // 204 Primary Specimen Object ID Name
    handleAttributeList.add(new HandleAttributeJson(PRIMARY_SPECIMEN_OBJECT_ID_NAME, timestamp,
        getObjectName(request.getSpecimenHost(), request.getPrimarySpecimenObjectIdName())));
    // 205 Normalised Specimen Object Id
    handleAttributeList.add(new HandleAttributeJson(NORMALISED_SPECIMEN_OBJECT_ID, timestamp,
        request.getNormalisedPrimarySpecimenObjectId()));
    // 206 Specimen Object Id Absence Reason
    handleAttributeList.add(new HandleAttributeJson(SPECIMEN_OBJECT_ID_ABSENCE_REASON, timestamp,
        request.getSpecimenObjectIdAbsenceReason()));
    // 206 Specimen Object Id Absence Reason
    handleAttributeList.add(new HandleAttributeJson(SPECIMEN_OBJECT_ID_ABSENCE_REASON, timestamp,
        request.getSpecimenObjectIdAbsenceReason()));
    // 207 Other Specimen Ids
    handleAttributeList.add(new HandleAttributeJson(OTHER_SPECIMEN_IDS, timestamp,
        mapper.writeValueAsString(request.getOtherSpecimenIds())));
    // 208 Topic Origin
    if (request.getTopicOrigin() != null) {
      handleAttributeList.add(
          new HandleAttributeJson(TOPIC_ORIGIN, timestamp, request.getTopicOrigin().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_ORIGIN, timestamp, null));
    }
    // 209 Topic Domain
    if (request.getTopicDomain() != null) {
      handleAttributeList.add(
          new HandleAttributeJson(TOPIC_DOMAIN, timestamp, request.getTopicDomain().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_DOMAIN, timestamp, null));
    }
    // 210 Topic Discipline
    if (request.getTopicDiscipline() != null) {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_DISCIPLINE, timestamp,
          request.getTopicDiscipline().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_DISCIPLINE, timestamp, null));
    }
    // 211 Topic Category
    if (request.getTopicDiscipline() != null) {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_CATEGORY, timestamp,
          request.getTopicDiscipline().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(TOPIC_CATEGORY, timestamp, null));
    }
    // 212 Living or Preserved
    if (request.getLivingOrPreserved() != null) {
      handleAttributeList.add(new HandleAttributeJson(LIVING_OR_PRESERVED, timestamp,
          request.getLivingOrPreserved().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(LIVING_OR_PRESERVED, timestamp, null));
    }
    // 213 Base Type of Specimen
    if (request.getBaseTypeOfSpecimen() != null) {
      handleAttributeList.add(new HandleAttributeJson(BASE_TYPE_OF_SPECIMEN, timestamp,
          request.getBaseTypeOfSpecimen().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(BASE_TYPE_OF_SPECIMEN, timestamp, null));
    }
    // 214 Information Artefact Type
    if (request.getInformationArtefactType() != null) {
      handleAttributeList.add(new HandleAttributeJson(INFORMATION_ARTEFACT_TYPE, timestamp,
          request.getInformationArtefactType().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(INFORMATION_ARTEFACT_TYPE, timestamp, null));
    }
    // 215 Material Sample Type
    if (request.getMaterialSampleType() != null) {
      handleAttributeList.add(new HandleAttributeJson(MATERIAL_SAMPLE_TYPE, timestamp,
          request.getMaterialSampleType().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(MATERIAL_SAMPLE_TYPE, timestamp, null));
    }
    // 216 Material or Digital Entity
    if (request.getMaterialOrDigitalEntity() != null) {
      handleAttributeList.add(new HandleAttributeJson(MATERIAL_OR_DIGITAL_ENTITY, timestamp,
          request.getMaterialOrDigitalEntity().toString()));
    } else {
      handleAttributeList.add(new HandleAttributeJson(MATERIAL_OR_DIGITAL_ENTITY, timestamp, null));
    }
    // 217 Marked as Type
    if (request.getMarkedAsType() != null) {
      handleAttributeList.add(new HandleAttributeJson(MARKED_AS_TYPE, timestamp,
          String.valueOf(request.getMarkedAsType())));
    } else {
      handleAttributeList.add(new HandleAttributeJson(MARKED_AS_TYPE, timestamp, null));
    }
    // 218 Derived From Entity
    handleAttributeList.add(
        new HandleAttributeJson(DERIVED_FROM_ENTITY, timestamp, request.getDerivedFromEntity()));
    // 219 Catalog ID
    handleAttributeList.add(
        new HandleAttributeJson(CATALOG_IDENTIFIER, timestamp, request.getCatalogIdentifier()));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* MAS Record Creation */
  public Document prepareNewMasDocument(MasRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewHandleJsonNodeRecord(request, handle, MAS, timestamp);
    fdoRecord.addAll(prepareMasAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedMasDocument(MasRequest request, String handle,
      Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedHandleJsonNodeRecord(request, handle, MAS, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareMasAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareMasAttributesFromRequest(MasRequest request,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    fdoProfile.add(mapper.valueToTree(
        new HandleAttributeJson(MAS_NAME, timestamp, request.getMachineAnnotationServiceName())));
    return fdoProfile;
  }

  /* Media Object Record Creation */
  public Document prepareNewDigitalMediaRecord(MediaObjectRequest request,
      String handle, Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewDoiJsonNodeRecord(request, handle, MEDIA_OBJECT, timestamp);
    fdoRecord.addAll(prepareDigitalMediaAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle)
        .append(PRIMARY_MEDIA_ID.get(),
            request.getPrimaryMediaId());
  }

  public List<JsonNode> prepareUpdatedDigitalMediaRecord(MediaObjectRequest request,
      String handle, Instant timestamp, int issueNumber)
      throws InvalidRequestException {
    var fdoRecord = prepareUpdatedDoiJsonNodeRecord(request, handle, MEDIA_OBJECT, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareDigitalMediaAttributesFromRequest(request, timestamp));
    return fdoRecord;
  }

  private List<JsonNode> prepareDigitalMediaAttributesFromRequest(
      MediaObjectRequest request, Instant timestamp)
      throws InvalidRequestException {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 400 Media Host
    handleAttributeList.add(
        new HandleAttributeJson(MEDIA_HOST, timestamp, request.getMediaHost()));
    // 401 MediaHostName
    handleAttributeList.add(
        new HandleAttributeJson(MEDIA_HOST_NAME, timestamp,
            getObjectName(request.getMediaHost(), request.getMediaHostName())));
    // 403 Is Derived From Specimen
    handleAttributeList.add(
        new HandleAttributeJson(IS_DERIVED_FROM_SPECIMEN, timestamp,
            String.valueOf(request.getIsDerivedFromSpecimen())));
    // 404 Linked Digital Object PID
    handleAttributeList.add(
        new HandleAttributeJson(LINKED_DO_PID, timestamp, request.getLinkedDigitalObjectPid()));
    // 405 Linked Digital Object Type
    handleAttributeList.add(
        new HandleAttributeJson(LINKED_DO_TYPE, timestamp,
            request.getLinkedDigitalObjectType().toString()));
    // 406 Linked Attribute
    handleAttributeList.add(
        new HandleAttributeJson(LINKED_ATTRIBUTE, timestamp, request.getLinkedAttribute()));
    // 407 Primary Media ID
    handleAttributeList.add(
        new HandleAttributeJson(PRIMARY_MEDIA_ID, timestamp, request.getPrimaryMediaId()));
    // 408 Primary Media Object Id Type
    if (request.getPrimaryMediaObjectIdType() != null) {
      handleAttributeList.add(
          new HandleAttributeJson(PRIMARY_MO_ID_TYPE, timestamp,
              request.getPrimaryMediaObjectIdType().toString()));
    } else {
      handleAttributeList.add(
          new HandleAttributeJson(PRIMARY_MO_ID_TYPE, timestamp, null));
    }
    // 409 Primary Media Object Id Name
    handleAttributeList.add(
        new HandleAttributeJson(PRIMARY_MO_ID_NAME, timestamp,
            request.getPrimaryMediaObjectIdName()));
    // 410 dcterms:type
    if (request.getDcTermsType() != null) {
      handleAttributeList.add(
          new HandleAttributeJson(DCTERMS_TYPE, timestamp, request.getDcTermsType().toString()));
    } else {
      handleAttributeList.add(
          new HandleAttributeJson(DCTERMS_TYPE, timestamp, null));
    }
    // 411 dcterms:subject
    handleAttributeList.add(
        new HandleAttributeJson(DCTERMS_SUBJECT, timestamp, request.getDctermsSubject()));
    // 412 dcterms:format
    if (request.getDctermsFormat() != null) {
      handleAttributeList.add(
          new HandleAttributeJson(DCTERMS_FORMAT, timestamp,
              request.getDctermsFormat().toString()));
    } else {
      handleAttributeList.add(
          new HandleAttributeJson(DCTERMS_FORMAT, timestamp, null));
    }
    // 413 Derived from Entity
    handleAttributeList.add(
        new HandleAttributeJson(DERIVED_FROM_ENTITY, timestamp, request.getDerivedFromEntity()));
    // 414 License Name
    handleAttributeList.add(
        new HandleAttributeJson(LICENSE_NAME, timestamp, request.getLicenseName()));
    // 415 License URL
    new HandleAttributeJson(LICENSE_URL, timestamp, request.getLicenseName());
    // 416 RightsholderName
    new HandleAttributeJson(RIGHTSHOLDER_NAME, timestamp, request.getRightsholderName());
    // 417 Rightsholder PID
    new HandleAttributeJson(RIGHTSHOLDER_PID, timestamp, request.getRightsholderPid());
    // 418 RightsholderPidType
    if (request.getRightsholderPidType() != null) {
      new HandleAttributeJson(RIGHTSHOLDER_PID_TYPE, timestamp,
          request.getRightsholderPidType().toString());
    } else {
      new HandleAttributeJson(RIGHTSHOLDER_PID_TYPE, timestamp, null);
    }
    // 419 dcterms:conformsTo
    new HandleAttributeJson(DC_TERMS_CONFORMS, timestamp, request.getDctermsConformsTo());
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* Organisation Record Creation */
  public Document prepareNewOrganisationDocument(OrganisationRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewDoiJsonNodeRecord(request, handle, ORGANISATION, timestamp);
    fdoRecord.addAll(prepareOrganisationAttributesFromRequest(request, handle, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedOrganisationDocument(OrganisationRequest request, String handle,
      Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedDoiJsonNodeRecord(request, handle, ORGANISATION, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareOrganisationAttributesFromRequest(request, handle, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareOrganisationAttributesFromRequest(OrganisationRequest request,
      String handle,
      Instant timestamp) throws InvalidRequestException {
    var fdoProfile = new ArrayList<JsonNode>();
    var handleAttributeList = new ArrayList<HandleAttributeJson>();
    // 101 10320/loc -> includes organisation ROR
    var userLocations = concatLocations(request.getLocations(),
        List.of(request.getOrganisationIdentifier()));
    handleAttributeList.add(
        new HandleAttributeJson(LOC, timestamp, setLocations(userLocations, handle, ORGANISATION)));
    // 601 Organisation Identifier
    handleAttributeList.add(
        new HandleAttributeJson(ORGANISATION_ID, timestamp, request.getOrganisationIdentifier()));
    // 602 Organisation Identifier type
    handleAttributeList.add(new HandleAttributeJson(ORGANISATION_ID_TYPE, timestamp,
        request.getOrganisationIdentifierType()));
    // 603 Organisation Name
    handleAttributeList.add(new HandleAttributeJson(ORGANISATION_NAME, timestamp,
        getObjectName(request.getOrganisationIdentifier(), null)));
    for (var attribute : handleAttributeList) {
      fdoProfile.add(mapper.valueToTree(attribute));
    }
    return fdoProfile;
  }

  /* Source System Record Creation */
  public Document prepareNewSourceSystemDocument(SourceSystemRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareNewHandleJsonNodeRecord(request, handle, SOURCE_SYSTEM, timestamp);
    fdoRecord.addAll(prepareSourceSystemAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  public Document prepareUpdatedSourceSystemDocument(SourceSystemRequest request, String handle,
      Instant timestamp, int issueNumber)
      throws InvalidRequestException, JsonProcessingException {
    var fdoRecord = prepareUpdatedHandleJsonNodeRecord(request, handle, SOURCE_SYSTEM, timestamp,
        issueNumber);
    fdoRecord.addAll(prepareSourceSystemAttributesFromRequest(request, timestamp));
    return toMongoDbDocument(fdoRecord, handle);
  }

  private List<JsonNode> prepareSourceSystemAttributesFromRequest(SourceSystemRequest request,
      Instant timestamp) {
    var fdoProfile = new ArrayList<JsonNode>();
    fdoProfile.add(mapper.valueToTree(
        new HandleAttributeJson(MAS_NAME, timestamp, request.getSourceSystemName())));
    return fdoProfile;
  }

  private String getObjectName(String url, String name) throws InvalidRequestException {
    if (name != null) {
      return name;
    }
    if (url.contains(ROR_DOMAIN)) {
      return pidResolver.getObjectName(getRor(url));
    } else if (url.contains(HANDLE_DOMAIN) || url.contains(DOI_DOMAIN)) {
      return pidResolver.getObjectName(url);
    } else if (url.contains(WIKIDATA_DOMAIN)) {
      return pidResolver.resolveQid(url.replace(WIKIDATA_DOMAIN, WIKIDATA_API));
    }
    throw new InvalidRequestException(String.format(PROXY_ERROR, url,
        (ROR_DOMAIN + ", " + HANDLE_DOMAIN + ", or " + DOI_DOMAIN)));
  }

  private static String getRor(String url) throws InvalidRequestException {
    if (!url.contains(ROR_DOMAIN)) {
      throw new InvalidRequestException(String.format(PROXY_ERROR, url, ROR_DOMAIN));
    }
    return url.replace(ROR_DOMAIN, ROR_API_DOMAIN);
  }

  public List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode requestAttributes,
      FdoType type) throws InvalidRequestException {
    requestAttributes = setLocationXmlFromJson(requestAttributes,
        new String(handle, StandardCharsets.UTF_8), type);
    Map<String, BaseJsonNode> updateRequestMap = mapper.convertValue(requestAttributes,
        new TypeReference<Map<String, BaseJsonNode>>() {
        });
    try {
      var updatedAttributeList = new ArrayList<>(
          updateRequestMap.entrySet().stream()
              .filter(entry -> !entry.getValue().isNull())
              .map(entry -> new HandleAttribute(FdoProfile.retrieveIndex(entry.getKey()), handle,
                  entry.getKey(),
                  getUpdateAttributeAsByte(entry.getValue())))
              .toList());
      updatedAttributeList.addAll(addResolvedNames(updateRequestMap, handle));
      return updatedAttributeList;
    } catch (InvalidRequestRuntimeException e) {
      throw new InvalidRequestException("Unable to parse update request");
    }
  }

  private byte[] getUpdateAttributeAsByte(BaseJsonNode attribute) {
    try {
      if (attribute instanceof TextNode) {
        return attribute.asText().getBytes(StandardCharsets.UTF_8);
      } else {
        return mapper.writeValueAsString(attribute).getBytes(StandardCharsets.UTF_8);
      }
    } catch (JsonProcessingException e) {
      log.error("Unable to parse update request", e);
      throw new InvalidRequestRuntimeException();
    }
  }

  private List<HandleAttribute> addResolvedNames(Map<String, BaseJsonNode> updateRequestMap,
      byte[] handle) throws InvalidRequestException {
    var resolvableKeys = updateRequestMap.entrySet().stream().filter(
            entry -> RESOLVABLE_KEYS.containsKey(entry.getKey()) && !hasResolvedPairInRequest(
                updateRequestMap, entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    if (resolvableKeys.isEmpty()) {
      return new ArrayList<>();
    }
    ArrayList<HandleAttribute> resolvedPidNameAttributes = new ArrayList<>();
    for (var resolvableKey : resolvableKeys.entrySet()) {
      var targetAttribute = RESOLVABLE_KEYS.get(resolvableKey.getKey());
      var resolvedPid = getObjectName(resolvableKey.getValue().asText(), null);
      resolvedPidNameAttributes.add(new HandleAttribute(FdoProfile.retrieveIndex(targetAttribute),
          handle, targetAttribute, resolvedPid.getBytes(StandardCharsets.UTF_8)));
    }
    return resolvedPidNameAttributes;
  }

  private boolean hasResolvedPairInRequest(Map<String, BaseJsonNode> updateRequestMap,
      String pidToResolve) {
    var targetName = RESOLVABLE_KEYS.get(pidToResolve);
    return updateRequestMap.containsKey(targetName);
  }

  public List<HandleAttribute> prepareTombstoneAttributes(byte[] handle,
      JsonNode requestAttributes) throws InvalidRequestException {
    var tombstoneAttributes = new ArrayList<>(
        prepareUpdateAttributes(handle, requestAttributes, FdoType.TOMBSTONE));
    tombstoneAttributes.add(new HandleAttribute(PID_STATUS, handle, "ARCHIVED"));
    tombstoneAttributes.add(genLandingPage(handle));
    return tombstoneAttributes;
  }

  private HandleAttribute genLandingPage(byte[] handle) throws InvalidRequestException {
    var landingPage = new String[]{"Placeholder landing page"};
    var data = setLocationsByte(landingPage, new String(handle, StandardCharsets.UTF_8),
        FdoType.TOMBSTONE);
    return new HandleAttribute(LOC.index(), handle, LOC.get(), data);
  }

  private JsonNode setLocationXmlFromJson(JsonNode request, String handle, FdoType type)
      throws InvalidRequestException {
    // Format request so that the given locations array is formatted according to 10320/loc specifications
    if (request.findValue(LOC_REQUEST) == null) {
      return request;
    }
    JsonNode locNode = request.get(LOC_REQUEST);
    ObjectNode requestObjectNode = request.deepCopy();
    try {
      String[] locArr = mapper.treeToValue(locNode, String[].class);
      requestObjectNode.put(LOC.get(),
          new String(setLocationsByte(locArr, handle, type), StandardCharsets.UTF_8));
      requestObjectNode.remove(LOC_REQUEST);
    } catch (IOException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing \"locations\" array. " + e.getMessage());
    }
    return requestObjectNode;
  }

  private String getDate(Instant timestamp) {
    return dt.format(timestamp);
  }

  private List<String> defaultLocations(String handle, FdoType fdoType) {
    switch (fdoType) {
      case DIGITAL_SPECIMEN -> {
        String api = applicationProperties.getApiUrl() + "/specimens/" + handle;
        String ui = applicationProperties.getUiUrl() + "/ds/" + handle;
        return List.of(api, ui);
      }
      case MAPPING -> {
        return List.of(applicationProperties.getOrchestrationUrl() + "/mapping/" + handle);
      }
      case SOURCE_SYSTEM -> {
        return List.of(applicationProperties.getOrchestrationUrl() + "/source-system/" + handle);
      }
      case MEDIA_OBJECT -> {
        String api = applicationProperties.getApiUrl() + "/digitalMedia/" + handle;
        String ui = applicationProperties.getUiUrl() + "/dm/" + handle;
        return List.of(api, ui);
      }
      case ANNOTATION -> {
        return List.of(applicationProperties.getApiUrl() + "/annotations/" + handle);
      }
      case MAS -> {
        return List.of(applicationProperties.getOrchestrationUrl() + "/mas/" + handle);
      }
      default -> {
        // Handle, DOI, Organisation (Org locations are all in userLocations)
        return Collections.emptyList();
      }
    }
  }

  private String documentToString(org.w3c.dom.Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }


  private String setLocations(@Nullable String[] userLocations, String handle, FdoType type)
      throws InvalidRequestException {
    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new InvalidRequestException(e.getMessage());
    }
    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQUEST);
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, defaultLocations(handle, type));
    if (objectLocations.length == 0) {
      return "<locations></locations>";
    }
    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      String weight = i < 1 ? "1" : "0";
      locs.setAttribute("weight", weight);
      locations.appendChild(locs);
    }
    try {
      return documentToString(doc);
    } catch (TransformerException e) {
      throw new InvalidRequestException("An error has occurred parsing location data");
    }
  }

  // Todo delete
  public byte[] setLocationsByte(@Nullable String[] userLocations, String handle, FdoType type)
      throws InvalidRequestException {

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new InvalidRequestException(e.getMessage());
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQUEST);
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, defaultLocations(handle, type));

    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations[i]);
      String weight = i < 1 ? "1" : "0";
      locs.setAttribute("weight", weight);
      locations.appendChild(locs);
    }
    try {
      return documentToString(doc).getBytes(StandardCharsets.UTF_8);
    } catch (TransformerException e) {
      throw new InvalidRequestException("An error has occurred parsing location data");
    }
  }

  private String[] concatLocations(String[] userLocations, List<String> defaultLocations) {
    var objectLocations = new ArrayList<>(defaultLocations);
    if (userLocations != null) {
      objectLocations.addAll(List.of(userLocations));
    }
    return objectLocations.toArray(new String[0]);
  }

}
