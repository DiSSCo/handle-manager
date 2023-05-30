package eu.dissco.core.handlemanager.testUtils;

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
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_ATTRIBUTES;
import static eu.dissco.core.handlemanager.domain.PidRecords.NODE_DATA;
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
import static eu.dissco.core.handlemanager.domain.PidRecords.SPECIMEN_HOST_REQ;
import static eu.dissco.core.handlemanager.domain.PidRecords.STRUCTURAL_TYPE;
import static eu.dissco.core.handlemanager.domain.PidRecords.SUBJECT_PHYSICAL_IDENTIFIER;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOMBSTONE_TEXT;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_DISCIPLINE;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_DOMAIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.TOPIC_ORIGIN;
import static eu.dissco.core.handlemanager.domain.PidRecords.WAS_DERIVED_FROM;
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
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenBotanyRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DigitalSpecimenRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.DoiRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.HandleRecordRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.LivingOrPreserved;
import eu.dissco.core.handlemanager.domain.requests.attributes.MediaObjectRequest;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdType;
import eu.dissco.core.handlemanager.domain.requests.attributes.PhysicalIdentifier;
import eu.dissco.core.handlemanager.domain.requests.attributes.TombstoneRecordRequest;
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
  public static final String HANDLE = "20.5000.1025/QRS-321-ABC";
  public static final String PREFIX = "20.5000.1025";
  public static final String SUFFIX = "QRS-321-ABC";
  public static final String HANDLE_ALT = "20.5000.1025/ABC-123-QRS";
  public static final List<String> HANDLE_LIST_STR;

  // Record types
  public static final String RECORD_TYPE_HANDLE = "handle";
  public static final String RECORD_TYPE_DOI = "doi";
  public static final String RECORD_TYPE_DS = "digitalSpecimen";
  public static final String RECORD_TYPE_DS_BOTANY = "digitalSpecimenBotany";
  public static final String RECORD_TYPE_TOMBSTONE = "tombstone";
  public static final String RECORD_TYPE_MEDIA = "mediaObject";

  // Request Test Vals
  // Handles
  public static final String HANDLE_DOMAIN = "https://hdl.handle.net/";
  public static final String ROR_DOMAIN = "https://ror.org/";
  public static final String FDO_PROFILE_TESTVAL = HANDLE_DOMAIN + "21.T11148/d8de0819e144e4096645";
  public static final String ISSUED_FOR_AGENT_TESTVAL = ROR_DOMAIN + "0566bfb96";
  public static final String DIGITAL_OBJECT_TYPE_TESTVAL = HANDLE_DOMAIN + "21.T11148/1c699a5d1b4ad3ba4956";
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
  public static final String SPECIMEN_HOST_TESTVAL = ROR_DOMAIN + "0x123";
  public static final String SPECIMEN_HOST_NAME_TESTVAL = "Naturalis";
  public static final String IN_COLLECTION_FACILITY_TESTVAL = "20.5000.1025/OTHER-TRIPLET";
  //Botany Specimens
  public static final String OBJECT_TYPE_TESTVAL = "Herbarium Sheet";
  public static final LivingOrPreserved PRESERVED_OR_LIVING_TESTVAL = LivingOrPreserved.PRESERVED;
  // Media Objects
  public static final String MEDIA_URL_TESTVAL = "https://naturalis.nl/media/123";
  public static final String MEDIA_HASH_TESTVAL = "47bce5c74f589f48";
  public static final String PTR_TYPE = "handle";
  public static final String PTR_PRIMARY_NAME = "DiSSCo";
  public static final String PTR_PID_DOI = "http://doi.org/" + PID_ISSUER_TESTVAL_OTHER;
  public static final String PTR_TYPE_DOI = "doi";
  public static final String PTR_REGISTRATION_DOI_NAME = "Registration Agency";
  public final static String PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL = "BOTANICAL.QRS.123";
  public final static PhysicalIdentifier PHYSICAL_IDENTIFIER_TESTVAL_CETAF = new PhysicalIdentifier(
      PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
      PhysicalIdType.CETAF
  );
  public final static String EXTERNAL_PID = "21.T11148/d8de0819e144e4096645";
  public static final String DIGITAL_OBJECT_NAME_TESTVAL = "digitalSpecimen";

  // Tombstone Record vals
  public final static String TOMBSTONE_TEXT_TESTVAL = "pid was deleted";
  // Pid Type Record vals
  private final static String HANDLE_URI = "https://hdl.handle.net/";
  public static final String PTR_PID = HANDLE_URI + PID_ISSUER_TESTVAL_OTHER;
  public final static String PTR_HANDLE_RECORD = genPtrHandleRecord(false);
  public final static String PTR_DOI_RECORD = genPtrHandleRecord(true);
  public static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

  static {
    HANDLE_LIST_STR = List.of(HANDLE, HANDLE_ALT);
  }

  public static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
  public static DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  // Pid Type Records
  private static String genPtrHandleRecord(boolean isDoi) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    if (isDoi) {
      objectNode.put("pid", PTR_PID_DOI);
      objectNode.put("pidType", PTR_TYPE_DOI);
      objectNode.put("primaryNameFromPid", PTR_PRIMARY_NAME);
      objectNode.put("registrationAgencyDoiName", PTR_REGISTRATION_DOI_NAME);
    } else {
      objectNode.put("pid", PTR_PID);
      objectNode.put("pidType", PTR_TYPE);
      objectNode.put("primaryNameFromPid", PTR_PRIMARY_NAME);
    }
    try {
      return mapper.writeValueAsString(objectNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }

  // Single Handle Attribute Lists
  public static List<HandleAttribute> genHandleRecordAttributes(byte[] handle) throws Exception{

    List<HandleAttribute> fdoRecord = new ArrayList<>();
    var request = givenHandleRecordRequestObject();
    byte[] loc = setLocations(request.getLocations(), new String(handle, StandardCharsets.UTF_8));
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, loc));

    // 1: FDO Profile
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_PROFILE), handle, FDO_PROFILE,
        request.getFdoProfile().getBytes(StandardCharsets.UTF_8)));

    // 2: FDO Record License
    byte[] pidKernelMetadataLicense = "https://creativecommons.org/publicdomain/zero/1.0/".getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(FDO_RECORD_LICENSE), handle,
        FDO_RECORD_LICENSE, pidKernelMetadataLicense));

    // 3: DigitalObjectType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_TYPE), handle, DIGITAL_OBJECT_TYPE,
            request.getDigitalObjectType().getBytes(StandardCharsets.UTF_8)));

    // 4: DigitalObjectName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(DIGITAL_OBJECT_NAME), handle, DIGITAL_OBJECT_NAME,
            DIGITAL_OBJECT_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 5: Pid
    byte[] pid = ("https://hdl.handle.net/" + new String(handle, StandardCharsets.UTF_8)).getBytes(
        StandardCharsets.UTF_8);
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID), handle, PID, pid));

    // 6: PidIssuer
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER), handle, PID_ISSUER,
        request.getPidIssuer().getBytes(StandardCharsets.UTF_8)));

    // 7: pidIssuerName
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_ISSUER_NAME), handle, PID_ISSUER_NAME,
        PID_ISSUER_TESTVAL_OTHER.getBytes(StandardCharsets.UTF_8)));

    // 8: issuedForAgent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT), handle, ISSUED_FOR_AGENT,
        request.getIssuedForAgent().getBytes(StandardCharsets.UTF_8)));

    // 9: issuedForAgentName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(ISSUED_FOR_AGENT_NAME), handle, ISSUED_FOR_AGENT_NAME,
            ISSUED_FOR_AGENT_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 10: pidRecordIssueDate
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_DATE), handle,
        PID_RECORD_ISSUE_DATE, ISSUE_DATE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 11: pidRecordIssueNumber
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_RECORD_ISSUE_NUMBER), handle,
        PID_RECORD_ISSUE_NUMBER, "1".getBytes(StandardCharsets.UTF_8)));

    // 12: structuralType
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(STRUCTURAL_TYPE), handle,
        STRUCTURAL_TYPE, STRUCTURAL_TYPE_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 13: PidStatus
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "TEST".getBytes(StandardCharsets.UTF_8)));


    return fdoRecord;
  }

  public static List<HandleAttribute> genHandleRecordAttributesAltLoc(byte[] handle)
      throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle);

    byte[] locOriginal = setLocations(LOC_TESTVAL, new String(handle, StandardCharsets.UTF_8));
    var locOriginalAttr = new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locOriginal);

    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8));
    var locAltAttr = new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locAlt);

    attributes.set(attributes.indexOf(locOriginalAttr), locAltAttr);

    return attributes;
  }

  public static List<HandleAttribute> genTombstoneRecordFullAttributes(byte[] handle) throws Exception {
    List<HandleAttribute> attributes = genHandleRecordAttributes(handle);
    HandleAttribute oldPidStatus = new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle,
        PID_STATUS, PID_STATUS_TESTVAL.getBytes(StandardCharsets.UTF_8));
    attributes.remove(oldPidStatus);
    attributes.addAll(genTombstoneRecordRequestAttributes(handle));

    return attributes;
  }

  public static List<HandleAttribute> genUpdateRecordAttributesAltLoc(byte[] handle)
      throws ParserConfigurationException, TransformerException {
    byte[] locAlt = setLocations(LOC_ALT_TESTVAL, new String(handle, StandardCharsets.UTF_8));
    return List.of(new HandleAttribute(FIELD_IDX.get(LOC), handle, LOC, locAlt));
  }

  public static List<HandleAttribute> genTombstoneRecordRequestAttributes(byte[] handle) {
    List<HandleAttribute> tombstoneAttributes = new ArrayList<>();
    tombstoneAttributes.add(
        new HandleAttribute(FIELD_IDX.get(TOMBSTONE_TEXT), handle, TOMBSTONE_TEXT,
            TOMBSTONE_TEXT_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    tombstoneAttributes.add(new HandleAttribute(FIELD_IDX.get(PID_STATUS), handle, PID_STATUS,
        "ARCHIVED".getBytes(StandardCharsets.UTF_8)));
    return tombstoneAttributes;
  }

  public static List<HandleAttribute> genDoiRecordAttributes(byte[] handle) throws Exception{
    List<HandleAttribute> fdoRecord = genHandleRecordAttributes(handle);
    var request = givenDoiRecordRequestObject();

    // 40: referentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_TYPE), handle, REFERENT_TYPE, request.getReferentType().getBytes(StandardCharsets.UTF_8)));

    // 41: referentDoiName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_DOI_NAME), handle, REFERENT_DOI_NAME_TESTVAL, handle));

    // 42: referentName
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(REFERENT_NAME), handle, REFERENT_NAME, request.getReferentName().getBytes(StandardCharsets.UTF_8)));

    // 43: primaryReferentType
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(PRIMARY_REFERENT_TYPE), handle, PRIMARY_REFERENT_TYPE, request.getPrimaryReferentType().getBytes(StandardCharsets.UTF_8)));

    // 44: referent
    fdoRecord.add(new HandleAttribute(FIELD_IDX.get(REFERENT), handle, REFERENT,
        request.getReferent().getBytes(StandardCharsets.UTF_8)));

    return fdoRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> fdoRecord = genDoiRecordAttributes(handle);
    var request = givenDigitalSpecimenRequestObjectNullOptionals();

    // 200: Specimen Host
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle,
            SPECIMEN_HOST,
            request.getSpecimenHost().getBytes(StandardCharsets.UTF_8)));

    // 201: Specimen Host name
    fdoRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST_NAME), handle,
            SPECIMEN_HOST_NAME,
            SPECIMEN_HOST_NAME_TESTVAL.getBytes(StandardCharsets.UTF_8)));

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
    if (request.getPrimarySpecimenObjectIdName() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID_NAME), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_NAME,
              request.getPrimarySpecimenObjectIdName().getBytes(StandardCharsets.UTF_8)));
    }

    // 205: specimenObjectIdAbsenceReason
    if (request.getPrimarySpecimenObjectIdAbsenceReason() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE), handle,
              PRIMARY_SPECIMEN_OBJECT_ID_ABSENCE,
              request.getPrimarySpecimenObjectIdAbsenceReason().getBytes(StandardCharsets.UTF_8)));
    }

    // 206: otherSpecimenIds
    if (request.getOtherSpecimenIds() != null ){
      var otherSpecimenIds = Arrays.toString(request.getOtherSpecimenIds()).getBytes(StandardCharsets.UTF_8);
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(OTHER_SPECIMEN_IDS), handle,
              OTHER_SPECIMEN_IDS,
              otherSpecimenIds));
    }

    // 207: topicOrigin
    if (request.getTopicOrigin() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_ORIGIN), handle,
              TOPIC_ORIGIN,
              request.getTopicOrigin().getBytes(StandardCharsets.UTF_8)));
    }

    // 208: topicDomain
    if (request.getTopicDomain() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_DOMAIN), handle,
              TOPIC_DOMAIN,
              request.getTopicDomain().getBytes(StandardCharsets.UTF_8)));
    }

    // 209: topicDiscipline
    if (request.getTopicDiscipline() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(TOPIC_DISCIPLINE), handle,
              TOPIC_DISCIPLINE,
              request.getTopicDiscipline().getBytes(StandardCharsets.UTF_8)));
    }

    // 210: objectType
    if (request.getObjectType() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(OBJECT_TYPE), handle,
              OBJECT_TYPE,
              request.getObjectType().getBytes(StandardCharsets.UTF_8)));
    }

    // 211: livingOrPreserved
    if (request.getLivingOrPreserved() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(LIVING_OR_PRESERVED), handle,
              LIVING_OR_PRESERVED,
              request.getLivingOrPreserved().getBytes()));
    }

    // 212: baseTypeOfSpecimen
    if (request.getBaseTypeOfSpecimen() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(BASE_TYPE_OF_SPECIMEN), handle,
              BASE_TYPE_OF_SPECIMEN,
              request.getBaseTypeOfSpecimen().getBytes(StandardCharsets.UTF_8)));
    }

    // 213: informationArtefactType
    if (request.getInformationArtefactType() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(INFORMATION_ARTEFACT_TYPE), handle,
              INFORMATION_ARTEFACT_TYPE,
              request.getInformationArtefactType().getBytes(StandardCharsets.UTF_8)));
    }

    // 214: materialSampleType
    if (request.getMaterialSampleType() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(MATERIAL_SAMPLE_TYPE), handle,
              MATERIAL_SAMPLE_TYPE,
              request.getMaterialSampleType().getBytes(StandardCharsets.UTF_8)));
    }

    // 215: materialOrDigitalEntity
    if (request.getMaterialSampleType() != null){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(MATERIAL_OR_DIGITAL_ENTITY), handle,
              MATERIAL_OR_DIGITAL_ENTITY,
              request.getMaterialSampleType().getBytes()));
    }

    // 216: markedAsType
    if (request.getMarkedAsType() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(MARKED_AS_TYPE), handle,
              MARKED_AS_TYPE,
              request.getMarkedAsType().toString().getBytes(StandardCharsets.UTF_8)));
    }

    // 217: wasDerivedFrom
    if (request.getWasDerivedFrom() != null ){
      fdoRecord.add(
          new HandleAttribute(FIELD_IDX.get(WAS_DERIVED_FROM), handle,
              WAS_DERIVED_FROM,
              request.getWasDerivedFrom().getBytes(StandardCharsets.UTF_8)));
    }

    return fdoRecord;
  }

  public static List<HandleAttribute> genDigitalSpecimenBotanyAttributes(byte[] handle)
      throws Exception {
    return genDigitalSpecimenAttributes(handle);
  }

  public static List<HandleAttribute> genMediaObjectAttributes(byte[] handle)
      throws Exception {
    List<HandleAttribute> handleRecord = genDoiRecordAttributes(handle);
    byte[] ptr_record = PTR_HANDLE_RECORD.getBytes(StandardCharsets.UTF_8);

    // 14 Media Hash
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_HASH), handle,
        MEDIA_HASH, MEDIA_HASH_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 15 Subject Specimen Host
    handleRecord.add(
        new HandleAttribute(FIELD_IDX.get(SPECIMEN_HOST), handle, SPECIMEN_HOST, ptr_record));

    // 16 media hash
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(MEDIA_URL), handle,
        MEDIA_URL, MEDIA_URL_TESTVAL.getBytes(StandardCharsets.UTF_8)));

    // 17 : Subject Physical Identifier
    handleRecord.add(new HandleAttribute(FIELD_IDX.get(SUBJECT_PHYSICAL_IDENTIFIER), handle,
        SUBJECT_PHYSICAL_IDENTIFIER, PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL.getBytes(StandardCharsets.UTF_8)));
    return handleRecord;
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

  public static DigitalSpecimenRequest givenDigitalSpecimenRequestObjectNullOptionals() {
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
          PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
          null,null, null, null, null, null, null, null, null, null, null,null, null, null, null
          );
    } catch (InvalidRequestException e) {
      return null;
    }
  }

  public static DigitalSpecimenBotanyRequest genDigitalSpecimenBotanyRequestObject() {
    try {
      return new DigitalSpecimenBotanyRequest(
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
          PRIMARY_SPECIMEN_OBJECT_ID_TESTVAL,
          null,null, null, null, null, null, null, null, null, null, null,null, null, null, null
      );
    } catch (InvalidRequestException e) {
     return null;
    }
  }

  public static MediaObjectRequest genMediaRequestObject() {
    return new MediaObjectRequest(
        FDO_PROFILE_TESTVAL,
        ISSUED_FOR_AGENT_TESTVAL,
        DIGITAL_OBJECT_TYPE_TESTVAL,
        PID_ISSUER_TESTVAL_OTHER,
        STRUCTURAL_TYPE_TESTVAL,
        LOC_TESTVAL,
        REFERENT_NAME_TESTVAL,
        PRIMARY_REFERENT_TYPE_TESTVAL,
        MEDIA_HASH_TESTVAL,
        MEDIA_URL_TESTVAL,
        SPECIMEN_HOST_TESTVAL,
        PHYSICAL_IDENTIFIER_TESTVAL_CETAF
    );
  }

  public static TombstoneRecordRequest genTombstoneRecordRequestObject() {
    return new TombstoneRecordRequest(
        TOMBSTONE_TEXT
    );
  }

  public static ObjectNode givenSearchByPhysIdRequest() {
    var request = MAPPER.createObjectNode();
    var dataNode = MAPPER.createObjectNode();
    var attributeNode = MAPPER.createObjectNode();
    attributeNode.set(PRIMARY_SPECIMEN_OBJECT_ID, MAPPER.valueToTree(
        PHYSICAL_IDENTIFIER_TESTVAL_CETAF));
    attributeNode.put(SPECIMEN_HOST_REQ, SPECIMEN_HOST_TESTVAL);
    dataNode.set(NODE_ATTRIBUTES, attributeNode);
    request.set(NODE_DATA, dataNode);
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

  public static JsonApiWrapperReadSingle givenRecordResponseReadSingle(String handle, String path, String type, JsonNode attributes){
    return new JsonApiWrapperReadSingle(
        new JsonApiLinks(path),
        new JsonApiDataLinks(handle, type, attributes, new JsonApiLinks("https://hdl.handle.net/"+handle)));
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
      case RECORD_TYPE_HANDLE, "PID" -> {
        return genHandleRecordAttributes(handle);
      }
      case RECORD_TYPE_DOI -> {
        return genDoiRecordAttributes(handle);
      }
      case RECORD_TYPE_DS -> {
        return genDigitalSpecimenAttributes(handle);
      }
      case RECORD_TYPE_DS_BOTANY -> {
        return genDigitalSpecimenBotanyAttributes(handle);
      }
      case RECORD_TYPE_MEDIA -> {
        return genMediaObjectAttributes(handle);
      }
      default -> {
        return null;
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
      requestNodeData.put("type", RECORD_TYPE_HANDLE);
      requestNodeData.put("id", handle);
      requestNodeData.set("attributes", genTombstoneRequest());
      requestNodeRoot.set("data", requestNodeData);

      requestNodeList.add(requestNodeRoot.deepCopy());

      requestNodeData.removeAll();
      requestNodeRoot.removeAll();
    }

    return requestNodeList;
  }

  public static JsonNode genUpdateRequestAltLoc() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.putArray(LOC_REQ).add(LOC_ALT_TESTVAL[0]);
    return rootNode;
  }

  public static JsonNode genTombstoneRequest() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put(TOMBSTONE_TEXT, TOMBSTONE_TEXT_TESTVAL);
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
      if (row.index() != FIELD_IDX.get(HS_ADMIN)) {
        rootNode.put(type, data); // We never want HS_ADMIN in our json
      }
    }
    return rootNode;
  }

  // Other Functions

  public static byte[] setLocations(String[] userLocations, String handle)
      throws TransformerException, ParserConfigurationException {
    DOC_BUILDER_FACTORY.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder documentBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();

    var doc = documentBuilder.newDocument();
    var locations = doc.createElement("locations");
    doc.appendChild(locations);
    String[] objectLocations = concatLocations(userLocations, handle);

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

  private static String[] concatLocations(String[] userLocations, String handle){
    ArrayList<String> objectLocations = new ArrayList<>();
    objectLocations.addAll(List.of(defaultLocations(handle)));
    objectLocations.addAll(List.of(userLocations));
    return objectLocations.toArray(new String[0]);
  }

  private static String[] defaultLocations(String handle){
    String api = "https://sandbox.dissco.tech/api/v1/specimens/" + handle;
    String ui = "https://sandbox.dissco.tech/ds/" + handle;
    return new String[]{api, ui};
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
