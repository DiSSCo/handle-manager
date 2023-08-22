package eu.dissco.core.handlemanager.testUtils;

import static eu.dissco.core.handlemanager.domain.FdoProfile.*;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_DATA;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_ID;
import static eu.dissco.core.handlemanager.domain.JsonApiFields.NODE_TYPE;
import static eu.dissco.core.handlemanager.service.ServiceUtils.setUniquePhysicalIdentifierId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiDataLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiLinks;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperRead;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperReadSingle;
import eu.dissco.core.handlemanager.domain.jsonapi.JsonApiWrapperWrite;
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
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ObjectType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.PhysicalIdentifier;
import eu.dissco.core.handlemanager.domain.requests.vocabulary.ReplaceOrAppend;
import eu.dissco.core.handlemanager.domain.requests.objects.TombstoneRecordRequest;
import eu.dissco.core.handlemanager.exceptions.InvalidRequestException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

@Slf4j
public class TestUtils {

  public static final Instant CREATED = Instant.parse("2022-11-01T09:59:24.00Z");
  public static final String ISSUE_DATE_TESTVAL = "2022-11-01";
  public final static String HANDLE_URI = "https://hdl.handle.net/";
  public static final String HANDLE = "20.5000.1025/QRS-321-ABC";
  public static final String PREFIX = "20.5000.1025";
  public static final String SUFFIX = "QRS-321-ABC";
  public static final String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";
  public static final List<String> HANDLE_LIST_STR;

  // Record types
  public static final String RECORD_TYPE_HANDLE = "handle";
  public static final String RECORD_TYPE_DOI = "doi";
  public static final String RECORD_TYPE_DS = "digitalSpecimen";
  public static final String RECORD_TYPE_TOMBSTONE = "tombstone";
  public static final String RECORD_TYPE_MEDIA = "mediaObject";
  public static final String RECORD_TYPE_SOURCE_SYSTEM = "sourceSystem";
  public static final String RECORD_TYPE_MAPPING = "mapping";
  public static final String RECORD_TYPE_ANNOTATION = "annotation";
  public static final String RECORD_TYPE_ORGANISATION = "organisation";
  public static final String RECORD_TYPE_MAS = "machineAnnotationService";

  // Request Test Vals
  // Handles
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String ROR_DOMAIN = "https://ror.org/";
  public static final String FDO_PROFILE_TESTVAL = HANDLE_DOMAIN + "21.T11148/d8de0819e144e4096645";
  public static final String ISSUED_FOR_AGENT_TESTVAL = ROR_DOMAIN + "0566bfb96";
  public static final String DIGITAL_OBJECT_TYPE_TESTVAL =
      HANDLE_DOMAIN + "21.T11148/1c699a5d1b4ad3ba4956";
  public static final String PID_ISSUER_TESTVAL_OTHER = HANDLE_DOMAIN + "20.5000.1025/PID-ISSUER";
  public static final String STRUCTURAL_TYPE_TESTVAL = "digital";
  public static final String[] LOC_TESTVAL = {"https://sandbox.dissco.tech/", "https://dissco.eu"};
  public static final String[] LOC_ALT_TESTVAL = {"naturalis.nl"};

  // DOI Request Attributes
  public static final String REFERENT_NAME_TESTVAL = "Bird nest";
  public static final String PRIMARY_REFERENT_TYPE_TESTVAL = "materialSample";


  // Generated Attributes
  public static final String PID_STATUS_TESTVAL = "TEST";
  public static final String REFERENT_DOI_NAME_TESTVAL = "10.20500/" + SUFFIX;
  //DOIs

  //Digital Specimens
  public static final String ROR_IDENTIFIER = "0x123";
  public static final String SPECIMEN_HOST_TESTVAL = ROR_DOMAIN + ROR_IDENTIFIER;
  public static final String SPECIMEN_HOST_NAME_TESTVAL = "Naturalis";
  // Annotations
  public static final String SUBJECT_DOI_TESTVAL = HANDLE_URI + "20.5000.1025/111";
  public static final String ANNOTATION_TOPIC_TESTVAL = "note";
  public static final String LINKED_URL_TESTVAL = "https://";


