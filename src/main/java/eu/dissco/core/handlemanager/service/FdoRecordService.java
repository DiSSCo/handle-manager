package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.configuration.AppConfig.DATE_STRING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.CATALOG_NUMBER;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE_ID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.FDO_RECORD_LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HAS_RELATED_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LICENSE_URL;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LINKED_DO_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MEDIA_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.MIME_TYPE;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.PRIMARY_MEDIA_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTS_HOLDER_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.RIGHTS_HOLDER_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_TEXT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DATA_MAPPING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DOI;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ORGANISATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.service.ServiceUtils.normalizeMediaId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.component.PidResolver;
import eu.dissco.core.handlemanager.domain.fdo.FdoProfile;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.PidStatus;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.properties.ProfileProperties;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.MachineAnnotationServiceRequestAttributes;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import eu.dissco.core.handlemanager.schema.OtherspecimenIds;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import eu.dissco.core.handlemanager.schema.TombstoneRequestAttributes;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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


@RequiredArgsConstructor
@Service
@Slf4j
public class FdoRecordService {

  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolver pidResolver;
  private final ObjectMapper mapper;
  private final ApplicationProperties applicationProperties;
  private final Pattern xmlLocPattern = Pattern.compile("href=\"[^\"]+\"");
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String DOI_DOMAIN = "https://doi.org/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/v2/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
  private static final String WIKIDATA_DOMAIN = "https://www.wikidata.org/wiki/";
  private static final String WIKIDATA_API = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/";
  private static final String PROXY_ERROR = "Invalid attribute: %s must contain proxy: %s";
  private static final String JSON_ERROR_MSG = "Unable to parse json request";
  private static final String FDO_LICENSE_VALUE = "https://spdx.org/licenses/CC0-1.0.json";
  private static final String FDO_LICENSE_NAME_VALUE = "CC0 1.0 Universal";
  public static final Set<FdoProfile> GENERATED_KEYS;

  static {
    GENERATED_KEYS = Set.of(FDO_PROFILE, FDO_RECORD_LICENSE_ID,
        FDO_RECORD_LICENSE_NAME, PID_ISSUER, PID_ISSUER_NAME,
        ISSUED_FOR_AGENT, ISSUED_FOR_AGENT_NAME, DIGITAL_OBJECT_TYPE,
        DIGITAL_OBJECT_NAME, PID, PID_RECORD_ISSUE_DATE, PID_STATUS,
        HS_ADMIN);
  }

  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern(DATE_STRING)
      .withZone(ZoneId.of("UTC"));
  private final ProfileProperties profileProperties;


