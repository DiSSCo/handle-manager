package eu.dissco.core.handlemanager.component;

import static eu.dissco.core.handlemanager.domain.PidRecords.BASE_TYPE_OF_SPECIMEN;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.DIGITAL_OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_PROFILE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FDO_RECORD_LICENSE;
import static eu.dissco.core.handlemanager.domain.PidRecords.FIELD_IDX;
import static eu.dissco.core.handlemanager.domain.PidRecords.HS_ADMIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.INFORMATION_ARTEFACT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.ISSUED_FOR_AGENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.LIVING_OR_PRESERVED;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC;
import static eu.dissco.core.handlemanager.domain.PidRecords.LOC_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.MARKED_AS_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.MATERIAL_OR_DIGITAL_ENTITY;
import static eu.dissco.core.handlemanager.domain.PidRecords.MATERIAL_SAMPLE_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_HASH;
import static eu.dissco.core.handlemanager.domain.PidRecords.MEDIA_URL;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.OBJECT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.OTHER_SPECIMEN_IDS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_ISSUER_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_DATE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_RECORD_ISSUE_NUMBER;
import static eu.dissco.core.handlemanager.domain.PidRecords.PID_STATUS;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.PRIMARY_SPECIMEN_OBJECT_ID_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_DOI_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.REFERENT_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_NAME;
import static eu.dissco.core.handlemanager.domain.PidRecords.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_SPECIMEN_HOST;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.WAS_DERIVED_FROM;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;
import static eu.dissco.core.handlemanager.utils.AdminHandleGenerator.genAdminHandle;

import eu.dissco.core.handlemanager.domain.repsitoryobjects.HandleAttribute;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import eu.dissco.core.handlemanager.exceptions.PidResolutionException;
import eu.dissco.core.handlemanager.exceptions.PidServiceInternalError;
import eu.dissco.core.handlemanager.exceptions.UnprocessableEntityException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@RequiredArgsConstructor
@Component
@Slf4j
public class FdoRecordBuilder {

