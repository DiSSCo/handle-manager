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
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ANNOTATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DATA_MAPPING;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_MEDIA;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DIGITAL_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.DOI;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.HANDLE;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.MAS;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.ORGANISATION;
import static eu.dissco.core.handlemanager.domain.fdo.FdoType.SOURCE_SYSTEM;
import static eu.dissco.core.handlemanager.service.ServiceUtils.getField;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.handlemanager.domain.fdo.FdoType;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.PidStatus;
import eu.dissco.core.handlemanager.domain.fdo.vocabulary.specimen.StructuralType;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoAttribute;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.FdoRecord;
import eu.dissco.core.handlemanager.domain.requests.TombstoneRequestAttributes;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.schema.AnnotationRequestAttributes;
import eu.dissco.core.handlemanager.schema.DataMappingRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalMediaRequestAttributes;
import eu.dissco.core.handlemanager.schema.DigitalSpecimenRequestAttributes;
import eu.dissco.core.handlemanager.schema.DoiKernelRequestAttributes;
import eu.dissco.core.handlemanager.schema.HandleRequestAttributes;
import eu.dissco.core.handlemanager.schema.MasRequestAttributes;
import eu.dissco.core.handlemanager.schema.OrganisationRequestAttributes;
import eu.dissco.core.handlemanager.schema.SourceSystemRequestAttributes;
import eu.dissco.core.handlemanager.web.PidResolver;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  private static final String JSON_ERROR_MSG = "Unable to parse json request";
  private static final String PID_KERNEL_METADATA_LICENSE = "https://creativecommons.org/publicdomain/zero/1.0/";
  private static final String DATACITE_ROR = "https://ror.org/04wxnsj81";
  private static final String DATACITE_NAME = "DataCite";
  private static final String PRIMARY_REFERENT_TYPE_VALUE = "creation";
  public static final List<Integer> GENERATED_KEYS;
  public static final List<Integer> TOMBSTONE_KEYS;

  static {
    GENERATED_KEYS = List.of(FDO_PROFILE.index(), FDO_RECORD_LICENSE.index(), PID_ISSUER.index(),
        PID_ISSUER_NAME.index(), DIGITAL_OBJECT_TYPE.index(), DIGITAL_OBJECT_NAME.index(),
        PID.index(), PID_RECORD_ISSUE_DATE.index(), PID_STATUS.index(), HS_ADMIN.index());
  }

  static {
    TOMBSTONE_KEYS = List.of(FDO_RECORD_LICENSE.index(), PID.index(), PID_RECORD_ISSUE_DATE.index(),
        PID_ISSUER.index(), PID_ISSUER_NAME.index(), ISSUED_FOR_AGENT.index(),
        ISSUED_FOR_AGENT_NAME.index(), STRUCTURAL_TYPE.index(), HS_ADMIN.index());
  }

  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern(DATE_STRING)
      .withZone(ZoneId.of("UTC"));


  /* Handle Record Creation */
  public FdoRecord prepareNewHandleRecord(HandleRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = prepareHandleAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, HANDLE, timestamp));
    return new FdoRecord(handle, HANDLE, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedHandleRecord(HandleRequestAttributes request,
      FdoType fdoType, Instant timestamp, FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedHandleAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), fdoType, fdoAttributes, null);
  }

  public List<FdoAttribute> prepareHandleAttributes(HandleRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(
        new FdoAttribute(LOC, timestamp, setLocations(handle, HANDLE, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedHandleAttributes(HandleRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(prepareHandleAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }


  /* DOI Record Creation */
  public FdoRecord prepareNewDoiRecord(DoiKernelRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = prepareDoiAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.DOI, timestamp));
    return new FdoRecord(handle, FdoType.DOI, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedDoiRecord(DoiKernelRequestAttributes request, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDoiAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DOI, fdoAttributes, null);
  }

  public List<FdoAttribute> prepareDoiAttributes(DoiKernelRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, DOI, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
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
        request.getPrimaryReferentType(), PRIMARY_REFERENT_TYPE_VALUE));

    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedDoiAttributes(DoiKernelRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(prepareDoiAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Annotation Record Creation */
  public FdoRecord prepareNewAnnotationRecord(AnnotationRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = prepareAnnotationAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.ANNOTATION, timestamp));
    return new FdoRecord(handle, FdoType.ANNOTATION, fdoAttributes, request.getAnnotationHash());
  }

  public FdoRecord prepareUpdatedAnnotationRecord(AnnotationRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedAnnotationAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.ANNOTATION, fdoAttributes,
        request.getAnnotationHash());
  }

  public List<FdoAttribute> prepareAnnotationAttributes(AnnotationRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, ANNOTATION, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
    handleAttributeList.add(new FdoAttribute(TARGET_PID, timestamp, request.getTargetPid()));
    // 501 Target Type
    handleAttributeList.add(
        new FdoAttribute(TARGET_TYPE, timestamp, request.getTargetType()));
    // 502 Motivation
    handleAttributeList.add(new FdoAttribute(MOTIVATION, timestamp, request.getMotivation()));
    // 503 Annotation Hash
    handleAttributeList.add(
        new FdoAttribute(ANNOTATION_HASH, timestamp, request.getAnnotationHash()));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedAnnotationAttributes(AnnotationRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareAnnotationAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Data Mapping Record Creation */
  public FdoRecord prepareNewDataMappingRecord(DataMappingRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var fdoAttributes = prepareDataMappingAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.DATA_MAPPING, timestamp));
    return new FdoRecord(handle, FdoType.DATA_MAPPING, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedDataMappingRecord(DataMappingRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    var fdoAttributes = prepareUpdatedDataMappingAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DATA_MAPPING, fdoAttributes,
        null);
  }

  public List<FdoAttribute> prepareDataMappingAttributes(DataMappingRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, DATA_MAPPING, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
    // 700 Source Data Standard
    handleAttributeList.add(
        new FdoAttribute(SOURCE_DATA_STANDARD, timestamp, request.getSourceDataStandard()));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedDataMappingAttributes(
      DataMappingRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareDataMappingAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Digital Specimen Record Creation */
  public FdoRecord prepareNewDigitalSpecimenRecord(DigitalSpecimenRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    try {
      fdoAttributes = prepareDigitalSpecimenAttributes(request, handle, timestamp);
    } catch (JsonProcessingException e) {
      log.error(JSON_ERROR_MSG, e);
      throw new InvalidRequestException(JSON_ERROR_MSG);
    }
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.DIGITAL_SPECIMEN, timestamp));
    return new FdoRecord(handle, FdoType.DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId());
  }

  public FdoRecord prepareUpdatedDigitalSpecimenRecord(DigitalSpecimenRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    try {
      fdoAttributes = prepareUpdatedDigitalSpecimenAttributes(request,
          previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    } catch (JsonProcessingException e) {
      log.error(JSON_ERROR_MSG, e);
      throw new InvalidRequestException(JSON_ERROR_MSG);
    }
    return new FdoRecord(previousVersion.handle(), FdoType.DIGITAL_SPECIMEN, fdoAttributes,
        request.getNormalisedPrimarySpecimenObjectId());
  }

  public List<FdoAttribute> prepareDigitalSpecimenAttributes(
      DigitalSpecimenRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException, JsonProcessingException {
    idXorAbsence(request);
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, DIGITAL_SPECIMEN, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
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
        request.getPrimaryReferentType(), PRIMARY_REFERENT_TYPE_VALUE));
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
    if (!request.getOtherSpecimenIds().isEmpty()) {
      handleAttributeList.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp,
          mapper.writeValueAsString(request.getOtherSpecimenIds())));
    } else {
      handleAttributeList.add(new FdoAttribute(OTHER_SPECIMEN_IDS, timestamp,
          null));
    }
    // 208 Topic Origin
    handleAttributeList.add(new FdoAttribute(TOPIC_ORIGIN, timestamp, request.getTopicOrigin()));
    // 209 Topic Domain
    handleAttributeList.add(
        new FdoAttribute(TOPIC_DOMAIN, timestamp, request.getTopicDomain()));
    // 210 Topic Discipline
    handleAttributeList.add(new FdoAttribute(TOPIC_DISCIPLINE, timestamp,
        request.getTopicDiscipline()));
    // 211 Topic Category
    handleAttributeList.add(new FdoAttribute(TOPIC_CATEGORY, timestamp,
        request.getTopicCategory()));
    // 212 Living or Preserved
    handleAttributeList.add(new FdoAttribute(LIVING_OR_PRESERVED, timestamp,
        request.getLivingOrPreserved()));
    // 213 Base Type of Specimen
    handleAttributeList.add(new FdoAttribute(BASE_TYPE_OF_SPECIMEN, timestamp,
        request.getBaseTypeOfSpecimen()));
    // 214 Information Artefact Type
    handleAttributeList.add(new FdoAttribute(INFORMATION_ARTEFACT_TYPE, timestamp,
        request.getInformationArtefactType()));
    // 215 Material Sample Type
    handleAttributeList.add(new FdoAttribute(MATERIAL_SAMPLE_TYPE, timestamp,
        request.getMaterialSampleType()));
    // 216 Material or Digital Entity
    handleAttributeList.add(new FdoAttribute(MATERIAL_OR_DIGITAL_ENTITY, timestamp,
        request.getMaterialOrDigitalEntity()));
    // 217 Marked as Type
    handleAttributeList.add(new FdoAttribute(MARKED_AS_TYPE, timestamp, request.getMarkedAsType()));
    // 218 Derived From Entity
    handleAttributeList.add(
        new FdoAttribute(DERIVED_FROM_ENTITY, timestamp, request.getDerivedFromEntity()));
    // 219 Catalog ID
    handleAttributeList.add(
        new FdoAttribute(CATALOG_IDENTIFIER, timestamp, request.getCatalogIdentifier()));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedDigitalSpecimenAttributes(
      DigitalSpecimenRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException, JsonProcessingException {
    var updatedAttributes = new ArrayList<>(
        prepareDigitalSpecimenAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* MAS Record Creation */
  public FdoRecord prepareNewMasRecord(MasRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareMasAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, MAS, timestamp));
    return new FdoRecord(handle, MAS, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedMasRecord(MasRequestAttributes request, Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareUpdatedMasAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), MAS, fdoAttributes,
        null);
  }

  public List<FdoAttribute> prepareMasAttributes(MasRequestAttributes request, String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, MAS, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
    // 604: MAS
    handleAttributeList.add(new FdoAttribute(MAS_NAME, timestamp, request.getMasName()));
    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedMasAttributes(MasRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareMasAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Media Object Record Creation */
  public FdoRecord prepareNewDigitalMediaRecord(DigitalMediaRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareDigitalMediaAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.DIGITAL_MEDIA, timestamp));
    return new FdoRecord(handle, FdoType.DIGITAL_MEDIA, fdoAttributes, request.getPrimaryMediaId());
  }

  public FdoRecord prepareUpdatedDigitalMediaRecord(DigitalMediaRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareUpdatedDigitalMediaAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.DIGITAL_MEDIA, fdoAttributes,
        request.getPrimaryMediaId());
  }

  public List<FdoAttribute> prepareDigitalMediaAttributes(DigitalMediaRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    validateRightsholder(request);
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, DIGITAL_MEDIA, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
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
        request.getPrimaryReferentType(), PRIMARY_REFERENT_TYPE_VALUE));
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
        new FdoAttribute(LINKED_DO_TYPE, timestamp, request.getLinkedDigitalObjectType()));
    // 406 Linked Attribute
    handleAttributeList.add(
        new FdoAttribute(LINKED_ATTRIBUTE, timestamp, request.getLinkedAttribute()));
    // 407 Primary Media ID
    handleAttributeList.add(
        new FdoAttribute(PRIMARY_MEDIA_ID, timestamp, request.getPrimaryMediaId()));
    // 408 Primary Media Object Id Type
    handleAttributeList.add(
        new FdoAttribute(PRIMARY_MO_ID_TYPE, timestamp,
            request.getPrimaryMediaObjectIdType()));
    // 409 Primary Media Object Id Name
    handleAttributeList.add(
        new FdoAttribute(PRIMARY_MO_ID_NAME, timestamp,
            request.getPrimaryMediaObjectIdName()));
    // 410 dcterms:type
    handleAttributeList.add(
        new FdoAttribute(DCTERMS_TYPE, timestamp, request.getDctermsType()));
    // 411 dcterms:subject
    handleAttributeList.add(
        new FdoAttribute(DCTERMS_SUBJECT, timestamp, request.getDctermsSubject()));
    // 412 dcterms:format
    handleAttributeList.add(
        new FdoAttribute(DCTERMS_FORMAT, timestamp,
            request.getDctermsFormat()));
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
    handleAttributeList.add(new FdoAttribute(RIGHTSHOLDER_PID_TYPE, timestamp,
        request.getRightsholderPidType()));
    // 419 dcterms:conformsTo
    handleAttributeList.add(
        new FdoAttribute(DC_TERMS_CONFORMS, timestamp, request.getDctermsConformsTo()));

    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedDigitalMediaAttributes(
      DigitalMediaRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareDigitalMediaAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Organisation Record Creation */
  public FdoRecord prepareNewOrganisationRecord(OrganisationRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareOrganisationAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, FdoType.ORGANISATION, timestamp));
    return new FdoRecord(handle, FdoType.ORGANISATION, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedOrganisationRecord(OrganisationRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareUpdatedOrganisationAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), FdoType.ORGANISATION, fdoAttributes,
        null);
  }

  public List<FdoAttribute> prepareOrganisationAttributes(OrganisationRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    var locList = request.getLocations();
    locList.add(request.getOrganisationIdentifier());
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, ORGANISATION, locList)));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
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
        request.getPrimaryReferentType(), PRIMARY_REFERENT_TYPE_VALUE));
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

  private List<FdoAttribute> prepareUpdatedOrganisationAttributes(
      OrganisationRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareOrganisationAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Source System Record Creation */
  public FdoRecord prepareNewSourceSystemRecord(SourceSystemRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareSourceSystemAttributes(request, handle, timestamp);
    fdoAttributes.addAll(prepareGeneratedAttributes(handle, SOURCE_SYSTEM, timestamp));
    return new FdoRecord(handle, SOURCE_SYSTEM, fdoAttributes, null);
  }

  public FdoRecord prepareUpdatedSourceSystemRecord(SourceSystemRequestAttributes request,
      Instant timestamp,
      FdoRecord previousVersion, boolean incrementVersion)
      throws InvalidRequestException {
    List<FdoAttribute> fdoAttributes;
    fdoAttributes = prepareUpdatedSourceSystemAttributes(request,
        previousVersion.handle(), timestamp, previousVersion, incrementVersion);
    return new FdoRecord(previousVersion.handle(), SOURCE_SYSTEM, fdoAttributes,
        null);
  }

  public List<FdoAttribute> prepareSourceSystemAttributes(SourceSystemRequestAttributes request,
      String handle,
      Instant timestamp)
      throws InvalidRequestException {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 101: 10320/Loc
    handleAttributeList.add(new FdoAttribute(LOC, timestamp,
        setLocations(handle, SOURCE_SYSTEM, request.getLocations())));
    // 8: Issued For Agent
    handleAttributeList.add(
        new FdoAttribute(ISSUED_FOR_AGENT, timestamp, request.getIssuedForAgent()));
    // 9: Issued for Agent Name
    handleAttributeList.add(new FdoAttribute(ISSUED_FOR_AGENT_NAME, timestamp,
        getObjectName(request.getIssuedForAgent(), null)));
    // 12: Structural Type
    handleAttributeList.add(
        new FdoAttribute(STRUCTURAL_TYPE, timestamp, request.getStructuralType(),
            StructuralType.DIGITAL.toString()));
    handleAttributeList.add(
        new FdoAttribute(SOURCE_SYSTEM_NAME, timestamp, request.getSourceSystemName()));

    return handleAttributeList;
  }

  private List<FdoAttribute> prepareUpdatedSourceSystemAttributes(
      SourceSystemRequestAttributes request,
      String handle, Instant timestamp, FdoRecord previousVersion,
      boolean incrementVersion)
      throws InvalidRequestException {
    var updatedAttributes = new ArrayList<>(
        prepareSourceSystemAttributes(request, handle, timestamp));
    prepareUpdateAttributes(updatedAttributes, previousVersion.attributes(), timestamp,
        incrementVersion);
    return updatedAttributes;
  }

  /* Tombstone Record Creation */
  public FdoRecord prepareTombstoneRecord(TombstoneRequestAttributes request, Instant timestamp,
      FdoRecord previousVersion) throws JsonProcessingException {
    var fdoAttributes = prepareTombstoneAttributes(request, timestamp, previousVersion);
    return new FdoRecord(previousVersion.handle(), previousVersion.fdoType(), fdoAttributes, null);
  }

  private List<FdoAttribute> prepareTombstoneAttributes(TombstoneRequestAttributes request,
      Instant timestamp, FdoRecord previousVersion) throws JsonProcessingException {
    var handleAttributeList = new ArrayList<>(previousVersion.attributes());
    var previousIssueNum = getField(previousVersion.attributes(), PID_RECORD_ISSUE_NUMBER);
    var newIssueNum = incrementIssueNumber(previousIssueNum, timestamp);
    var previousStatus = getField(previousVersion.attributes(), PID_STATUS);
    var newStatus = new FdoAttribute(PID_STATUS, timestamp, PidStatus.TOMBSTONED);
    handleAttributeList.set(handleAttributeList.indexOf(previousIssueNum), newIssueNum);
    handleAttributeList.set(handleAttributeList.indexOf(previousStatus), newStatus);
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

  /* Validation Functions */

  private static void idXorAbsence(DigitalSpecimenRequestAttributes request)
      throws InvalidRequestException {
    if ((request.getPrimarySpecimenObjectId() == null) == (
        request.getSpecimenObjectIdAbsenceReason()
            == null)) {
      throw new InvalidRequestException(
          "Request must contain exactly one of: [primarySpecimenObjectId, primarySpecimenObjectIdAbsenceReason]");
    }
  }

  private static void validateRightsholder(DigitalMediaRequestAttributes request)
      throws InvalidRequestException {
    if (request.getRightsholderName() != null && request.getRightsholderPid() == null) {
      throw new InvalidRequestException(
          "Invalid media request. Rightsholder name provided without an identifier");
    }
  }

  /* Generalized attribute Building */

  private FdoAttribute incrementIssueNumber(FdoAttribute previousVersion, Instant timestamp) {
    var previousIssueNumber = previousVersion.getValue();
    var incrementedIssueNumber = String.valueOf(Integer.parseInt(previousIssueNumber) + 1);
    return new FdoAttribute(PID_RECORD_ISSUE_NUMBER, timestamp, incrementedIssueNumber);
  }

  private List<FdoAttribute> prepareGeneratedAttributes(String handle, FdoType fdoType,
      Instant timestamp) {
    var handleAttributeList = new ArrayList<FdoAttribute>();
    // 1: FDO Profile
    handleAttributeList.add(
        new FdoAttribute(FDO_PROFILE, timestamp, fdoType.getFdoProfile()));
    // 2: FDO Record License
    handleAttributeList.add(
        new FdoAttribute(FDO_RECORD_LICENSE, timestamp, PID_KERNEL_METADATA_LICENSE));
    // 3: Digital Object Type
    handleAttributeList.add(
        new FdoAttribute(DIGITAL_OBJECT_TYPE, timestamp, fdoType.getDigitalObjectType()));
    // 4: Digital ObjectName
    handleAttributeList.add(
        new FdoAttribute(DIGITAL_OBJECT_NAME, timestamp, fdoType.getDigitalObjectName()));
    // 5: PID
    handleAttributeList.add(new FdoAttribute(PID, timestamp, fdoType.getDomain() + handle));
    // 6: PID Issuer
    handleAttributeList.add(new FdoAttribute(PID_ISSUER, timestamp, DATACITE_ROR));
    // 7: PID Issuer Name
    handleAttributeList.add(new FdoAttribute(PID_ISSUER_NAME, timestamp, DATACITE_NAME));
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
    // 101 10320/Loc
    return handleAttributeList;
  }

  private void prepareUpdateAttributes(ArrayList<FdoAttribute> updatedAttributes,
      List<FdoAttribute> previousAttributes, Instant timestamp, boolean incrementVersion) {
    updatedAttributes.addAll(previousAttributes.stream()
        .filter(previousAttribute -> GENERATED_KEYS.contains(previousAttribute.getIndex()))
        .toList());
    var previousIssueNumber = getField(previousAttributes, PID_RECORD_ISSUE_NUMBER);
    if (incrementVersion) {
      updatedAttributes.add(incrementIssueNumber(previousIssueNumber, timestamp));
    } else {
      updatedAttributes.add(previousIssueNumber);
    }
  }

  /* Helper Functions */

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

  private static String getRor(String url) {
    return url.replace(ROR_DOMAIN, ROR_API_DOMAIN);
  }

  private String getDate(Instant timestamp) {
    return dt.format(timestamp);
  }

  private List<String> getURIs(String handle, FdoType fdoType, List<String> userLocations) {
    var locations = new ArrayList<String>();
    switch (fdoType) {
      case DIGITAL_SPECIMEN -> {
        locations.add(applicationProperties.getApiUrl() + "/specimens/" + handle);
        locations.add(applicationProperties.getUiUrl() + "/ds/" + handle);
      }
      case DATA_MAPPING ->
          locations.add(applicationProperties.getOrchestrationUrl() + "/mapping/" + handle);
      case SOURCE_SYSTEM ->
          locations.add(applicationProperties.getOrchestrationUrl() + "/source-system/" + handle);
      case DIGITAL_MEDIA -> {
        locations.add(applicationProperties.getApiUrl() + "/digitalMedia/" + handle);
        locations.add(applicationProperties.getUiUrl() + "/dm/" + handle);
      }
      case ANNOTATION ->
          locations.add(applicationProperties.getApiUrl() + "/annotations/" + handle);
      case MAS -> locations.add(applicationProperties.getOrchestrationUrl() + "/mas/" + handle);
      default -> {
        // Handle, DOI, Organisation (Org locations are all in userLocations)
      }
    }
    locations.addAll(userLocations);
    return locations;
  }

  private String documentToString(org.w3c.dom.Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  private String setLocations(String handle, FdoType fdoType, List<String> userLocations)
      throws InvalidRequestException {
    var objectLocations = getURIs(handle, fdoType, userLocations);
    if (objectLocations.isEmpty()) {
      return "<locations></locations>";
    }
    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new InvalidRequestException(e.getMessage());
    }
    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    for (int i = 0; i < objectLocations.size(); i++) {
      var locs = doc.createElement("location");
      locs.setAttribute("id", String.valueOf(i));
      locs.setAttribute("href", objectLocations.get(i));
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

}