  /* Handle Record Creation */
  public FdoRecord prepareNewHandleRecord(HandleRequestAttributes request, String handle,
      Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareHandleAttributes(request, handle, timestamp);
    fdoAttributes.putAll(prepareGeneratedAttributes(handle, HANDLE, timestamp, isDraft));
    return new FdoRecord(handle, HANDLE, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedHandleRecord(HandleRequestAttributes request, FdoType fdoType,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, previousVersion.handle(),
        timestamp,
        previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), fdoType, fdoAttributes, null,
        fdoAttributes.values());
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedHandleAttributes(
      HandleRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(prepareHandleAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  public Map<FdoProfile, FdoAttribute> prepareHandleAttributes(HandleRequestAttributes request,
      String handle,
      Instant timestamp) throws InvalidRequestException {
    var handleAttributes = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributes.put(LOC, new FdoAttribute(LOC, timestamp,
        setLocations(handle, HANDLE, null, request.getLocations(), false)));
    return handleAttributes;
  }

  /* DOI Record Creation */
  public FdoRecord prepareNewDoiRecord(DoiKernelRequestAttributes request, String handle,
      Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareDoiAttributes(request, handle, timestamp, isDraft);
    fdoAttributes.putAll(prepareGeneratedAttributes(handle, FdoType.DOI, timestamp, isDraft));
    fdoAttributes.putAll(prepareGeneratedAttributesDoi(timestamp));
    return new FdoRecord(handle, FdoType.DOI, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedDoiRecord(DoiKernelRequestAttributes request, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request, previousVersion.handle(), timestamp,
        previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DOI, fdoAttributes, null,
        fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareDoiAttributes(DoiKernelRequestAttributes request,
      String handle,
      Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC, new FdoAttribute(LOC, timestamp,
        setLocations(handle, DOI, null, request.getLocations(), isDraft)));
    // 42: Referent Name
    handleAttributeList.put(REFERENT_NAME,
        new FdoAttribute(REFERENT_NAME, timestamp, request.getReferentName()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedDoiAttributes(
      DoiKernelRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(prepareDoiAttributes(request, handle, timestamp, false));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Annotation Record Creation */
  public FdoRecord prepareNewAnnotationRecord(AnnotationRequestAttributes request, String handle,
      Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareAnnotationAttributes(request, handle, timestamp);
    fdoAttributes.putAll(
        prepareGeneratedAttributes(handle, FdoType.ANNOTATION, timestamp, isDraft));
    var hash = request.getAnnotationHash() == null ? null : request.getAnnotationHash().toString();
    return new FdoRecord(handle, FdoType.ANNOTATION, fdoAttributes, hash, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedAnnotationRecord(AnnotationRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedAnnotationAttributes(request, previousVersion.handle(),
        timestamp, previousVersion, incrementVersion);
    var hash = request.getAnnotationHash() == null ? null : request.getAnnotationHash().toString();
    return new FdoRecord(previousVersion.handle(), FdoType.ANNOTATION, fdoAttributes,
        hash, fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareAnnotationAttributes(
      AnnotationRequestAttributes request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC, new FdoAttribute(LOC, timestamp,
        setLocations(handle, ANNOTATION, null, request.getLocations(), false)));
    handleAttributeList.put(TARGET_PID,
        new FdoAttribute(TARGET_PID, timestamp, request.getTargetPid()));
    // 501 Target Type
    handleAttributeList.put(TARGET_TYPE,
        new FdoAttribute(TARGET_TYPE, timestamp, request.getTargetType()));
    // 502 Target Type Name
    String targetTypeName = null;
    try {
      targetTypeName = FdoType.fromString(request.getTargetType()).getDigitalObjectName();
    } catch (IllegalArgumentException | NullPointerException ignored) {
      log.warn("Target type {} for pid {} is not a valid target type", request.getTargetType(),
          handle);
    }
    handleAttributeList.put(TARGET_TYPE_NAME,
        new FdoAttribute(TARGET_TYPE_NAME, timestamp, targetTypeName));
    // 503 Annotation Hash
    handleAttributeList.put(ANNOTATION_HASH,
        new FdoAttribute(ANNOTATION_HASH, timestamp, request.getAnnotationHash()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedAnnotationAttributes(
      AnnotationRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(
        prepareAnnotationAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Data Mapping Record Creation */
  public FdoRecord prepareNewDataMappingRecord(DataMappingRequestAttributes request, String handle,
      Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareDataMappingAttributes(request, handle, timestamp);
    fdoAttributes.putAll(
        prepareGeneratedAttributes(handle, FdoType.DATA_MAPPING, timestamp, isDraft));
    return new FdoRecord(handle, FdoType.DATA_MAPPING, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedDataMappingRecord(DataMappingRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDataMappingAttributes(request, previousVersion.handle(),
        timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DATA_MAPPING, fdoAttributes, null,
        fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareDataMappingAttributes(
      DataMappingRequestAttributes request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC, new FdoAttribute(LOC, timestamp,
        setLocations(handle, DATA_MAPPING, null, request.getLocations(), false)));
    // 700 Source Data Standard
    handleAttributeList.put(SOURCE_DATA_STANDARD,
        new FdoAttribute(SOURCE_DATA_STANDARD, timestamp, request.getSourceDataStandard()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedDataMappingAttributes(
      DataMappingRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(
        prepareDataMappingAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Digital Specimen Record Creation */
  public FdoRecord prepareNewDigitalSpecimenRecord(DigitalSpecimenRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    Map<FdoProfile, FdoAttribute> fdoAttributes;
    try {
      fdoAttributes = prepareDigitalSpecimenAttributes(request, handle, timestamp, isDraft);
    } catch (JsonProcessingException e) {
      log.error(JSON_ERROR_MSG, e);
      throw new InvalidRequestException(JSON_ERROR_MSG);
    }
    fdoAttributes.putAll(
        prepareGeneratedAttributes(handle, FdoType.DIGITAL_SPECIMEN, timestamp, isDraft));
    fdoAttributes.putAll(prepareGeneratedAttributesDoi(timestamp));
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId(), fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedDigitalSpecimenRecord(DigitalSpecimenRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    Map<FdoProfile, FdoAttribute> fdoAttributes;
    try {
      fdoAttributes = prepareUpdatedDigitalSpecimenAttributes(request, previousVersion.handle(),
          timestamp, previousVersion, incrementVersion);
    } catch (JsonProcessingException e) {
      log.error(JSON_ERROR_MSG, e);
      throw new InvalidRequestException(JSON_ERROR_MSG);
    }
    return new FdoRecord(previousVersion.handle(), FdoType.DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId(), fdoAttributes.values());
  }

  private Map<FdoProfile, FdoAttribute> prepareDigitalSpecimenAttributes(
      DigitalSpecimenRequestAttributes request, String handle, Instant timestamp, boolean isDraft)
      throws InvalidRequestException, JsonProcessingException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    var keyAttribute = getSpecimenResolvableId(request.getCatalogNumber(),
        request.getOtherSpecimenIds());
    handleAttributeList.put(LOC,
        new FdoAttribute(LOC, timestamp,
            setLocations(handle, DIGITAL_SPECIMEN, keyAttribute, request.getLocations(), isDraft)));
    // 42: Referent Name
    handleAttributeList.put(REFERENT_NAME,
        new FdoAttribute(REFERENT_NAME, timestamp, request.getReferentName()));
    // 200 Specimen Host
    handleAttributeList.put(SPECIMEN_HOST,
        new FdoAttribute(SPECIMEN_HOST, timestamp, request.getSpecimenHost()));
    // 201 Specimen Host Name
    handleAttributeList.put(SPECIMEN_HOST_NAME,
        new FdoAttribute(SPECIMEN_HOST_NAME, timestamp,
            getObjectName(request.getSpecimenHost(), request.getSpecimenHostName())));
    // 202 Normalised Specimen Object Id
    handleAttributeList.put(NORMALISED_SPECIMEN_OBJECT_ID,
        new FdoAttribute(NORMALISED_SPECIMEN_OBJECT_ID, timestamp,
            request.getNormalisedPrimarySpecimenObjectId()));
    // 203 Other Specimen Ids
    if (!request.getOtherSpecimenIds().isEmpty()) {
      handleAttributeList.put(OTHER_SPECIMEN_IDS,
          new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp,
              mapper.writeValueAsString(request.getOtherSpecimenIds())));
    } else {
      handleAttributeList.put(OTHER_SPECIMEN_IDS,
          new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp, null));
    }
    // 204 Topic Origin
    handleAttributeList.put(TOPIC_ORIGIN,
        new FdoAttribute(TOPIC_ORIGIN, timestamp, request.getTopicOrigin()));
    // 205 Topic Domain
    handleAttributeList.put(TOPIC_DOMAIN,
        new FdoAttribute(TOPIC_DOMAIN, timestamp, request.getTopicDomain()));
    // 206 Topic Discipline
    handleAttributeList.put(TOPIC_DISCIPLINE,
        new FdoAttribute(TOPIC_DISCIPLINE, timestamp, request.getTopicDiscipline()));
    // 207 Topic Category
    handleAttributeList.put(TOPIC_CATEGORY,
        new FdoAttribute(TOPIC_CATEGORY, timestamp, request.getTopicCategory()));
    // 208 Living or Preserved
    handleAttributeList.put(LIVING_OR_PRESERVED,
        new FdoAttribute(LIVING_OR_PRESERVED, timestamp, request.getLivingOrPreserved()));
    // 209 Material Sample Type
    handleAttributeList.put(MATERIAL_SAMPLE_TYPE,
        new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp, request.getMaterialSampleType()));
    // 210 Marked as Type
    handleAttributeList.put(MARKED_AS_TYPE,
        new FdoAttribute(MARKED_AS_TYPE, timestamp, request.getMarkedAsType()));
    // 211 Catalog Number
    handleAttributeList.put(CATALOG_NUMBER,
        new FdoAttribute(CATALOG_NUMBER, timestamp, request.getCatalogNumber()));
    return handleAttributeList;
  }

  private static String getSpecimenResolvableId(String catalogId,
      List<OtherspecimenIds> otherSpecimenIds) {
    if (catalogId != null && catalogId.matches("http(s)://.+")) {
      return catalogId;
    }
    for (var otherId : otherSpecimenIds) {
      if (Boolean.TRUE.equals(otherId.getResolvable())) {
        return otherId.getIdentifierValue();
      }
    }
    return null;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedDigitalSpecimenAttributes(
      DigitalSpecimenRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException, JsonProcessingException {
    var updatedAttributes = new EnumMap<>(
        prepareDigitalSpecimenAttributes(request, handle, timestamp, false));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* MAS Record Creation */
  public FdoRecord prepareNewMasRecord(MachineAnnotationServiceRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = new EnumMap<>(prepareMasAttributes(request, handle, timestamp));
    fdoAttributes.putAll(prepareGeneratedAttributes(handle, MAS, timestamp, isDraft));
    return new FdoRecord(handle, MAS, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedMasRecord(MachineAnnotationServiceRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedMasAttributes(request, previousVersion.handle(), timestamp,
        previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), MAS, fdoAttributes, null,
        fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareMasAttributes(
      MachineAnnotationServiceRequestAttributes request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC,
        new FdoAttribute(LOC, timestamp,
            setLocations(handle, MAS, null, request.getLocations(), false)));
    // 604: MAS
    handleAttributeList.put(MAS_NAME,
        new FdoAttribute(MAS_NAME, timestamp, request.getMachineAnnotationServiceName()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedMasAttributes(
      MachineAnnotationServiceRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(prepareMasAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Media Object Record Creation */
  public FdoRecord prepareNewDigitalMediaRecord(DigitalMediaRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareDigitalMediaAttributes(request, handle, timestamp, isDraft);
    fdoAttributes.putAll(
        prepareGeneratedAttributes(handle, FdoType.DIGITAL_MEDIA, timestamp, isDraft));
    fdoAttributes.putAll(prepareGeneratedAttributesDoi(timestamp));
    return new FdoRecord(handle, FdoType.DIGITAL_MEDIA, fdoAttributes, normalizeMediaId(request),
        fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedDigitalMediaRecord(DigitalMediaRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDigitalMediaAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DIGITAL_MEDIA, fdoAttributes,
        normalizeMediaId(request), fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareDigitalMediaAttributes(
      DigitalMediaRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC,
        new FdoAttribute(LOC, timestamp,
            setLocations(handle, DIGITAL_MEDIA, request.getPrimaryMediaId(), request.getLocations(),
                isDraft)));
    // Referent Name
    var referentName = request.getReferentName() == null ?
        request.getPrimaryMediaId().replaceAll("http(s)://", "") :
        request.getReferentName();
    handleAttributeList.put(REFERENT_NAME,
        new FdoAttribute(REFERENT_NAME, timestamp, referentName));
    // 400 Media Host
    handleAttributeList.put(MEDIA_HOST,
        new FdoAttribute(MEDIA_HOST, timestamp, request.getMediaHost()));
    // 401 MediaHostName
    var mediaHostName = getObjectName(request.getMediaHost(), request.getMediaHostName());
    handleAttributeList.put(MEDIA_HOST_NAME, new FdoAttribute(MEDIA_HOST_NAME, timestamp,
        getObjectName(request.getMediaHost(), request.getMediaHostName())));
    // 402 Linked Digital Object PID
    handleAttributeList.put(LINKED_DO_PID,
        new FdoAttribute(LINKED_DO_PID, timestamp, request.getLinkedDigitalObjectPid()));
    // 403 Linked Digital Object Type
    handleAttributeList.put(LINKED_DO_TYPE,
        new FdoAttribute(LINKED_DO_TYPE, timestamp, request.getLinkedDigitalObjectType()));
    // 404 Primary Media ID
    handleAttributeList.put(PRIMARY_MEDIA_ID,
        new FdoAttribute(PRIMARY_MEDIA_ID, timestamp, request.getPrimaryMediaId()));
    // 405 Primary Media Id Type
    handleAttributeList.put(PRIMARY_MEDIA_ID_TYPE,
        new FdoAttribute(PRIMARY_MEDIA_ID_TYPE, timestamp, request.getPrimaryMediaIdType()));
    // 406 Primary Media Id Name
    handleAttributeList.put(PRIMARY_MEDIA_ID_NAME,
        new FdoAttribute(PRIMARY_MEDIA_ID_NAME, timestamp, request.getPrimaryMediaIdName()));
    // 407 media type
    handleAttributeList.put(MEDIA_TYPE,
        new FdoAttribute(MEDIA_TYPE, timestamp, request.getMediaType()));
    // 408 mime type
    handleAttributeList.put(MIME_TYPE,
        new FdoAttribute(MIME_TYPE, timestamp, request.getMimeType()));
    // 409 License Name
    handleAttributeList.put(LICENSE_NAME,
        new FdoAttribute(LICENSE_NAME, timestamp, request.getLicenseName()));
    // 410 License Id
    handleAttributeList.put(LICENSE_URL,
        new FdoAttribute(LICENSE_URL, timestamp, request.getLicenseUrl()));
    // 411 Rights Holder Pid
    var rightsHolder =
        request.getRightsHolderPid() == null ? request.getMediaHost()
            : request.getRightsHolderPid();
    handleAttributeList.put(RIGHTS_HOLDER_PID,
        new FdoAttribute(RIGHTS_HOLDER_PID, timestamp, rightsHolder));
    String rightsHolderName = request.getRightsHolder() != null ?
        getObjectName(request.getRightsHolderPid(), request.getRightsHolder()) : mediaHostName;
    // 412 Rights Holder Name
    handleAttributeList.put(RIGHTS_HOLDER_NAME,
        new FdoAttribute(RIGHTS_HOLDER_NAME, timestamp, rightsHolderName));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedDigitalMediaAttributes(
      DigitalMediaRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(
        prepareDigitalMediaAttributes(request, handle, timestamp, false));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Organisation Record Creation */
  public FdoRecord prepareNewOrganisationRecord(OrganisationRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = prepareOrganisationAttributes(request, handle, timestamp);
    fdoAttributes.putAll(
        prepareGeneratedAttributes(handle, FdoType.ORGANISATION, timestamp, isDraft));
    fdoAttributes.putAll(prepareGeneratedAttributesDoi(timestamp));
    return new FdoRecord(handle, FdoType.ORGANISATION, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedOrganisationRecord(OrganisationRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedOrganisationAttributes(request, previousVersion.handle(),
        timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.ORGANISATION, fdoAttributes, null,
        fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareOrganisationAttributes(
      OrganisationRequestAttributes request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC,
        new FdoAttribute(LOC, timestamp,
            setLocations(handle, ORGANISATION, request.getOrganisationIdentifier(),
                request.getLocations(), false)));
    // 42: Referent Name
    handleAttributeList.put(REFERENT_NAME,
        new FdoAttribute(REFERENT_NAME, timestamp, request.getReferentName()));
    // 601 Organisation Identifier
    handleAttributeList.put(ORGANISATION_ID,
        new FdoAttribute(ORGANISATION_ID, timestamp, request.getOrganisationIdentifier()));
    // 602 Organisation Identifier type
    handleAttributeList.put(ORGANISATION_ID_TYPE,
        new FdoAttribute(ORGANISATION_ID_TYPE, timestamp, request.getOrganisationIdentifierType()));
    // 603 Organisation Name
    handleAttributeList.put(ORGANISATION_NAME,
        new FdoAttribute(ORGANISATION_NAME, timestamp,
            getObjectName(request.getOrganisationIdentifier(), null)));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedOrganisationAttributes(
      OrganisationRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(
        prepareOrganisationAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Source System Record Creation */
  public FdoRecord prepareNewSourceSystemRecord(SourceSystemRequestAttributes request,
      String handle, Instant timestamp, boolean isDraft) throws InvalidRequestException {
    var fdoAttributes = new EnumMap<>(prepareSourceSystemAttributes(request, handle, timestamp));
    fdoAttributes.putAll(prepareGeneratedAttributes(handle, SOURCE_SYSTEM, timestamp, isDraft));
    return new FdoRecord(handle, SOURCE_SYSTEM, fdoAttributes, null, fdoAttributes.values());
  }

  public FdoRecord prepareUpdatedSourceSystemRecord(SourceSystemRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedSourceSystemAttributes(request, previousVersion.handle(),
        timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), SOURCE_SYSTEM, fdoAttributes, null,
        fdoAttributes.values());
  }

  public Map<FdoProfile, FdoAttribute> prepareSourceSystemAttributes(
      SourceSystemRequestAttributes request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 101: 10320/Loc
    handleAttributeList.put(LOC,
        new FdoAttribute(LOC, timestamp,
            setLocations(handle, SOURCE_SYSTEM, null, request.getLocations(), false)));
    // 600: source system
    handleAttributeList.put(SOURCE_SYSTEM_NAME,
        new FdoAttribute(SOURCE_SYSTEM_NAME, timestamp, request.getSourceSystemName()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareUpdatedSourceSystemAttributes(
      SourceSystemRequestAttributes request, String handle, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var updatedAttributes = new EnumMap<>(
        prepareSourceSystemAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Tombstone Record Creation */
  public FdoRecord prepareTombstoneRecord(TombstoneRequestAttributes request, Instant timestamp,
      FdoRecord previousVersion) throws JsonProcessingException {
    var fdoAttributes = prepareTombstoneAttributes(request, timestamp, previousVersion);
    return new FdoRecord(previousVersion.handle(), previousVersion.fdoType(), fdoAttributes,
        null, fdoAttributes.values());
  }

  private Map<FdoProfile, FdoAttribute> prepareTombstoneAttributes(
      TombstoneRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion) throws JsonProcessingException {
    var fdoAttributes = new EnumMap<>(previousVersion.attributes());
    fdoAttributes.replace(PID_RECORD_ISSUE_NUMBER,
        incrementIssueNumber(previousVersion.attributes().get(PID_RECORD_ISSUE_NUMBER),
            timestamp));
    fdoAttributes.replace(PID_STATUS,
        new FdoAttribute(PID_STATUS, timestamp, PidStatus.TOMBSTONED));
    // 30: Tombstoned Text
    fdoAttributes.put(TOMBSTONED_TEXT,
        new FdoAttribute(TOMBSTONED_TEXT, timestamp, request.getTombstoneText()));
    // 31: hasRelatedPID
    if (request.getHasRelatedPid() != null && !request.getHasRelatedPid().isEmpty()) {
      fdoAttributes.put(HAS_RELATED_PID, new FdoAttribute(HAS_RELATED_PID, timestamp,
          mapper.writeValueAsString(request.getHasRelatedPid())));
    } else {
      fdoAttributes.put(HAS_RELATED_PID, new FdoAttribute(HAS_RELATED_PID, timestamp,
          mapper.writeValueAsString(Collections.emptyList())));
    }
    // 32: tombstonedDate
    fdoAttributes.put(TOMBSTONED_DATE,
        new FdoAttribute(TOMBSTONED_DATE, timestamp, getDate(timestamp)));
    return fdoAttributes;
  }

  /* Generalized attribute Building */

  private FdoAttribute incrementIssueNumber(FdoAttribute previousVersion, Instant timestamp) {
    var previousIssueNumber = previousVersion.getValue();
    var incrementedIssueNumber = String.valueOf(Integer.parseInt(previousIssueNumber) + 1);
    return new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp, incrementedIssueNumber);
  }

  private Map<FdoProfile, FdoAttribute> prepareGeneratedAttributes(String handle, FdoType fdoType,
      Instant timestamp, boolean isDraft) {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 1: FDO Profile
    handleAttributeList.put(FDO_PROFILE,
        new FdoAttribute(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 2: FDO Record LicenseId
    handleAttributeList.put(FDO_RECORD_LICENSE_ID,
        new FdoAttribute(FDO_RECORD_LICENSE_ID, timestamp, FDO_LICENSE_VALUE));
    // 3: FDO Record LicenseName
    handleAttributeList.put(FDO_RECORD_LICENSE_NAME,
        new FdoAttribute(FDO_RECORD_LICENSE_NAME, timestamp, FDO_LICENSE_NAME_VALUE));
    // 4: Digital Object Type
    handleAttributeList.put(DIGITAL_OBJECT_TYPE,
        new FdoAttribute(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 5: Digital ObjectName
    handleAttributeList.put(DIGITAL_OBJECT_NAME,
        new FdoAttribute(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectName()));
    // 6: PID
    handleAttributeList.put(PID,
        new FdoAttribute(PID, timestamp, fdoType.getDomain() + handle));
    // 7: PID Issuer
    handleAttributeList.put(PID_ISSUER,
        new FdoAttribute(PID_ISSUER, timestamp, profileProperties.getPidIssuer()));
    // 8: PID Issuer Name
    handleAttributeList.put(PID_ISSUER_NAME,
        new FdoAttribute(PID_ISSUER_NAME, timestamp, profileProperties.getPidIssuerName()));
    // 9: PID Record Issue Date
    handleAttributeList.put(PID_RECORD_ISSUE_DATE,
        new FdoAttribute(PID_RECORD_ISSUE_DATE, timestamp, getDate(timestamp)));
    // 10: Pid Record Issue Number
    handleAttributeList.put(PID_RECORD_ISSUE_NUMBER,
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp,
            "1")); // This gets replaced on an update
    // 11: Pid Status
    var pidStatus = isDraft ? PidStatus.DRAFT : PidStatus.ACTIVE;
    handleAttributeList.put(PID_STATUS,
        new FdoAttribute(PID_STATUS, timestamp, pidStatus));
    // 100 HS Admin
    handleAttributeList.put(HS_ADMIN,
        new FdoAttribute(timestamp, applicationProperties.getPrefix()));
    return handleAttributeList;
  }

  private Map<FdoProfile, FdoAttribute> prepareGeneratedAttributesDoi(Instant timestamp) {
    var handleAttributeList = new EnumMap<FdoProfile, FdoAttribute>(FdoProfile.class);
    // 40: Issued for Agent
    handleAttributeList.put(ISSUED_FOR_AGENT,
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, profileProperties.getIssuedForAgent()));
    // 41: Issued for Agent Name
    handleAttributeList.put(ISSUED_FOR_AGENT_NAME,
        new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
            profileProperties.getIssuedForAgentName()));
    return handleAttributeList;
  }

  private void prepareUpdateAttributes(Map<FdoProfile, FdoAttribute> updatedAttributes,
      Map<FdoProfile, FdoAttribute> previousAttributes, Instant timestamp,
      boolean incrementVersion) {
    updatedAttributes.putAll(previousAttributes.entrySet().stream()
        .filter(previousAttributeEntry -> GENERATED_KEYS.contains(previousAttributeEntry.getKey()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    updatedAttributes.replace(PID_STATUS,
        new FdoAttribute(PID_STATUS, timestamp,
            PidStatus.ACTIVE)); // We re-activate every updated PID
    if (incrementVersion) {
      updatedAttributes.put(PID_RECORD_ISSUE_NUMBER,
          incrementIssueNumber(previousAttributes.get(PID_RECORD_ISSUE_NUMBER), timestamp));
    } else {
      updatedAttributes.put(PID_RECORD_ISSUE_NUMBER,
          previousAttributes.get(PID_RECORD_ISSUE_NUMBER));
    }
  }

  /* Activate FdoRecords */
  public FdoRecord activatePidRecord(FdoRecord draftRecord, Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = draftRecord.attributes();
    var userLocations = getUserLocationsFromXml(fdoAttributes.get(LOC).getValue());
    var locationAttribute = setLocations(draftRecord.handle(), draftRecord.fdoType(),
        getKeyLocationFromPidRecord(fdoAttributes, draftRecord.fdoType()), userLocations,
        false);
    fdoAttributes.replace(LOC, new FdoAttribute(LOC, timestamp, locationAttribute));
    fdoAttributes.replace(PID_RECORD_ISSUE_NUMBER,
        incrementIssueNumber(fdoAttributes.get(PID_RECORD_ISSUE_NUMBER), timestamp));
    fdoAttributes.replace(PID_STATUS,
        new FdoAttribute(PID_STATUS, timestamp, PidStatus.ACTIVE));

    return new FdoRecord(draftRecord.handle(), draftRecord.fdoType(), fdoAttributes,
        draftRecord.primaryLocalId(), fdoAttributes.values());
  }

  private String getKeyLocationFromPidRecord(Map<FdoProfile, FdoAttribute> fdoAttributes,
      FdoType fdoType) {
    String keyLocation;
    try {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> {
          var catalogIdValue = fdoAttributes.get(CATALOG_NUMBER);
          var catalogId = catalogIdValue == null ? null : catalogIdValue.getValue();
          var otherSpecimenIdsField = fdoAttributes.get(OTHER_SPECIMEN_IDS);
          var otherSpecimenIds =
              (otherSpecimenIdsField == null || otherSpecimenIdsField.getValue().isEmpty()) ? null
                  : mapper.convertValue(otherSpecimenIdsField.getValue(),
                      new TypeReference<ArrayList<OtherspecimenIds>>() {
                      });
          keyLocation = getSpecimenResolvableId(catalogId, otherSpecimenIds);
        }
        case DIGITAL_MEDIA -> keyLocation = fdoAttributes.get(PRIMARY_MEDIA_ID).getValue();
        default -> {
          return null;
        }
      }
    } catch (NullPointerException e) {
      log.warn("Missing key field in fdo attribute for fdo record {}", fdoAttributes);
      return null;
    }
    return keyLocation;
  }

  /* Helper Functions */
  private String getObjectName(String url, String name) throws InvalidRequestException {
    if (name != null) {
      return name;
    }
    if (url.contains(ROR_DOMAIN)) {
      return pidResolver.getObjectName(getRor(url), true);
    } else if (url.contains(HANDLE_DOMAIN) || url.contains(DOI_DOMAIN)) {
      return pidResolver.getObjectName(url, false);
    } else if (url.contains(WIKIDATA_DOMAIN)) {
      return pidResolver.resolveQid(url.replace(WIKIDATA_DOMAIN, WIKIDATA_API));
    }
    throw new InvalidRequestException(String.format(PROXY_ERROR, url,
        (ROR_DOMAIN + ", " + HANDLE_DOMAIN + ", or " + DOI_DOMAIN)));
  }

  private static String getRor(String url) {
    return url.replace(ROR_DOMAIN, ROR_API_DOMAIN);
  }

  private String getDate(Instant timestamp) {
    return dt.format(timestamp);
  }

  private List<XmlElement> getXmlElements(String handle, FdoType fdoType, String keyAttribute,
      List<String> userLocations, boolean isDraft) {
    var locations = new ArrayList<XmlElement>();
    AtomicInteger i = new AtomicInteger();
    if (!isDraft) {
      switch (fdoType) {
        case DIGITAL_SPECIMEN -> {
          locations.add(
              new XmlElement(i.getAndIncrement(), "1",
                  applicationProperties.getUiUrl() + "/ds/" + handle,
                  "HTML"));
          locations.add(new XmlElement(i.getAndIncrement(), "0",
              applicationProperties.getApiUrl() + "/digital-specimen/v1/" + handle, "JSON"));
          if (keyAttribute != null) {
            locations.add(new XmlElement(i.getAndIncrement(), "0", keyAttribute, "CATALOG"));
          }
        }
        case DATA_MAPPING -> {
          locations.add(new XmlElement(i.getAndIncrement(), "1",
              applicationProperties.getOrchestrationUi() + "/data-mapping/" + handle, "HTML"));
          locations.add(new XmlElement(i.getAndIncrement(), "0",
              applicationProperties.getOrchestrationApi() + "/data-mapping/v1/" + handle, "JSON"));
        }
        case SOURCE_SYSTEM -> {
          locations.add(new XmlElement(i.getAndIncrement(), "1",
              applicationProperties.getOrchestrationUi() + "/source-system/" + handle, "HTML"));
          locations.add(new XmlElement(i.getAndIncrement(), "0",
              applicationProperties.getOrchestrationApi() + "/source-system/v1/" + handle,
              "JSON"));
        }
        case DIGITAL_MEDIA -> {
          locations.add(
              new XmlElement(i.getAndIncrement(), "1",
                  applicationProperties.getUiUrl() + "/dm/" + handle,
                  "HTML"));
          locations.add(new XmlElement(i.getAndIncrement(), "0",
              applicationProperties.getApiUrl() + "/digital-media/v1/" + handle, "JSON"));
          if (keyAttribute != null) {
            locations.add(new XmlElement(i.getAndIncrement(), "0", keyAttribute, "MEDIA"));
          }
        }
        case ANNOTATION -> locations.add(new XmlElement(i.getAndIncrement(), "1",
            applicationProperties.getApiUrl() + "/annotations/v1/" + handle, "JSON"));
        case MAS -> {
          locations.add(new XmlElement(i.getAndIncrement(), "1",
              applicationProperties.getOrchestrationUi() + "/mas/" + handle, "HTML"));
          locations.add(new XmlElement(i.getAndIncrement(), "0",
              applicationProperties.getOrchestrationApi() + "/mas/v1/" + handle, "JSON"));
        }
        case ORGANISATION ->
            locations.add(new XmlElement(i.getAndIncrement(), "1", keyAttribute, "ROR"));
        default -> {
          // Handle, DOI are all in user locations
        }
      }
    }
    userLocations.forEach(
        loc -> locations.add(new XmlElement(i.getAndIncrement(), "0", loc, null)));
    return locations;
  }

  private List<String> getUserLocationsFromXml(String xmlLoc) {
    var locMatcher = xmlLocPattern.matcher(xmlLoc);
    var userLocations = new ArrayList<String>();
    while (locMatcher.find()) {
      var loc = locMatcher.group();
      loc = loc.replaceAll("href=|\"", "");
      userLocations.add(loc);
    }
    return userLocations;
  }

  private String setLocations(String handle, FdoType fdoType, String keyLocation,
      List<String> userLocations, boolean isDraft) throws InvalidRequestException {
    var xmlElements = getXmlElements(handle, fdoType, keyLocation, userLocations, isDraft);
    if (xmlElements.isEmpty()) {
      return "<locations></locations>";
    }
    try {
      var documentBuilder = dbf.newDocumentBuilder();
      var doc = documentBuilder.newDocument();
      var locations = doc.createElement("locations");
      doc.appendChild(locations);
      xmlElements.forEach(xmlLoc -> {
        var locs = doc.createElement("location");
        locs.setAttribute("id", String.valueOf(xmlLoc.id));
        locs.setAttribute("href", xmlLoc.loc);
        locs.setAttribute("weight", xmlLoc.weight);
        if (xmlLoc.view != null) {
          locs.setAttribute("view", xmlLoc.view);
        }
        locations.appendChild(locs);
      });
      return documentToString(doc);
    } catch (TransformerException | ParserConfigurationException e) {
      throw new InvalidRequestException("An error has occurred parsing location data");
    }
  }

  private String documentToString(org.w3c.dom.Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  private record XmlElement(int id, String weight, String loc, String view) {

  }

}
