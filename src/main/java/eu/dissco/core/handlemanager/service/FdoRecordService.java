package eu.dissco.core.handlemanager.service;


import static eu.dissco.core.handlemanager.domain.FdoProfile.ANNOTATION_HASH;
import static eu.dissco.core.handlemanager.domain.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.CATALOG_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DC_TERMS_CONFORMS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.IS_DERIVED_FROM_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LICENSE_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LICENSE_URL;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_ATTRIBUTE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_DO_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_DO_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_FORMAT;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_MIME_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MOTIVATION;
import static eu.dissco.core.handlemanager.domain.FdoProfile.NORMALISED_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ORGANISATION_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ORGANISATION_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ORGANISATION_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_MEDIA_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_MO_ID_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_MO_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_MO_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.RIGHTSHOLDER_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.RIGHTSHOLDER_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.RIGHTSHOLDER_PID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_OBJECT_ID_ABSENCE_REASON;
import static eu.dissco.core.handlemanager.domain.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TARGET_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TARGET_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_CATEGORY;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.WAS_DERIVED_FROM_ENTITY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.Profiles;
import eu.dissco.core.handlemanager.domain.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MappingRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MasRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.specimen.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.PidRepository;
import eu.dissco.core.handlemanager.web.PidResolver;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@RequiredArgsConstructor
@Service
@Slf4j
public class FdoRecordService {

  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolver pidResolver;
  private final ObjectMapper mapper;
  private final PidRepository pidRepository;
  private final ApplicationProperties appProperties;
  private final Environment environment;
  private static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  private static final String DOI_DOMAIN = "https://doi.org/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
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

  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
      .withZone(ZoneId.of("UTC"));

  public HandleAttribute genHsAdmin(byte[] handle) {
    return new HandleAttribute(HS_ADMIN.index(), handle, HS_ADMIN.get(), ADMIN_HEX);
  }