  // Media Objects
  public static final String MEDIA_URL_TESTVAL = "https://naturalis.nl/media/123";
  public static final String MEDIA_HASH_TESTVAL = "47bce5c74f589f48";
  public static final String MEDIA_HASH_ALG_TESTVAL = "SHA256";
  // Mappings
  public static final String SOURCE_DATA_STANDARD_TESTVAL = "dwc";
  // MAS
  public static final String MAS_NAME_TESTVAL = "Plant Organ detection";

  public static final String API_URL = "https://sandbox.dissco.tech/api/v1";
  public static final String UI_URL = "https://sandbox.dissco.tech/";
  public static final String ORCHESTRATION_URL = "https://orchestration.dissco.tech/api/v1";
  public static final String PTR_TYPE_DOI = "doi";
  public final static String PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL = "BOTANICAL.QRS.123";
  public final static String NORMALISED_PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL = PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL + ":" + ROR_IDENTIFIER;
  public final static PhysicalIdentifier PHYSICAL_IDENTIFIER_TESTVAL_CETAF = new PhysicalIdentifier(
      PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
      PhysicalIdType.CETAF
  );
  public final static String EXTERNAL_PID = "21.T11148/d8de0819e144e4096645";
  public static final String DIGITAL_OBJECT_NAME_TESTVAL = "digitalSpecimen";

  // Tombstone Record vals
  public final static String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Pid Type Record vals
  public static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

  static {
    HANDLE_LIST_STR = List.of(HANDLE, HANDLE_ALT);
  }

