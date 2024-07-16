package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.configuration.AppConfig.DATE_STRING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.ANNOTATION_HASH;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.HAS_RELATED_PID;
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
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.SPECIMEN_OBJECT_ID_ABSENCE_REASON;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_DATE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOMBSTONED_TEXT;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoProfile.WAS_DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DATA_MAPPING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ORGANISATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.service.ServiceUtils.getField;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.domain.fdo.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.fdo.DataMappingRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalMediaRequest;
import eu.dissco.core.handlemanager.domain.fdo.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.fdo.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.MasRequest;
import eu.dissco.core.handlemanager.domain.fdo.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.fdo.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.fdo.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.PidStatus;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.web.PidResolver;
import jakarta.annotation.Nullable;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String DOI_DOMAIN = "https://doi.org/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
  private static final String WIKIDATA_DOMAIN = "https://www.wikidata.org/wiki/";
  private static final String WIKIDATA_API = "https://wikidata.org/w/rest.php/wikibase/v0/entities/items/";
  private static final String PROXY_ERROR = "Invalid attribute: %s must contain proxy: %s";
  private static final String PID_KERNEL_METADATA_LICENSE = "https://creativecommons.org/publicdomain/zero/1.0/";
  private static final String LOC_REQUEST = "locations";
  public static final Map<String, String> RESOLVABLE_KEYS;
  public static final List<Integer> GENERATED_KEYS;
  public static final List<Integer> TOMBSTONE_KEYS;

  static {
    GENERATED_KEYS = List.of(FDO_RECORD_LICENSE.index(), PID.index(), PID_RECORD_ISSUE_DATE.index(),
        PID_STATUS.index(), HS_ADMIN.index());
  }

  static {
    TOMBSTONE_KEYS = List.of(FDO_RECORD_LICENSE.index(), PID.index(), PID_RECORD_ISSUE_DATE.index(),
        PID_ISSUER.index(), PID_ISSUER_NAME.index(), ISSUED_FOR_AGENT.index(),
        ISSUED_FOR_AGENT_NAME.index(), STRUCTURAL_TYPE.index(), HS_ADMIN.index());
  }

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

  public List<Document> toMongoDbDocument(List<FdoRecord> fdoRecords)
      throws JsonProcessingException {
    var documentList = new ArrayList<Document>();
    for (var fdoRecord : fdoRecords) {
      var doc = Document.parse(mapper.writeValueAsString(fdoRecord));
      addLocalId(fdoRecord, doc);
      documentList.add(doc);
    }
    return documentList;
  }

  private void addLocalId(FdoRecord fdoRecord, Document doc) {
    if (fdoRecord.primaryLocalId() == null) {
      return;
    }
    if (DIGITAL_SPECIMEN.equals(fdoRecord.fdoType())) {
      doc.append(NORMALISED_SPECIMEN_OBJECT_ID.get(), fdoRecord.primaryLocalId());
    } else if (DIGITAL_MEDIA.equals(fdoRecord.fdoType())) {
      doc.append(PRIMARY_MEDIA_ID.get(), fdoRecord.primaryLocalId());
    } else if (ANNOTATION.equals(fdoRecord.fdoType())) {
      doc.append(ANNOTATION_HASH.get(), fdoRecord.primaryLocalId());
    }
  }

  /* Handle Record Creation */
  public FdoRecord prepareNewHandleRecord(HandleRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, fdoType, timestamp);
    return new FdoRecord(handle, fdoType, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedHandleRecord(HandleRecordRequest recordRequest,
      FdoType fdoType, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(recordRequest,
        previousVersion.handle(), fdoType,
        timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), fdoType, fdoAttributes, null);
  }

  private List<FdoAttribute> prepareNewHandleAttributes(HandleRecordRequest request,
      String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var handleAttributes = prepareHandleAttributesFromRequest(request, handle, fdoType, timestamp);
    handleAttributes.addAll(prepareHandleAttributesGenerated(handle, fdoType, timestamp));
    return handleAttributes;
  }

  private List<FdoAttribute> prepareUpdatedHandleAttributes(HandleRecordRequest request,
      String handle, FdoType fdoType, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var previousAttributes = new ArrayList<>(previousVersion.attributes());
    var updatedAttributes = prepareHandleAttributesFromRequest(request, handle, fdoType, timestamp);
    updatedAttributes.addAll(previousAttributes.stream()
        .filter(previousAttribute -> GENERATED_KEYS.contains(previousAttribute.getIndex()))
        .toList());
    var previousIssueNumber = getField(previousVersion.attributes(), PID_RECORD_ISSUE_NUMBER);
    if (incrementVersion) {
      updatedAttributes.add(incrementIssueNumber(previousIssueNumber, timestamp));
    } else {
      updatedAttributes.add(previousIssueNumber);
    }
    return updatedAttributes;
  }

  private FdoAttribute incrementIssueNumber(FdoAttribute previousVersion, Instant timestamp) {
    var previousIssueNumber = previousVersion.getValue();
    var incrementedIssueNumber = String.valueOf(Integer.parseInt(previousIssueNumber) + 1);
    return new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp, incrementedIssueNumber);
  }

  // These attributes may change on an update
  private ArrayList<FdoAttribute> prepareHandleAttributesFromRequest(
      HandleRecordRequest request,
      String handle,
      FdoType fdoType, Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    if (!fdoType.equals(ORGANISATION)) {
      handleAttributeList.add(new FdoAttribute(LOC, timestamp,
          setLocations(request.getLocations(), handle, fdoType)));
    }
    // 1: FDO Profile
    handleAttributeList.add(
        new FdoAttribute(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 3: Digital Object Type
    handleAttributeList.add(
        new FdoAttribute(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 4: Digital ObjectName
    handleAttributeList.add(
        new FdoAttribute(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectName()));
    // 6: PID Issuer
    handleAttributeList.add(new FdoAttribute(PID_ISSUER, timestamp, request.getPidIssuer()));
    // 7: PID Issuer Name
    handleAttributeList.add(new FdoAttribute(PID_ISSUER_NAME, timestamp,
        getObjectName(request.getPidIssuer(), null)));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp,
            request.getStructuralType().toString()));
    return handleAttributeList;
  }

  // These attributes do not depend on the request and do not change on an update (except issue number)
  private List<FdoAttribute> prepareHandleAttributesGenerated(String handle, FdoType fdoType,
      Instant timestamp) {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 2: FDO Record License
    handleAttributeList.add(
        new FdoAttribute(FDO_RECORD_LICENSE, timestamp, PID_KERNEL_METADATA_LICENSE));
    // 5: PID
    handleAttributeList.add(new FdoAttribute(PID, timestamp, fdoType.getDomain() + handle));
    // 10: PID Record Issue Date
    handleAttributeList.add(
        new FdoAttribute(PID_RECORD_ISSUE_DATE, timestamp, getDate(timestamp)));
    // 11: Pid Record Issue Number
    handleAttributeList.add(
        new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp,
            "1")); // This gets replaced on an update
    // 13: Pid Status
    handleAttributeList.add(new FdoAttribute(PID_STATUS, timestamp, PidStatus.ACTIVE.name()));
    // 100 HS Admin
    handleAttributeList.add(new FdoAttribute(timestamp, applicationProperties.getPrefix()));
    return handleAttributeList;
  }

  /* DOI Record Creation */
  public FdoRecord prepareNewDoiRecord(DoiRecordRequest request, String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewDoiAttributes(request, handle, fdoType, timestamp);
    return new FdoRecord(handle, fdoType, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedDoiRecord(DoiRecordRequest request,
      FdoType fdoType, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request, previousVersion.handle(), fdoType,
        timestamp,
        previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), fdoType, fdoAttributes, null);
  }

  private List<FdoAttribute> prepareNewDoiAttributes(DoiRecordRequest request,
      String handle,
      FdoType fdoType, Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, fdoType, timestamp);
    fdoAttributes.addAll(prepareDoiAttributesFromRequest(request, handle, timestamp));
    return fdoAttributes;
  }

  private List<FdoAttribute> prepareUpdatedDoiAttributes(DoiRecordRequest request,
      String handle,
      FdoType fdoType, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, handle, fdoType, timestamp,
        previousVersion,
        incrementVersion);
    fdoAttributes.addAll(prepareDoiAttributesFromRequest(request, handle, timestamp));
    return fdoAttributes;
  }

  private List<FdoAttribute> prepareDoiAttributesFromRequest(DoiRecordRequest request,
      String handle,
      Instant timestamp) {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 40: Referent Type
    handleAttributeList.add(
        new FdoAttribute(REFERENT_TYPE, timestamp, request.getReferentType()));
    // 41: Referent DOI Name
    handleAttributeList.add(new FdoAttribute(REFERENT_DOI_NAME, timestamp, handle));
    // 42: Referent Name
    handleAttributeList.add(
        new FdoAttribute(REFERENT_NAME, timestamp, request.getReferentName()));
    // 43: Primary Referent Type
    handleAttributeList.add(new FdoAttribute(PRIMARY_REFERENT_TYPE, timestamp,
        request.getPrimaryReferentType()));
    return handleAttributeList;
  }

  /* Annotation Record Creation */
  public FdoRecord prepareNewAnnotationRecord(AnnotationRequest request, String handle,
      Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, ANNOTATION,
        timestamp);
    fdoAttributes.addAll(prepareAnnotationAttributesFromRequest(request, timestamp));
    var localId =
        request.getAnnotationHash() != null ? request.getAnnotationHash().toString() : null;
    return new FdoRecord(handle, ANNOTATION, fdoAttributes, localId);
  }

  public FdoRecord prepareUpdatedAnnotationRecord(AnnotationRequest request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, previousVersion.handle(),
        ANNOTATION,
        timestamp, previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareAnnotationAttributesFromRequest(request, timestamp));
    var localId =
        request.getAnnotationHash() == null ? null : request.getAnnotationHash().toString();
    return new FdoRecord(previousVersion.handle(), ANNOTATION, fdoAttributes, localId);
  }

  private List<FdoAttribute> prepareAnnotationAttributesFromRequest(
      AnnotationRequest request,
      Instant timestamp) {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 500 Target PID
    handleAttributeList.add(new FdoAttribute(TARGET_PID, timestamp, request.getTargetPid()));
    // 501 Target Type
    handleAttributeList.add(
        new FdoAttribute(TARGET_TYPE, timestamp, request.getTargetType()));
    // 502 Motivation
    handleAttributeList.add(new FdoAttribute(MOTIVATION, timestamp,
        request.getMotivation().toString()));
    // 503 Annotation Hash
    if (request.getAnnotationHash() != null) {
      handleAttributeList.add(new FdoAttribute(ANNOTATION_HASH, timestamp,
          request.getAnnotationHash().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(ANNOTATION_HASH, timestamp,
          null));
    }

    return handleAttributeList;
  }

  /* Data Mapping Record Creation */
  public FdoRecord prepareNewDataMappingRecord(DataMappingRequest request, String handle,
      Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, DATA_MAPPING, timestamp);
    fdoAttributes.addAll(prepareDataMappingAttributesFromRequest(request, timestamp));
    return new FdoRecord(handle, DATA_MAPPING, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedDataMappingRecord(DataMappingRequest request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, previousVersion.handle(),
        DATA_MAPPING, timestamp,
        previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareDataMappingAttributesFromRequest(request, timestamp));
    return new FdoRecord(previousVersion.handle(), DATA_MAPPING, fdoAttributes, null);
  }

  private List<FdoAttribute> prepareDataMappingAttributesFromRequest(DataMappingRequest request,
      Instant timestamp) {
    return List.of(
        new FdoAttribute(SOURCE_DATA_STANDARD, timestamp, request.getSourceDataStandard()));
  }

  /* Digital Specimen Record Creation */
  public FdoRecord prepareNewDigitalSpecimenRecord(DigitalSpecimenRequest request,
      String handle, Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var fdoAttributes = prepareNewDoiAttributes(request, handle, DIGITAL_SPECIMEN, timestamp);
    fdoAttributes.addAll(prepareDigitalSpecimenAttributesFromRequest(request, timestamp));
    return new FdoRecord(handle, DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId());
  }

  public FdoRecord prepareUpdatedDigitalSpecimenRecord(
      DigitalSpecimenRequest request, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException, JsonProcessingException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request, previousVersion.handle(),
        DIGITAL_SPECIMEN, timestamp, previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareDigitalSpecimenAttributesFromRequest(request, timestamp));
    return new FdoRecord(previousVersion.handle(), DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId());
  }

  private List<FdoAttribute> prepareDigitalSpecimenAttributesFromRequest(
      DigitalSpecimenRequest request, Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 200 Specimen Host
    handleAttributeList.add(
        new FdoAttribute(SPECIMEN_HOST, timestamp, request.getSpecimenHost()));
    // 201 Specimen Host Name
    handleAttributeList.add(new FdoAttribute(SPECIMEN_HOST_NAME, timestamp,
        getObjectName(request.getSpecimenHost(), request.getSpecimenHostName())));
    // 202 Primary Specimen Object ID
    handleAttributeList.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID, timestamp,
        getObjectName(request.getSpecimenHost(), request.getPrimarySpecimenObjectId())));
    // 203 Primary Specimen Object ID Type
    handleAttributeList.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, timestamp,
        getObjectName(request.getSpecimenHost(),
            request.getPrimarySpecimenObjectIdType().toString())));
    // 204 Primary Specimen Object ID Name
    handleAttributeList.add(new FdoAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME, timestamp,
        request.getPrimarySpecimenObjectIdName()));
    // 205 Normalised Specimen Object Id
    handleAttributeList.add(new FdoAttribute(NORMALISED_SPECIMEN_OBJECT_ID, timestamp,
        request.getNormalisedPrimarySpecimenObjectId()));
    // 206 Specimen Object Id Absence Reason
    handleAttributeList.add(new FdoAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON, timestamp,
        request.getSpecimenObjectIdAbsenceReason()));
    // 206 Specimen Object Id Absence Reason
    handleAttributeList.add(new FdoAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON, timestamp,
        request.getSpecimenObjectIdAbsenceReason()));
    // 207 Other Specimen Ids
    if (request.getOtherSpecimenIds() != null && !request.getOtherSpecimenIds().isEmpty()) {
      handleAttributeList.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp,
          mapper.writeValueAsString(request.getOtherSpecimenIds())));
    } else {
      handleAttributeList.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp,
          null));
    }
    // 208 Topic Origin
    if (request.getTopicOrigin() != null) {
      handleAttributeList.add(
          new FdoAttribute(TOPIC_ORIGIN, timestamp, request.getTopicOrigin().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(TOPIC_ORIGIN, timestamp, null));
    }
    // 209 Topic Domain
    if (request.getTopicDomain() != null) {
      handleAttributeList.add(
          new FdoAttribute(TOPIC_DOMAIN, timestamp, request.getTopicDomain().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(TOPIC_DOMAIN, timestamp, null));
    }
    // 210 Topic Discipline
    if (request.getTopicDiscipline() != null) {
      handleAttributeList.add(new FdoAttribute(TOPIC_DISCIPLINE, timestamp,
          request.getTopicDiscipline().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(TOPIC_DISCIPLINE, timestamp, null));
    }
    // 211 Topic Category
    if (request.getTopicCategory() != null) {
      handleAttributeList.add(new FdoAttribute(TOPIC_CATEGORY, timestamp,
          request.getTopicCategory().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(TOPIC_CATEGORY, timestamp, null));
    }
    // 212 Living or Preserved
    if (request.getLivingOrPreserved() != null) {
      handleAttributeList.add(new FdoAttribute(LIVING_OR_PRESERVED, timestamp,
          request.getLivingOrPreserved().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(LIVING_OR_PRESERVED, timestamp, null));
    }
    // 213 Base Type of Specimen
    if (request.getBaseTypeOfSpecimen() != null) {
      handleAttributeList.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, timestamp,
          request.getBaseTypeOfSpecimen().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, timestamp, null));
    }
    // 214 Information Artefact Type
    if (request.getInformationArtefactType() != null) {
      handleAttributeList.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, timestamp,
          request.getInformationArtefactType().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, timestamp, null));
    }
    // 215 Material Sample Type
    if (request.getMaterialSampleType() != null) {
      handleAttributeList.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp,
          request.getMaterialSampleType().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp, null));
    }
    // 216 Material or Digital Entity
    if (request.getMaterialOrDigitalEntity() != null) {
      handleAttributeList.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, timestamp,
          request.getMaterialOrDigitalEntity().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, timestamp, null));
    }
    // 217 Marked as Type
    if (request.getMarkedAsType() != null) {
      handleAttributeList.add(new FdoAttribute(MARKED_AS_TYPE, timestamp,
          String.valueOf(request.getMarkedAsType())));
    } else {
      handleAttributeList.add(new FdoAttribute(MARKED_AS_TYPE, timestamp, null));
    }
    // 218 Was Derived From Entity
    handleAttributeList.add(
        new FdoAttribute(WAS_DERIVED_FROM_ENTITY, timestamp,
            String.valueOf(request.getDerivedFromEntity() != null)));
    // 219 Catalog ID
    handleAttributeList.add(
        new FdoAttribute(CATALOG_IDENTIFIER, timestamp, request.getCatalogIdentifier()));
    return handleAttributeList;
  }

  /* MAS Record Creation */
  public FdoRecord prepareNewMasRecord(MasRequest request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, MAS, timestamp);
    fdoAttributes.addAll(prepareMasAttributesFromRequest(request, timestamp));
    return new FdoRecord(handle, MAS, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedMasRecord(MasRequest request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, previousVersion.handle(), MAS,
        timestamp,
        previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareMasAttributesFromRequest(request, timestamp));
    return new FdoRecord(previousVersion.handle(), MAS, fdoAttributes, null);
  }

  private List<FdoAttribute> prepareMasAttributesFromRequest(MasRequest request,
      Instant timestamp) {
    return List.of(
        new FdoAttribute(MAS_NAME, timestamp, request.getMachineAnnotationServiceName()));
  }

  /* Media Object Record Creation */
  public FdoRecord prepareNewDigitalMediaRecord(DigitalMediaRequest request,
      String handle, Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewDoiAttributes(request, handle, DIGITAL_MEDIA, timestamp);
    fdoAttributes.addAll(prepareDigitalMediaAttributesFromRequest(request, timestamp));
    return new FdoRecord(handle, DIGITAL_MEDIA, fdoAttributes, request.getPrimaryMediaId());
  }

  public FdoRecord prepareUpdatedDigitalMediaRecord(DigitalMediaRequest request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request, previousVersion.handle(),
        DIGITAL_MEDIA, timestamp,
        previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareDigitalMediaAttributesFromRequest(request, timestamp));
    return new FdoRecord(previousVersion.handle(), DIGITAL_MEDIA, fdoAttributes,
        request.getPrimaryMediaId());
  }

  private List<FdoAttribute> prepareDigitalMediaAttributesFromRequest(
      DigitalMediaRequest request, Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 400 Media Host
    handleAttributeList.add(
        new FdoAttribute(MEDIA_HOST, timestamp, request.getMediaHost()));
    // 401 MediaHostName
    handleAttributeList.add(
        new FdoAttribute(MEDIA_HOST_NAME, timestamp,
            getObjectName(request.getMediaHost(), request.getMediaHostName())));
    // 403 Is Derived From Specimen
    handleAttributeList.add(
        new FdoAttribute(IS_DERIVED_FROM_SPECIMEN, timestamp,
            String.valueOf(request.getIsDerivedFromSpecimen())));
    // 404 Linked Digital Object PID
    handleAttributeList.add(
        new FdoAttribute(LINKED_DO_PID, timestamp, request.getLinkedDigitalObjectPid()));
    // 405 Linked Digital Object Type
    handleAttributeList.add(
        new FdoAttribute(LINKED_DO_TYPE, timestamp,
            request.getLinkedDigitalObjectType().toString()));
    // 406 Linked Attribute
    handleAttributeList.add(
        new FdoAttribute(LINKED_ATTRIBUTE, timestamp, request.getLinkedAttribute()));
    // 407 Primary Media ID
    handleAttributeList.add(
        new FdoAttribute(PRIMARY_MEDIA_ID, timestamp, request.getPrimaryMediaId()));
    // 408 Primary Media Object Id Type
    if (request.getPrimaryMediaObjectIdType() != null) {
      handleAttributeList.add(
          new FdoAttribute(PRIMARY_MO_ID_TYPE, timestamp,
              request.getPrimaryMediaObjectIdType().toString()));
    } else {
      handleAttributeList.add(
          new FdoAttribute(PRIMARY_MO_ID_TYPE, timestamp, null));
    }
    // 409 Primary Media Object Id Name
    handleAttributeList.add(
        new FdoAttribute(PRIMARY_MO_ID_NAME, timestamp,
            request.getPrimaryMediaObjectIdName()));
    // 410 dcterms:type
    if (request.getDcTermsType() != null) {
      handleAttributeList.add(
          new FdoAttribute(DCTERMS_TYPE, timestamp, request.getDcTermsType().toString()));
    } else {
      handleAttributeList.add(
          new FdoAttribute(DCTERMS_TYPE, timestamp, null));
    }
    // 411 dcterms:subject
    handleAttributeList.add(
        new FdoAttribute(DCTERMS_SUBJECT, timestamp, request.getDctermsSubject()));
    // 412 dcterms:format
    if (request.getDctermsFormat() != null) {
      handleAttributeList.add(
          new FdoAttribute(DCTERMS_FORMAT, timestamp,
              request.getDctermsFormat().toString()));
    } else {
      handleAttributeList.add(
          new FdoAttribute(DCTERMS_FORMAT, timestamp, null));
    }
    // 413 Derived from Entity
    handleAttributeList.add(
        new FdoAttribute(DERIVED_FROM_ENTITY, timestamp, request.getDerivedFromEntity()));
    // 414 License Name
    handleAttributeList.add(
        new FdoAttribute(LICENSE_NAME, timestamp, request.getLicenseName()));
    // 415 License URL
    handleAttributeList.add(new FdoAttribute(LICENSE_URL, timestamp, request.getLicenseName()));
    // 416 RightsholderName
    handleAttributeList.add(
        new FdoAttribute(RIGHTSHOLDER_NAME, timestamp, request.getRightsholderName()));
    // 417 Rightsholder PID
    handleAttributeList.add(
        new FdoAttribute(RIGHTSHOLDER_PID, timestamp, request.getRightsholderPid()));
    // 418 RightsholderPidType
    if (request.getRightsholderPidType() != null) {
      handleAttributeList.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, timestamp,
          request.getRightsholderPidType().toString()));
    } else {
      handleAttributeList.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, timestamp, null));
    }
    // 419 dcterms:conformsTo
    handleAttributeList.add(
        new FdoAttribute(DC_TERMS_CONFORMS, timestamp, request.getDctermsConformsTo()));
    return handleAttributeList;
  }

  /* Organisation Record Creation */
  public FdoRecord prepareNewOrganisationRecord(OrganisationRequest request, String handle,
      Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewDoiAttributes(request, handle, ORGANISATION, timestamp);
    fdoAttributes.addAll(prepareOrganisationAttributesFromRequest(request, handle, timestamp));
    return new FdoRecord(handle, ORGANISATION, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedOrganisationRecord(OrganisationRequest request,
      Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request, previousVersion.handle(),
        ORGANISATION, timestamp, previousVersion, incrementVersion);
    fdoAttributes.addAll(
        prepareOrganisationAttributesFromRequest(request, previousVersion.handle(), timestamp));
    return new FdoRecord(previousVersion.handle(), ORGANISATION, fdoAttributes, null);
  }

  private List<FdoAttribute> prepareOrganisationAttributesFromRequest(
      OrganisationRequest request,
      String handle,
      Instant timestamp) throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101 10320/loc -> includes organisation ROR
    var userLocations = concatLocations(request.getLocations(),
        List.of(request.getOrganisationIdentifier()));
    handleAttributeList.add(
        new FdoAttribute(LOC, timestamp, setLocations(userLocations, handle, ORGANISATION)));
    // 601 Organisation Identifier
    handleAttributeList.add(
        new FdoAttribute(ORGANISATION_ID, timestamp, request.getOrganisationIdentifier()));
    // 602 Organisation Identifier type
    handleAttributeList.add(new FdoAttribute(ORGANISATION_ID_TYPE, timestamp,
        request.getOrganisationIdentifierType()));
    // 603 Organisation Name
    handleAttributeList.add(new FdoAttribute(ORGANISATION_NAME, timestamp,
        getObjectName(request.getOrganisationIdentifier(), null)));
    return handleAttributeList;
  }

  /* Source System Record Creation */
  public FdoRecord prepareNewSourceSystemRecord(SourceSystemRequest request, String handle,
      Instant timestamp) throws InvalidRequestException {
    var fdoAttributes = prepareNewHandleAttributes(request, handle, SOURCE_SYSTEM, timestamp);
    fdoAttributes.addAll(prepareSourceSystemAttributesFromRequest(request, timestamp));
    return new FdoRecord(handle, SOURCE_SYSTEM, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedSourceSystemRecord(SourceSystemRequest request, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion) throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request, previousVersion.handle(),
        SOURCE_SYSTEM,
        timestamp, previousVersion, incrementVersion);
    fdoAttributes.addAll(prepareSourceSystemAttributesFromRequest(request, timestamp));
    return new FdoRecord(previousVersion.handle(), SOURCE_SYSTEM, fdoAttributes, null);
  }

  /* Tombstone Record Creation */
  public FdoRecord prepareTombstoneRecord(TombstoneRecordRequest recordRequest, Instant timestamp,
      FdoRecord previousVersion) throws JsonProcessingException {
    var fdoAttributes = prepareTombstoneAttributes(recordRequest, timestamp, previousVersion);
    return new FdoRecord(previousVersion.handle(), previousVersion.fdoType(), fdoAttributes, null);
  }

  private List<FdoAttribute> prepareTombstoneAttributes(TombstoneRecordRequest request,
      Instant timestamp, FdoRecord previousVersion) throws JsonProcessingException {
    var handleAttributeList = new ArrayList<>(previousVersion.attributes());
    var previousIssueNum = getField(previousVersion.attributes(), PID_RECORD_ISSUE_NUMBER);
    var newIssueNum = incrementIssueNumber(previousIssueNum, timestamp);
    handleAttributeList.set(handleAttributeList.indexOf(previousIssueNum), newIssueNum);
    // 30: Tombstoned Text
    handleAttributeList.add(
        new FdoAttribute(TOMBSTONED_TEXT, timestamp, request.getTombstonedText()));
    // 31: hasRelatedPID
    if (request.getHasRelatedPID() != null && !request.getHasRelatedPID().isEmpty()) {
      handleAttributeList.add(new FdoAttribute(HAS_RELATED_PID, timestamp,
          mapper.writeValueAsString(request.getHasRelatedPID())));
    } else {
      handleAttributeList.add(new FdoAttribute(HAS_RELATED_PID, timestamp,
          mapper.writeValueAsString(Collections.emptyList())));
    }
    // 32: tombstonedDate
    handleAttributeList.add(new FdoAttribute(TOMBSTONED_DATE, timestamp, getDate(timestamp)));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareSourceSystemAttributesFromRequest(
      SourceSystemRequest request,
      Instant timestamp) {
    return List.of(
        new FdoAttribute(SOURCE_SYSTEM_NAME, timestamp, request.getSourceSystemName()));
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
      case DATA_MAPPING -> {
        return List.of(applicationProperties.getOrchestrationUrl() + "/mapping/" + handle);
      }
      case SOURCE_SYSTEM -> {
        return List.of(applicationProperties.getOrchestrationUrl() + "/source-system/" + handle);
      }
      case DIGITAL_MEDIA -> {
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

  private String[] concatLocations(String[] userLocations, List<String> defaultLocations) {
    var objectLocations = new ArrayList<>(defaultLocations);
    if (userLocations != null) {
      objectLocations.addAll(List.of(userLocations));
    }
    return objectLocations.toArray(new String[0]);
  }

}