  public List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle, ObjectType type)
      throws InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    List<HandleAttribute> fdoRecord = new ArrayList<>();

    // 100: Admin Handle
    fdoRecord.add(genHsAdmin(handle));

    // 101: 10320/loc
    if (type != ObjectType.ORGANISATION) {
      byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8),
          type);
      fdoRecord.add(new HandleAttribute(LOC.index(), handle, LOC.get(), loc));
    }

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FDO_PROFILE, handle, request.getFdoProfile()));

    // 2: FDO Record License
    fdoRecord.add(new HandleAttribute(FDO_RECORD_LICENSE, handle, PID_KERNEL_METADATA_LICENSE));

    // 3: DigitalObjectType
    fdoRecord.add(new HandleAttribute(DIGITAL_OBJECT_TYPE, handle, request.getDigitalObjectType()));

    // 4: DigitalObjectName
    checkHandle(request.getDigitalObjectType());
    var digitalObjectName = pidResolver.getObjectName(request.getDigitalObjectType());
    fdoRecord.add(new HandleAttribute(DIGITAL_OBJECT_NAME, handle, digitalObjectName));

    // 5: Pid
    var pid = HANDLE_DOMAIN + new String(handle, StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(PID, handle, pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(PID_ISSUER, handle, request.getPidIssuer()));

    // 7: pidIssuerName
    String pidIssuerName = prepareRorOrHandle(request.getPidIssuer());
    fdoRecord.add(new HandleAttribute(PID_ISSUER_NAME, handle, pidIssuerName));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(ISSUED_FOR_AGENT, handle, request.getIssuedForAgent()));

    // 9: issuedForAgentName
    var agentNameRor = getRor(request.getIssuedForAgent());
    var issuedForAgentName = pidResolver.getObjectName(agentNameRor);
    fdoRecord.add(
        new HandleAttribute(ISSUED_FOR_AGENT_NAME, handle, issuedForAgentName));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_DATE, handle, getDate()));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_NUMBER, handle, "1"));

    // 12: structuralType
    fdoRecord.add(
        new HandleAttribute(STRUCTURAL_TYPE, handle, request.getStructuralType().toString()));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(PID_STATUS, handle, "TEST"));

    return fdoRecord;
  }

  private String prepareRorOrHandle(String url)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException {
    if (url.contains(ROR_DOMAIN)) {
      return pidResolver.getObjectName(getRor(url));
    } else if (url.contains(HANDLE_DOMAIN)) {
      return pidResolver.getObjectName(url);
    }
    throw new InvalidRequestException(
        String.format(PROXY_ERROR, url, (ROR_DOMAIN + " or " + HANDLE_DOMAIN)));
  }

  private static String getRor(String url) throws InvalidRequestException {
    if (!url.contains(ROR_DOMAIN)) {
      throw new InvalidRequestException(String.format(PROXY_ERROR, url, ROR_DOMAIN));
    }
    return url.replace(ROR_DOMAIN, ROR_API_DOMAIN);
  }

  private static void checkHandle(String url) throws InvalidRequestException {
    if (!url.contains(HANDLE_DOMAIN)) {
      throw new InvalidRequestException(String.format(PROXY_ERROR, url, HANDLE_DOMAIN));
    }
  }

  public List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(REFERENT_TYPE, handle, request.getReferentType()));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(REFERENT_DOI_NAME.index(), handle, REFERENT_DOI_NAME.get(), handle));

    // 42: referentName
    if (request.getReferentName() != null) {
      fdoRecord.add(new HandleAttribute(REFERENT_NAME, handle, request.getReferentName()));
    }
    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_REFERENT_TYPE, handle, request.getPrimaryReferentType()));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMediaObjectAttributes(MediaObjectRequest request,
      byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle, type);

    fdoRecord.add(new HandleAttribute(MEDIA_HOST, handle, request.getMediaHost()));
    var mediaHostName = setHostName(request.getMediaHostName(), request.getMediaHost(), handle,
        MEDIA_HOST_NAME);
    if (mediaHostName != null) {
      fdoRecord.add(mediaHostName);
    }
    if (request.getMediaFormat() != null) {
      fdoRecord.add(new HandleAttribute(MEDIA_FORMAT, handle, request.getMediaFormat().toString()));
    }
    fdoRecord.add(new HandleAttribute(IS_DERIVED_FROM_SPECIMEN, handle,
        request.getIsDerivedFromSpecimen().toString()));
    fdoRecord.add(
        new HandleAttribute(LINKED_DO_PID, handle, request.getLinkedDigitalObjectPid()));
    fdoRecord.add(
        new HandleAttribute(LINKED_DO_TYPE, handle, request.getLinkedDigitalObjectType()
            .toString()));
    if (request.getLinkedAttribute() != null) {
      fdoRecord.add(
          new HandleAttribute(LINKED_ATTRIBUTE, handle, request.getLinkedAttribute()));
    }
    fdoRecord.add(
        new HandleAttribute(PRIMARY_MEDIA_ID, handle, request.getPrimaryMediaId()));

    if (request.getPrimaryMediaObjectIdType() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MO_TYPE, handle,
              request.getPrimaryMediaObjectIdType().toString()));
    }
    if (request.getPrimaryMediaObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MO_ID_NAME, handle, request.getPrimaryMediaObjectIdName()));
    }
    if (request.getPrimaryMediaObjectIdType() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_MO_ID_TYPE, handle,
              request.getPrimaryMediaObjectIdType().toString()));
    }
    if (request.getMediaMimeType() != null) {
      fdoRecord.add(
          new HandleAttribute(MEDIA_MIME_TYPE, handle, request.getMediaMimeType()));
    }
    if (request.getDerivedFromEntity() != null) {
      fdoRecord.add(
          new HandleAttribute(DERIVED_FROM_ENTITY, handle, request.getDerivedFromEntity()));
    }
    if (request.getLicenseName() != null) {
      fdoRecord.add(new HandleAttribute(LICENSE_NAME, handle, request.getLicenseName()));
    }
    if (request.getLicense() != null) {
      fdoRecord.add(new HandleAttribute(LICENSE_URL, handle, request.getLicense()));
    }
    var rightsholderName = setHostName(request.getRightsholderName(), request.getRightsholderPid(),
        handle, RIGHTSHOLDER_NAME);
    if (rightsholderName != null) {
      fdoRecord.add(rightsholderName);
    }
    if (request.getRightsholderPid() != null) {
      fdoRecord.add(
          new HandleAttribute(RIGHTSHOLDER_PID, handle, request.getRightsholderPid()));
    }
    if (request.getRightsholderPidType() != null) {
      fdoRecord.add(
          new HandleAttribute(RIGHTSHOLDER_PID_TYPE, handle,
              request.getRightsholderPidType().toString()));
    }
    if (request.getDctermsConforms() != null) {
      fdoRecord.add(
          new HandleAttribute(DC_TERMS_CONFORMS, handle, request.getDctermsConforms()));
    }
    return fdoRecord;
  }

  public List<HandleAttribute> prepareAnnotationAttributes(AnnotationRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 500 TargetPid
    fdoRecord.add(new HandleAttribute(TARGET_PID, handle, request.getTargetPid()));

    // 501 TargetType
    fdoRecord.add(new HandleAttribute(TARGET_TYPE, handle, request.getTargetType()));

    // 502 motivation
    fdoRecord.add(
        new HandleAttribute(MOTIVATION, handle, request.getMotivation().toString()));

    // 503 AnnotationHash
    if (request.getAnnotationHash() != null) {
      fdoRecord.add(
          new HandleAttribute(ANNOTATION_HASH, handle, request.getAnnotationHash().toString()));
    }

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMasRecordAttributes(MasRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    fdoRecord.add(new HandleAttribute(MAS_NAME, handle, request.getMachineAnnotationServiceName()));

    return fdoRecord;
  }


  public List<HandleAttribute> prepareSourceSystemAttributes(SourceSystemRequest request,
      byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 600 sourceSystemName
    fdoRecord.add(new HandleAttribute(SOURCE_SYSTEM_NAME, handle, request.getSourceSystemName()));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareOrganisationAttributes(OrganisationRequest request,
      byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle, type);

    //101 10320/loc -> must contain ROR
    var objectLocations = new ArrayList<>(List.of(request.getOrganisationIdentifier()));
    if (request.getLocations() != null) {
      objectLocations.addAll(List.of(request.getLocations()));
    }
    byte[] loc = setLocations(objectLocations.toArray(new String[0]),
        new String(handle, StandardCharsets.UTF_8), ObjectType.ORGANISATION);
    fdoRecord.add(new HandleAttribute(LOC.index(), handle, LOC.get(), loc));

    // 601 OrganisationIdentifier
    fdoRecord.add(
        new HandleAttribute(ORGANISATION_ID, handle, request.getOrganisationIdentifier()));

    // 602 OrganisationIdentifierType
    fdoRecord.add(
        new HandleAttribute(ORGANISATION_ID_TYPE, handle, request.getOrganisationIdentifierType()));

    // 603 OrganisationName
    var organisationName = pidResolver.getObjectName(getRor(request.getOrganisationIdentifier()));
    fdoRecord.add(new HandleAttribute(ORGANISATION_NAME, handle, organisationName));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMappingAttributes(MappingRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 700 Source Data Standard
    fdoRecord.add(
        new HandleAttribute(SOURCE_DATA_STANDARD, handle, request.getSourceDataStandard()));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle, type);

    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST, handle, request.getSpecimenHost()));

    // 201: Specimen Host name
    var specimenHostName = setHostName(request.getSpecimenHostName(), request.getSpecimenHost(),
        handle, SPECIMEN_HOST_NAME);
    fdoRecord.add(specimenHostName);

    // 202: primarySpecimenObjectId
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID, handle,
            request.getPrimarySpecimenObjectId()));

    // 203: primarySpecimenObjectIdType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE, handle,
            request.getPrimarySpecimenObjectIdType().toString()));

    // 204-217 are optional

    // 204: primarySpecimenObjectIdName
    if (request.getPrimarySpecimenObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME, handle,
              request.getPrimarySpecimenObjectIdName()));
    }

    // 205 normalisedSpecimenObjectId
    fdoRecord.add(
        new HandleAttribute(NORMALISED_SPECIMEN_OBJECT_ID, handle,
            request.getNormalisedPrimarySpecimenObjectId()));

    // 206: specimenObjectIdAbsenceReason
    if (request.getPrimarySpecimenObjectIdAbsenceReason() != null) {
      fdoRecord.add(
          new HandleAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON, handle,
              request.getPrimarySpecimenObjectIdAbsenceReason()));
    }

    // 207: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null) {
      var otherSpecimenIds = request.getOtherSpecimenIds().toString();
      fdoRecord.add(new HandleAttribute(OTHER_SPECIMEN_IDS, handle, otherSpecimenIds));
    }

    // 208: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(new HandleAttribute(TOPIC_ORIGIN, handle, request.getTopicOrigin().toString()));
    }

    // 209: topicDomain
    var topicDomain = request.getTopicDomain();
    if (topicDomain != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DOMAIN, handle, topicDomain.toString()));
    }

    // 210: topicDiscipline
    var topicDisc = request.getTopicDiscipline();
    if (topicDisc != null) {
      fdoRecord.add(new HandleAttribute(TOPIC_DISCIPLINE, handle, topicDisc.toString()));
    }

    // 211 topicCategory
    var topicCategory = request.getTopicCategory();
    if (topicCategory != null) {
      fdoRecord.add(new HandleAttribute(TOPIC_CATEGORY, handle, topicCategory.toString()));
    }

    // 212: livingOrPreserved
    var livingOrPres = request.getLivingOrPreserved();
    if (livingOrPres != null) {
      fdoRecord.add(new HandleAttribute(LIVING_OR_PRESERVED, handle, livingOrPres.toString()));
    }

    // 213 baseTypeOfSpecimen
    var baseType = request.getBaseTypeOfSpecimen();
    if (baseType != null) {
      fdoRecord.add(new HandleAttribute(BASE_TYPE_OF_SPECIMEN, handle, baseType.toString()));
    }

    // 214: informationArtefactType
    var artType = request.getInformationArtefactType();
    if (artType != null) {
      fdoRecord.add(new HandleAttribute(INFORMATION_ARTEFACT_TYPE, handle, artType.toString()));
    }

    // 215: materialSampleType
    var matSamp = request.getMaterialSampleType();
    if (matSamp != null) {
      fdoRecord.add(new HandleAttribute(MATERIAL_SAMPLE_TYPE, handle, matSamp.toString()));
    }

    // 216: materialOrDigitalEntity
    if (request.getMaterialOrDigitalEntity() != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_OR_DIGITAL_ENTITY, handle,
              request.getMaterialOrDigitalEntity().toString()));
    }

    // 217: markedAsType
    var markedAsType = request.getMarkedAsType();
    if (markedAsType != null) {
      fdoRecord.add(
          new HandleAttribute(MARKED_AS_TYPE, handle,
              markedAsType.toString()));
    }

    // 218: wasDerivedFromEntity
    var wasDerivedFrom = request.getDerivedFromEntity();
    if (wasDerivedFrom != null) {
      fdoRecord.add(new HandleAttribute(WAS_DERIVED_FROM_ENTITY, handle, wasDerivedFrom));
    }

    // 219 catalogId
    var catId = request.getCatalogIdentifier();
    if (catId != null) {
      fdoRecord.add(new HandleAttribute(CATALOG_IDENTIFIER, handle, catId));
    }

    return fdoRecord;
  }

  private HandleAttribute setHostName(String hostName, String hostId, byte[] handle,
      FdoProfile targetAttribute) throws PidResolutionException {
    if (hostName != null) {
      return new HandleAttribute(targetAttribute, handle, hostName);
    } else {
      String hostNameResolved;
      try {
        if (isAcceptedSpecimenHostId(hostId)) {
          if (hostId.contains(ROR_DOMAIN)) {
            hostNameResolved = pidResolver.getObjectName(getRor(hostId));
          } else {
            hostNameResolved = pidResolver.resolveQid(WIKIDATA_API + hostId);
          }
          return new HandleAttribute(targetAttribute, handle, hostNameResolved);
        } else {
          throw new PidResolutionException("Specimen host ID {} is neither QID nor ROR.");
        }
      } catch (UnprocessableEntityException | InvalidRequestException |
               PidResolutionException e) {
        log.error(
            "AgentId is not a resolvable ROR and no Name is provided in the request for the field {}",
            targetAttribute.get(), e);
        throw new PidResolutionException("Unable to resolve Host Id" + e.getMessage());
      }
    }
  }

  private boolean isAcceptedSpecimenHostId(String id) {
    return (id.contains(ROR_DOMAIN) || id.toLowerCase().charAt(0) == 'q');
  }

  public List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode requestAttributes,
      ObjectType type)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException {
    requestAttributes = setLocationXmlFromJson(requestAttributes,
        new String(handle, StandardCharsets.UTF_8), type);
    Map<String, String> updateRequestMap = mapper.convertValue(requestAttributes,
        new TypeReference<Map<String, String>>() {
        });

    var updatedAttributeList = new ArrayList<>(updateRequestMap.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .map(entry -> new HandleAttribute(FdoProfile.retrieveIndex(entry.getKey()), handle,
            entry.getKey(),
            entry.getValue().getBytes(StandardCharsets.UTF_8)))
        .toList());
    updatedAttributeList.addAll(addResolvedNames(updateRequestMap, handle));
    return updatedAttributeList;
  }

  private List<HandleAttribute> addResolvedNames(Map<String, String> updateRequestMap,
      byte[] handle)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var resolvableKeys = updateRequestMap.entrySet()
        .stream()
        .filter(entry -> RESOLVABLE_KEYS.containsKey(entry.getKey())
            && !hasResolvedPairInRequest(updateRequestMap, entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    if (resolvableKeys.isEmpty()) {
      return new ArrayList<>();
    }
    ArrayList<HandleAttribute> resolvedPidNameAttributes = new ArrayList<>();
    for (var resolvableKey : resolvableKeys.entrySet()) {
      var targetAttribute = RESOLVABLE_KEYS.get(resolvableKey.getKey());

      var resolvedPid = prepareRorOrHandle(resolvableKey.getValue());
      resolvedPidNameAttributes.add(new HandleAttribute(FdoProfile.retrieveIndex(targetAttribute),
          handle, targetAttribute, resolvedPid.getBytes(StandardCharsets.UTF_8)));
    }
    return resolvedPidNameAttributes;
  }

  private boolean hasResolvedPairInRequest(Map<String, String> updateRequestMap,
      String pidToResolve) {
    var targetName = RESOLVABLE_KEYS.get(pidToResolve);
    return updateRequestMap.containsKey(targetName);
  }

  public List<HandleAttribute> prepareTombstoneAttributes(byte[] handle, JsonNode requestAttributes)
      throws InvalidRequestException, UnprocessableEntityException, PidResolutionException {
    var tombstoneAttributes = new ArrayList<>(
        prepareUpdateAttributes(handle, requestAttributes, ObjectType.TOMBSTONE));
    tombstoneAttributes.add(new HandleAttribute(PID_STATUS, handle, "ARCHIVED"));
    tombstoneAttributes.add(genLandingPage(handle));
    return tombstoneAttributes;
  }

  private HandleAttribute genLandingPage(byte[] handle)
      throws UnprocessableEntityException {
    var landingPage = new String[]{"Placeholder landing page"};
    var data = setLocations(landingPage, new String(handle, StandardCharsets.UTF_8),
        ObjectType.TOMBSTONE);
    return new HandleAttribute(LOC.index(), handle, LOC.get(), data);
  }

  private JsonNode setLocationXmlFromJson(JsonNode request, String handle, ObjectType type)
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
          new String(setLocations(locArr, handle, type), StandardCharsets.UTF_8));
      requestObjectNode.remove(LOC_REQUEST);
    } catch (IOException | UnprocessableEntityException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing \"locations\" array. " + e.getMessage());
    }
    return requestObjectNode;
  }

  private String getDate() {
    return dt.format(Instant.now());
  }

  public byte[] setLocations(String[] userLocations, String handle, ObjectType type)
      throws UnprocessableEntityException {

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new UnprocessableEntityException(e.getMessage());
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQUEST);
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, handle, type);

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
      throw new UnprocessableEntityException(
          "An internal error has occurred parsing location data");
    }
  }

  private String[] concatLocations(String[] userLocations, String handle, ObjectType type) {
    ArrayList<String> objectLocations = new ArrayList<>();

    if (!environment.matchesProfiles(Profiles.DOI)) {
      objectLocations.addAll(List.of(defaultLocations(handle, type)));
    }
    if (userLocations != null) {
      objectLocations.addAll(List.of(userLocations));
    }
    return objectLocations.toArray(new String[0]);
  }

  private String[] defaultLocations(String handle, ObjectType type) {
    switch (type) {
      case DIGITAL_SPECIMEN -> {
        String api = appProperties.getApiUrl() + "/specimens/" + handle;
        String ui = appProperties.getUiUrl() + "/ds/" + handle;
        return new String[]{ui, api};
      }
      case MAPPING -> {
        return new String[]{appProperties.getOrchestrationUrl() + "/mapping/" + handle};
      }
      case SOURCE_SYSTEM -> {
        return new String[]{appProperties.getOrchestrationUrl() + "/source-system/" + handle};
      }
      case MEDIA_OBJECT -> {
        String api = appProperties.getApiUrl() + "/digitalMedia/" + handle;
        String ui = appProperties.getUiUrl() + "/dm/" + handle;
        return new String[]{ui, api};
      }
      case ANNOTATION -> {
        return new String[]{appProperties.getApiUrl() + "/annotations/" + handle};
      }
      case MAS -> {
        return new String[]{appProperties.getOrchestrationUrl() + "/mas/" + handle};
      }
      default -> {
        // Handle, DOI, Organisation (organisation handled separately)
        return new String[]{};
      }
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
