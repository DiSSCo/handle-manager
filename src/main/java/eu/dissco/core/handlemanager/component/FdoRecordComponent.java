package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.domain.FdoProfile.ACCESS_RESTRICTED;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ANNOTATION_TOPIC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.FdoProfile.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_OBJECT_IS_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LINKED_OBJECT_URL;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.FdoProfile.LOC;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MAS_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.MEDIA_URL;
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
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.REPLACE_OR_APPEND;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_DATA_STANDARD;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SOURCE_SYSTEM_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SPECIMEN_OBJECT_ID_ABSENCE_REASON;
import static eu.dissco.core.handlemanager.domain.FdoProfile.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SUBJECT_DIGITAL_OBJECT_IDS;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SUBJECT_LOCAL_ID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.SUBJECT_PID;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.FdoProfile.WAS_DERIVED_FROM_ENTITY;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;
import static eu.dissco.core.handlemanager.utils.AdminHandleGenerator.genAdminHandle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.FdoProfile;
import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.objects.AnnotationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MappingRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MasRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.OrganisationRequest;
import eu.dissco.core.handlemanager.domain.requests.objects.SourceSystemRequest;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import eu.dissco.core.handlemanager.repository.HandleRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@RequiredArgsConstructor
@Component
@Slf4j
public class FdoRecordComponent {

  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolverComponent pidResolver;
  private final ObjectMapper mapper;
  private final HandleRepository handleRep;
  private final ApplicationProperties appProperties;
  private static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
  private static final String PROXY_ERROR = "Invalid attribute: %s must contain proxy: %s";
  private static final byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
      StandardCharsets.UTF_8);
  private static final byte[] PLACEHOLDER = "Needs to be fixed!".getBytes(StandardCharsets.UTF_8);
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

  public List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle, ObjectType type)
      throws PidServiceInternalError, InvalidRequestException, PidResolutionException, UnprocessableEntityException {
    List<HandleAttribute> fdoRecord = new ArrayList<>();

    // 100: Admin Handle
    fdoRecord.add(
        new HandleAttribute(HS_ADMIN.index(), handle, HS_ADMIN.get(), genAdminHandle()));

    // 101: 10320/loc
    if (type != ObjectType.ORGANISATION) {
      byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8),
          type);
      fdoRecord.add(new HandleAttribute(LOC.index(), handle, LOC.get(), loc));
    }

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FDO_PROFILE.index(), handle, FDO_PROFILE.get(),
        request.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    fdoRecord.add(new HandleAttribute(FDO_RECORD_LICENSE.index(), handle,
        FDO_RECORD_LICENSE.get(), pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_TYPE.index(), handle, DIGITAL_OBJECT_TYPE.get(),
            request.getDigitalObjectType().getBytes(StandardCharsets.UTF_8)));

    // 4: DigitalObjectName - Handle
    checkHandle(request.getDigitalObjectType());
    var digitalObjectName = pidResolver.getObjectName(request.getDigitalObjectType())
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_NAME.index(), handle, DIGITAL_OBJECT_NAME.get(),
            digitalObjectName));

    // 5: Pid
    byte[] pid = (HANDLE_DOMAIN + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(PID.index(), handle, PID.get(), pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(PID_ISSUER.index(), handle, PID_ISSUER.get(),
        request.getPidIssuer().getBytes(StandardCharsets.UTF_8)));

    // 7: pidIssuerName
    String pidIssuerName = prepareRorOrHandle(request.getPidIssuer());
    fdoRecord.add(new HandleAttribute(PID_ISSUER_NAME.index(), handle, PID_ISSUER_NAME.get(),
        pidIssuerName.getBytes(StandardCharsets.UTF_8)));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(ISSUED_FOR_AGENT.index(), handle, ISSUED_FOR_AGENT.get(),
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName
    var agentNameRor = getRor(request.getIssuedForAgent());
    var issuedForAgentName = pidResolver.getObjectName(agentNameRor)
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(ISSUED_FOR_AGENT_NAME.index(), handle, ISSUED_FOR_AGENT_NAME.get(),
            issuedForAgentName));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_DATE.index(), handle,
        PID_RECORD_ISSUE_DATE.get(), getDate().getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_NUMBER.index(), handle,
        PID_RECORD_ISSUE_NUMBER.get(), "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(STRUCTURAL_TYPE.index(), handle,
        STRUCTURAL_TYPE.get(), request.getStructuralType().getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "TEST".getBytes(StandardCharsets.UTF_8)));

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
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(REFERENT_TYPE.index(), handle, REFERENT_TYPE.get(),
            request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(REFERENT_DOI_NAME.index(), handle, REFERENT_DOI_NAME.get(), handle));

    // 42: referentName
    if (request.getReferentName() != null) {
      fdoRecord.add(
          new HandleAttribute(REFERENT_NAME.index(), handle, REFERENT_NAME.get(),
              request.getReferentName().getBytes(StandardCharsets.UTF_8)));
    }
    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_REFERENT_TYPE.index(), handle, PRIMARY_REFERENT_TYPE.get(),
            request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMediaObjectAttributes(MediaObjectRequest request,
      byte[] handle, ObjectType type)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle, type);

    // 403 mediaUrl
    fdoRecord.add(new HandleAttribute(MEDIA_URL.index(), handle, MEDIA_URL.get(),
        request.getMediaUrl().getBytes(StandardCharsets.UTF_8)));

    // 404 Media Object Type
    fdoRecord.add(new HandleAttribute(MEDIA_OBJECT_TYPE.index(), handle, MEDIA_OBJECT_TYPE.get(),
        PLACEHOLDER));

    // 405 Media Object Type
    fdoRecord.add(new HandleAttribute(MEDIA_OBJECT_TYPE.index(), handle, MEDIA_OBJECT_TYPE.get(),
        PLACEHOLDER));

    // 406 Media Host
    fdoRecord.add(new HandleAttribute(MEDIA_HOST.index(), handle, MEDIA_HOST.get(), PLACEHOLDER));

    // 407 Subject Local Id
    fdoRecord.add(new HandleAttribute(SUBJECT_LOCAL_ID.index(), handle, SUBJECT_LOCAL_ID.get(),
        request.getSubjectIdentifier().physicalId().getBytes(StandardCharsets.UTF_8)));

    // 408 Subject PID -> might need to be resolved
    fdoRecord.add(new HandleAttribute(SUBJECT_PID.index(), handle, SUBJECT_PID.get(), PLACEHOLDER));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareAnnotationAttributes(AnnotationRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException, PidServiceInternalError {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 500 subjectDigitalObjectId
    resolveInternalPid(request);
    fdoRecord.add(new HandleAttribute(SUBJECT_DIGITAL_OBJECT_IDS.index(), handle,
        SUBJECT_DIGITAL_OBJECT_IDS.get(),
        request.getSubjectDigitalObjectId().getBytes(StandardCharsets.UTF_8)));

    // 501 AnnotationTopic
    fdoRecord.add(new HandleAttribute(ANNOTATION_TOPIC.index(), handle,
        ANNOTATION_TOPIC.get(), request.getAnnotationTopic().getBytes(StandardCharsets.UTF_8)));

    // 502 replaceOrAppend
    fdoRecord.add(new HandleAttribute(REPLACE_OR_APPEND.index(), handle, REPLACE_OR_APPEND.get(),
        request.getReplaceOrAppend().getState().getBytes(
            StandardCharsets.UTF_8)));

    // 503 AccessRestricted
    fdoRecord.add(new HandleAttribute(ACCESS_RESTRICTED.index(), handle, ACCESS_RESTRICTED.get(),
        String.valueOf(request.getAccessRestricted()).getBytes(
            StandardCharsets.UTF_8)));

    // 504 LinkedObjectUrl
    var linkedObjectUrl = request.getLinkedObjectUrl();
    if (linkedObjectUrl != null) {
      fdoRecord.add(new HandleAttribute(LINKED_OBJECT_URL.index(), handle, LINKED_OBJECT_URL.get(),
          linkedObjectUrl.getBytes(
              StandardCharsets.UTF_8)));
    }

    if (linkedObjectUrl != null) {
      fdoRecord.add(
          new HandleAttribute(LINKED_OBJECT_IS_PID.index(), handle, LINKED_OBJECT_IS_PID.get(),
              PLACEHOLDER));
    }

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMasRecordAttributes(MasRequest request, byte[] handle,
      ObjectType type)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    fdoRecord.add(new HandleAttribute(MAS_NAME.index(), handle, MAS_NAME.get(),
        String.valueOf(request.getMachineAnnotationServiceName()).getBytes(
            StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  private void resolveInternalPid(AnnotationRequest request) throws PidResolutionException {
    var resolvedSubject = handleRep.resolveHandleAttributes(
        request.getSubjectDigitalObjectId()
            .replace(HANDLE_DOMAIN, "")
            .getBytes(StandardCharsets.UTF_8));
    if (resolvedSubject.isEmpty()) {
      throw new PidResolutionException(
          "Invalid Subject Object ID. Unable to resolve " + request.getSubjectDigitalObjectId());
    }
  }

  public List<HandleAttribute> prepareSourceSystemAttributes(SourceSystemRequest request,
      byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException, PidServiceInternalError {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 600 sourceSystemName
    fdoRecord.add(new HandleAttribute(SOURCE_SYSTEM_NAME.index(), handle,
        SOURCE_SYSTEM_NAME.get(), request.getSourceSystemName().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareOrganisationAttributes(OrganisationRequest request,
      byte[] handle, ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException, PidServiceInternalError {
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
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID.index(), handle,
        ORGANISATION_ID.get(),
        request.getOrganisationIdentifier().getBytes(StandardCharsets.UTF_8)));

    // 602 OrganisationIdentifierType
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID_TYPE.index(), handle,
        ORGANISATION_ID_TYPE.get(),
        request.getOrganisationIdentifierType().getBytes(StandardCharsets.UTF_8)));

    // 603 OrganisationName
    var organisationName = pidResolver.getObjectName(getRor(request.getOrganisationIdentifier()))
        .getBytes(
            StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(ORGANISATION_NAME.index(), handle, ORGANISATION_NAME.get(),
        organisationName));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMappingAttributes(MappingRequest request, byte[] handle,
      ObjectType type)
      throws UnprocessableEntityException, PidResolutionException, InvalidRequestException, PidServiceInternalError {
    var fdoRecord = prepareHandleRecordAttributes(request, handle, type);

    // 700 Source Data Standard
    fdoRecord.add(new HandleAttribute(SOURCE_DATA_STANDARD.index(), handle,
        SOURCE_DATA_STANDARD.get(),
        request.getSourceDataStandard().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle, ObjectType type)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle, type);

    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST.index(), handle,
            SPECIMEN_HOST.get(),
            request.getSpecimenHost().getBytes(StandardCharsets.UTF_8)));

    // 201: Specimen Host name
    fdoRecord = setSpecimenHostName(request, fdoRecord, handle);

    // 202: primarySpecimenObjectId
    var primarySpecimenObjectId = setUniquePhysicalIdentifierId(request);
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID.index(), handle,
            PRIMARY_SPECIMEN_OBJECT_ID.get(),
            primarySpecimenObjectId.getBytes(StandardCharsets.UTF_8)));

    // 203: primarySpecimenObjectIdType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_TYPE.index(), handle,
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE.get(),
            request.getPrimarySpecimenObjectIdType().getBytes()));

    // 204-217 are optional

    // 204: primarySpecimenObjectIdName
    if (request.getPrimarySpecimenObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(PRIMARY_SPECIMEN_OBJECT_ID_NAME.index(), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_NAME.get(),
              request.getPrimarySpecimenObjectIdName().getBytes(StandardCharsets.UTF_8)));
    }

    // 205 normalisedSpecimenObjectId

    // 206: specimenObjectIdAbsenceReason
    if (request.getPrimarySpecimenObjectIdAbsenceReason() != null) {
      fdoRecord.add(
          new HandleAttribute(SPECIMEN_OBJECT_ID_ABSENCE_REASON.index(), handle,
              SPECIMEN_OBJECT_ID_ABSENCE_REASON.get(),
              request.getPrimarySpecimenObjectIdAbsenceReason().getBytes(StandardCharsets.UTF_8)));
    }

    // 207: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null) {
      var otherSpecimenIds = Arrays.toString(request.getOtherSpecimenIds())
          .getBytes(StandardCharsets.UTF_8);
      fdoRecord.add(
          new HandleAttribute(OTHER_SPECIMEN_IDS.index(), handle,
              OTHER_SPECIMEN_IDS.get(),
              otherSpecimenIds));
    }

    // 208: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_ORIGIN.index(), handle,
              TOPIC_ORIGIN.get(),
              request.getTopicOrigin().getBytes(StandardCharsets.UTF_8)));
    }

    // 209: topicDomain
    var topicDomain = request.getTopicDomain();
    if (topicDomain != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DOMAIN.index(), handle,
              TOPIC_DOMAIN.get(),
              topicDomain.getBytes(StandardCharsets.UTF_8)));
    }

    // 210: topicDiscipline
    var topicDisc = request.getTopicDiscipline();
    if (topicDisc != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DISCIPLINE.index(), handle,
              TOPIC_DISCIPLINE.get(),
              topicDisc.getBytes(StandardCharsets.UTF_8)));
    }

    // 211 topicCategory

    // 212: livingOrPreserved
    var livingOrPres = request.getLivingOrPreserved();
    if (livingOrPres != null) {
      fdoRecord.add(
          new HandleAttribute(LIVING_OR_PRESERVED.index(), handle,
              LIVING_OR_PRESERVED.get(),
              livingOrPres.getBytes()));
    }

    // 213 baseTypeOfSpecimen
    var baseType = request.getBaseTypeOfSpecimen();
    if (baseType != null) {
      fdoRecord.add(
          new HandleAttribute(BASE_TYPE_OF_SPECIMEN.index(), handle,
              BASE_TYPE_OF_SPECIMEN.get(),
              baseType.getBytes(StandardCharsets.UTF_8)));
    }

    // 214: informationArtefactType
    var artefactType = request.getInformationArtefactType();
    if (artefactType != null) {
      fdoRecord.add(
          new HandleAttribute(INFORMATION_ARTEFACT_TYPE.index(), handle,
              INFORMATION_ARTEFACT_TYPE.get(),
              artefactType.getBytes(StandardCharsets.UTF_8)));
    }

    // 215: materialSampleType
    var matSamp = request.getMaterialSampleType();
    if (matSamp != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_SAMPLE_TYPE.index(), handle,
              MATERIAL_SAMPLE_TYPE.get(),
              matSamp.getBytes(StandardCharsets.UTF_8)));
    }

    // 216: materialOrDigitalEntity
    fdoRecord.add(
        new HandleAttribute(MATERIAL_OR_DIGITAL_ENTITY.index(), handle,
            MATERIAL_OR_DIGITAL_ENTITY.get(),
            request.getMaterialOrDigitalEntity().getBytes()));

    // 217: markedAsType
    var markedAsType = request.getMarkedAsType();
    if (markedAsType != null) {
      fdoRecord.add(
          new HandleAttribute(MARKED_AS_TYPE.index(), handle,
              MARKED_AS_TYPE.get(),
              markedAsType.toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 218: wasDerivedFrom
    var wasDerivedFrom = request.getWasDerivedFrom();
    if (wasDerivedFrom != null) {
      fdoRecord.add(
          new HandleAttribute(WAS_DERIVED_FROM_ENTITY.index(), handle,
              WAS_DERIVED_FROM_ENTITY.get(),
              wasDerivedFrom.getBytes(StandardCharsets.UTF_8)));
    }

    return fdoRecord;
  }

  private List<HandleAttribute> setSpecimenHostName(DigitalSpecimenRequest request,
      List<HandleAttribute> fdoRecord, byte[] handle) {
    var specimenHostName = request.getSpecimenHostName();
    if (specimenHostName != null) {
      fdoRecord.add(
          new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle,
              SPECIMEN_HOST_NAME.get(),
              specimenHostName.getBytes(StandardCharsets.UTF_8)));
    } else {
      try {
        var specimenHostNameResolved = pidResolver.getObjectName(getRor(request.getSpecimenHost()));
        fdoRecord.add(
            new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle,
                SPECIMEN_HOST_NAME.get(),
                specimenHostNameResolved.getBytes(StandardCharsets.UTF_8)));

      } catch (Exception e) {
        log.error(
            "SpecimenHostId is not a resolvable ROR and no SpecimenHostName is provided in the request. SpecimenHostName field left blank. More information: "
                + e.getMessage());
      }
    }
    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request, byte[] handle, ObjectType type)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    return prepareDigitalSpecimenRecordAttributes(request, handle, type);
  }

  public List<HandleAttribute> prepareUpdateAttributes(byte[] handle, JsonNode requestAttributes,
      ObjectType type)
      throws InvalidRequestException, PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
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
      throws InvalidRequestException, PidServiceInternalError, UnprocessableEntityException, PidResolutionException {
    var tombstoneAttributes = new ArrayList<>(
        prepareUpdateAttributes(handle, requestAttributes, ObjectType.TOMBSTONE));
    tombstoneAttributes.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(genLandingPage(handle));
    return tombstoneAttributes;
  }

  private HandleAttribute genLandingPage(byte[] handle) throws PidServiceInternalError {
    var landingPage = new String[]{"Placeholder landing page"};
    var data = setLocations(landingPage, new String(handle, StandardCharsets.UTF_8),
        ObjectType.TOMBSTONE);
    return new HandleAttribute(LOC.index(), handle, LOC.get(), data);
  }

  private JsonNode setLocationXmlFromJson(JsonNode request, String handle, ObjectType type)
      throws InvalidRequestException, PidServiceInternalError {
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
    } catch (IOException e) {
      throw new InvalidRequestException(
          "An error has occurred parsing \"locations\" array. " + e.getMessage());
    }
    return requestObjectNode;
  }

  private String getDate() {
    return dt.format(Instant.now());
  }

  public byte[] setLocations(String[] userLocations, String handle, ObjectType type)
      throws PidServiceInternalError {

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new PidServiceInternalError(e.getMessage(), e);
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
      throw new PidServiceInternalError("An internal error has occurred parsing location data", e);
    }
  }

  private String[] concatLocations(String[] userLocations, String handle, ObjectType type) {
    ArrayList<String> objectLocations = new ArrayList<>(List.of(defaultLocations(handle, type)));
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
        return new String[]{api, ui};
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
        return new String[]{api, ui};
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