  public static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
  public static DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  // Single Handle Attribute Lists

  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle) throws Exception{
    return genHandleRecordAttributes(handle, ObjectType.HANDLE);
  }
  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle, ObjectType type) throws Exception {

    List<HandleAttribute> fdoRecord = new ArrayList<>();
    var request = givenHandleRecordRequestObject();
    byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8),
        type);
    var a = new String(loc, StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(LOC.index(), handle, LOC.get(), loc));

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FDO_PROFILE.index(), handle, FDO_PROFILE.get(),
        request.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FDO_RECORD_LICENSE.index(), handle,
        FDO_RECORD_LICENSE.get(), pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_TYPE.index(), handle, DIGITAL_OBJECT_TYPE.get(),
            request.getDigitalObjectType().getBytes(StandardCharsets.UTF_8)));

    // 4: DigitalObjectName
    fdoRecord.add(
        new HandleAttribute(DIGITAL_OBJECT_NAME.index(), handle, DIGITAL_OBJECT_NAME.get(),
            DIGITAL_OBJECT_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 5: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(PID.index(), handle, PID.get(), pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(PID_ISSUER.index(), handle, PID_ISSUER.get(),
        request.getPidIssuer().getBytes(StandardCharsets.UTF_8)));

    // 7: pidIssuerName
    fdoRecord.add(new HandleAttribute(PID_ISSUER_NAME.index(), handle, PID_ISSUER_NAME.get(),
        PID_ISSUER_TESTVAL_OTHER.getBytes(StandardCharsets.UTF_8)));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(ISSUED_FOR_AGENT.index(), handle, ISSUED_FOR_AGENT.get(),
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName
    fdoRecord.add(
        new HandleAttribute(ISSUED_FOR_AGENT_NAME.index(), handle, ISSUED_FOR_AGENT_NAME.get(),
            ISSUED_FOR_AGENT_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_DATE.index(), handle,
        PID_RECORD_ISSUE_DATE.get(), ISSUE_DATE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(PID_RECORD_ISSUE_NUMBER.index(), handle,
        PID_RECORD_ISSUE_NUMBER.get(), "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(STRUCTURAL_TYPE.index(), handle,
        STRUCTURAL_TYPE.get(), STRUCTURAL_TYPE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "TEST".getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genHandleRecordAttributesAltLoc(byte[] handle)
      throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle, ObjectType.HANDLE);

    byte[] locOriginal = setLocations(LOC_TESTVAL, new String(handle, StandardCharsets.UTF_8), ObjectType.HANDLE);
    var locOriginalAttr = new HandleAttribute(LOC.index(), handle, LOC.get(), locOriginal);

    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8), ObjectType.HANDLE);
    var locAltAttr = new HandleAttribute(LOC.index(), handle, LOC.get(), locAlt);

    attributes.set(attributes.indexOf(locOriginalAttr), locAltAttr);

    return attributes;
  }

  public static List<HandleAttribute> genTombstoneRecordFullAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle, ObjectType.HANDLE);
    HandleAttribute oldPidStatus = new HandleAttribute(PID_STATUS.index(), handle,
        PID_STATUS.get(), PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8));
    attributes.addAll(genHandleRecordAttributes(handle, ObjectType.TOMBSTONE));
    attributes.remove(oldPidStatus);
    attributes = new ArrayList<>((attributes.stream().filter(row -> row.index()!=LOC.index())).toList());
    attributes.add(givenLandingPageAttribute(handle));
    return attributes;
  }

  public static List<HandleAttribute> genUpdateRecordAttributesAltLoc(byte[] handle)
      throws ParserConfigurationException, TransformerException {
    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8), ObjectType.HANDLE);
    return List.of(new HandleAttribute(LOC.index(), handle, LOC.get(), locAlt));
  }

  public static List<HandleAttribute> genTombstoneRecordRequestAttributes(byte[] handle) throws Exception{
    List<HandleAttribute> tombstoneAttributes = new ArrayList<>();
    tombstoneAttributes.add(
        new HandleAttribute(TOMBSTONE_TEXT.index(), handle, TOMBSTONE_TEXT.get(),
            TOMBSTONE_TEXT_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(new HandleAttribute(PID_STATUS.index(), handle, PID_STATUS.get(),
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(givenLandingPageAttribute(handle));
    return tombstoneAttributes;
  }

  public static List<HandleAttribute> genDoiRecordAttributes(byte[] handle, ObjectType type) throws Exception {
    List<HandleAttribute> fdoRecord = genHandleRecordAttributes(handle, type);
    var request = givenDoiRecordRequestObject();

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(REFERENT_TYPE.index(), handle, REFERENT_TYPE.get(),
            request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(REFERENT_DOI_NAME.index(), handle, REFERENT_DOI_NAME.get(),
            REFERENT_DOI_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 42: referentName
    fdoRecord.add(
        new HandleAttribute(REFERENT_NAME.index(), handle, REFERENT_NAME.get(),
            request.getReferentName().getBytes(StandardCharsets.UTF_8)));

    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(PRIMARY_REFERENT_TYPE.index(), handle, PRIMARY_REFERENT_TYPE.get(),
            request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle, DigitalSpecimenRequest request) throws Exception{
    List<HandleAttribute> fdoRecord = genDoiRecordAttributes(handle, ObjectType.DIGITAL_SPECIMEN);
    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST.index(), handle,
            SPECIMEN_HOST.get(),
            request.getSpecimenHost().getBytes(StandardCharsets.UTF_8)));

    // 201: Specimen Host name
    fdoRecord.add(
        new HandleAttribute(SPECIMEN_HOST_NAME.index(), handle,
            SPECIMEN_HOST_NAME.get(),
            SPECIMEN_HOST_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

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

    // 205: normalisedSpecimenObjectId

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
    if (request.getTopicDomain() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DOMAIN.index(), handle,
              TOPIC_DOMAIN.get(),
              request.getTopicDomain().getBytes(StandardCharsets.UTF_8)));
    }

    // 210: topicDiscipline
    if (request.getTopicDiscipline() != null) {
      fdoRecord.add(
          new HandleAttribute(TOPIC_DISCIPLINE.index(), handle,
              TOPIC_DISCIPLINE.get(),
              request.getTopicDiscipline().getBytes(StandardCharsets.UTF_8)));
    }

    // 211: topicCategory

    // 212: livingOrPreserved
    if (request.getLivingOrPreserved() != null) {
      fdoRecord.add(
          new HandleAttribute(LIVING_OR_PRESERVED.index(), handle,
              LIVING_OR_PRESERVED.get(),
              request.getLivingOrPreserved().getBytes()));
    }

    // 213: baseTypeOfSpecimen
    if (request.getBaseTypeOfSpecimen() != null) {
      fdoRecord.add(
          new HandleAttribute(BASE_TYPE_OF_SPECIMEN.index(), handle,
              BASE_TYPE_OF_SPECIMEN.get(),
              request.getBaseTypeOfSpecimen().getBytes(StandardCharsets.UTF_8)));
    }

    // 214: informationArtefactType
    if (request.getInformationArtefactType() != null) {
      fdoRecord.add(
          new HandleAttribute(INFORMATION_ARTEFACT_TYPE.index(), handle,
              INFORMATION_ARTEFACT_TYPE.get(),
              request.getInformationArtefactType().getBytes(StandardCharsets.UTF_8)));
    }

    // 215: materialSampleType
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_SAMPLE_TYPE.index(), handle,
              MATERIAL_SAMPLE_TYPE.get(),
              request.getMaterialSampleType().getBytes(StandardCharsets.UTF_8)));
    }

    // 216: materialOrDigitalEntity
    if (request.getMaterialSampleType() != null) {
      fdoRecord.add(
          new HandleAttribute(MATERIAL_OR_DIGITAL_ENTITY.index(), handle,
              MATERIAL_OR_DIGITAL_ENTITY.get(),
              request.getMaterialSampleType().getBytes()));
    }

    // 217: markedAsType
    if (request.getMarkedAsType() != null) {
      fdoRecord.add(
          new HandleAttribute(MARKED_AS_TYPE.index(), handle,
              MARKED_AS_TYPE.get(),
              request.getMarkedAsType().toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 218: wasDerivedFromEntity
    if (request.getWasDerivedFrom() != null) {
      fdoRecord.add(
          new HandleAttribute(WAS_DERIVED_FROM_ENTITY.index(), handle,
              WAS_DERIVED_FROM_ENTITY.get(),
              request.getWasDerivedFrom().getBytes(StandardCharsets.UTF_8)));
    }

    return fdoRecord;

  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle)
      throws Exception {
    var request = givenDigitalSpecimenRequestObjectNullOptionals();
    return genDigitalSpecimenAttributes(handle, request);
  }

  public static List<HandleAttribute> genMediaObjectAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle, ObjectType.MEDIA_OBJECT);

    byte[] PLACEHOLDER = "Needs to be fixed!".getBytes(StandardCharsets.UTF_8);

    // 403 Media URL
    handleRecord.add(new HandleAttribute(MEDIA_URL.index(), handle,
        MEDIA_URL.get(), MEDIA_URL_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 405 Media Object Type
    handleRecord.add(new HandleAttribute(MEDIA_OBJECT_TYPE.index(), handle, MEDIA_OBJECT_TYPE.get(),
        PLACEHOLDER));

    // 406 Media Host
    handleRecord.add(
        new HandleAttribute(MEDIA_HOST.index(), handle, MEDIA_HOST.get(), PLACEHOLDER));

    // 407 Subject Local Id
    handleRecord.add(new HandleAttribute(SUBJECT_LOCAL_ID.index(), handle, SUBJECT_LOCAL_ID.get(),
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 408 Subject PID -> might need to be resolved
    handleRecord.add(
        new HandleAttribute(SUBJECT_PID.index(), handle, SUBJECT_PID.get(), PLACEHOLDER));

    return handleRecord;
  }

  public static List<HandleAttribute> genAnnotationAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, ObjectType.ANNOTATION);

    // 500 subjectDigitalObjectId
    fdoRecord.add(new HandleAttribute(SUBJECT_DIGITAL_OBJECT_ID.index(), handle,
        SUBJECT_DIGITAL_OBJECT_ID.get(),
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 501 AnnotationTopic
    fdoRecord.add(new HandleAttribute(ANNOTATION_TOPIC.index(), handle,
        ANNOTATION_TOPIC.get(), ANNOTATION_TOPIC_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 502 replaceOrAppend
    fdoRecord.add(new HandleAttribute(REPLACE_OR_APPEND.index(), handle, REPLACE_OR_APPEND.get(),
        ReplaceOrAppend.REPLACE.toString().getBytes(StandardCharsets.UTF_8)));

    // 503 AccessRestricted
    fdoRecord.add(new HandleAttribute(ACCESS_RESTRICTED.index(), handle, ACCESS_RESTRICTED.get(),
        String.valueOf(false).getBytes(StandardCharsets.UTF_8)));

    // 504 LinkedObjectUrl
      fdoRecord.add(new HandleAttribute(LINKED_OBJECT_URL.index(), handle, LINKED_OBJECT_URL.get(),
          LINKED_URL_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genMasAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, ObjectType.MAS);

    fdoRecord.add(new HandleAttribute(MAS_NAME.index(), handle, MAS_NAME.get(),
        (MAS_NAME_TESTVAL).getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genMappingAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, ObjectType.MAPPING);

    // 500 subjectDigitalObjectId
    fdoRecord.add(new HandleAttribute(SOURCE_DATA_STANDARD.index(), handle,
        SOURCE_DATA_STANDARD.get(), SOURCE_DATA_STANDARD_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genSourceSystemAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genHandleRecordAttributes(handle, ObjectType.SOURCE_SYSTEM);

    // 600 hostInstitution
    fdoRecord.add(new HandleAttribute(SOURCE_SYSTEM_NAME.index(), handle,
        SOURCE_SYSTEM_NAME.get(), SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genOrganisationAttributes(byte[] handle)
      throws Exception {
    var fdoRecord = genDoiRecordAttributes(handle, ObjectType.ORGANISATION);

    // 800 OrganisationIdentifier
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID.index(), handle,
        ORGANISATION_ID.get(), SPECIMEN_HOST_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 801 OrganisationIdentifier
    fdoRecord.add(new HandleAttribute(ORGANISATION_ID_TYPE.index(), handle,
        ORGANISATION_ID_TYPE.get(), PTR_TYPE_DOI.getBytes(StandardCharsets.UTF_8)));

    // 802 OrganisationName
    fdoRecord.add(new HandleAttribute(ORGANISATION_NAME.index(), handle, ORGANISATION_NAME.get(),
        SPECIMEN_HOST_NAME_TESTVAL.getBytes(
            StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static <T extends HandleRecordRequest> ObjectNode genCreateRecordRequest(T request,
      String recordType) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    ObjectNode rootNode = mapper.createObjectNode();
    ObjectNode dataNode = mapper.createObjectNode();
    ObjectNode attributeNode = mapper.valueToTree(request);

    if (attributeNode.has("referent")) {
      attributeNode.remove("referent");
    }

    dataNode.put("type", recordType);
    dataNode.set("attributes", attributeNode);
    rootNode.set("data", dataNode);

    return rootNode;
  }

  // Single Requests
  public static HandleRecordRequest givenHandleRecordRequestObject() {
    return new HandleRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL
    );
  }

  public static DoiRecordRequest givenDoiRecordRequestObject() {
    return new DoiRecordRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL
    );
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals(){
    return givenDigitalSpecimenRequestObjectNullOptionals(PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL);
  }

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals(String physicalId) {
    try {
      return new DigitalSpecimenRequest(
          FDO_PROFILE_TESTVAL,
          ISSUED_FOR_AGENT_TESTVAL,
          DIGITAL_OBJECT_TYPE_TESTVAL,
          PID_ISSUER_TESTVAL_OTHER,
          STRUCTURAL_TYPE_TESTVAL,
          LOC_TESTVAL,
          REFERENT_NAME_TESTVAL,
          PRIMARY_REFERENT_TYPE_TESTVAL,
          SPECIMEN_HOST_TESTVAL,
          SPECIMEN_HOST_NAME_TESTVAL,
          physicalId,
          null,null, null, null, null, null, null, null, null, null, null,null, null, null, null
          );
    } catch (InvalidRequestException e) {
      return null;
    }
  }

  public static MediaObjectRequest givenMediaRequestObject() {
    return new MediaObjectRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        MEDIA_URL_TESTVAL,
        PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL
    );
  }

  public static AnnotationRequest givenAnnotationRequestObject() {
    return new AnnotationRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        SUBJECT_DOI_TESTVAL,
        ANNOTATION_TOPIC_TESTVAL,
        ReplaceOrAppend.REPLACE,
        false,
        LINKED_URL_TESTVAL
    );
  }

  public static MappingRequest givenMappingRequestObject() {
    return new MappingRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        SOURCE_DATA_STANDARD_TESTVAL
    );
  }

  public static SourceSystemRequest givenSourceSystemRequestObject() {
    return new SourceSystemRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        SPECIMEN_HOST_TESTVAL
    );
  }

  public static OrganisationRequest givenOrganisationRequestObject() {
    return new OrganisationRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        PTR_TYPE_DOI
    );
  }

  public static MasRequest givenMasRecordRequestObject() {
    return new MasRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        MAS_NAME_TESTVAL
    );
  }


  public static TombstoneRecordRequest genTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(
        TOMBSTONE_TEXT_TESTVAL
    );
  }

  public static ObjectNode givenSearchByPhysIdRequest() {
    var request = MAPPER.createObjectNode();
    var dataNode = MAPPER.createObjectNode();
    var attributeNode = MAPPER.createObjectNode();
    attributeNode.set(PRIMARY_SPECIMEN_OBJECT_ID.get(), MAPPER.valueToTree(
        PHYSICAL_IDENTIFIER_TESTVAL_CETAF));
    attributeNode.put(SPECIMEN_HOST.get(), SPECIMEN_HOST_TESTVAL);
    dataNode.set("attributes", attributeNode);
    request.set("data", dataNode);
    return request;
  }

  public static JsonApiWrapperRead givenRecordResponseRead(List<byte[]> handles, String path,
      String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);
      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }

    var responseLink = new JsonApiLinks(path);
    return new JsonApiWrapperRead(responseLink, dataNodes);
  }

  public static JsonApiWrapperReadSingle givenRecordResponseReadSingle(String handle, String path,
      String type, JsonNode attributes) {
    return new JsonApiWrapperReadSingle(
        new JsonApiLinks(path),
        new JsonApiDataLinks(handle, type, attributes,
            new JsonApiLinks("https://hdl.handle.net/" + handle)));
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteGeneric(List<byte[]> handles,
      String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(recordType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), "PID",
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWrite(List<byte[]> handles,
      String attributeType, String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genAttributes(attributeType, handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
          recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles)
      throws Exception {
    return givenRecordResponseWriteAltLoc(handles, RECORD_TYPE_HANDLE);
  }

  public static JsonApiWrapperWrite givenRecordResponseWriteAltLoc(List<byte[]> handles,
      String recordType)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genHandleRecordAttributesAltLoc(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), recordType,
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }


  public static JsonApiWrapperWrite givenRecordResponseWriteArchive(List<byte[]> handles)
      throws Exception {
    List<JsonApiDataLinks> dataNodes = new ArrayList<>();

    for (byte[] handle : handles) {
      var testDbRecord = genTombstoneRecordFullAttributes(handle);
      JsonNode recordAttributes = genObjectNodeAttributeRecord(testDbRecord);

      var pidLink = new JsonApiLinks(HANDLE_URI + new String(handle, StandardCharsets.UTF_8));
      dataNodes.add(
          new JsonApiDataLinks(new String(handle, StandardCharsets.UTF_8), RECORD_TYPE_TOMBSTONE,
              recordAttributes, pidLink));
    }
    return new JsonApiWrapperWrite(dataNodes);
  }

  public static List<HandleAttribute> genAttributes(String recordType, byte[] handle)
      throws Exception {
    switch (recordType) {
      case RECORD_TYPE_DOI -> {
        return genDoiRecordAttributes(handle, ObjectType.fromString(recordType));
      }
      case RECORD_TYPE_DS -> {
        return genDigitalSpecimenAttributes(handle);
      }
      case RECORD_TYPE_MEDIA -> {
        return genMediaObjectAttributes(handle);
      }
      case RECORD_TYPE_ANNOTATION -> {
        return genAnnotationAttributes(handle);
      }
      case RECORD_TYPE_MAPPING -> {
        return genMappingAttributes(handle);
      }
      case RECORD_TYPE_SOURCE_SYSTEM -> {
        return genSourceSystemAttributes(handle);
      }
      case RECORD_TYPE_ORGANISATION -> {
        return genOrganisationAttributes(handle);
      }
      case RECORD_TYPE_MAS -> {
        return genMasAttributes(handle);
      }
      default -> {
        log.warn("Default type");
        return genHandleRecordAttributes(handle, ObjectType.HANDLE);
      }
    }
  }

  public static List<JsonNode> genUpdateRequestBatch(List<byte[]> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (byte[] handle : handles) {
      requestNodeData.put("type", RECORD_TYPE_HANDLE);
      requestNodeData.put("id", new String(handle, StandardCharsets.UTF_8));
      requestNodeData.set("attributes", genUpdateRequestAltLoc());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }
    return requestNodeList;
  }

  public static List<JsonNode> genTombstoneRequestBatch(List<String> handles) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode requestNodeRoot = mapper.createObjectNode();
    ObjectNode requestNodeData = mapper.createObjectNode();
    List<JsonNode> requestNodeList = new ArrayList<>();

    for (String handle : handles) {
      requestNodeData.put(NODE_TYPE, RECORD_TYPE_HANDLE);
      requestNodeData.put(NODE_ID, handle);
      requestNodeData.set(NODE_ATTRIBUTES, genTombstoneRequest());
      requestNodeRoot.set(NODE_DATA, requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }

    return requestNodeList;
  }

  public static JsonNode genUpdateRequestAltLoc() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.putArray("locations").add(LOC_ALT_TESTVAL[0]);
    return rootNode;
  }

  public static JsonNode genTombstoneRequest() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put(TOMBSTONE_TEXT.get(), TOMBSTONE_TEXT_TESTVAL);
    return rootNode;
  }

  // Handle Attributes as ObjectNode 
  public static JsonNode genObjectNodeAttributeRecord(List<HandleAttribute> dbRecord)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    for (HandleAttribute row : dbRecord) {
      String type = row.type();
      String data = new String(row.data());
      if (row.index() != HS_ADMIN.index()) {
        rootNode.put(type, data); // We never want HS_ADMIN in our json
      }
    }
    return rootNode;
  }

  // Other Functions

  public static byte[] givenLandingPage(String handle) throws Exception{
    var landingPage = new String[]{"Placeholder landing page"};
    return setLocations(landingPage, handle, ObjectType.TOMBSTONE);
  }

  public static HandleAttribute givenLandingPageAttribute(byte[] handle) throws Exception{
    var data = givenLandingPage(new String(handle, StandardCharsets.UTF_8));
    return new HandleAttribute(LOC.index(), handle, LOC.get(), data);
  }

  public static byte[] setLocations(String[] userLocations, String handle, ObjectType type)
      throws TransformerException, ParserConfigurationException {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
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
    return documentToString(doc).getBytes(StandardCharsets.UTF_8);
  }

  private static String[] concatLocations(String[] userLocations, String handle, ObjectType type) {
    ArrayList<String> objectLocations = new ArrayList<>();
    objectLocations.addAll(List.of(defaultLocations(handle, type)));
    objectLocations.addAll(List.of(userLocations));
    return objectLocations.toArray(new String[0]);
  }

  private static String[] defaultLocations(String handle, ObjectType type) {
    switch (type) {
      case DIGITAL_SPECIMEN -> {
        String api = API_URL + "/specimens/" + handle;
        String ui = UI_URL + "/ds/" + handle;
        return new String[]{api, ui};
      }
      case MAPPING -> {
        return new String[]{ORCHESTRATION_URL + "/mapping/" + handle};
      }
      case SOURCE_SYSTEM -> {
        return new String[]{ORCHESTRATION_URL + "/source-system/" + handle};
      }
      case MEDIA_OBJECT -> {
        String api = API_URL + "/digitalMedia/" + handle;
        String ui = UI_URL + "/dm/" + handle;
        return new String[]{api, ui};
      }
      case ANNOTATION -> {
        return new String[]{API_URL + "/annotations/" + handle};
      }
      case ORGANISATION -> {
        return new String[]{SPECIMEN_HOST_TESTVAL};
      }
      case MAS -> {
        return new String[]{ORCHESTRATION_URL + "/mas/" + handle};
      }
      default -> {
        // Handle, DOI, Organisation (organisation handled separately)
        return new String[]{};
      }
    }
  }

  private static String documentToString(Document document) throws TransformerException {
    TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

    var transformer = TRANSFORMER_FACTORY.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  public static String loadResourceFile(String fileName) throws IOException {
    return new String(new ClassPathResource(fileName).getInputStream()
        .readAllBytes(), StandardCharsets.UTF_8);
  }

}