  private final TransformerFactory tf;
  private final DocumentBuilderFactory dbf;
  private final PidResolverComponent pidResolver;
  private static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  private static final String ROR_API_DOMAIN = "https://api.ror.org/organizations/";
  private static final String ROR_DOMAIN = "https://ror.org/";
  private static final String PROXY_ERROR = "Invalid attribute: %s must contain proxy: %s";
  private static final byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
      StandardCharsets.UTF_8);
  private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .withZone(ZoneId.of("UTC"));

  public List<HandleAttribute> prepareHandleRecordAttributes(HandleRecordRequest request,
      byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    List<HandleAttribute> fdoRecord = new ArrayList<>();

    // 100: Admin Handle
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(HS_ADMIN), handle, HS_ADMIN, genAdminHandle()));

    // 101: 10320/loc
    byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8));
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_PROFILE), handle, FDO_PROFILE,
        request.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_RECORD_LICENSE), handle,
        FDO_RECORD_LICENSE, pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            request.getDigitalObjectType().getBytes(StandardCharsets.UTF_8)));

    // 4: DigitalObjectName - Handle
    checkHandle(request.getDigitalObjectType());
    var digitalObjectName = pidResolver.getObjectName(request.getDigitalObjectType())
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_NAME), handle, DIGITAL_OBJECT_NAME,
            digitalObjectName));

    // 5: Pid
    byte[] pid = (HANDLE_DOMAIN + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER,
        request.getPidIssuer().getBytes(StandardCharsets.UTF_8)));

    // 7: pidIssuerName (ROR or Handle)
    String pidIssuerName = prepareRorOrHandle(request.getPidIssuer());
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER_NAME), handle, PID_ISSUER_NAME,
        pidIssuerName.getBytes(StandardCharsets.UTF_8)));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT), handle, ISSUED_FOR_AGENT,
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName - ROR
    var agentNameRor = getRor(request.getIssuedForAgent());
    var issuedForAgentName = pidResolver.getObjectName(agentNameRor)
        .getBytes(StandardCharsets.UTF_8);
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT_NAME), handle, ISSUED_FOR_AGENT_NAME,
            issuedForAgentName));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_DATE), handle,
        PID_RECORD_ISSUE_DATE, getDate().getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_NUMBER), handle,
        PID_RECORD_ISSUE_NUMBER, "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(STRUCTURAL_TYPE), handle,
        STRUCTURAL_TYPE, request.getStructuralType().getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
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

  public List<HandleAttribute> prepareDoiRecordAttributes(DoiRecordRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareHandleRecordAttributes(request, handle);

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_TYPE), handle, REFERENT_TYPE,
            request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME, handle));

    // 42: referentName
    if (request.getReferentName() != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(REFERENT_NAME), handle, REFERENT_NAME,
              request.getReferentName().getBytes(StandardCharsets.UTF_8)));
    }
    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_REFERENT_TYPE), handle, PRIMARY_REFERENT_TYPE,
            request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    // 44: referent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        request.getReferent().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public List<HandleAttribute> prepareMediaObjectAttributes(MediaObjectRequest request,
      byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle);

    // 14 Media Hash
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_HASH), handle, MEDIA_HASH,
        request.getMediaHash().getBytes(StandardCharsets.UTF_8)));

    // 15 Subject Specimen Host
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(SUBJECT_SPECIMEN_HOST), handle, SUBJECT_SPECIMEN_HOST,
            request.getSubjectSpecimenHostPid().getBytes(StandardCharsets.UTF_8)));

    // 16 Media Url
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_URL), handle, MEDIA_URL,
        request.getMediaUrl().getBytes(StandardCharsets.UTF_8)));

    // 17 : Subject Physical Identifier
    // Encoding here is UTF-8
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(SUBJECT_PHYSICAL_IDENTIFIER), handle,
        SUBJECT_PHYSICAL_IDENTIFIER, setUniquePhysicalIdentifierId(request)));
    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenRecordAttributes(
      DigitalSpecimenRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    var fdoRecord = prepareDoiRecordAttributes(request, handle);

    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle,
            SPECIMEN_HOST,
            request.getSpecimenHost().getBytes(StandardCharsets.UTF_8)));

    // 201: Specimen Host name
    if (request.getSpecimenHostName() != null) {
      var specimenHostNameRequest = request.getSpecimenHostName();
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST_NAME), handle,
              SPECIMEN_HOST_NAME,
              specimenHostNameRequest.getBytes(StandardCharsets.UTF_8)));
    } else {
      try {
        var specimenHostNameResolved = pidResolver.getObjectName(getRor(request.getSpecimenHost()));
        fdoRecord.add(
            new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST_NAME), handle,
                SPECIMEN_HOST_NAME,
                specimenHostNameResolved.getBytes(StandardCharsets.UTF_8)));

      } catch (InvalidRequestException e) {
        log.info("SpecimenHostId is not a resolvable ROR and no SpecimenHostName is provided in the request. SpecimenHostName field left blank.");
      }
    }

    // 202: primarySpecimenObjectId
    var primarySpecimenObjectId = setUniquePhysicalIdentifierId(request);
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID), handle,
            PRIMARY_SPECIMEN_OBJECT_ID,
            primarySpecimenObjectId));

    // 203: primarySpecimenObjectIdType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID_TYPE), handle,
            PRIMARY_SPECIMEN_OBJECT_ID_TYPE,
            request.getPrimarySpecimenObjectIdType().getBytes()));

    // 204-217 are optional

    // 204: primarySpecimenObjectIdName
    if (request.getPrimarySpecimenObjectIdName() != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID_NAME), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_NAME,
              request.getPrimarySpecimenObjectIdName().getBytes(StandardCharsets.UTF_8)));
    }

    // 205: specimenObjectIdAbsenceReason
    if (request.getPrimarySpecimenObjectIdAbsenceReason() != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE,
              request.getPrimarySpecimenObjectIdAbsenceReason().getBytes(StandardCharsets.UTF_8)));
    }

    // 206: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null) {
      var otherSpecimenIds = Arrays.toString(request.getOtherSpecimenIds())
          .getBytes(StandardCharsets.UTF_8);
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(OTHER_SPECIMEN_IDS), handle,
              OTHER_SPECIMEN_IDS,
              otherSpecimenIds));
    }

    // 207: topicOrigin
    if (request.getTopicOrigin() != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_ORIGIN), handle,
              TOPIC_ORIGIN,
              request.getTopicOrigin().getBytes(StandardCharsets.UTF_8)));
    }

    // 208: topicDomain
    var topicDomain = request.getTopicDomain();
    if (topicDomain != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_DOMAIN), handle,
              TOPIC_DOMAIN,
              topicDomain.getBytes(StandardCharsets.UTF_8)));
    }

    // 209: topicDiscipline
    var topicDisc = request.getTopicDiscipline();
    if (topicDisc != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_DISCIPLINE), handle,
              TOPIC_DISCIPLINE,
              topicDisc.getBytes(StandardCharsets.UTF_8)));
    }

    // 210: objectType
    var objType = request.getObjectType();
    if (objType != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle,
              OBJECT_TYPE,
              objType.getBytes(StandardCharsets.UTF_8)));
    }

    // 211: livingOrPreserved
    var livingOrPres = request.getLivingOrPreserved();
    if (livingOrPres != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(LIVING_OR_PRESERVED), handle,
              LIVING_OR_PRESERVED,
              livingOrPres.getBytes()));
    }

    // 212: baseTypeOfSpecimen
    var baseType = request.getBaseTypeOfSpecimen();
    if (baseType != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(BASE_TYPE_OF_SPECIMEN), handle,
              BASE_TYPE_OF_SPECIMEN,
              baseType.getBytes(StandardCharsets.UTF_8)));
    }

    // 213: informationArtefactType
    var artefactType = request.getInformationArtefactType();
    if (artefactType != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(INFORMATION_ARTEFACT_TYPE), handle,
              INFORMATION_ARTEFACT_TYPE,
              artefactType.getBytes(StandardCharsets.UTF_8)));
    }

    // 214: materialSampleType
    var matSamp = request.getMaterialSampleType();
    if (matSamp != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(MATERIAL_SAMPLE_TYPE), handle,
              MATERIAL_SAMPLE_TYPE,
              matSamp.getBytes(StandardCharsets.UTF_8)));
    }

    // 215: materialOrDigitalEntity
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(MATERIAL_OR_DIGITAL_ENTITY), handle,
            MATERIAL_OR_DIGITAL_ENTITY,
            request.getMaterialOrDigitalEntity().getBytes()));

    // 216: markedAsType
    var markedAsType = request.getMarkedAsType();
    if (markedAsType != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(MARKED_AS_TYPE), handle,
              MARKED_AS_TYPE,
              markedAsType.getBytes(StandardCharsets.UTF_8)));
    }

    // 217: wasDerivedFrom
    var wasDerivedFrom = request.getWasDerivedFrom();
    if (wasDerivedFrom != null) {
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(WAS_DERIVED_FROM), handle,
              WAS_DERIVED_FROM,
              wasDerivedFrom.getBytes(StandardCharsets.UTF_8)));
    }

    return fdoRecord;
  }

  public List<HandleAttribute> prepareDigitalSpecimenBotanyRecordAttributes(
      DigitalSpecimenBotanyRequest request, byte[] handle)
      throws PidServiceInternalError, UnprocessableEntityException, PidResolutionException, InvalidRequestException {
    return prepareDigitalSpecimenRecordAttributes(request, handle);
  }

  private String getDate() {
    return dt.format(Instant.now());
  }

  public byte[] setLocations(String[] userLocations, String handle) throws PidServiceInternalError {

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new PidServiceInternalError(e.getMessage(), e);
    }

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement(LOC_REQ);
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, handle);

    for (int i = 0; i < objectLocations.length; i++) {
      var locs = doc.createElement("location");
      locs.setAttribute(NODE_ID, String.valueOf(i));
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

  private String[] concatLocations(String[] userLocations, String handle) {
    ArrayList<String> objectLocations = new ArrayList<>(List.of(defaultLocations(handle)));
    if (userLocations != null) {
      objectLocations.addAll(List.of(userLocations));
    }
    return objectLocations.toArray(new String[0]);
  }

  private String[] defaultLocations(String handle) {
    String api = "https://sandbox.dissco.tech/api/v1/specimens/" + handle;
    String ui = "https://sandbox.dissco.tech/ds/" + handle;
    return new String[]{api, ui};
  }

  private String documentToString(Document document) throws TransformerException {
    var transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }
}
